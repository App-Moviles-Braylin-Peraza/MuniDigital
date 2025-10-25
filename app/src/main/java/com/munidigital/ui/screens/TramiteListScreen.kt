// NewTramiteScreen.kt en el paquete com.munidigital.ui.screens
package com.munidigital.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.munidigital.controller.TramiteController
import com.munidigital.util.FileUtil

// Necesitas la función 'onNavigateBack' para indicar a la Activity que debe cerrar esta pantalla
@Composable
fun NewTramiteScreen(
    tramiteController: TramiteController,
    onNavigateBack: () -> Unit // Función que se llama para regresar
) {
    // 1. Declarar los estados de la interfaz
    // Estas variables 'remember' guardan el valor que el usuario escribe en los campos.
    var tipoInput by remember { mutableStateOf("Patente Comercial") }
    var descripcionInput by remember { mutableStateOf("") }
    var archivosAdjuntos by remember { mutableStateOf(listOf<String>()) }
    val context = LocalContext.current // Obtenemos el contexto para mostrar el diálogo

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nuevo Trámite Municipal") }) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- CAMPO TIPO DE TRÁMITE (Simulado con TextField) ---
                OutlinedTextField(
                    value = tipoInput,
                    onValueChange = { tipoInput = it },
                    label = { Text("Tipo de Trámite") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- CAMPO DESCRIPCIÓN (EditText en Compose) ---
                OutlinedTextField(
                    value = descripcionInput,
                    onValueChange = { descripcionInput = it },
                    label = { Text("Descripción Detallada") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- BOTÓN ADJUNTAR ARCHIVOS (Placeholder) ---
                Button(onClick = {
                    // Lógica para abrir la cámara/galería (se implementaría aquí)
                    // Por ahora, solo simula que se adjuntó un archivo.
                    archivosAdjuntos = archivosAdjuntos + "/temp/adjunto_${archivosAdjuntos.size + 1}.jpg"
                }) {
                    Text("Adjuntar Archivo (${archivosAdjuntos.size} adjuntos)")
                }
                Spacer(modifier = Modifier.height(32.dp))

                // --- BOTÓN ENVIAR ---
                Button(
                    onClick = {
                        // 3. CONEXIÓN CLAVE CON EL CONTROLLER
                        if (descripcionInput.isNotBlank()) {

                            // 3a. El Controller hace la lógica de negocio y CRUD (crea el ID, fecha, estado)
                            val tramiteCreado = tramiteController.iniciarNuevoTramite(
                                tipo = tipoInput,
                                descripcion = descripcionInput,
                                rutasArchivos = archivosAdjuntos
                            )

                            // 3b. Muestra el diálogo y navega de vuelta
                            FileUtil.mostrarDialogo(
                                context = context,
                                titulo = "Trámite Enviado con Éxito",
                                mensaje = "Tu solicitud ID ${tramiteCreado.id} ha sido registrada."
                            )

                            // Llama a la función de navegación (cerrar esta pantalla)
                            onNavigateBack()

                        } else {
                            // Mostrar mensaje de error si la descripción está vacía
                            FileUtil.mostrarDialogo(context, "Error", "La descripción no puede estar vacía.")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Enviar Trámite")
                }
            }
        }
    )
}