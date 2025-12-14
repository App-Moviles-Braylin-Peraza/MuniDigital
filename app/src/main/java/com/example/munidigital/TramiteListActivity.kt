package com.example.munidigital

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.munidigital.network.Result
import com.example.munidigital.network.RetrofitClient
import com.example.munidigital.repository.TramiteRepository
import com.example.munidigital.ui.TramiteAdapter
import com.example.munidigital.viewmodel.TramitesViewModel
import com.example.munidigital.viewmodel.TramitesViewModelFactory

class TramiteListActivity : AppCompatActivity() {

    private lateinit var viewModel: TramitesViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    
    private var currentFilter: String? = "INICIADO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tramite_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configurar ViewModel
        val apiService = RetrofitClient.instance
        val repository = TramiteRepository(apiService)
        val factory = TramitesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[TramitesViewModel::class.java]

        // Inicializar vistas
        recyclerView = findViewById(R.id.tramite_recycler_view)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        progressBar = findViewById(R.id.progress_bar)
        emptyView = findViewById(R.id.tv_empty_state)

        recyclerView.layoutManager = LinearLayoutManager(this)

        swipeRefresh.setOnRefreshListener {
            loadTramites()
        }
        setupObservers()
        loadTramites()
    }

    private fun setupObservers() {
        viewModel.tramites.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                }
                is Result.Success -> {
                    swipeRefresh.isRefreshing = false
                    progressBar.visibility = View.GONE
                    
                    if (result.data.isEmpty()) {
                        emptyView.visibility = View.VISIBLE
                        emptyView.text = getString(R.string.no_hay_tramites)
                        recyclerView.visibility = View.GONE
                    } else {
                        emptyView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        
                        val adapter = TramiteAdapter(result.data)
                        recyclerView.adapter = adapter
                    }
                }
                is Result.Error -> {
                    swipeRefresh.isRefreshing = false
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
                null -> {
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (!swipeRefresh.isRefreshing) {
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun loadTramites() {
        // Cargar con paginación y filtro
        viewModel.loadTramites(
            page = 1,
            limit = 50,
            status = currentFilter,
            order = "DESC"
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tramite_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_filter_all -> {
                currentFilter = null
                loadTramites()
                true
            }
            R.id.action_filter_iniciado -> {
                currentFilter = "INICIADO"
                loadTramites()
                true
            }
            R.id.action_filter_en_proceso -> {
                currentFilter = "EN_PROCESO"
                loadTramites()
                true
            }
            R.id.action_filter_finalizado -> {
                currentFilter = "FINALIZADO"
                loadTramites()
                true
            }
            R.id.action_refresh -> {
                loadTramites()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTramites()
    }
}