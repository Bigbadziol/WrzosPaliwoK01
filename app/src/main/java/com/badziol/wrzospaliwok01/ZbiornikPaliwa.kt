package com.badziol.wrzospaliwok01

class ZbiornikPaliwa {
    private var mojTyp = 0
    private val zbiornik: Array<IntArray>
    private var stan = 0
    private var pomiarCm = 0f

    /**
     * Konstruktor tworzacy bazowy obiekt<br></br>
     * @param typ Obslugiwane typy zbiornika.
     *
     *  * 0 - zbiornik 0l (blad)
     *  * 1 - zbiornik 1850l
     *  * 2 - zbiornik 1100l
     *
     */
    internal constructor(typ: Int) {
        mojTyp = typ
        if (mojTyp != 1 && mojTyp != 2) mojTyp = 0
        zbiornik = when (typ) {
            1 -> MojeStale.ZBIORNIK_1850L
            2 -> MojeStale.ZBIORNIK_1100L
            else -> MojeStale.ZBIORNIK_0L
        }
    }

    /**
     * Konstruktor rozszerzony<br></br>
     * @param typ Obslugiwane typy zbiornika.
     *
     *  * 0 - zbiornik 0l (blad)
     *  * 1 - zbiornik 1850l
     *  * 2 - zbiornik 1100l
     *
     * @param pomiar Wysokosc slupa cieczy wyrazona w cm.
     */
    internal constructor(typ: Int, pomiar: Float) {
        mojTyp = typ
        if (mojTyp != 1 && mojTyp != 2) mojTyp = 0
        zbiornik = when (typ) {
            1 -> MojeStale.ZBIORNIK_1850L
            2 -> MojeStale.ZBIORNIK_1100L
            else -> MojeStale.ZBIORNIK_0L
        }
        ustawPomiar(pomiar)
        obliczPaliwo()
    }

    /**
     * Ustaw pomiar w cm , na podstawie ktorego zostanie wykonane obliczenie<br></br>
     * Uwaga! : Metoda nie sprawdza poprawnosci zakresu - przypisanie na pale.
     * Po ustawieniu nalzy uzyc metode obliczPaliwo()
     * @param nowyPomiar
     */
    fun ustawPomiar(nowyPomiar: Float) {
        pomiarCm = nowyPomiar
    }

    /**
     * typ(liczba) - wskazanie na konretny ziornik odbywa sie w konstruktorze <br></br>
     * Dostepne identyfikatory (typy)
     *
     *  * 0 - blad
     *  * 1 - zbiornik 1850l
     *  * 2 - zbiornik 1100l
     *
     * @return identyfikator typu zbiornika
     */
    fun wezTyp(): Int {
        return mojTyp
    }

    /**
     * @return Stan obliczonego paliwa na podstawie pomiaru wykonanego w cm.
     */
    fun wezStan(): Int {
        return stan
    }

    /**
     * @return Aktualnie ustawiona wartosc parametru w cm.
     */
    fun wezPomiar(): Float {
        return pomiarCm
    }


    /**
     * Mapowanie typu(indeksu) do nazwy<br></br>
     * @param typ - Indeks , odowlanie do konkretnego zbiornika
     * @return String opisujacy typ
    */

    fun typDoNazwy(typ: Int): String {
        val opis: String
        opis = when (typ) {
            1 -> "Zbiornik 1850l"
            2 -> "Zbiornik 1100l"
            else -> "Nieznany typ."
        }
        return opis
    }

    /**
     *
     * @return maksymalna wysokosc zbiornika w mm.
     */
    fun maxWysokoscZbiornikaMm(): Int {
        return zbiornik[zbiornik.size - 1][0]
    }

    /**
     * Wyswietla najistotniejsze informacje o ustawieniach obiektu.
     */
    fun daneZbiornika() {
        System.out.printf(
            "Typ : %d - %-17s pomiar : %6.1f \tstan: %5d  \n",
            mojTyp,
            typDoNazwy(mojTyp),
            pomiarCm,
            stan
        )
    }

    /**
     * Metoda zaokraglajaca liczbe typu float do okreslonej ilosci miejsc po przecinku<br></br>
     * @param liczba - zaokraglana liczba
     * @param iloscMiejscPoPrzecinku - precyzja zaokraglenia
     * @return Zaokraglona liczba typu float
     */
    private fun zaokraglijLiczbe(liczba: Float, iloscMiejscPoPrzecinku: Int): Float {
        return (Math.round(liczba * Math.pow(10.0, iloscMiejscPoPrzecinku.toDouble())) / Math.pow(
            10.0,
            iloscMiejscPoPrzecinku.toDouble()
        )).toFloat()
    }
    /**
     * UWAGA w przypadku przekroczenia zakresu lub innego bledu bez wzgledu czy chcemy pomiar zapamietac czy nie
     * wartosc wysokoscCm bedzie ustawiona na 0.<br></br>
     * Spowoduje to rowniez ustawienie stanu na 0.
     * @param wysokoscCm Wysokosc slupa cieczy wyrazona w cm.
     * @param zapamietajWysokosc spowoduje zapamietanie parametru wysokoscCm.
     *
     * @return Stan paliwa na podstawie typu(pojemnosci zbiornika) i wysokosci cieczy wyrazonej w cm.
     */

    fun obliczPaliwo(wysokoscCm: Float = pomiarCm, zapamietajWysokosc: Boolean = true): Int {
        var tmp = 0f // tymczasowa wartosc do obliczen
        var wartoscDolnaMm = 0f
        var wartoscGornaMm = 0f
        var wartoscDolnaLitry = 0f
        var wartoscGornaLitry = 0f
        var roznicaLitry = 0f
        var skala = 0f
        if (zapamietajWysokosc) pomiarCm = wysokoscCm
        tmp = zaokraglijLiczbe(wysokoscCm, 1) // zaokraglij liczbe do 1 miejsca po przecinku
        tmp = tmp * 10 // przelicz centymetry na milimetry
        //nasz pomiar jest mniejszy niz pierwszy element
        if (tmp < zbiornik[0][0]) {
            pomiarCm = 0f //ustawiamy na 0 bo bledny parametr
            stan = 0
            return -1
        }
        // nasz pomiar jest wiekszy niz ostatni element
        if (tmp > zbiornik[zbiornik.size - 1][0]) {
            pomiarCm = 0f //ustawiamy na 0 bo bledny parametr
            stan = 0
            return -2
        }
        //szukamy odpowiednich danych
        for (i in 0 until zbiornik.size - 1) {  // -1 by nie przekroczyc zakresu tablicy (patrz : waroscGornaMm)
            wartoscDolnaMm = zbiornik[i][0].toFloat()
            wartoscGornaMm = zbiornik[i + 1][0].toFloat()
            if (tmp == wartoscDolnaMm) {            //  idealne trafienie w podzialke
                stan = zbiornik[i][1]
                return zbiornik[i][1]
            } else if (tmp == wartoscGornaMm) {        //  idealne trafienie w podzialke
                stan = zbiornik[i + 1][1]
                return zbiornik[i + 1][1]
            } else if (tmp > wartoscDolnaMm && tmp < wartoscGornaMm) {    //  szukamy widelek
                wartoscDolnaLitry = zbiornik[i][1].toFloat()
                wartoscGornaLitry = zbiornik[i + 1][1].toFloat()
                roznicaLitry = wartoscGornaLitry - wartoscDolnaLitry
                skala = zbiornik[i + 1][0].toFloat() - zbiornik[i][0].toFloat()
                val pseudoProcent = (tmp - wartoscDolnaMm) / skala
                val litry = Math.round(pseudoProcent * roznicaLitry).toFloat()
                val wynik = wartoscDolnaLitry + litry
                stan = wynik.toInt()
                return wynik.toInt()
            }
        }
        //pojawil sie nieznany blad
        pomiarCm = 0f
        stan = 0
        return -3
    }
}
