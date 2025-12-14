package com.example.munidigital.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.munidigital.R
import com.example.munidigital.controller.TramiteController
import com.example.munidigital.data.TramiteApiDataManager
import com.example.munidigital.network.RetrofitClient
import kotlinx.coroutines.launch

class TramiteListActivity : AppCompatActivity() {

    private lateinit var tramiteController: TramiteController
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tramite_list)

        val apiService = RetrofitClient.instance
        val tramiteDataManager = TramiteApiDataManager(apiService)
        tramiteController = TramiteController(tramiteDataManager)

        recyclerView = findViewById(R.id.tramite_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadTramites()
    }

    private fun loadTramites() {
        lifecycleScope.launch {
            try {
                val tramites = tramiteController.obtenerTodosLosTramites()
                val adapter = TramiteAdapter(tramites)
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                Toast.makeText(this@TramiteListActivity, getString(R.string.error_cargar_tramites, e.message), Toast.LENGTH_LONG).show()
            }
        }
    }
}