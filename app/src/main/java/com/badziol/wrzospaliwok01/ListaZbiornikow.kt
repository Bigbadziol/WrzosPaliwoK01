package com.badziol.wrzospaliwok01

class ListaZbiornikow internal constructor() {
    private val zbiorniki: MutableList<ZbiornikPaliwa> = ArrayList()

    /**
     * Lista zbiornikow wraz z najistotniejszymi informacjami
     */
    fun daneZbiornikow() {
        println("Liczba zdefiniowanych zbiornikow : " + zbiorniki.size)
        var num = 0
        for (it in zbiorniki) {
            System.out.printf("%2d)  ", num)
            it.daneZbiornika()
            num++
        }
    }

    /**
     * Ustaw pomiar w cm dla konkretnego zbiornika. Numeracja zbiornikow 0..n<br></br>
     * @param numerZbiornika numer zbiornika
     * @param pomiar dokonany pomiar
     */
    fun ustaw(numerZbiornika: Int, pomiar: Float) {
        val typ = zbiorniki[numerZbiornika].wezTyp()
        val zb = ZbiornikPaliwa(typ)
        zb.ustawPomiar(pomiar)
        zb.obliczPaliwo()
        zbiorniki[numerZbiornika] = zb
    }

    /**
     *
     * @return suma stanu paliwa w wszyskich zbiornikach
     */
    fun obliczStan(): Int {
        var suma = 0
        for (it in zbiorniki) {
            suma += it.wezStan()
        }
        return suma
    }

    /**
     *
     * @param numerZbiornika Index zbiornika
     * @return Zwraca w centymetrach maksymalna wysokosc zbiornika np 180.00, 131.15
     */
    fun maxZbiornika(numerZbiornika: Int): Float {
        return zbiorniki[numerZbiornika].maxWysokoscZbiornikaMm().toFloat() / 10
    }

    init {
        //zbiorniki w piwnicy (dol)
        zbiorniki.add(ZbiornikPaliwa(2)) //1
        zbiorniki.add(ZbiornikPaliwa(2)) //2
        zbiorniki.add(ZbiornikPaliwa(2)) //3
        zbiorniki.add(ZbiornikPaliwa(2)) //4
        zbiorniki.add(ZbiornikPaliwa(2)) //5
        zbiorniki.add(ZbiornikPaliwa(2)) //6
        //zbiorniki na gorze
        zbiorniki.add(ZbiornikPaliwa(1)) //1
        zbiorniki.add(ZbiornikPaliwa(1)) //2
        zbiorniki.add(ZbiornikPaliwa(1)) //3
        zbiorniki.add(ZbiornikPaliwa(1)) //4
        zbiorniki.add(ZbiornikPaliwa(1)) //5
        zbiorniki.add(ZbiornikPaliwa(1)) //6
        zbiorniki.add(ZbiornikPaliwa(1)) //7
        //zbiornik kotlownia
        zbiorniki.add(ZbiornikPaliwa(1)) //1
    }
}