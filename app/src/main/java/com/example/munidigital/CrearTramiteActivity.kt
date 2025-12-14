package com.example.munidigital

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.munidigital.controller.CatalogoController
import com.example.munidigital.data.TipoTramiteMemoryDataManager
import com.example.munidigital.network.Result
import com.example.munidigital.network.RetrofitClient
import com.example.munidigital.network.SessionManager
import com.example.munidigital.repository.TramiteRepository
import com.example.munidigital.viewmodel.TramitesViewModel
import com.example.munidigital.viewmodel.TramitesViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

class CrearTramiteActivity : AppCompatActivity() {

    // ViewModels y Controllers
    private lateinit var viewModel: TramitesViewModel
    private lateinit var catalogoController: CatalogoController

    // UI Elements
    private lateinit var spinnerType: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvImageLoaded: TextView
    private lateinit var tvAddressDisplay: TextView
    private lateinit var tvPhotosCounter: TextView

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentAddress: String = ""

    // Photos
    private val selectedPhotos = mutableListOf<Uri>()
    private var imageBitmap: Bitmap? = null
    private var createdTramiteId: Int? = null

    // Activity Result Launchers
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val clipData = data?.clipData

            if (clipData != null) {
                // Múltiples imágenes seleccionadas
                val count = clipData.itemCount.coerceAtMost(5 - selectedPhotos.size)
                for (i in 0 until count) {
                    val imageUri = clipData.getItemAt(i).uri
                    selectedPhotos.add(imageUri)
                }
                tvImageLoaded.visibility = View.VISIBLE
                updatePhotosCounter()
                Toast.makeText(
                    this,
                    "Total: ${selectedPhotos.size}/5 fotos seleccionadas",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("CrearTramite", "Múltiples fotos desde galería. Total: ${selectedPhotos.size}")
            } else if (data?.data != null) {
                // Una sola imagen seleccionada
                val imageUri = data.data!!
                if (selectedPhotos.size < 5) {
                    selectedPhotos.add(imageUri)
                    tvImageLoaded.visibility = View.VISIBLE
                    updatePhotosCounter()
                    Toast.makeText(
                        this,
                        "Foto ${selectedPhotos.size}/5 añadida",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("CrearTramite", "Una foto desde galería. Total: ${selectedPhotos.size}")
                } else {
                    Toast.makeText(this, "Máximo 5 fotos", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Foto desde cámara (viene como Bitmap en extras)
                val extras = data?.extras
                imageBitmap = extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    try {
                        val imageUri = saveBitmapToCache(imageBitmap!!)
                        selectedPhotos.add(imageUri)
                        tvImageLoaded.visibility = View.VISIBLE
                        updatePhotosCounter()
                        Toast.makeText(
                            this,
                            "Foto ${selectedPhotos.size}/5 capturada",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("CrearTramite", "Foto capturada con cámara. Total: ${selectedPhotos.size}")
                    } catch (e: Exception) {
                        Log.e("CrearTramite", "Error al guardar foto de cámara", e)
                        Toast.makeText(this, "Error al guardar foto", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private val cameraPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, getString(R.string.permiso_camara_requerido), Toast.LENGTH_SHORT).show()
        }
    }

    private val storagePermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchGallery()
        } else {
            Toast.makeText(this, getString(R.string.permiso_almacenamiento_requerido), Toast.LENGTH_SHORT).show()
        }
    }

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getCurrentLocation()
            }
            else -> {
                Toast.makeText(this, getString(R.string.permiso_ubicacion_denegado), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_tramite)

        // Inicializar SessionManager
        SessionManager.init(applicationContext)

        // Configuración de ViewModel
        val apiService = RetrofitClient.instance
        val repository = TramiteRepository(apiService)
        val factory = TramitesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[TramitesViewModel::class.java]

        // Configuración de Catálogo
        val tipoTramiteDataManager = TipoTramiteMemoryDataManager()
        catalogoController = CatalogoController(tipoTramiteDataManager)

        // Binding de Vistas
        spinnerType = findViewById(R.id.spinner_type)
        descriptionEditText = findViewById(R.id.edit_text_description)
        submitButton = findViewById(R.id.btn_submit)
        progressBar = findViewById(R.id.progress_bar)
        tvImageLoaded = findViewById(R.id.tv_image_loaded)
        tvAddressDisplay = findViewById(R.id.tv_address_display)
        tvPhotosCounter = findViewById(R.id.tv_photos_counter)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Setup
        setupObservers()
        setupListeners()
        setupCatalogoSpinner()
    }

    private fun setupObservers() {
        // Observar resultado de creación
        viewModel.createResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    submitButton.isEnabled = false
                }
                is Result.Success -> {
                    // Guardar el ID del trámite creado
                    createdTramiteId = result.data.id
                    Log.d("CrearTramite", "Trámite creado con ID: ${result.data.id}")

                    // Si hay fotos seleccionadas, subirlas
                    if (selectedPhotos.isNotEmpty()) {
                        uploadPhotos(result.data.id)
                    } else {
                        progressBar.visibility = View.GONE
                        submitButton.isEnabled = true
                        Toast.makeText(this, getString(R.string.tramite_creado_exitoso), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    submitButton.isEnabled = true
                    Toast.makeText(this, getString(R.string.error_crear_tramite, result.message), Toast.LENGTH_LONG).show()
                }
                null -> {
                    // Estado inicial
                }
            }
        }

        // Observar resultado de subida de fotos
        viewModel.uploadPhotoResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    Log.d("CrearTramite", "Subiendo foto...")
                }
                is Result.Success -> {
                    Log.d("CrearTramite", "Foto subida exitosamente: ${result.data.url}")
                }
                is Result.Error -> {
                    Log.e("CrearTramite", "Error al subir foto: ${result.message}")
                    Toast.makeText(this, "Error al subir foto: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                null -> {}
            }
        }
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.btn_camera).setOnClickListener { takePicture() }
        findViewById<Button>(R.id.btn_gallery).setOnClickListener { pickFromGallery() }
        findViewById<Button>(R.id.btn_add_address).setOnClickListener { showAddAddressDialog() }
        findViewById<Button>(R.id.btn_use_location).setOnClickListener { requestLocationPermission() }

        submitButton.setOnClickListener {
            val title = spinnerType.selectedItem?.toString()
            val description = descriptionEditText.text.toString().trim()

            // Validaciones
            when {
                !spinnerType.isEnabled -> {
                    Toast.makeText(this, getString(R.string.error_cargar_tipos_procedimiento), Toast.LENGTH_LONG).show()
                }
                title.isNullOrBlank() -> {
                    Toast.makeText(this, getString(R.string.error_seleccione_tipo_tramite), Toast.LENGTH_SHORT).show()
                }
                description.isEmpty() -> {
                    Toast.makeText(this, getString(R.string.error_ingrese_descripcion), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    crearTramite(title, description)
                }
            }
        }
    }

    private fun setupCatalogoSpinner() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val tiposDisponibles = catalogoController.obtenerTodosLosTipos()

                Log.d("CrearTramite", "Tipos cargados: ${tiposDisponibles.size}")
                tiposDisponibles.forEach { tipo ->
                    Log.d("CrearTramite", "  - ${tipo.nombre}")
                }

                if (tiposDisponibles.isEmpty()) {
                    Toast.makeText(
                        this@CrearTramiteActivity,
                        getString(R.string.no_hay_tipos_tramite),
                        Toast.LENGTH_LONG
                    ).show()
                    spinnerType.isEnabled = false
                } else {
                    val nombres = tiposDisponibles.map { it.nombre }
                    val adapter = ArrayAdapter(
                        this@CrearTramiteActivity,
                        android.R.layout.simple_spinner_item,
                        nombres
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerType.adapter = adapter
                    spinnerType.isEnabled = true
                    Log.d("CrearTramite", "Spinner configurado con ${nombres.size} elementos")
                }

                progressBar.visibility = View.GONE
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("CrearTramite", "Error al cargar el catálogo", e)
                Toast.makeText(
                    this@CrearTramiteActivity,
                    getString(R.string.error_inesperado, e.message),
                    Toast.LENGTH_LONG
                ).show()
                spinnerType.isEnabled = false
            }
        }
    }

    private fun crearTramite(title: String, description: String) {
        Log.d("CrearTramite", "Creando trámite con dirección: $currentAddress")
        viewModel.createTramite(
            title = title,
            description = description,
            direccion = currentAddress
        )
    }

    /**
     * Sube todas las fotos seleccionadas al trámite creado
     */
    private fun uploadPhotos(tramiteId: Int) {
        Log.d("CrearTramite", "=== INICIANDO SUBIDA DE FOTOS ===")
        Log.d("CrearTramite", "Trámite ID: $tramiteId")
        Log.d("CrearTramite", "Total fotos en lista: ${selectedPhotos.size}")

        if (selectedPhotos.isEmpty()) {
            Log.d("CrearTramite", "No hay fotos para subir")
            progressBar.visibility = View.GONE
            submitButton.isEnabled = true
            Toast.makeText(this, getString(R.string.tramite_creado_exitoso), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        var uploadedCount = 0
        val totalPhotos = selectedPhotos.size

        Toast.makeText(this, "Subiendo $totalPhotos foto(s)...", Toast.LENGTH_SHORT).show()
        Log.d("CrearTramite", "Iniciando proceso de subida...")

        lifecycleScope.launch {
            for ((index, photoUri) in selectedPhotos.withIndex()) {
                try {
                    Log.d("CrearTramite", ">>> Subiendo foto ${index + 1}/$totalPhotos - URI: $photoUri")
                    viewModel.uploadPhoto(tramiteId, applicationContext, photoUri)

                    // Esperar un momento entre subidas para evitar sobrecarga
                    kotlinx.coroutines.delay(500)
                    uploadedCount++
                    Log.d("CrearTramite", "✓ Foto ${index + 1} subida exitosamente")

                } catch (e: Exception) {
                    Log.e("CrearTramite", "✗ Error al subir foto ${index + 1}: ${e.message}", e)
                }
            }

            // Una vez terminadas todas las subidas
            progressBar.visibility = View.GONE
            submitButton.isEnabled = true

            if (uploadedCount == totalPhotos) {
                Toast.makeText(
                    this@CrearTramiteActivity,
                    "Trámite creado con $uploadedCount foto(s)",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this@CrearTramiteActivity,
                    "Trámite creado. $uploadedCount de $totalPhotos fotos subidas",
                    Toast.LENGTH_LONG
                ).show()
            }

            finish()
        }
    }

    private fun updatePhotosCounter() {
        if (selectedPhotos.isEmpty()) {
            tvPhotosCounter.visibility = View.GONE
            tvImageLoaded.visibility = View.GONE
            Log.d("CrearTramite", "Contador actualizado: 0 fotos")
        } else {
            tvPhotosCounter.visibility = View.VISIBLE
            tvPhotosCounter.text = "${selectedPhotos.size} foto(s) seleccionada(s) (máx 5)"
            tvImageLoaded.visibility = View.VISIBLE
            Log.d("CrearTramite", "Contador actualizado: ${selectedPhotos.size} fotos")
        }
    }

    private fun takePicture() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            pickImageLauncher.launch(takePictureIntent)
        } else {
            Toast.makeText(this, "No se encontró aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                launchGallery()
            } else {
                storagePermissionRequest.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                launchGallery()
            } else {
                storagePermissionRequest.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun launchGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        pickImageLauncher.launch(pickPhotoIntent)
    }

    private fun showAddAddressDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_address, null)
        val etDistrito = dialogView.findViewById<EditText>(R.id.et_distrito)
        val etOtrasSenas = dialogView.findViewById<EditText>(R.id.et_otras_senas)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.agregar_direccion))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.agregar)) { _, _ ->
                val distrito = etDistrito.text.toString()
                val otrasSenas = etOtrasSenas.text.toString()
                val address = "Puntarenas, Puntarenas, $distrito, $otrasSenas"
                currentAddress = address
                tvAddressDisplay.text = address
                tvAddressDisplay.visibility = View.VISIBLE
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.permiso_ubicacion_denegado), Toast.LENGTH_SHORT).show()
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            val fullAddress = "${address.locality ?: ""}, ${address.subLocality ?: ""}, ${address.thoroughfare ?: ""}"
                            currentAddress = fullAddress
                            tvAddressDisplay.text = fullAddress
                            tvAddressDisplay.visibility = View.VISIBLE
                            Toast.makeText(this, "Ubicación obtenida", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        Log.e("CrearTramite", "Error al obtener dirección", e)
                        Toast.makeText(this, "Error al obtener dirección", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No se pudo obtener ubicación", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("CrearTramite", "Error al obtener ubicación", e)
                Toast.makeText(this, "Error al obtener ubicación", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri {
        val file = File(cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return Uri.fromFile(file)
    }
}