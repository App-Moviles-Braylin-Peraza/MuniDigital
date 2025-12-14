package com.example.munidigital

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.munidigital.network.SessionManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar SessionManager
        SessionManager.init(applicationContext)
        
        // Verificar autenticación
        if (!SessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_main)

        // Inicializar botones
        val myProceduresButton: Button = findViewById(R.id.btn_my_procedures)
        myProceduresButton.setOnClickListener {
            val intent = Intent(this, TramiteListActivity::class.java)
            startActivity(intent)
        }

        val startNewButton: Button = findViewById(R.id.btn_start_new)
        startNewButton.setOnClickListener {
            val intent = Intent(this, CrearTramiteActivity::class.java)
            startActivity(intent)
        }

        val logoutButton: Button = findViewById(R.id.btn_logout)
        logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.cerrar_sesion))
            .setMessage(getString(R.string.confirmar_cerrar_sesion))
            .setPositiveButton(getString(R.string.si)) { _, _ ->
                performLogout()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun performLogout() {
        SessionManager.clearSession()
        Toast.makeText(this, getString(R.string.sesion_cerrada), Toast.LENGTH_SHORT).show()
        
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
