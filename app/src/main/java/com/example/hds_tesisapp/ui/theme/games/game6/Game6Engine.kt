package com.example.hds_tesisapp.ui.theme.games.game6

// ─── Loop Round (Niveles 1-4) ─────────────────────────────────────────────────

data class LoopRound(
    val taskTitle: String,
    val taskDesc: String,
    val codeLines: List<String>,
    val options: List<Int>,
    val correct: Int
)

// ─── Nivel 1: Reparar la Máquina ──────────────────────────────────────────────

private val ROUNDS_L1 = listOf(
    LoopRound("Engranaje Roto", "El engranaje necesita 3 ajustes para funcionar.",
        listOf("repetir(?) {", "  ajustar_engranaje()", "}"), listOf(2, 3, 5), 3),
    LoopRound("Cable Dañado", "El cable necesita soldarse 4 veces.",
        listOf("repetir(?) {", "  soldar_cable()", "}"), listOf(3, 4, 6), 4),
    LoopRound("Pantalla Rota", "La pantalla necesita 2 limpiezas.",
        listOf("repetir(?) {", "  limpiar_pantalla()", "}"), listOf(1, 2, 4), 2),
    LoopRound("Tornillos Sueltos", "Hay 5 tornillos que necesitan ajuste.",
        listOf("repetir(?) {", "  apretar_tornillo()", "}"), listOf(3, 5, 7), 5)
)

fun buildL1G6Round(i: Int): LoopRound = ROUNDS_L1[i % ROUNDS_L1.size]

// ─── Nivel 2: Bits Dañados ────────────────────────────────────────────────────

private val ROUNDS_L2 = listOf(
    LoopRound("Re-Ensamblar Bits", "3 bits están desarmados. Debes re-ensamblarlos.",
        listOf("repetir(?) {", "  re_ensamblar(bit)", "}"), listOf(2, 3, 5), 3),
    LoopRound("Limpiar Bits", "4 bits están sucios de glitch. Necesitan limpieza.",
        listOf("repetir(?) {", "  limpiar(bit)", "}"), listOf(3, 4, 6), 4),
    LoopRound("Re-Activar Bits", "2 bits apagados necesitan reactivación.",
        listOf("repetir(?) {", "  reactivar(bit)", "}"), listOf(1, 2, 4), 2),
    LoopRound("Reparación Total", "5 bits dañados esperan reparación completa.",
        listOf("repetir(?) {", "  reparar_bit()", "}"), listOf(4, 5, 7), 5)
)

fun buildL2G6Round(i: Int): LoopRound = ROUNDS_L2[i % ROUNDS_L2.size]

// ─── Nivel 3: Línea de Producción ─────────────────────────────────────────────

private val ROUNDS_L3 = listOf(
    LoopRound("Ensamblar Piezas", "3 Bits Obreros, cada uno ensambla 1 pieza.",
        listOf("repetir(3) {", "  obrero.ensamblar()", "}", "// Total = ?"), listOf(1, 3, 6), 3),
    LoopRound("Activar Sistemas", "4 Bits Obreros, cada uno activa 2 sistemas.",
        listOf("repetir(4) {", "  obrero.activar(2)", "}", "// Total = ?"), listOf(4, 6, 8), 8),
    LoopRound("Enseñar Habilidades", "2 Bits Obreros, cada uno enseña 3 habilidades.",
        listOf("repetir(2) {", "  obrero.ensenar(3)", "}", "// Total = ?"), listOf(3, 5, 6), 6),
    LoopRound("Procesar Tareas", "5 Bits Obreros, cada uno procesa 1 tarea.",
        listOf("repetir(5) {", "  obrero.procesar()", "}", "// Total = ?"), listOf(3, 5, 10), 5)
)

fun buildL3G6Round(i: Int): LoopRound = ROUNDS_L3[i % ROUNDS_L3.size]

// ─── Nivel 4: Almacén de Cajas ────────────────────────────────────────────────

private val ROUNDS_L4 = listOf(
    LoopRound("Estantes Pequeños", "2 estantes con 3 cajas cada uno.",
        listOf("repetir(2) { // estantes", "  repetir(3) {", "    cargar_caja()", "  }", "}", "// Total = ?"), listOf(4, 6, 8), 6),
    LoopRound("Fila de Cajas", "3 filas con 4 cajas por fila.",
        listOf("repetir(3) { // filas", "  repetir(4) {", "    colocar_caja()", "  }", "}", "// Total = ?"), listOf(7, 10, 12), 12),
    LoopRound("Columnas del Almacén", "4 columnas con 2 cajas por columna.",
        listOf("repetir(4) { // columnas", "  repetir(2) {", "    apilar_caja()", "  }", "}", "// Total = ?"), listOf(6, 8, 10), 8),
    LoopRound("Gran Almacén", "3 zonas con 5 cajas por zona.",
        listOf("repetir(3) { // zonas", "  repetir(5) {", "    guardar_caja()", "  }", "}", "// Total = ?"), listOf(10, 15, 18), 15)
)

fun buildL4G6Round(i: Int): LoopRound = ROUNDS_L4[i % ROUNDS_L4.size]

// ─── Nivel 5: Boss ────────────────────────────────────────────────────────────

data class BossG6Question(
    val question: String,
    val codeLines: List<String>,
    val options: List<Int>,
    val correct: Int
)

private val BOSS_QUESTIONS = listOf(
    BossG6Question("2 fabricas × 3 bits = ?",
        listOf("repetir(2) {", "  repetir(3) { crear_bit() }", "}"), listOf(4, 6, 8), 6),
    BossG6Question("4 lineas × 2 acciones = ?",
        listOf("repetir(4) {", "  repetir(2) { ejecutar() }", "}"), listOf(6, 8, 10), 8),
    BossG6Question("3 sectores × 4 bits = ?",
        listOf("repetir(3) {", "  repetir(4) { procesar() }", "}"), listOf(9, 12, 15), 12),
    BossG6Question("5 obreros × 2 turnos = ?",
        listOf("repetir(5) {", "  repetir(2) { trabajar() }", "}"), listOf(8, 10, 12), 10),
    BossG6Question("3 lineas × 5 piezas = ?",
        listOf("repetir(3) {", "  repetir(5) { fabricar() }", "}"), listOf(12, 15, 18), 15),
    BossG6Question("4 zonas × 4 bits = ?",
        listOf("repetir(4) {", "  repetir(4) { activar() }", "}"), listOf(12, 16, 20), 16)
)

fun buildBossG6Question(roundIndex: Int): BossG6Question =
    BOSS_QUESTIONS[roundIndex.coerceAtMost(BOSS_QUESTIONS.lastIndex)]
