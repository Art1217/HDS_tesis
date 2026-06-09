# HDS Tesis — Documentación Técnica del Proyecto

## Tabla de Contenidos

1. [Descripción General](#1-descripción-general)
2. [Arquitectura del Proyecto](#2-arquitectura-del-proyecto)
3. [Estructura de Archivos](#3-estructura-de-archivos)
4. [Sistema de Navegación](#4-sistema-de-navegación)
5. [Pantallas Principales](#5-pantallas-principales)
6. [Zonas de Juego](#6-zonas-de-juego)
   - [Zona 1 · El Bit Perdido](#zona-1--el-bit-perdido)
   - [Zona 2 · El Bosque de los Grupos](#zona-2--el-bosque-de-los-grupos)
   - [Zona 3 · Las Cascadas en Secuencia](#zona-3--las-cascadas-en-secuencia)
   - [Zona 4 · El Valle de los Patrones](#zona-4--el-valle-de-los-patrones)
   - [Zona 5 · La Ciudad de los Portales](#zona-5--la-ciudad-de-los-portales)
   - [Zona 6 · La Fábrica de Bits](#zona-6--la-fábrica-de-bits)
   - [Zona 7 · El Laboratorio del Cambio](#zona-7--el-laboratorio-del-cambio)
   - [Zona 8 · La Ciudad de las Reacciones](#zona-8--la-ciudad-de-las-reacciones)
   - [Zona 9 · El Taller de Correcciones](#zona-9--el-taller-de-correcciones)
7. [Modelos de Datos](#7-modelos-de-datos)
8. [Componentes Compartidos por Zona](#8-componentes-compartidos-por-zona)
9. [Recursos Gráficos](#9-recursos-gráficos)
10. [Configuración del Proyecto](#10-configuración-del-proyecto)
11. [Patrones de Implementación](#11-patrones-de-implementación)
12. [Notas de Optimización](#12-notas-de-optimización)

---

## 1. Descripción General

**HDS Tesis** es una aplicación Android educativa para niños y jóvenes que enseña conceptos de programación y pensamiento computacional a través de 9 zonas de juego temáticas. Cada zona representa un concepto informático distinto presentado como una aventura narrativa.

| Campo | Detalle |
|-------|---------|
| Plataforma | Android (Jetpack Compose) |
| Lenguaje | Kotlin |
| Min SDK | 21+ |
| Orientación | Variable (Portrait en zonas 1-6, Landscape en zonas 7-9) |
| Total de niveles | 45 (9 zonas × 5 niveles) |
| Total archivos .kt | ~120 |
| Total assets gráficos | 83 imágenes PNG/JPG |

### Personajes principales

| Personaje | Rol | Zona principal |
|-----------|-----|---------------|
| **Max** | Héroe principal, destruye obstáculos | Z1, Z8 |
| **Lina** | Recolecta recursos y beneficios | Z7, Z8 |
| **Tom** | Repara infraestructura (objetos de suelo) | Z8 |
| **Atom** | Activa sistemas tecnológicos | Z8 |
| **Bit** | Guía narrativo en zonas iniciales | Z1-Z3 |

---

## 2. Arquitectura del Proyecto

```
HDS_tesis/
├── app/
│   ├── src/main/java/com/example/hds_tesisapp/
│   │   ├── MainActivity.kt              ← Entry point, NavHost host
│   │   ├── Nav/
│   │   │   ├── routes.kt                ← Todos los identificadores de rutas
│   │   │   └── navigation.kt            ← Grafo de navegación completo
│   │   └── ui/theme/
│   │       ├── Color.kt / Theme.kt / Type.kt   ← Tema global
│   │       ├── games/
│   │       │   ├── game1/ ... game9/     ← Lógica + UI de cada zona
│   │       ├── levels/
│   │       │   └── LevelsScreen.kt      ← Selector de zonas/niveles
│   │       ├── menu/
│   │       │   └── menu.kt              ← Menú principal
│   │       ├── personajes/              ← Pantallas de personajes
│   │       ├── splash/                  ← Pantalla de inicio
│   │       └── story/                   ← Tutoriales y narrativa
│   └── src/main/res/drawable-nodpi/     ← 83 imágenes del juego
└── gradle.properties                    ← JVM heap 4GB
```

### Patrón arquitectónico

Cada zona sigue el mismo patrón de capas:

```
GameNEngine.kt          ← Modelos de datos, configuraciones, lógica de generación
TutorialScreen.kt       ← Tutorial introductorio (4 slides)
level1/LevelNG1Screen.kt← Composables COMPARTIDOS + implementación Nivel 1
level2/LevelNG2Screen.kt← Solo lógica diferencial + referencia a composables del nivel 1
level3/...
level4/...
level5/...
ZoneNCompleteScreen.kt  ← Pantalla de victoria narrativa
```

Los composables del `level1/` se declaran como `internal` y son importados por los niveles 2-5 via wildcard `import .game9.level1.*`.

---

## 3. Estructura de Archivos

### Archivos por carpeta

```
Nav/
  routes.kt              → sealed class Routes con todas las rutas (string identifiers)
  navigation.kt          → NavHost + composable() por cada pantalla

ui/theme/
  Color.kt               → Paleta de colores global
  Theme.kt               → MaterialTheme configuration
  Type.kt                → Familias de fuentes (Baloo2, Orbitron)

ui/theme/games/game1/    → Zona 1: Algoritmos / secuencias
  Game1Engine.kt
  Game1LevelScreen.kt
  Game1TransitionScreen.kt
  Game1UI.kt
  Zone1CompleteScreen.kt
  level1/ → Level1Board.kt, Level1Commands.kt, Level1Dialogs.kt, Level1Screen.kt
  level2..5/ → Level[N]Screen.kt

ui/theme/games/game2/    → Zona 2: Clasificación
  Game2Engine.kt, Game2UI.kt, Zone2CompleteScreen.kt
  level1..5/ → Level[N]G2Screen.kt

ui/theme/games/game3/    → Zona 3: Ordenamiento (Bubble Sort)
  Game3Engine.kt, Game3Screen.kt, Zone3CompleteScreen.kt
  level1..5/ → Level[N]G3Screen.kt

ui/theme/games/game4/    → Zona 4: Patrones visuales
  Game4Engine.kt, Game4Screen.kt, Zone4CompleteScreen.kt
  level1..5/ → Level[N]G4Screen.kt

ui/theme/games/game5/    → Zona 5: Condiciones (if/else)
  Game5Engine.kt, Game5Screen.kt, Zone5CompleteScreen.kt
  level1..5/ → Level[N]G5Screen.kt

ui/theme/games/game6/    → Zona 6: Bucles (loops)
  Game6Engine.kt, Game6Screen.kt, Zone6CompleteScreen.kt
  level1..5/ → Level[N]G6Screen.kt

ui/theme/games/game7/    → Zona 7: Variables y operaciones
  Game7Engine.kt, Game7Screen.kt, Zone7CompleteScreen.kt
  level1..5/ → Level[N]G7Screen.kt

ui/theme/games/game8/    → Zona 8: Eventos y reacciones
  Game8Engine.kt, CityTutorialScreen.kt, Zone8CompleteScreen.kt
  level1..5/ → Level[N]G8Screen.kt

ui/theme/games/game9/    → Zona 9: Depuración de errores
  Game9Engine.kt, WorkshopTutorialScreen.kt, Zone9CompleteScreen.kt
  level1..5/ → Level[N]G9Screen.kt

ui/theme/levels/
  LevelsScreen.kt        → Selector de zonas con paginación (8 páginas)

ui/theme/menu/
  menu.kt                → Menú principal con acceso a personajes, niveles, historia

ui/theme/personajes/
  MaxScreen.kt, LinaScreen.kt, TomAtomScreen.kt

ui/theme/splash/
  splash.kt              → Pantalla de carga inicial

ui/theme/story/
  StoryScreen.kt         → Historia introductoria
  ZoneIntroScreen.kt     → Intro de zona genérica
  AlgorithmTutorialScreen.kt   → Tutorial Zona 1
  ClassificationTutorialScreen.kt
  SortingTutorialScreen.kt
  PatternTutorialScreen.kt
  PortalTutorialScreen.kt
  FactoryTutorialScreen.kt
  LabTutorialScreen.kt         → Tutorial Zona 7
  CityTutorialScreen.kt        → Tutorial Zona 8
```

---

## 4. Sistema de Navegación

### Archivo: `Nav/routes.kt`

Define una `sealed class Routes(val route: String)` con todos los identificadores:

```kotlin
// Core
Splash, Menu, Story, ZoneIntro, Levels
MaxCharacter, LinaCharacter, TomAtomCharacter

// Zona 1
AlgorithmTutorial, Level1..Level5, Zone1Complete, Game1Transition

// Zona 2–9 (patrón: Tutorial + Level1GN..Level5GN + ZoneNComplete)
ClassificationTutorial | Level1G2..Level5G2 | Zone2Complete
SortingTutorial        | Level1G3..Level5G3 | Zone3Complete
PatternTutorial        | Level1G4..Level5G4 | Zone4Complete
PortalTutorial         | Level1G5..Level5G5 | Zone5Complete
FactoryTutorial        | Level1G6..Level5G6 | Zone6Complete
LabTutorial            | Level1G7..Level5G7 | Zone7Complete
CityTutorial           | Level1G8..Level5G8 | Zone8Complete
WorkshopTutorial       | Level1G9..Level5G9 | Zone9Complete
```

### Flujo de navegación principal

```
Splash → Menu
          ├── Story → ZoneIntro → AlgorithmTutorial → L1 → L2 → L3 → L4 → L5 → Zone1Complete
          │                                                                         ↓
          │           ClassificationTutorial → L1G2..L5G2 → Zone2Complete
          │                                                        ↓
          │           SortingTutorial → ... → Zone3Complete
          │                                        ↓
          │           PatternTutorial → ... → Zone4Complete
          │                                        ↓
          │           PortalTutorial → ... → Zone5Complete
          │                                        ↓
          │           FactoryTutorial → ... → Zone6Complete
          │                                        ↓
          │           LabTutorial → ... → Zone7Complete
          │                                    ↓
          │           CityTutorial → ... → Zone8Complete
          │                                    ↓
          │           WorkshopTutorial → ... → Zone9Complete → Menu
          │
          ├── Levels (selector de zonas)
          ├── MaxCharacter / LinaCharacter / TomAtomCharacter
```

### Navegación entre niveles (patrón estándar)

```kotlin
composable(Routes.Level1GN.route) {
    Level1GNScreen(
        onLevelComplete = {
            navController.navigate(Routes.Level2GN.route) {
                popUpTo(Routes.Level1GN.route) { inclusive = true }
            }
        },
        onNavigateToMenu = {
            navController.navigate(Routes.Menu.route) { popUpTo(0) { inclusive = true } }
        }
    )
}
```

El `popUpTo { inclusive = true }` evita que el backstack acumule pantallas completadas.

---

## 5. Pantallas Principales

### SplashScreen (`splash/splash.kt`)
- Pantalla de carga con logo y animación
- Navega automáticamente al Menú

### MenuScreen (`menu/menu.kt`)
- Acceso a: Inicio de historia, Pantalla de niveles, Personajes
- Botones animados con efecto de brillo

### LevelsScreen (`levels/LevelsScreen.kt`)
- Paginación horizontal: **8 páginas** (MAX_PAGE = 7, índice 0-7)
- Página 0: Zona 1 + Zona 2 (mostradas juntas)
- Páginas 1-7: Zona 3 hasta Zona 9 (una por página)
- Cada zona usa `ZoneData` con: número, título, subtítulo, ícono, colores, rutas de niveles
- Botones de nivel individuales con navegación directa

```kotlin
private data class ZoneData(
    val zoneNumber: Int,
    val title: String,
    val subtitle: String,
    val icon: String,
    val accentColor: Color,
    val bgGradient: List<Color>,
    val borderColor: Color,
    val levelRoutes: List<String>,
    val levelTitles: List<String>
)
```

### StoryScreen (`story/StoryScreen.kt`)
- Historia introductoria que presenta el universo del juego
- Presenta al Glitch como antagonista

---

## 6. Zonas de Juego

---

### Zona 1 · El Bit Perdido

| Campo | Valor |
|-------|-------|
| Concepto | Algoritmos y secuencias |
| Ícono | 🤖 |
| Color acento | `#00E5FF` (cyan) |
| Orientación | Portrait |
| Vidas | 3 |
| Personaje | Max + Bit |
| Fondo | `fondogame1.jpg` |

#### Mecánica general
El jugador programa una secuencia de movimientos (comandos) para guiar al personaje a través de un tablero. El robot ejecuta los comandos en orden.

#### Comandos disponibles
- Avanzar, Girar izquierda, Girar derecha
- En niveles avanzados: bucles y condiciones simples

#### Archivos clave
- `Game1Engine.kt` — lógica del tablero, validación de rutas
- `level1/Level1Board.kt` — renderizado del tablero de celdas
- `level1/Level1Commands.kt` — UI del panel de comandos
- `level1/Level1Dialogs.kt` — diálogos de éxito/fallo

#### Progresión de niveles
| Nivel | Subtítulo | Diferencial |
|-------|-----------|-------------|
| 1 | El Regreso | Movimientos básicos |
| 2 | El Callejón | Rutas más largas |
| 3 | Rutas Falsas | Obstáculos y caminos incorrectos |
| 4 | Doble Obstáculo | Múltiples obstáculos |
| 5 | Mini-Glitch | Primer encuentro con el Glitch |

---

### Zona 2 · El Bosque de los Grupos

| Campo | Valor |
|-------|-------|
| Concepto | Clasificación y organización |
| Ícono | 🌳 |
| Color acento | `#69FF47` (verde) |
| Orientación | Portrait |
| Fondo | `forest_bg.png` |

#### Mecánica general
Clasificar elementos en grupos según categorías (frutas, animales por hábitat, etc.). El jugador arrastra o selecciona elementos para asignarlos al grupo correcto.

#### Archivos clave
- `Game2Engine.kt` — categorías, elementos, validación
- `Game2UI.kt` — componentes de UI compartidos (tarjetas de elementos, canastas de grupos)

#### Progresión de niveles
| Nivel | Subtítulo | Diferencial |
|-------|-----------|-------------|
| 1 | Frutas | 2-3 categorías simples |
| 2 | Hábitats | Animales por entorno |
| 3 | Rotación | Categorías que cambian |
| 4 | Mezcla | Elementos de múltiples categorías |
| 5 | Mapache | Mini-jefe que desordena las categorías |

---

### Zona 3 · Las Cascadas en Secuencia

| Campo | Valor |
|-------|-------|
| Concepto | Ordenamiento / Bubble Sort |
| Ícono | 💧 |
| Color acento | `#00B0FF` (azul) |
| Orientación | Portrait |
| Fondos | `waterfall_bg.png`, `bridge_bg.png`, `rocks_path_bg.png`, `garden_bg.png` |
| Mini-jefe | `miniboss_zone3.png` |

#### Mecánica general
Ordenar elementos de menor a mayor (o según criterio dado) usando lógica de Bubble Sort. El jugador selecciona pares de elementos adyacentes para intercambiarlos.

#### Archivos clave
- `Game3Engine.kt` — generación de secuencias desordenadas, validación de orden
- `Game3Screen.kt` — pantalla base compartida

#### Progresión de niveles
| Nivel | Subtítulo | Diferencial |
|-------|-----------|-------------|
| 1 | El Puente | Secuencias cortas (4 elementos) |
| 2 | Las Piedras | Secuencias medianas |
| 3 | El Jardín | Mayor cantidad de intercambios |
| 4 | La Cascada | Secuencias largas con tiempo |
| 5 | Mini Jefe | Mini-jefe que reordena elementos |

---

### Zona 4 · El Valle de los Patrones

| Campo | Valor |
|-------|-------|
| Concepto | Patrones visuales y secuencias |
| Ícono | 🔁 |
| Color acento | `#8BC34A` (verde lima) |
| Orientación | Portrait |
| Fondos | `valley_bg.png`, `firefly_night_bg.png`, `rocks.png` |
| Mini-jefe | `miniboss_zone4.png` |

#### Mecánica general
Identificar y completar secuencias de patrones. El jugador observa una secuencia con un elemento faltante o incorrecto y debe seleccionar la opción correcta.

#### Archivos clave
- `Game4Engine.kt` — generación de patrones, tipos de secuencias
- `Game4Screen.kt` — pantalla base

#### Progresión de niveles
| Nivel | Subtítulo | Diferencial |
|-------|-----------|-------------|
| 1 | Las Flores | Patrones simples de colores |
| 2 | Luciérnagas | Patrones de movimiento |
| 3 | Las Piedras | Patrones más complejos |
| 4 | El Puente | Patrones combinados |
| 5 | Glitch | Mini-jefe que corrompe el patrón |

---

### Zona 5 · La Ciudad de los Portales

| Campo | Valor |
|-------|-------|
| Concepto | Condiciones (if / else / if-else if) |
| Ícono | 🌀 |
| Color acento | `#9C27B0` (morado) |
| Orientación | Portrait |
| Fondos | `portal_yesno_bg.png`, `portal_double_bg.png`, `portal_maze_bg.png`, `portal_room_bg.png`, `portal_glitch_bg.png` |
| Mini-jefe | `miniboss_zone5.png` |

#### Mecánica general
El jugador decide qué portal tomar basándose en condiciones lógicas (Sí/No, mayor/menor, etc.). Simula la lógica de decisiones de un programa.

#### Archivos clave
- `Game5Engine.kt` — condiciones, portales, validación de decisiones
- `Game5Screen.kt` — pantalla base

#### Progresión de niveles
| Nivel | Subtítulo | Diferencial |
|-------|-----------|-------------|
| 1 | El Portal Inicial | Condiciones binarias simples |
| 2 | Sí y No | if / else |
| 3 | Portales Dobles | if / else if / else |
| 4 | Sala Múltiple | Condiciones anidadas |
| 5 | Caóticos | Mini-jefe con condiciones caóticas |

---

### Zona 6 · La Fábrica de Bits

| Campo | Valor |
|-------|-------|
| Concepto | Bucles (loops / repetición) |
| Ícono | ⚙️ |
| Color acento | `#FF6D00` (naranja) |
| Orientación | Portrait |
| Fondos | `factory_interface_bg.png`, `factory_repair_bg.png`, `factory_warehouse_bg.png`, `factory_boss_bg.png` |
| Mini-jefe | `miniboss_zone6.png` |

#### Mecánica general
El jugador programa ciclos repetitivos para fabricar bits. Debe especificar cuántas veces ejecutar una acción y qué acción realizar dentro del bucle.

#### Archivos clave
- `Game6Engine.kt` — lógica de producción, contadores de ciclos
- `Game6Screen.kt` — pantalla base

#### Progresión de niveles
| Nivel | Subtítulo | Diferencial |
|-------|-----------|-------------|
| 1 | La Máquina | Bucles simples (for básico) |
| 2 | Bits Dañados | Bucles con condición de parada |
| 3 | Producción | Bucles anidados |
| 4 | El Almacén | Bucles complejos con acumuladores |
| 5 | Glitch Boss | Jefe final con loops infinitos |

---

### Zona 7 · El Laboratorio del Cambio

| Campo | Valor |
|-------|-------|
| Concepto | Variables y operaciones matemáticas |
| Ícono | 🔬 |
| Color acento | `#00E5FF` (cyan) |
| Orientación | **Landscape** |
| Personajes | Lina (científica) |
| Fondos | `lab_door_bg.png`, `lab_flask_bg.png`, `lab_central_bg.png` |
| Sprites | `lina_lab.png` (reemplazada por `linalab_1.png`) |

#### Mecánica general
Rompecabezas de variables: el jugador evalúa expresiones matemáticas y asigna valores correctos a variables. Cada nivel introduce nuevos operadores y tipos de operaciones.

#### Componentes especiales (`Game7Engine.kt`)
```kotlin
data class LabOp(val symbol: String, val apply: (Int, Int) -> Int)
data class LabPuzzle(val varA: Int, val varB: Int, val op: LabOp, val target: Int)
enum class L5Phase { PHASE1, PHASE2, PHASE3 }  // Nivel 5: 3 fases
```

#### Progresión de niveles
| Nivel | Subtítulo | Diferencial | Concepto |
|-------|-----------|-------------|----------|
| 1 | La Puerta | Asignación básica | `x = valor` |
| 2 | Sobrecarga | Operaciones simples | `x + y` |
| 3 | Frasco Verde | Compuesto A | Operaciones encadenadas |
| 4 | Frasco Morado | Compuesto B | Múltiples variables |
| 5 | Sala Central | 3 fases + operador rotante | Expresiones complejas |

#### Nivel 5 — Mecánica especial
- `altOpIndex` rota el operador central cada 3 segundos
- Fase 3: el target cambia cada 5 segundos
- Timer: 90 segundos total
- `L5FailOverlay` personalizado con mensaje "⏰ TIEMPO AGOTADO"

---

### Zona 8 · La Ciudad de las Reacciones

| Campo | Valor |
|-------|-------|
| Concepto | Eventos y reacciones |
| Ícono | 🏙️ |
| Color acento | `#FF9800` (naranja) |
| Orientación | **Landscape** |
| Personajes | Max, Lina, Tom, Atom |
| Fondo | `city_bg.png` |
| Jefe | `bossgame8.png` |

#### Héroes y sus acciones

| Héroe | Acción | Objetos asignados | Color |
|-------|--------|-------------------|-------|
| **Max** | DESTRUIR | Roca, Bloque, Puerta, Barril | Azul `#2196F3` |
| **Lina** | RECOGER | ❤️ Corazón, 🛡️ Escudo, 🐌 Caracol, 💣 Bomba | Cyan `#00E5FF` |
| **Tom** | REPARAR | Máquina, Edificio, Auto, Agujero (objetos de suelo) | Naranja `#FF9800` |
| **Atom** | ACTIVAR | Batería, Antena, Panel, Drone | Morado `#9C27B0` |

#### Estructura de datos (`Game8Engine.kt`)

```kotlin
enum class G8Hero { MAX, LINA, TOM, ATOM }

enum class G8ObjType {
    // Max (caen)
    ROCK, CONCRETE, CAR_DOOR, BARREL,
    // Lina (caen, beneficiosos)
    HEART_BLOCK, SHIELD_BLOCK, SNAIL_BLOCK, BOMB_BLOCK,
    // Tom (suelo, estáticos)
    MACHINE, BUILDING_RUBBLE, CAR_BROKEN, WOOD_HOLE,
    // Atom (caen, tech)
    BATTERY, ANTENNA, ENERGY_PANEL, DRONE,
    // Maligno
    EVIL_BOMB
}

data class G8LevelConfig(
    val totalEvents: Int,        // eventos correctos para ganar
    val fallDurationMs: Long,    // velocidad de caída
    val spawnIntervalMs: Long,   // intervalo entre spawns
    val maxOnScreen: Int,        // objetos simultáneos (1 o 2)
    val heroCanMove: Boolean,
    val availableTypes: List<G8ObjType>,
    val hasEvilBomb: Boolean,
    val hasMorphing: Boolean,    // L5: objetos que cambian de tipo
    val heroCooldownMs: Long
)
```

#### Configuraciones de nivel

| Nivel | Eventos | Velocidad caída | Max en pantalla | Especial |
|-------|---------|-----------------|-----------------|---------|
| 1 | 10 | 5000ms (lento) | 1 | Introducción, sin Atom |
| 2 | 12 | 3500ms | 1 | Héroe se mueve ←→ |
| 3 | 14 | 3500ms | 2 | Hasta 2 simultáneos + caracol |
| 4 | 15 | 2800ms | 2 | Bombas malignas |
| 5 | 18 | 2800ms | 2 | Objetos morfantes + bombas |

> **Buffer de secuencia:** cada nivel genera `totalEvents + 20` objetos extra para que el jugador nunca se quede sin objetos al fallar algunos.

#### Mecánica de posicionamiento (L1-L5)
Usa `BoxWithConstraints` + `absoluteOffset` para posicionar objetos y héroes con píxel-exacto:
```kotlin
val maxYOff = (gameH - heroH - objSize).coerceAtLeast(0.dp)
// objeto cae desde y=0 hasta maxYOff (nunca superpone al héroe)
val yOff = (maxYOff * yFrac).coerceIn(0.dp, maxYOff)
```

Hero: `zIndex(2f)`, Objetos: `zIndex(1f)` — el héroe siempre queda encima.

#### Auto-selección de objetivo (L3-L5)
```kotlin
LaunchedEffect(selectedHero, handled) {
    val hero = selectedHero ?: return@LaunchedEffect
    val match = objs.filter { it.id !in handled }.firstOrNull { it.type.hero == hero }
    if (match != null) targetObjId = match.id
}
```
Al elegir un héroe, el objeto correspondiente se resalta automáticamente.

#### Nivel 5 — Morfing de objetos
- `MorphObj(base, isMorphing, hasMorphed, morphType)` — wrapper del objeto base
- El objeto cambia de tipo al bajar el 45% (`MORPH_AT_YFRAC = 0.45f`)
- 30% de probabilidad de morph en tipos elegibles
- Efecto visual de parpadeo durante la transformación

#### Badge de héroe en objetos
Cada `FallingObjCard` muestra un badge con el nombre del héroe necesario (MAX/LINA/TOM/ATOM) en el color correspondiente, para que el jugador sepa inmediatamente cuál elegir.

#### Progresión de niveles
| Nivel | Subtítulo | Mecánica nueva |
|-------|-----------|---------------|
| 1 | La Ciudad Reacciona | Básico: 1 objeto, héroe fijo |
| 2 | Velocidad Aumentada | Héroe movible ←→, rango de captura |
| 3 | Doble Evento | 2 objetos simultáneos, auto-targeting |
| 4 | ¡Bombas! | Bomba maligna: esquivar o usar bomba de Lina |
| 5 | El Glitch Muta | Objetos morfantes, máxima dificultad |

---

### Zona 9 · El Taller de Correcciones

| Campo | Valor |
|-------|-------|
| Concepto | Depuración de errores (debugging) |
| Ícono | 🔧 |
| Color acento | `#00BCD4` (cyan oscuro) |
| Orientación | **Landscape** |
| Fondo | `workshop_bg.png` |
| Mini-jefe | `miniboss_zone9.png` |
| Vidas | **5** (no 3) |

#### Mecánica general
Grilla de **5 filas × 5 datos**. Cada fila tiene 4 datos correctos y 1 bug (dato incorrecto). El jugador debe encontrar y tocar el bug para "depurar" la línea de código.

#### Sets de datos por nivel

| Set | Nivel | Símbolos |
|-----|-------|---------|
| A | L1 | 🔴 🔵 🟢 🟡 🟣 (puntos de color) |
| B | L2 | ⭐ 💎 🔷 🔶 🔺 (gemas/formas) |
| C | L3 | 🌿 🌊 ⛰️ ☀️ ❄️ (naturaleza) |
| D | L4/L5 | Mix de A + B + C |

#### Estructura de datos (`Game9Engine.kt`)

```kotlin
data class G9Row(
    val id: Int,
    val symbols: List<String>,  // 5 datos: 4 correctos + 1 bug
    val correctSymbol: String,  // el dato correcto (patrón)
    val wrongIndex: Int,        // posición del bug (0-4)
    val isFixed: Boolean = false
)

data class G9LevelConfig(
    val symbolSet: List<String>,
    val isBlurry: Boolean,           // datos borrosos
    val isScrolling: Boolean,        // líneas en movimiento
    val hasModal: Boolean,           // confirmación por modal
    val hasMiniJefe: Boolean,        // mini-jefe que sabotea
    val timerMs: Long,               // 0 = sin timer
    val scrollSpeedMs: Long,         // ms para recorrer una fila completa
    val bossSwapIntervalMs: Long     // intervalo de sabotaje del jefe
)
```

#### Progresión de niveles

| Nivel | Subtítulo | Timer | Blur | Scroll | Modal | Jefe |
|-------|-----------|-------|------|--------|-------|------|
| 1 | Depuración Básica | 120s | ✗ | ✗ | ✗ | ✗ |
| 2 | Datos Borrosos | 90s | ✓ | ✗ | ✗ | ✗ |
| 3 | Código en Movimiento | 90s | ✓ | ✓ | ✗ | ✗ |
| 4 | Confirma el Error | — | ✓ | ✓ | ✓ | ✗ |
| 5 | El Glitch Interfiere | — | ✓ | ✓ | ✓ | ✓ |

#### Mecánica del scroll (L3, L4, L5)
Cada fila scrollea horizontalmente con wrap-around:
- Filas pares: dirección izquierda (`direction = 1`)
- Filas impares: dirección derecha (`direction = -1`)
- Velocidad: 4000ms para recorrer el ancho completo (~30fps con `delay(32L)`)

```kotlin
// Render de 6 celdas para wrap-around seamless
val viewStart = effectiveOffset * rowWidthPx
val firstSymIdx = (viewStart / cellSizePx).toInt()
repeat(6) { renderIdx ->
    val logIdx = ((firstSymIdx + renderIdx) % 5 + 5) % 5
    // Celda visible en posición: renderOffset + renderIdx * cellSizePx
}
```

**Detección de toque en scroll:** usa `rememberUpdatedState` para que el `pointerInput` tenga key estable (`row.id`) y lea el `viewStart` actualizado sin recrear el detector de gestos cada frame.

```kotlin
val latestViewStart by rememberUpdatedState(viewStart)
.pointerInput(row.id) {
    detectTapGestures { tap ->
        val absPos = latestViewStart + tap.x
        val logIdx = ((absPos / cellSizePx).toInt() % 5 + 5) % 5
        onSymbolTap(logIdx)
    }
}
```

#### Modal de confirmación (L4, L5)
Al tocar cualquier celda de una fila (no fija), se abre un overlay que pregunta:
> "¿Cuál es el dato correcto en esta línea?"

Con 3 opciones: `[correctSymbol, distractor1, distractor2]` (shuffled).
- **Correcto:** fila marcada como depurada
- **Incorrecto:** pierde una vida

#### Mini-jefe (L5)
- **HP del jefe = número de filas con bugs** (derivado dinámicamente)
- Cada `bossSwapIntervalMs` (9s): reemplaza una fila aleatoria con nuevos bugs
- Si la fila reemplazada estaba depurada → el jefe "recupera" vida
- Si la fila reemplazada ya tenía bugs → HP sin cambio
- **Victoria:** todas las 5 filas depuradas simultáneamente (HP jefe = 0)

#### Zone9CompleteScreen — Narrativa dramática
La pantalla de completado pasa por 4 fases automáticas:

```
Phase.SUCCESS      → "Taller restaurado" (1.5s)
Phase.ANALYSIS     → "Cuarteles: análisis de huellas completado..." (3s)
Phase.INTERRUPTION → Static noise + sismo (shake animation) + mensaje fragmentado
Phase.ALERT        → "¡EL CUARTEL HA SIDO TOMADO! — vuelvan inmediatamente"
```

---

## 7. Modelos de Datos

### ZoneData (LevelsScreen)
```kotlin
private data class ZoneData(
    val zoneNumber: Int,
    val title: String,
    val subtitle: String,
    val icon: String,
    val accentColor: Color,
    val bgGradient: List<Color>,
    val borderColor: Color,
    val levelRoutes: List<String>,   // rutas de cada nivel (incluye tutorial como primer elemento en Z9)
    val levelTitles: List<String>    // nombres de cada nivel
)
```

### Game8Engine — Datos principales
```kotlin
data class G8Obj(
    val id: Int, val type: G8ObjType, val xFrac: Float, var yFrac: Float,
    var handled: Boolean, var missed: Boolean,
    var isGlitching: Boolean, var morphTo: G8ObjType?, var morphProgress: Float
)

// Secuencia de eventos: totalEvents + 20 (buffer para fallos)
fun generateEventSequence(config, seed): List<G8ObjType>
```

### Game9Engine — Datos principales
```kotlin
data class G9Row(val id: Int, val symbols: List<String>,
    val correctSymbol: String, val wrongIndex: Int, val isFixed: Boolean)

fun generateG9Row(id, symbolSet, rng): G9Row
fun generateG9Rows(config, seed): List<G9Row>
fun generateModalOptions(row, symbolSet, rng): List<String>  // [correct, d1, d2] shuffled
```

---

## 8. Componentes Compartidos por Zona

### Zona 7 — Composables compartidos
Definidos en `level1/Level1G7Screen.kt`, importados por niveles 2-5:
- `G7MenuButton`, `G7LivesRow`, `G7TimerBar`
- `G7DoneOverlay`, `G7FailOverlay`
- `PuzzleCard` — tarjeta de ecuación con A ○ B = ?
- `AnswerButton` — botón de respuesta con feedback

### Zona 8 — Composables compartidos
Definidos en `level1/Level1G8Screen.kt`, importados por niveles 2-5:

```kotlin
internal fun G8MenuButton(onClick)
internal fun G8LivesRow(lives, hasShield, hasBomb)
internal fun HeroFaceButton(hero, isSelected, isCoolingDown, cooldownFrac, onClick)
    // 80dp, cooldown ring drawBehind, selección con glow
internal fun ActionButton(selectedHero, hasBomb, onAction, onUseBomb)
    // 130dp, muestra hero.actionLabel(), sub-botón "💣 USAR BOMBA"
internal fun FallingObjCard(obj, isGlitching)
    // 100dp, badge con nombre del héroe, animación glitch scale
internal fun GroundObjCard(obj)
    // 110dp de ancho, imagen 64dp, para objetos Tom (estáticos)
internal fun EventProgressBar(handled, total, color)
internal fun G8DoneOverlay(message, onContinue)
internal fun G8FailOverlay(onRetry)
```

### Zona 9 — Composables compartidos
Definidos en `level1/Level1G9Screen.kt`, importados por niveles 2-5:

```kotlin
internal fun G9MenuButton(onClick)
internal fun G9LivesRow(lives: Int)              // 5 corazones
internal fun G9TimerBar(remainingMs, totalMs)    // barra con color adaptivo
internal fun G9DoneOverlay(message, onContinue)  // "✅ ¡CÓDIGO DEPURADO!"
internal fun G9FailOverlay(reason, onRetry)      // "💥 Sin Vidas" / "⏰ Tiempo Agotado"
internal fun G9SymbolCell(symbol, isFixed, isBlurry, isFlashOk, modifier)
internal fun G9StaticRow(row, cellSize, isBlurry, flashRowIdx, flashOk, onSymbolTap)
internal fun G9ScrollingRow(row, offsetFrac, direction, cellSize, isBlurry, ...)
    // wrap-around con 6 celdas, pointerInput(row.id) + rememberUpdatedState
internal fun G9PatternModal(row, options, cellSize, isBlurry, onChoice)
    // Overlay: "¿Cuál es el dato correcto?" + 3 opciones 70dp
```

---

## 9. Recursos Gráficos

Todos los recursos están en `app/src/main/res/drawable-nodpi/` (83 archivos PNG/JPG).

### Personajes
| Archivo | Descripción |
|---------|-------------|
| `max.png` | Max (genérico) |
| `max_city.png` | Max versión ciudad (Zona 8) |
| `lina.png` | Lina (genérico) |
| `lina_city.png` | Lina versión ciudad (Zona 8) |
| `lina_lab.png` | Lina en laboratorio (Zona 7) |
| `tom_city.png` | Tom versión ciudad (Zona 8) |
| `atom_city.png` | Atom versión ciudad (Zona 8) |
| `tom_y_atom.png` | Tom y Atom juntos |
| `bit.png` / `bit_maestro.png` / `bit_obrero.png` / `bit_triste.png` | Variantes de Bit |
| `pj.png` | Personaje genérico |
| `pajaro_carp.png` | Personaje pájaro carpintero |
| `robots.png` | Imagen de robots varios |

### Fondos de zona
| Archivo | Zona |
|---------|------|
| `fondogame1.jpg` | Zona 1 |
| `forest_bg.png` | Zona 2 |
| `waterfall_bg.png`, `bridge_bg.png`, `rocks_path_bg.png`, `garden_bg.png` | Zona 3 |
| `valley_bg.png`, `firefly_night_bg.png` | Zona 4 |
| `portal_yesno_bg.png`, `portal_double_bg.png`, `portal_maze_bg.png`, `portal_room_bg.png`, `portal_glitch_bg.png` | Zona 5 |
| `factory_interface_bg.png`, `factory_repair_bg.png`, `factory_warehouse_bg.png`, `factory_boss_bg.png` | Zona 6 |
| `lab_door_bg.png`, `lab_flask_bg.png`, `lab_central_bg.png` | Zona 7 |
| `city_bg.png` | Zona 8 |
| `workshop_bg.png` | Zona 9 |
| `glitch_valley_bg.png`, `portal_glitch_bg.png`, `rhythm_bridge_bg.png`, `zone3_intro_bg.png` | Varios |
| `fondo_menu.png` | Menú principal |

### Objetos Zona 8
| Archivo | Héroe | Tipo |
|---------|-------|------|
| `obj_rock.png` | Max | Cae |
| `obj_concrete.png` | Max | Cae |
| `obj_car_door.png` | Max | Cae |
| `obj_barrel.png` | Max | Cae |
| `obj_heart_block.png` | Lina | Cae (+ vida) |
| `obj_shield_block.png` | Lina | Cae (escudo) |
| `obj_snail_block.png` | Lina | Cae (ralentiza) |
| `obj_bomb_block.png` | Lina | Cae (bomba) |
| `obj_machine.png` | Tom | Suelo |
| `obj_building_rubble.png` | Tom | Suelo |
| `obj_car_broken.png` | Tom | Suelo |
| `obj_wood_hole.png` | Tom | Suelo |
| `obj_battery.png` | Atom | Cae |
| `obj_antenna.png` | Atom | Cae |
| `obj_energy_panel.png` | Atom | Cae |
| `obj_drone.png` | Atom | Cae |
| `obj_bomb_evil.png` | — | Maligno |

### Mini-jefes y jefes
| Archivo | Zona |
|---------|------|
| `miniboss_zone3.png` | Zona 3 |
| `miniboss_zone4.png` | Zona 4 |
| `miniboss_zone5.png` | Zona 5 |
| `miniboss_zone6.png` | Zona 6 |
| `miniboss_zone9.png` | Zona 9 |
| `bossgame8.png` | Zona 8 (jefe principal "El Glitch") |

### UI / Otros
| Archivo | Uso |
|---------|-----|
| `titulo.png` | Logo/título del juego |
| `pers_g1.png` | Personaje Zona 1 |
| `m1_0.png` ... `m1_7.png` | Sprites de movimiento Zona 1 |
| `plank_wood.png`, `stone_path_bg.png` | Elementos de escenario |
| `tree_limon.png`, `tree_manzana.png`, `tree_naranja.png` | Árboles Zona 2 |
| `water_drop.png` | Gota de agua Zona 3 |

---

## 10. Configuración del Proyecto

### `gradle.properties`
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m -Dfile.encoding=UTF-8
org.gradle.daemon=true
org.gradle.caching=true
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

> El heap se subió a 4GB (desde 2GB) para evitar crashes del Kotlin compile daemon al compilar ~120 archivos simultáneamente.

### Fuentes globales
Definidas en `Type.kt` e importadas en todos los archivos:
- `Baloo2FontFamily` — texto de cuerpo, descripciones, mensajes narrativos
- `OrbitronFontFamily` — títulos, etiquetas de UI, textos de HUD

---

## 11. Patrones de Implementación

### Patrón gameKey (reset sin navegación)
```kotlin
var gameKey by remember { mutableIntStateOf(0) }
var rows by remember(gameKey) { mutableStateOf(generateG9Rows(CONFIG)) }
// En onRetry:
gameKey++  // invalida todos los remember(gameKey), reinicia estado
```

### Patrón flash (feedback visual)
```kotlin
var flashRowIdx by remember { mutableStateOf<Int?>(null) }
var flashOk     by remember { mutableStateOf<Boolean?>(null) }

// En onSymbolTap:
flashRowIdx = rowIdx; flashOk = correct
scope.launch {
    delay(350L)
    flashRowIdx = null; flashOk = null
    // aplica consecuencia
}
```

### Patrón LaunchedEffect seguro para animaciones con estado cambiante
```kotlin
// MAL: pointerInput con key que cambia cada frame → detector se cancela
.pointerInput(offsetFrac, direction, row.id) { ... }

// BIEN: key estable + rememberUpdatedState para leer valor actual
val latestViewStart by rememberUpdatedState(viewStart)
.pointerInput(row.id) {
    detectTapGestures { tap ->
        val pos = latestViewStart + tap.x  // siempre el valor más reciente
    }
}
```

### Patrón BoxWithConstraints para posicionamiento preciso (Zona 8)
```kotlin
BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
    val gameH = maxHeight; val gameW = maxWidth
    val objSize = 100.dp; val heroH = 144.dp
    val maxYOff = (gameH - heroH - objSize).coerceAtLeast(0.dp)
    // objeto nunca superpone al héroe
    Box(Modifier.absoluteOffset(x = xOff, y = yOff).zIndex(1f)) { objeto }
    Box(Modifier.absoluteOffset(x = heroXOff, y = heroYOff).zIndex(2f)) { héroe }
}
```

### Orientación de pantalla por zona
```kotlin
DisposableEffect(Unit) {
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    onDispose {}
}
```

---

## 12. Notas de Optimización

### Zona 9 — Scroll de filas
El scroll actualiza `rowOffsets` cada 32ms. Se usa `mutableStateListOf` para modificación in-place sin crear objetos nuevos:

```kotlin
// Sin allocations por frame:
val rowOffsets = remember(gameKey) { mutableStateListOf(*Array(5) { 0f }) }
// En game loop:
for (i in rowOffsets.indices) rowOffsets[i] = (rowOffsets[i] + delta) % 1f
```

Evita:
```kotlin
// Crea una nueva List cada 32ms → GC pressure → jank
rowOffsets = rowOffsets.map { off -> (off + delta) % 1f }
```

### Zona 9 — Blur de celdas
El efecto blur usa un solo composable con `graphicsLayer { alpha = 0.45f }` en lugar de 3 Text superpuestos, reduciendo 66% los composables por celda.

### Zona 8 — Secuencia de eventos
`generateEventSequence` genera `totalEvents + 20` eventos para que el jugador nunca se quede sin objetos al fallar varios seguidos, sin cambiar la condición de victoria (`handledCount >= totalEvents`).

### General — Composable tracking
En loops de filas se usa `key(rowIdx) { ... }` para que Compose reutilice composables existentes en lugar de recrearlos al cambiar el estado de las filas.

---

*Documentación generada para HDS_tesis · Junio 2026*
