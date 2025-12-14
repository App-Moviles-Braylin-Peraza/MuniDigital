# MuniDigital API Integration

**URL de la API:** https://apimunidigital.onrender.com
---


### 1. **Dependencias Actualizadas**
**Archivo:** `app/build.gradle`

 `logging-interceptor` para debugging:
```gradle
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
```

---

### 2. **Nuevos Modelos Creados**

#### 📄 `TramiteRequest.kt`
- Modelo para crear/actualizar trámites
- Todos los campos opcionales según la API
- Usado en POST/PUT requests

#### 📄 `ApiResult.kt`
- Sealed class `Result<T>` para manejo de estados
- Función `safeApiCall()` para manejo consistente de errores
- Parseo inteligente de mensajes de error de la API

---

### 3. **RetrofitClient Mejorado**
**Archivo:** `network/RetrofitClient.kt`

✅ **Cambios implementados:**
- Logging interceptor (solo en DEBUG)
- Timeouts configurados (30 segundos cada uno)
- Orden correcto de interceptores
- Configuración lista para producción

```kotlin
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .addInterceptor(AuthInterceptor())
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()
```

---

### 4. **ApiService Actualizado**
**Archivo:** `network/ApiService.kt`

✅ **Cambios:**
- `createTramite()` ahora usa `TramiteRequest`
- `updateTramite()` ahora usa `TramiteRequest`
- `deleteTramite()` retorna `Response<Unit>` (código 204)
- Mejor organización con comentarios

**Antes:**
```kotlin
@POST("tramites")
suspend fun createTramite(@Body tramite: Tramite): Tramite
```

**Ahora:**
```kotlin
@POST("tramites")
suspend fun createTramite(@Body request: TramiteRequest): Tramite
```

---

### 5. **TramiteApiDataManager Actualizado**
**Archivo:** `data/TramiteApiDataManager.kt`

✅ Convierte `Tramite` a `TramiteRequest` automáticamente
✅ Mantiene compatibilidad con código existente

---

### 6. **Arquitectura MVVM Completa**

#### 📄 `TramiteRepository.kt` (NUEVO)
- Capa de repositorio para trámites
- Métodos limpios y claros
- Manejo de transformaciones de datos

#### 📄 `TramitesViewModel.kt` (NUEVO)
- ViewModel completo para gestión de trámites
- LiveData para observar cambios
- Manejo de estados (Loading, Success, Error)
- Métodos: `loadTramites()`, `createTramite()`, `updateTramite()`, `deleteTramite()`
- Recarga automática después de operaciones

#### 📄 `TramitesViewModelFactory.kt` (NUEVO)
- Factory para crear ViewModel con dependencias

---

### 7. **LoginActivity Mejorado**
**Archivo:** `LoginActivity.kt`

✅ **Validaciones según la API:**
- Username: mínimo 4 caracteres
- Password: mínimo 6 caracteres
- Mensajes específicos y amigables

```kotlin
when {
    username.isEmpty() -> "Por favor, ingresa tu usuario"
    username.length < 4 -> "El usuario debe tener al menos 4 caracteres"
    password.isEmpty() -> "Por favor, ingresa tu contraseña"
    password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
    else -> { /* Login */ }
}
```

---

### 8. **TramiteListActivity Completamente Renovado**
**Archivo:** `TramiteListActivity.kt`

✅ **Features implementadas:**
- ✅ ViewModel pattern
- ✅ Pull-to-refresh con `SwipeRefreshLayout`
- ✅ Paginación (page=1, limit=50)
- ✅ Filtros por estado (INICIADO, EN_PROCESO, FINALIZADO)
- ✅ Estado de loading con ProgressBar
- ✅ Estado de lista vacía
- ✅ Manejo de errores con mensajes
- ✅ Menú de opciones con filtros
- ✅ Recarga automática al volver (onResume)

#### 📄 Layout Actualizado: `activity_tramite_list.xml`
- SwipeRefreshLayout container
- ProgressBar centrado
- TextView para estado vacío
- RecyclerView con padding

#### 📄 Menu Nuevo: `menu_tramite_list.xml`
- Opción de actualizar
- Submenu con filtros por estado

---

### 9. **MainActivity con Logout**
**Archivo:** `MainActivity.kt`

✅ **Implementado:**
- Menú con opción "Cerrar Sesión"
- Diálogo de confirmación antes de logout
- Limpia sesión completamente
- Redirección a LoginActivity
- Flags correctos para limpiar back stack

#### 📄 Menu Nuevo: `menu_main.xml`
- Opción de logout en overflow menu

---

### 10. **Utilidades Nuevas**

#### 📄 `utils/DateUtils.kt` (NUEVO)
Funciones para formateo de fechas:
- `formatDate()`: "12/12/2025 10:30"
- `formatDateShort()`: "12/12/2025"
- `getTimeAgo()`: "Hace 2 días"
- Manejo correcto de ISO 8601 (UTC)

#### 📄 `utils/StatusUtils.kt` (NUEVO)
Funciones para estados de trámites:
- `getStatusText()`: Texto legible en español
- `getStatusColor()`: Color del texto según estado
- `getStatusBackgroundColor()`: Color de fondo

---




---

## 🎯 FUNCIONALIDADES API

### ✅ Autenticación
- [x] Login con validaciones completas
- [x] Token JWT guardado persistentemente
- [x] Interceptor automático para requests
- [x] Logout con confirmación

### ✅ Gestión de Trámites
- [x] Listar con paginación
- [x] Filtrar por estado (INICIADO, EN_PROCESO, FINALIZADO)
- [x] Crear nuevo trámite
- [x] Actualizar trámite
- [x] Eliminar trámite (soft delete)
- [x] Pull-to-refresh
- [x] Estados de loading/vacío/error

### ✅ Manejo de Errores
- [x] Errores HTTP interpretados
- [x] Mensajes amigables al usuario
- [x] Logging en modo DEBUG
- [x] Timeouts configurados

### ✅ UX/UI
- [x] Loading indicators
- [x] Estados de lista vacía
- [x] Confirmaciones para acciones críticas
- [x] Mensajes de éxito/error
- [x] Pull-to-refresh
- [x] Menús de opciones

---

## 🚀 ¿CÓMO UTILIZAR?

### 1. **Sincronizar Gradle**
```bash
./gradlew build
```

### 2. **Compilar y Ejecutar**
- Abrir en Android Studio
- Sync Gradle Files
- Run app

### 3. **Probar el Flujo**
1. **Login** con credenciales válidas (username ≥4, password ≥6)
2. **Home** → "Mis Trámites" o "Iniciar Nuevo"
3. **Lista de Trámites** → Pull to refresh, filtrar por estado
4. **Logout** → Menú → Cerrar Sesión

---

## 📝 ENDPOINTS UTILIZADOS

| Método | Endpoint | Uso |
|--------|----------|-----|
| POST | `/auth/login` | Iniciar sesión |
| POST | `/auth/register` | Registrar usuario (pendiente UI) |
| GET | `/tramites` | Listar con filtros/paginación |
| POST | `/tramites` | Crear trámite |
| GET | `/tramites/{id}` | Obtener detalle |
| PUT | `/tramites/{id}` | Actualizar trámite |
| DELETE | `/tramites/{id}` | Eliminar (soft delete) |

---

## 🔍 DEBUGGING

### Ver Logs de Red (solo DEBUG):
```
Logcat → Filtrar por "OkHttp"
```

- URL completa de requests
- Headers (incluido Authorization)
- Body de request/response
- Códigos de estado HTTP

---

## ⚠️ NOTAS IMPORTANTES

1. **Primera petición a Render puede tardar 30-60 segundos** (servidor se despierta)
2. **Token expira en 7 días** (no hay refresh automático, relogin manual)
3. **Todas las fechas en UTC** (convertidas con DateUtils)
4. **Soft delete**: trámites eliminados no se pueden recuperar desde la app

---

## 🎯 PRÓXIMAS MEJORAS OPCIONALES

### Alta Prioridad:
- [ ] Pantalla de registro (RegisterActivity)
- [ ] Actualizar CrearTramiteActivity para usar ViewModel
- [ ] Actualizar DetalleTramiteActivity para usar ViewModel

### Media Prioridad:
- [ ] Manejo automático de token expirado (interceptor 401)
- [ ] Cache local con Room
- [ ] Búsqueda de trámites

### Baja Prioridad:
- [ ] EncryptedSharedPreferences para token
- [ ] Modo offline
- [ ] Notificaciones push
- [ ] Exportar/compartir trámites

---



