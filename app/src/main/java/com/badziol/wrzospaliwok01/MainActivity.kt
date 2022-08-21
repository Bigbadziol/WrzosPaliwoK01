package com.badziol.wrzospaliwok01

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.Reader
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.PatternSyntaxException

class MainActivity : AppCompatActivity() {
    private val TAG = "DEBUG"
    private val MINIMUM_DANYCH_ZAPIS = 5
    private val nazwaPlikDanych = "wrzos_pomiary.json"
    private var listaZbiornikow = ListaZbiornikow()
    private var daneWejsciowe: MutableList<EditText> = ArrayList()
    private val  permissionRequestCode = 105
    private lateinit var historiaList: ArrayList<HistoriaListDataModel>// for listview

    var mojeZapisy = JsonObject()

    /**
     *  Konstruktor
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG,"----------------------------------")
        Log.d(TAG,"SDK INT : ${Build.VERSION.SDK_INT}")
        Log.d(TAG,"TEST S  : ${Build.VERSION_CODES.S}")


        val zbiornikDol1 = findViewById<EditText>(R.id.etZbiornikDol1)
        val zbiornikDol2 = findViewById<EditText>(R.id.etZbiornikDol2)
        val zbiornikDol3 = findViewById<EditText>(R.id.etZbiornikDol3)
        val zbiornikDol4 = findViewById<EditText>(R.id.etZbiornikDol4)
        val zbiornikDol5 = findViewById<EditText>(R.id.etZbiornikDol5)
        val zbiornikDol6 = findViewById<EditText>(R.id.etZbiornikDol6)
        val zbiornikGora1 = findViewById<EditText>(R.id.etZbiornikGora1)
        val zbiornikGora2 = findViewById<EditText>(R.id.etZbiornikGora2)
        val zbiornikGora3 = findViewById<EditText>(R.id.etZbiornikGora3)
        val zbiornikGora4 = findViewById<EditText>(R.id.etZbiornikGora4)
        val zbiornikGora5 = findViewById<EditText>(R.id.etZbiornikGora5)
        val zbiornikGora6 = findViewById<EditText>(R.id.etZbiornikGora6)
        val zbiornikGora7 = findViewById<EditText>(R.id.etZbiornikGora7)
        val zbiornikKotlownia1 = findViewById<EditText>(R.id.etZbiornikKotlownia1)

        historiaList = ArrayList()

        //Button btnTest = findViewById(R.id.btnTest);
        daneWejsciowe.add(zbiornikDol1)
        daneWejsciowe.add(zbiornikDol2)
        daneWejsciowe.add(zbiornikDol3)
        daneWejsciowe.add(zbiornikDol4)
        daneWejsciowe.add(zbiornikDol5)
        daneWejsciowe.add(zbiornikDol6)
        daneWejsciowe.add(zbiornikGora1)
        daneWejsciowe.add(zbiornikGora2)
        daneWejsciowe.add(zbiornikGora3)
        daneWejsciowe.add(zbiornikGora4)
        daneWejsciowe.add(zbiornikGora5)
        daneWejsciowe.add(zbiornikGora6)
        daneWejsciowe.add(zbiornikGora7)
        daneWejsciowe.add(zbiornikKotlownia1)
        dodajFiltrDoWprowadzaniaDanych()
        dodajNasluchiwanieZmian()
        this.title = "STAN PALIWA : 0"

        if (!wcztajPomiary()) {
            Log.d(TAG,"Nie udalo sie wczytac pliku z pomiarami.")
            ustawDomyslnaBudowe()
            if (!zapiszPomiary()) {
                Log.d(TAG,"[BLAD] po probie zapisu danych domyslnych.")
            }else{
                Log.d(TAG,"Plik domyslny zapisano poprawnie.")
            }
        } else {
            Log.d(TAG,"Dane pomiarow wczytane poprawnie.")
            Log.d(TAG,mojeZapisy.toString())

        }

        /*
        // Przycisk do debugowania
        btnTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JsonObject daneDoZapisu = new JsonObject();
                daneDoZapisu = przygotujNowyZapis();
                System.out.println("TEST ZAPIS :"+daneDoZapisu.toString());
                //ustawDomyslnaBudowe();
            }
        });
         */
    }

    /**
     * Rozszezenie funkcionalnosci Edittexta o filtr wprowadzania danych
     */
    private fun EditText.inputFilterDecimal(
        maxDigitsIncludingPoint: Int, //calkowita maksymalna ilosc cyfr
        maxDecimalPlaces: Int // ilosc cyfr po przecinku
    ){
        try {
            filters = arrayOf<InputFilter>(
                DecimalDigitsInputFilter(maxDigitsIncludingPoint, maxDecimalPlaces)
            )
        }catch (e: PatternSyntaxException){
            isEnabled = false
            hint = e.message
        }
    }

    /**
     * Ta metoda ustawia filtr dla wszystkich pol edycyjnych, eliminuje wiekszosc mozliwych bledow.
     * Mozna wprowadzac tylko liczby w formacie 000.00<br></br>
     * Dokladnie co i jak definiuje to obiekt filter
     */
    private fun dodajFiltrDoWprowadzaniaDanych() {
        for (it in daneWejsciowe) {
            it.inputFilterDecimal(4,1);
        }
    }

    /**
     * Tu odbywa sie stworzenie naszego menu programu
     * @param menu
     * @return
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Tu odbywa sie wykonanie akcji po wybraniu konkretnej pozycji z menu glownego.
     * Parametry i zwrot to zdecydowanie bardziej skomplikowana sprawa
     * @param item
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //zapisz dane do pliku, sprawdz minimum wprowadzonych danych
        if (id == R.id.menuPozZapiszDane) {
            if (minimumDanych()) {
                var noweDane = JsonObject()
                noweDane = przygotujNowyZapis()
                if (mojeZapisy.has("zapisy")) {
                    val zapisy = mojeZapisy.getAsJsonArray("zapisy")
                    zapisy.add(noweDane)
                    mojeZapisy.add("zapisy", zapisy)
                    zapiszPomiary()
                } else {
                    Log.d(TAG, "[BLAD] obiekt mojeZapisy nie maja czlonka zapisy.")
                }
            } else {
                val informacja =
                    "Wprowadz minimum $MINIMUM_DANYCH_ZAPIS danych do zapisu."
                Toast.makeText(applicationContext, informacja, Toast.LENGTH_SHORT).show()
            }
            return true
        }
        //wyslij sms do... , wymagaj do wyslania wypelnienia wszystkich pol
        if (id == R.id.menuPozSMS){
            if (polaWypelnione()) {
                val permissionCheck =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    val maxStan = realnyMaxDoTankowania()
                    val aktualnyStan = listaZbiornikow.obliczStan()
                    val zamowienie = maxStan - aktualnyStan
                    Log.d(TAG,"Max zbiornikow : $maxStan")
                    Log.d(TAG,"Do zamowienia : $zamowienie")
                    val informacja = "[AUTOMAT]:\nSTAN PALIWA :  " + aktualnyStan + " L.\n" +
                            "MAX ZAMOWIENIA : " + zamowienie + " L.\n"
                    //sendSMS("+48730140327","Test SMS")//filip
                    //sendSMS("+48791866618", informacja)//maja
                    //sendSMS("+48607672267",informacja)// moj prywatny 1
                    sendSMS("+48725553806",informacja) //moj prywatny 2
                    //sendSMS("+48601753822",informacja)//mama

                    //Toast.makeText(applicationContext, "Wyslano wiadomosc:\n$informacja", Toast.LENGTH_LONG).show()
                } else {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.SEND_SMS), permissionRequestCode)
                }
            }else{
                val informacja="Aby wyslac SMS do szefostwa, wypelnij wpierw wszystkie pola."
                Toast.makeText(applicationContext, informacja, Toast.LENGTH_LONG).show()
            }
        }

        //wyswietl zapisane wczesniej dane
        if (id == R.id.menuPozHistoria) {
            pokazDialogHistoria()
            return true
        }

        //wyczysc pola
        if (id == R.id.menuPozWyczyscPola) {
            for (it in daneWejsciowe) {
                it.setText("")
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Ta metoda powoduje, ze mozliwe staje sie dynamiczne uaktualnianie stanu w naglowku programu. <br></br>
     * Ta metoda jest uruchamiana tylko raz dla wszystkich pol edycyjnych.<br></br>
     * Po jej wykonaniu nasluchiwanie odbywa sie w tle <br></br>
     */
    private fun dodajNasluchiwanieZmian() {
        for (it in daneWejsciowe) {
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    //System.out.println("----------ZBIORNIK  ZMIANA -------");
                    uaktualnijStanPaliwa()
                }
            })
        }
    }

    /**
     * Ta metoda odpowiada za dynamiczne aktualizowanie stanu paliwa w naglowku aplikacji<br></br>
     *
     * Dzialam tak:
     *  *  zbieram dane z wszystkich pol edycyjnych
     *  *  uaktualniam w oparciu o te dane , stany zbiornikow
     *  *  jesli "wysokosc cm"jest wieksza niz dopuszczalna dla danego zbiornika, ustaw maksymalny poziom.
     *
     */
    private fun uaktualnijStanPaliwa() {
        var wartosc: String
        var cm: Float
        var stan: Int
        var max: Float
        for (i in daneWejsciowe.indices) {
            wartosc = daneWejsciowe[i].text.toString()
            cm = if (wartosc.isEmpty()) {
                0f
            } else {
                try {
                    wartosc.toFloat()
                } catch (e: Exception) {
                    0f
                }
            }
            listaZbiornikow.ustaw(i, cm)
            max = listaZbiornikow.maxZbiornika(i)
            if (cm > max) {
                daneWejsciowe[i].setText(java.lang.Float.toString(max))
            }
            stan = listaZbiornikow.obliczStan()
            this.title = "STAN PALIWA : $stan"
        } //for
    }

    /**
     * Metoda przygotowuje obiekt json do zapisu.<br></br>
     * Metoda nie sprawdza kryterium minimum wypelnionych danych ! <br></br>
     * Umieszczane dane :
     *  *  - autor pomiarow
     *  *  - data i czas w formie czytelnej dla czlowieka
     *  *  - czas jako unix timetamp
     *  *  - tablice pomiarow z wszystkich pol edycyjnych
     *
     * @return przygotowany objekt json
     */
    private fun przygotujNowyZapis(): JsonObject {
        Log.d(TAG,"Przygotowuje nowy zapis danych")
        val zapis = JsonObject()
        val tablicaPomiarow = JsonArray()
        var wartoscPola: String
        val dataGodzinaCzas = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
            Date()
        )
        val dataTimestamp = Timestamp(System.currentTimeMillis())
        zapis.addProperty("autor", "[xxx]")
        zapis.addProperty("data", dataGodzinaCzas)
        zapis.addProperty("timestamp", dataTimestamp.time)
        zapis.addProperty("stan", listaZbiornikow.obliczStan())
        for (i in daneWejsciowe.indices) {
            wartoscPola = daneWejsciowe[i].text.toString()
            if (wartoscPola.isEmpty()) wartoscPola = "0"
            tablicaPomiarow.add(wartoscPola.toFloat())
        }
        zapis.add("pomiary", tablicaPomiarow)
        return zapis
    }

    /**
     * Metoda sprawdza ilosc danych wprowadzonych w pola edycyjne.<br></br>
     * Minimum wypelnionych pol okreslone jest w zmiennej MINIMUM_DANYCH_ZAPIS<br></br>
     * @return true - jesli ilosc wypelnionych danych jest >= kryterium
     */
    private fun minimumDanych(): Boolean {
        var licznik = 0
        var wartoscPola: String
        if (MINIMUM_DANYCH_ZAPIS == 0) return true
        for (pole in daneWejsciowe) {
            wartoscPola = pole.text.toString()
            if (!wartoscPola.isEmpty() && !wartoscPola.equals(
                    ".",
                    ignoreCase = true
                )
            ) licznik++
            if (licznik == MINIMUM_DANYCH_ZAPIS) return true
        }
        return false
    }

    /**
     * Ta metoda zapisuje wszystkie dane zgromadzone w obiekcie mojeZapisy <br></br>
     * @return true - udalo sie zapisac dane , false wystapil jakis blad
     */
    private fun zapiszPomiary(): Boolean {
        try {
            Log.d(TAG,"Zapisuje pomiary do pliku.")
            val gson = Gson()
            val pelnaSciezka = applicationContext.filesDir.toString() + "/" + nazwaPlikDanych
            println("Sciezka :$pelnaSciezka")
            val writer: Writer = Files.newBufferedWriter(Paths.get(pelnaSciezka))
            gson.toJson(mojeZapisy, writer)
            writer.close()
            return true
        } catch (e: Exception) {
            //e.printStackTrace();
            println("[BLAD] Zapisz pomiary :$e")
        }
        return false
    }

    /**
     * Ta metoda odpowiada za wczytanie danych z pliku o nazwie umieszczonej w zmiennej "nazwaPlikDanych"
     * @return
     *  *  true - jesli udalo sie wczytac plik i dane umiescic w obiekcie mojeZapisy
     *  *  false - na 99% plik nie istnieje , 1% ktos recznie grzebal w pliku i popsul strukture danych
     *
     */
    private fun wcztajPomiary(): Boolean {
        try {
            val gson = Gson()
            val pelnaSciezka = applicationContext.filesDir.toString() + "/" + nazwaPlikDanych
            Log.d(TAG,"Wczytuje dane z : $pelnaSciezka")
            val reader: Reader = Files.newBufferedReader(Paths.get(pelnaSciezka))
            mojeZapisy = gson.fromJson(reader, JsonObject::class.java)
            reader.close()
            return true
        } catch (e: Exception) {
            Log.d(TAG,"[BLAD] wczytaj pomiary :$e")
        }
        return false
    }

    /**
     * Ta metoda wywolywana jest tak naprawde tylko w przypadku kiedy plik z danymi (wczesniejsze zapisy) nie istnie.<br></br>
     * Najpierw jest generowany "pierwszy" wpis(obiekt json), ktory wyglada tak : <br></br>
     * {"autor":"Artur","data":"2022-08-02 18:14:31","timestamp":1659456871002,"stan":0,"pomiary":[0.0,0.0, ... ]} <br></br>
     * wszystkie pomiary beda wskazywaly na 0.0 <br></br>
     * obiekt ten jest umieszczany w tablicy "zapisy" , zatem : <br></br>
     * globalna zmienna mojeZapisy przechowuje wartosc : <br></br>
     * {"zapisy":[ <br></br>
     * {"autor":"Artur","data":"2022-08-02 18:14:31","timestamp":1659456871002,"stan":0,"pomiary":[0.0,0.0, ... ]} <br></br>
     * ]}<br></br>
     */
    private fun ustawDomyslnaBudowe() {
        Log.d(TAG,"Ustawiam domyslna budowe.")
        var pierwszyWpis = JsonObject()
        pierwszyWpis = przygotujNowyZapis()
        val zapisy = JsonArray()
        zapisy.add(pierwszyWpis)
        mojeZapisy.add("zapisy", zapisy)
        Log.d(TAG,"Domyslne dane: ${mojeZapisy}")
    }

    /**
     * Ta metoda tworzy okienko z skroconymi informacjami o wczesniejszych zapisach
     */
    fun pokazDialogHistoria() {

        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_histowia_v2)

        val lvLista = dialog.findViewById<ListView>(R.id.lvPomiaryLista)
        val btnZamknij = dialog.findViewById<Button>(R.id.btnPomiaryZamknij)

        historiaList.clear()
        if (mojeZapisy.has("zapisy")) {
            val wpisy = mojeZapisy.getAsJsonArray("zapisy")
            Log.d(TAG,"------Pobrane dane-----")
            for (wpis  in wpisy){
                //Log.d(TAG,wpis.toString())
                if (wpis.asJsonObject.has("data") &&
                    wpis.asJsonObject.has("stan") &&
                    wpis.asJsonObject.has("timestamp") &&
                    wpis.asJsonObject.has("pomiary")
                ){
                        val data = wpis.asJsonObject.get("data").asString
                        val stan = wpis.asJsonObject.get("stan").asString
                        val tstamp = wpis.asJsonObject.get("timestamp").asLong
                        val pomiary = wpis.asJsonObject.getAsJsonArray("pomiary")
                        Log.d(TAG,"Data : $data , Stan : $stan Timestamp : $tstamp")
                        historiaList.add(0,HistoriaListDataModel(data,stan,tstamp,pomiary))
                }
            }
        } else {
            Log.d(TAG, "[BLAD] obiekt mojeZapisy nie maja tablicy zapisy.")
        }
        lvLista.adapter = HistoriaListAdapter(this,historiaList)

        btnZamknij.setOnClickListener { v: View? -> dialog.dismiss() }
        dialog.show()
    }

    /**
     *  Sprawdz czy wszystkie pola zbiornikow sa wypelnione, w tym przypadku, jesli
     *  zbiornik jest pusty trzeba wstawic 0, nie jest stosowana zasada "nic" = 0
     */
    private fun polaWypelnione():Boolean{
        for (pole in daneWejsciowe){
            if (pole.text.toString().isBlank()) return false
        }
        return true
    }

    /**
     *  Wyslij SMS pod wskazany numer o okreslonej tresci
     */
    private fun sendSMS(phoneNumber: String, message: String) {
        //---TO DZIALA NA SAMSUNGU
        //val sentPI: PendingIntent = PendingIntent.getBroadcast(this, 0, Intent("SMS_SENT"), 0)
        //SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, sentPI, null)
        //---
        //POMYSLY
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                applicationContext.getSystemService(SmsManager::class.java) as SmsManager
            }else{
                SmsManager.getDefault()
            }
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            //Toast.makeText(applicationContext, "Poszlo", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "UPS! cos nie poszlo"+e.message.toString(), Toast.LENGTH_LONG)
                .show()
        }
        //KONIEC POMYSLOW
    }

    /**
     *    Uwzględniamy TYLKO zbiorniki w magazynie paliw. Przyjmij "realny" max , nie pod korek.
     */
    private fun realnyMaxDoTankowania() : Int{
        val iloscMaleZbiorniki = 6
        val iloscDuzeZbiorniki = 7

        return (iloscMaleZbiorniki * MojeStale.ZBIORNIK_1100L_MAX) + (iloscDuzeZbiorniki * MojeStale.ZBIORNIK_1850L_MAX)
    }
}


