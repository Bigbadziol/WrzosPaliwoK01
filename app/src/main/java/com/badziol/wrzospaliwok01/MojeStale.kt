package com.badziol.wrzospaliwok01

object MojeStale {
    /**
     * Tabela dla zbiornika 0l - generalnie tylko i wylacznie do obslugi bledow<br></br>
     * n - zakres 0..1 - jest to ilosc podzialek
     * [n][0] - wysokosc w mm <br></br>
     * [n][1] - odpowiadajaca ilosc cieczy w litrach <br></br>
     */
    val ZBIORNIK_0L = arrayOf(intArrayOf(0, 0), intArrayOf(0, 0))
    val ZBIORNIK_0L_MAX = 0; //maksymalna sensowna ilosc litrow do wlania

    /**
     * Tabela dla zbiornika 1850l <br></br>
     * n - zakres 0..17 - jest to ilosc podzialek
     * [n][0] - wysokosc w mm <br></br>
     * [n][1] - odpowiadajaca ilosc cieczy w litrach <br></br>
     */
    val ZBIORNIK_1850L = arrayOf(
        intArrayOf(0, 0),           //  1
        intArrayOf(200, 200),       //  2
        intArrayOf(300, 310),
        intArrayOf(400, 420),
        intArrayOf(500, 520),
        intArrayOf(600, 630),
        intArrayOf(700, 740),
        intArrayOf(800, 850),
        intArrayOf(900, 960),
        intArrayOf(1000, 1070),
        intArrayOf(1100, 1180),
        intArrayOf(1200, 1290),
        intArrayOf(1300, 1400),
        intArrayOf(1400, 1510),
        intArrayOf(1500, 1620),
        intArrayOf(1600, 1730),
        intArrayOf(1700, 1815),
        intArrayOf(1800, 1870)
    )
    val ZBIORNIK_1850L_MAX = 1798; //przyjeto 168 cm.

    /**
     * Tabela dla zbiornika 1100l <br></br>
     * n - zakres 0..25 - jest to ilosc podzialek
     * [n][0] - wysokosc w mm <br></br>
     * [n][1] - odpowiadajaca ilosc cieczy w litrach <br></br>
     */
    val ZBIORNIK_1100L = arrayOf(
        intArrayOf(0, 0),
        intArrayOf(100, 59),
        intArrayOf(150, 94),
        intArrayOf(200, 155),
        intArrayOf(250, 192),
        intArrayOf(300, 228),
        intArrayOf(350, 273),
        intArrayOf(400, 317),
        intArrayOf(450, 365),
        intArrayOf(500, 413),
        intArrayOf(550, 462),
        intArrayOf(600, 510),
        intArrayOf(650, 549),
        intArrayOf(700, 587),
        intArrayOf(750, 637),
        intArrayOf(800, 686),
        intArrayOf(850, 736),
        intArrayOf(900, 787),
        intArrayOf(950, 835),
        intArrayOf(1000, 882),
        intArrayOf(1050, 934),
        intArrayOf(1100, 985),
        intArrayOf(1150, 1023),
        intArrayOf(1200, 1061),
        intArrayOf(1250, 1090),
        intArrayOf(1315, 1118)
    )
    val ZBIORNIK_1100L_MAX = 1023 //przyjeto 115cm
}