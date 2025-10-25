package com.munidigital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import com.munidigital.controller.TramiteController
import com.munidigital.data.MemoryDataManager
import com.munidigital.model.Tramite
import com.munidigital.ui.screens.NewTramiteScreen
import com.munidigital.ui.theme.MuniDigitalTheme
import com.munidigital.ui.screens.SimpleDashboard

// NOTA: Para que esto compile, necesitas crear la Composable SimpleDashboard
// (que ahora incluye la navegación) y la NewTramiteScreen.

class MainActivity : ComponentActivity() {

    // 1. Declarar las dependencias (Inicialización tardía)
    private lateinit var dataManager: MemoryDataManager
    private lateinit var tramiteController: TramiteController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. INYECCIÓN DE DEPENDENCIAS: Conectando la arquitectura
        dataManager = MemoryDataManager()
        tramiteController = TramiteController(dataManager)

        // Cargar datos de prueba al iniciar (Opcional, pero útil para probar la lista)
        agregarDatosDePrueba()

        enableEdgeToEdge()
        setContent {
            MuniDigitalTheme {

                // Estado para manejar la navegación entre pantallas: "LIST" o "NEW"
                var currentScreen by remember { mutableStateOf("LIST") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // Lógica para mostrar la pantalla actual
                    when (currentScreen) {
                        "LIST" -> {
                            SimpleDashboard(
                                controller = tramiteController,
                                // Función para navegar a la pantalla de creación
                                onNavigateToNew = { currentScreen = "NEW" },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        "NEW" -> {
                            NewTramiteScreen(
                                tramiteController = tramiteController,
                                // Función para regresar al listado
                                onNavigateBack = { currentScreen = "LIST" }
                            )

                            when (currentScreen) {
                                "LIST" -> {
                                    // Muestra la pantalla de listado
                                    SimpleDashboard(
                                        controller = tramiteController,
                                        // Al presionar 'NUEVO' se actualiza el estado a "NEW"
                                        onNavigateToNew = { currentScreen = "NEW" },
                                        modifier = Modifier.padding(innerPadding)
                                    )
                                }
                                "NEW" -> {
                                    // Muestra la pantalla de creación
                                    NewTramiteScreen(
                                        tramiteController = tramiteController,
                                        // Al finalizar el trámite, regresa a "LIST"
                                        onNavigateBack = { currentScreen = "LIST" }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Función auxiliar para poblar con datos de prueba
    private fun agregarDatosDePrueba() {
        val controller = tramiteController

        // Trámite Pendiente
        controller.iniciarNuevoTramite("Patente Comercial", "Solicitud para negocio de ropa", listOf("/path/a/foto1.jpg"))

        // Trámite Aprobado (Necesita actualizar el estado en el DataManager)
        val aprobado = controller.iniciarNuevoTramite("Permiso Construcción", "Ampliación de terraza", emptyList())
        aprobado.estado = Tramite.ESTADO_APROBADO
        dataManager.actualizarTramite(aprobado)

        // Trámite Rechazado
        val rechazado = controller.iniciarNuevoTramite("Certificación Uso Suelo", "Para terreno rural sin servicios", emptyList())
        rechazado.estado = Tramite.ESTADO_RECHAZADO
        dataManager.actualizarTramite(rechazado)
    }
}

