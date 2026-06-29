package com.example.hds_tesisapp.ui.theme.games.game10

import com.example.hds_tesisapp.R

// An object that can appear in a level. Items marked isCorrect are part of
// the solution; the rest are distractors. hidden = true means the item starts
// inside a box/drawer and must be tapped open before it can be selected.
// drawableRes overrides the emoji with real artwork when available.
data class G10Item(
    val id: Int,
    val name: String,
    val emoji: String,
    val isCorrect: Boolean,
    val hidden: Boolean = false,
    val drawableRes: Int? = null
)

data class G10LevelConfig(
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val backgroundRes: Int,
    val tasks: List<String>,
    val items: List<G10Item>,
    val maxPicks: Int,
    val timerMs: Long
)

val G10_LEVEL_CONFIGS = listOf(
    // Nivel 1 — Lobby del Cuartel
    G10LevelConfig(
        levelNumber = 1,
        title = "Lobby del Cuartel",
        subtitle = "Revisa la sala y recoge solo lo necesario",
        backgroundRes = R.drawable.g10_lobby_bg,
        tasks = listOf(
            "Necesitamos abrir una puerta bloqueada.",
            "La puerta no tiene energía."
        ),
        items = listOf(
            G10Item(1, "Tarjeta de Acceso", "🪪", isCorrect = true, hidden = true, drawableRes = R.drawable.item_tarjeta_acceso),
            G10Item(2, "Núcleo de Energía", "💠", isCorrect = true, hidden = true, drawableRes = R.drawable.item_nucleo_energia),
            G10Item(3, "Piedra", "🪨", isCorrect = false, drawableRes = R.drawable.item_piedra),
            G10Item(4, "Plátano", "🍌", isCorrect = false, drawableRes = R.drawable.item_platano)
        ),
        maxPicks = 2,
        timerMs = 60_000L
    ),

    // Nivel 2 — Sala de Seguridad
    G10LevelConfig(
        levelNumber = 2,
        title = "Sala de Seguridad",
        subtitle = "El Glitch dañó los sistemas — encuentra lo que sirve",
        backgroundRes = R.drawable.g10_security_room_bg,
        tasks = listOf(
            "Necesitamos volver a ver lo que ocurre en los pasillos.",
            "Necesitamos devolver energía al sistema de vigilancia.",
            "La puerta de seguridad no tiene energía para abrirse."
        ),
        items = listOf(
            G10Item(1, "Cámara de Vigilancia", "📹", isCorrect = true, hidden = true, drawableRes = R.drawable.item_camara_vigilancia),
            G10Item(2, "Batería de Energía", "🔋", isCorrect = true, drawableRes = R.drawable.item_bateria_energia),
            G10Item(3, "Núcleo de Energía", "💠", isCorrect = true, hidden = true, drawableRes = R.drawable.item_nucleo_energia),
            G10Item(4, "Pelota", "⚽", isCorrect = false, drawableRes = R.drawable.item_pelota),
            G10Item(5, "Audífonos", "🎧", isCorrect = false, drawableRes = R.drawable.item_audifonos),
            G10Item(6, "Martillo", "🔨", isCorrect = false, hidden = true, drawableRes = R.drawable.item_martillo),
            G10Item(7, "Celular", "📱", isCorrect = false, drawableRes = R.drawable.item_celular),
            G10Item(8, "Paraguas", "☂️", isCorrect = false, drawableRes = R.drawable.item_paraguas)
        ),
        maxPicks = 3,
        timerMs = 75_000L
    ),

    // Nivel 3 — Área de Operaciones
    G10LevelConfig(
        levelNumber = 3,
        title = "Área de Operaciones",
        subtitle = "Reúne lo necesario para acceder a los archivos",
        backgroundRes = R.drawable.g10_operations_room_bg,
        tasks = listOf(
            "Necesitamos ver los resultados del análisis de los científicos.",
            "Necesitamos acceder a los archivos protegidos del cuartel.",
            "El sistema necesita identificarnos para darnos acceso.",
            "La puerta hacia el Laboratorio Central no tiene energía."
        ),
        items = listOf(
            G10Item(1, "Computadora", "🖥️", isCorrect = true, drawableRes = R.drawable.item_computadora),
            G10Item(2, "Llave Digital", "🗝️", isCorrect = true, hidden = true, drawableRes = R.drawable.item_llave_digital),
            G10Item(3, "Núcleo de Energía", "💠", isCorrect = true, hidden = true, drawableRes = R.drawable.item_nucleo_energia),
            G10Item(4, "Tarjeta de Acceso", "🪪", isCorrect = true, drawableRes = R.drawable.item_tarjeta_acceso),
            G10Item(5, "Linterna", "🔦", isCorrect = false, hidden = true, drawableRes = R.drawable.item_linterna),
            G10Item(6, "Botella", "🧴", isCorrect = false, drawableRes = R.drawable.item_botella),
            G10Item(7, "Cepillo", "🧹", isCorrect = false, drawableRes = R.drawable.item_cepillo),
            G10Item(8, "Paraguas", "☂️", isCorrect = false, drawableRes = R.drawable.item_paraguas),
            G10Item(9, "Audífonos", "🎧", isCorrect = false, drawableRes = R.drawable.item_audifonos)
        ),
        maxPicks = 4,
        timerMs = 90_000L
    ),

    // Nivel 4 — Laboratorio Central
    G10LevelConfig(
        levelNumber = 4,
        title = "Laboratorio Central",
        subtitle = "Localiza el Cristal Protector sin tocarlo directamente",
        backgroundRes = R.drawable.g10_central_lab_bg,
        tasks = listOf(
            "Necesitamos algo que ayude a ubicar el Cristal Protector.",
            "Necesitamos abrir la cápsula donde está guardado.",
            "Necesitamos algo que nos permita tocarlo sin dañarnos."
        ),
        items = listOf(
            G10Item(1, "Radar de Energía", "📡", isCorrect = true, hidden = true, drawableRes = R.drawable.item_radar_energia),
            G10Item(2, "Llave de la Cápsula", "🗝️", isCorrect = true, hidden = true, drawableRes = R.drawable.item_llave_capsula),
            G10Item(3, "Guantes Protectores", "🧤", isCorrect = true, drawableRes = R.drawable.item_guantes_protectores),
            G10Item(4, "Martillo", "🔨", isCorrect = false, drawableRes = R.drawable.item_martillo),
            G10Item(5, "Teclado", "⌨️", isCorrect = false, drawableRes = R.drawable.item_teclado),
            G10Item(6, "Tijeras", "✂️", isCorrect = false, hidden = true, drawableRes = R.drawable.item_tijeras),
            G10Item(7, "Bolsa de Papel", "🛍️", isCorrect = false, drawableRes = R.drawable.item_bolsa_papel),
            G10Item(8, "Candado", "🔒", isCorrect = false, drawableRes = R.drawable.item_candado),
            G10Item(9, "Paraguas", "☂️", isCorrect = false, drawableRes = R.drawable.item_paraguas)
        ),
        maxPicks = 3,
        timerMs = 90_000L
    )
)
