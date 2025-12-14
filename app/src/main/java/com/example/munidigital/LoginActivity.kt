package com.example.munidigital

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.munidigital.model.LoginRequest
import com.example.munidigital.network.RetrofitClient
import com.example.munidigital.network.SessionManager
import com.example.munidigital.repository.AuthRepository
import com.example.munidigital.viewmodel.LoginViewModel
import com.example.munidigital.viewmodel.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar SessionManager
        SessionManager.init(applicationContext)
        
        // Verificar si ya está logueado
        if (SessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_login) // Asumiendo que tienes este layout

        // --- Configuración del ViewModel ---
        val apiService = RetrofitClient.instance
        val authRepository = AuthRepository(apiService)
        val factory = LoginViewModelFactory(authRepository)
        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        // --- Binding de Vistas ---
        usernameEditText = findViewById(R.id.et_username)
        passwordEditText = findViewById(R.id.et_password)
        loginButton = findViewById(R.id.btn_login)
        loadingProgressBar = findViewById(R.id.loading_progress_bar) // Asumiendo que tienes este ProgressBar

        // --- Observadores ---
        viewModel.loginResult.observe(this) { result ->
            result.success?.let {
                // Guardar token y userId
                SessionManager.authToken = it.token
                SessionManager.userId = it.user.id
                Toast.makeText(this, getString(R.string.login_exitoso), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            result.error?.let {
                Toast.makeText(this, getString(R.string.error_login, it), Toast.LENGTH_LONG).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            loginButton.isEnabled = !isLoading
        }

        // --- Listener del Botón ---
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validaciones según la API
            when {
                username.isEmpty() -> {
                    Toast.makeText(this, getString(R.string.error_ingrese_usuario), Toast.LENGTH_SHORT).show()
                }
                username.length < 4 -> {
                    Toast.makeText(this, getString(R.string.error_usuario_minimo), Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(this, getString(R.string.error_ingrese_contrasena), Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(this, getString(R.string.error_contrasena_minimo), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val loginRequest = LoginRequest(username, password)
                    viewModel.login(loginRequest)
                }
            }
        }
    }
}