package com.example.munidigital

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.munidigital.controller.TramiteController
import com.example.munidigital.data.TramiteApiDataManager
import com.example.munidigital.model.Adjunto
import com.example.munidigital.model.Attachment
import com.example.munidigital.network.Result
import com.example.munidigital.network.RetrofitClient
import com.example.munidigital.repository.TramiteRepository
import com.example.munidigital.viewmodel.TramitesViewModel
import com.example.munidigital.viewmodel.TramitesViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class DetalleTramiteActivity : AppCompatActivity() {

    private lateinit var tramiteController: TramiteController
    private lateinit var viewModel: TramitesViewModel
    private var tramiteId: Int = -1

    // Views
    private lateinit var tvStatus: TextView
    private lateinit var tvSummaryTitle: TextView
    private lateinit var tvSummaryId: TextView
    private lateinit var tvSummaryType: TextView
    private lateinit var tvSummarySubmitted: TextView
    private lateinit var tvSummaryAddress: TextView
    private lateinit var tvDescriptionContent: TextView
    private lateinit var tvLocationAddress: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnShare: Button
    private lateinit var btnDelete: Button
    private lateinit var tvAttachmentsLabel: TextView
    private lateinit var layoutAttachments: LinearLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_tramite)

        // Inicialización del Controller con API
        val apiService = RetrofitClient.instance
        val tramiteDataManager = TramiteApiDataManager(apiService)
        tramiteController = TramiteController(tramiteDataManager)

        // Inicializar ViewModel
        val repository = TramiteRepository(apiService)
        val factory = TramitesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[TramitesViewModel::class.java]

        // Inicializar vistas
        initViews()
        setupObservers()

        // Recibir el ID del trámite
        tramiteId = intent.getIntExtra("TRAMITE_ID", -1)
        if (tramiteId != -1) {
            cargarDetalles(tramiteId)
            // Cargar fotos desde la API
            viewModel.loadPhotos(tramiteId)
        } else {
            Toast.makeText(this, getString(R.string.error_tramite_id_no_encontrado), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initViews() {
        tvStatus = findViewById(R.id.tv_status)
        tvSummaryTitle = findViewById(R.id.tv_summary_title)
        tvSummaryId = findViewById(R.id.tv_summary_id)
        tvSummaryType = findViewById(R.id.tv_summary_type)
        tvSummarySubmitted = findViewById(R.id.tv_summary_submitted)
        tvSummaryAddress = findViewById(R.id.tv_summary_address)
        tvDescriptionContent = findViewById(R.id.tv_description_content)
        tvLocationAddress = findViewById(R.id.tv_location_address)
        btnBack = findViewById(R.id.btn_back)
        btnShare = findViewById(R.id.btn_share)
        btnDelete = findViewById(R.id.btn_delete)
        tvAttachmentsLabel = findViewById(R.id.tv_attachments_label)
        layoutAttachments = findViewById(R.id.layout_attachments)
        progressBar = findViewById(R.id.progress_bar)

        // Configurar botón de regreso
        btnBack.setOnClickListener {
            finish()
        }

        // Configurar botón de compartir
        btnShare.setOnClickListener {
            compartirTramite()
        }

        // Configurar botón eliminar trámite
        btnDelete.setOnClickListener {
            showDeleteTramiteDialog()
        }
    }

    /**
     * Comparte la información del trámite
     */
    private fun compartirTramite() {
        val shareText = """
            Trámite: ${tvSummaryTitle.text}
            ID: ${tvSummaryId.text}
            Estado: ${tvStatus.text}
            Descripción: ${tvDescriptionContent.text}
        """.trimIndent()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.compartir_tramite)))
    }

    /**
     * Muestra un diálogo de confirmación para eliminar el trámite
     */
    private fun showDeleteTramiteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar trámite")
            .setMessage("¿Estás seguro de que deseas eliminar este trámite? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarTramite()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Lógica para eliminar el trámite actual
     */
    private fun eliminarTramite() {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                // Llamar al controller para eliminar el trámite
                tramiteController.eliminarTramite(tramiteId)
                progressBar.visibility = View.GONE

                Toast.makeText(this@DetalleTramiteActivity, "Trámite eliminado", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK) // Notificar a la actividad anterior
                finish()
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("DetalleTramite", "Error al eliminar trámite", e)
                Toast.makeText(this@DetalleTramiteActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        // Observar las fotos del trámite
        viewModel.photos.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    Log.d("DetalleTramite", "Cargando fotos...")
                    progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    Log.d("DetalleTramite", "=== FOTOS RECIBIDAS ===")
                    Log.d("DetalleTramite", "Total de fotos: ${result.data.size}")
                    result.data.forEachIndexed { index, foto ->
                        Log.d("DetalleTramite", "Foto ${index + 1}: id=${foto.id}, url=${foto.url}")
                    }
                    progressBar.visibility = View.GONE
                    displayPhotos(result.data)
                }
                is Result.Error -> {
                    Log.e("DetalleTramite", "Error al cargar fotos: ${result.message}")
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error al cargar fotos: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                null -> {
                    progressBar.visibility = View.GONE
                }
            }
        }

        // Observar resultado de eliminar foto
        viewModel.deletePhotoResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Foto eliminada exitosamente", Toast.LENGTH_SHORT).show()
                    // Recargar fotos después de eliminar
                    viewModel.loadPhotos(tramiteId)
                }
                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error al eliminar foto: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                null -> {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun cargarDetalles(id: Int) {
        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                val tramite = tramiteController.obtenerTramitePorId(id)
                progressBar.visibility = View.GONE

                if (tramite != null) {
                    // Log para verificar qué dirección recibimos de la API
                    Log.d("DetalleTramite", "Dirección del trámite: '${tramite.direccion}'")

                    // Llenar los datos en la UI
                    tvSummaryTitle.text = tramite.title
                    tvSummaryId.text = getString(R.string.tramite_id_format, tramite.id)
                    tvSummaryType.text = tramite.title // Usar el título como tipo o puedes usar getString(R.string.tipo_de_tramite)
                    tvSummarySubmitted.text = tramite.creationDate
                    tvSummaryAddress.text = tramite.direccion ?: getString(R.string.sin_direccion)
                    tvDescriptionContent.text = tramite.description ?: getString(R.string.sin_descripcion)
                    tvLocationAddress.text = tramite.direccion ?: getString(R.string.sin_direccion_especificada)
                    tvStatus.text = tramite.status

                    // Configurar color del status según el estado
                    configureStatusColor(tramite.status)

                    // Mostrar attachments (si no son fotos)
                    displayAttachments(tramite.attachments)
                } else {
                    Toast.makeText(this@DetalleTramiteActivity, getString(R.string.tramite_no_encontrado), Toast.LENGTH_LONG).show()
                    finish()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("DetalleTramite", "Error al cargar detalles", e)
                Toast.makeText(this@DetalleTramiteActivity, getString(R.string.error_cargar_detalles, e.message), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun configureStatusColor(status: String) {
        val colorRes = when (status.lowercase()) {
            "iniciado", "pendiente" -> android.R.color.holo_orange_dark
            "en_proceso", "en proceso" -> android.R.color.holo_blue_dark
            "finalizado", "aprobado" -> android.R.color.holo_green_dark
            "rechazado", "cancelado" -> android.R.color.holo_red_dark
            else -> android.R.color.darker_gray
        }
        tvStatus.setTextColor(getColor(colorRes))
    }

    private fun displayAttachments(attachments: List<Attachment>?) {
        if (attachments.isNullOrEmpty()) {
            // No mostrar esta sección si no hay attachments
            // Las fotos se manejan por separado en displayPhotos()
            return
        }

        // Agregar cada attachment dinámicamente (solo archivos, no imágenes)
        attachments.forEach { attachment ->
            // Filtrar solo archivos que NO son imágenes
            if (attachment.file_type?.contains("image", ignoreCase = true) != true) {
                val attachmentView = LayoutInflater.from(this)
                    .inflate(R.layout.item_attachment, layoutAttachments, false)

                val attachmentContainer = attachmentView.findViewById<View>(R.id.attachment_container)
                val attachmentIcon = attachmentView.findViewById<ImageView>(R.id.attachment_icon)
                val attachmentName = attachmentView.findViewById<TextView>(R.id.attachment_name)
                val attachmentSize = attachmentView.findViewById<TextView>(R.id.attachment_size)

                // Configurar datos
                attachmentName.text = attachment.name

                // Mostrar tamaño si está disponible
                attachment.size?.let { size ->
                    attachmentSize.visibility = View.VISIBLE
                    attachmentSize.text = formatFileSize(size)
                }

                // Configurar icono según tipo de archivo
                val iconRes = when {
                    attachment.file_type?.contains("pdf", ignoreCase = true) == true -> android.R.drawable.ic_menu_report_image
                    attachment.file_type?.contains("video", ignoreCase = true) == true -> android.R.drawable.ic_menu_slideshow
                    else -> android.R.drawable.ic_menu_info_details
                }
                attachmentIcon.setImageResource(iconRes)

                // Click listener para abrir el attachment
                attachmentContainer.setOnClickListener {
                    openAttachment(attachment)
                }

                layoutAttachments.addView(attachmentView)
            }
        }
    }

    private fun openAttachment(attachment: Attachment) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse(attachment.file_url)

            // Determinar MIME type
            val mimeType = when {
                attachment.file_type != null -> attachment.file_type
                attachment.file_url.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
                attachment.file_url.endsWith(".jpg", ignoreCase = true) ||
                        attachment.file_url.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
                attachment.file_url.endsWith(".png", ignoreCase = true) -> "image/png"
                attachment.file_url.endsWith(".mp4", ignoreCase = true) -> "video/mp4"
                else -> "*/*"
            }

            intent.setDataAndType(uri, mimeType)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            // Intentar abrir con una aplicación
            startActivity(Intent.createChooser(intent, getString(R.string.abrir_con)))
        } catch (e: Exception) {
            Log.e("DetalleTramite", "Error al abrir attachment", e)
            Toast.makeText(
                this,
                getString(R.string.error_abrir_archivo, e.message),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }

    /**
     * Muestra las fotos cargadas desde la API
     */
    private fun displayPhotos(photos: List<Adjunto>) {
        // Limpiar fotos previas (pero no todos los attachments)
        // Solo limpiar si vamos a agregar fotos
        if (photos.isEmpty()) {
            // Si no hay fotos, solo ocultar la etiqueta si no hay nada más
            if (layoutAttachments.childCount == 0) {
                tvAttachmentsLabel.visibility = View.GONE
                layoutAttachments.visibility = View.GONE
            }
            Log.d("DetalleTramite", "No hay fotos para mostrar")
            return
        }

        // Mostrar sección
        tvAttachmentsLabel.visibility = View.VISIBLE
        layoutAttachments.visibility = View.VISIBLE

        // Actualizar el título con el contador de fotos
        tvAttachmentsLabel.text = "Fotos (${photos.size}):"

        // Agregar cada foto dinámicamente
        photos.forEach { photo ->
            Log.d("DetalleTramite", "Agregando foto con URL: ${photo.url}")

            val photoView = LayoutInflater.from(this)
                .inflate(R.layout.item_photo, layoutAttachments, false)

            val imageView = photoView.findViewById<ImageView>(R.id.iv_photo)
            val btnDelete = photoView.findViewById<ImageButton>(R.id.btn_delete_photo)

            // Cargar imagen desde URL
            loadImageFromUrl(photo.url, imageView)

            // Click en la imagen para verla en grande
            imageView.setOnClickListener {
                openPhotoInBrowser(photo.url)
            }

            // Click en el contenedor completo para abrir
            photoView.setOnClickListener {
                openPhotoInBrowser(photo.url)
            }

            // Click en botón de eliminar
            btnDelete.setOnClickListener {
                showDeletePhotoDialog(photo)
            }

            layoutAttachments.addView(photoView)
        }
    }

    /**
     * Abre la foto en el navegador
     */
    private fun openPhotoInBrowser(url: String) {
        try {
            Log.d("DetalleTramite", "Intentando abrir foto en navegador: $url")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("DetalleTramite", "Error al abrir foto en navegador", e)
            Toast.makeText(this, "Error al abrir la foto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Muestra un diálogo de confirmación para eliminar una foto
     */
    private fun showDeletePhotoDialog(photo: Adjunto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar foto")
            .setMessage("¿Estás seguro de que deseas eliminar esta foto?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deletePhoto(tramiteId, photo.url)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Carga una imagen desde URL de forma asíncrona
     */
    private fun loadImageFromUrl(url: String, imageView: ImageView) {
        Log.d("DetalleTramite", ">>> Cargando imagen: $url")

        // Mostrar placeholder
        imageView.setImageResource(android.R.drawable.ic_menu_gallery)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as java.net.HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                withContext(Dispatchers.Main) {
                    if (bitmap != null) {
                        Log.d("DetalleTramite", "✓ Imagen cargada: ${bitmap.width}x${bitmap.height}")
                        imageView.setImageBitmap(bitmap)
                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    } else {
                        Log.e("DetalleTramite", "✗ Bitmap null")
                        imageView.setImageResource(android.R.drawable.ic_menu_report_image)
                    }
                }
            } catch (e: Exception) {
                Log.e("DetalleTramite", "✗ Error al cargar imagen: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    imageView.setImageResource(android.R.drawable.ic_menu_report_image)
                }
            }
        }
    }
}