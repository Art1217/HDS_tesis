# HDS_tesis — Reglas de arquitectura

Juego educativo en Android/Kotlin/Jetpack Compose (paquete `com.example.hds_tesisapp`), 10 zonas × 5 niveles. Backend separado en `codeland-backend` (Node/Express + MySQL, puerto 30001 en dev). MVVM aplica a todo lo que toca el backend o la sesión del jugador; el estado interno de juego de los niveles (`remember{}`) se deja como está.

## Arquitectura

```
UI (Composable) → ViewModel → Repository → { ApiService (Retrofit) | SessionRepository (DataStore) }
```

- La UI nunca importa un `Repository` ni un DTO directamente.
- El ViewModel nunca referencia `Context`, `Activity`, `View` ni tipos de Compose (`Modifier`, `Color`, `@Composable`). Única excepción documentada: `SessionRepositoryImpl` recibe `@ApplicationContext` vía Hilt — vive en la capa de repositorio, no en el ViewModel.
- Un ViewModel por pantalla/feature. No ViewModels compartidos entre pantallas no relacionadas.
- El ViewModel expone un único `StateFlow<XyzUiState>` inmutable, actualizado con `MutableStateFlow.update { it.copy(...) }`. Nunca se expone `MutableStateFlow` ni `mutableStateOf` suelto hacia la UI.
- **El estado interno `remember{}` de las pantallas de nivel (`GamePhase`, posiciones, slots, etc.) se mantiene igual.** MVVM no es obligatorio para esos 50 niveles salvo en el punto exacto donde tocan backend (el callback `onLevelComplete`, resuelto en `navigation.kt`, no dentro del Composable del nivel).

## Convenciones de nombres

`XyzScreen` (Composable) · `XyzViewModel` (`@HiltViewModel`) · `XyzUiState` (data class) · `XyzRepository` + `XyzRepositoryImpl` (interfaz + impl, bindeados con `@Binds` en un módulo Hilt).

## Convenciones de red

- Base URL única en `BuildConfig.API_BASE_URL` (nunca hardcodear `http://10.0.2.2` en código de feature).
- **Este backend no es confiable en su código HTTP**: los errores de negocio (ej. "usuario ya existe", "contraseña incorrecta") se lanzan sin `.status`, así que `handleError` siempre responde 500. Por eso `ApiService` devuelve `Response<T>` (no `T` directo), y cada Repository siempre intenta parsear el body JSON aunque la respuesta no sea 2xx, antes de caer a un mensaje genérico.
- `ApiResult<T>` (`Success`/`Failure`) es la única moneda de error que cruza de Repository hacia ViewModel. Las excepciones de red (`IOException`, servidor caído) se capturan **dentro del repositorio**, nunca llegan crudas al ViewModel.
- DTOs en `data/remote/dto/`, modelos de dominio en `domain/model/` (desacoplados del wire format), la función de mapeo `toDomain()` vive junto al DTO.
- Logging interceptor en nivel `BODY` en debug, `NONE` en release (tag `OkHttp` en Logcat).

## Convenciones de DI (Hilt)

- KSP, no kapt. Versión de KSP debe coincidir exactamente con la versión de Kotlin del proyecto.
- `@Binds` para pares interfaz/impl (`RepositoryModule`); `@Provides` para construcción con lógica (`Retrofit`, `OkHttpClient`, `Moshi` en `NetworkModule`).
- Las Composables obtienen ViewModels solo con `hiltViewModel()`, nunca con `viewModel()` crudo.
- `@HiltAndroidApp` en `HdsTesisApp`, `@AndroidEntryPoint` en `MainActivity`.

## Sesión / persistencia

`SessionRepository` (DataStore Preferences) guarda el `uid` del jugador logueado. Nunca usar `SharedPreferences` directo ni leer DataStore desde un Composable — siempre a través de un ViewModel (ver `SplashViewModel.hasActiveSession()`).

## Cómo agregar una nueva feature que toque el backend

1. DTOs en `data/remote/dto/`.
2. Endpoint nuevo/extendido en `ApiService`.
3. Modelo de dominio en `domain/model/` si no existe.
4. Interfaz + impl de Repository en `data/repository/`, bindeado en `di/RepositoryModule.kt`.
5. `XyzUiState` + `XyzViewModel` (`@HiltViewModel`) en `ui/<feature>/`.
6. `XyzScreen` Composable que consume `hiltViewModel()`.
7. Entrada en `Routes` (`Nav/routes.kt`) y bloque `composable(...)` en `Nav/navigation.kt` — usar lambdas (`onXSuccess`, `onNavigateToY`) si la pantalla está en mitad de un flujo (como los niveles), o `NavController` directo solo si es una pantalla "shell" de nivel superior (como `MenuScreen`).
8. Verificar end-to-end contra el backend corriendo en local (ver sección siguiente).

## Qué NO hacer

- No usar `viewModel()` crudo — siempre `hiltViewModel()`.
- No confiar en el código de estado HTTP de `codeland-backend` sin parsear el body.
- No llamar a un Repository directo desde un Composable.
- No exponer `MutableStateFlow` ni `mutableStateOf` desde un ViewModel hacia la UI.
- No reescribir los 50 niveles a MVVM sin necesidad real — solo el punto de contacto con el backend (`onLevelComplete`) se conecta, siguiendo el patrón ya hecho en `Routes.Level1`.
- El guardado de progreso (`ProgressViewModel.saveLevelCompleted`) **nunca bloquea la navegación**: si falla, el jugador avanza igual. Cualquier nueva feature de progreso debe replicar esta regla.

## Mapeo de niveles

`domain/model/LevelCatalog.kt` centraliza la conversión "Zona N, Nivel M" → `idLevel` entero del backend (`idLevel = (zona-1)*5 + nivel`), basado en el orden exacto del seed SQL de la tabla `level`. Las 50 pantallas de nivel en `navigation.kt` ya están conectadas a `/progress/save` usando `LevelCatalog.idFor(zona, nivel)` — no usar números mágicos sueltos, siempre pasar por este objeto.

## Pendiente / TODO

- Sugerir al backend asignar `error.status` explícito (409 para "ya existe", 404 para "no existe") en lugar de dejar todo en 500 genérico.

## Verificación end-to-end

1. Levantar `codeland-backend` con `npm run dev` (puerto 30001).
2. `./gradlew installDebug` — en el primer build, vigilar errores de versión KSP/Hilt (punto de fallo más común al añadir esta infraestructura).
3. Probar registro desde la UI → confirmar fila nueva en la tabla `player`.
4. Matar y reabrir la app → debe ir directo a `Menu` (sesión persistida). Para volver a ver `Login`: `adb shell pm clear com.example.hds_tesisapp`.
5. Login con contraseña incorrecta → debe mostrarse el mensaje exacto del backend aunque la respuesta HTTP sea 500.
6. Completar el Nivel 1 → confirmar fila nueva/actualizada en `progress` con el `uid` de la sesión y `idLevel = 1`.
7. Apagar el backend y reintentar login → debe mostrarse un mensaje de "no se pudo conectar" sin crashear.
