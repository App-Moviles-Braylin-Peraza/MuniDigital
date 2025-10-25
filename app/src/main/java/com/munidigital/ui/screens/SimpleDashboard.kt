// SimpleDashboard.kt en el paquete com.munidigital.ui.screens
package com.munidigital.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.munidigital.controller.TramiteController
import com.munidigital.model.Tramite // Asegúrate de importar tu clase Tramite

@Composable
fun SimpleDashboard(
    controller: TramiteController,
    onNavigateToNew: () -> Unit, // Función para ir a la pantalla de creación
    modifier: Modifier = Modifier
) {
    // 1. Obtener los datos del Controller (Idealmente, este estado debería ser observado)
    // Por simplicidad, llamamos al controller directamente:
    val todosLosTramites = controller.verTodosLosTramites()
    val total = todosLosTramites.size
    val pendientes = controller.verTramitesPorEstado(Tramite.ESTADO_PENDIENTE).size

    // Usamos el Scaffold para darle la estructura visual principal a esta pantalla
    Scaffold(
        modifier = modifier.fillMaxSize(),
        // 3. Definición del botón flotante (FAB)
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNew) {
                Text("NUEVO")
            }
        }
    ) { innerPadding ->
        // Contenido principal de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // Texto simple como PLACEHOLDER de la lista real
            Text(
                text = "Total de Trámites: $total \nPendientes de Revisión: $pendientes",
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // NOTA: Aquí iría tu LazyColumn o RecyclerView real para mostrar los ítems.
        }
    }
}