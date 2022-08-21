package com.badziol.wrzospaliwok01

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.google.gson.JsonArray

/**
 * Wypychanie okna dialogowego do zewnetrznej klasy. Troche na pale.
 * Wersja z customowym okienkiem
 * Wypisanie szczegolow konkretnego pomiaru
 */
class DialogSzczegoly2(val context: Context, val dane : JsonArray) {
    private val dialog = Dialog(context)

    public fun przygotuj(){
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_szczegoly)
        var info=""
        var poz =0
        for (i in 0..dane.size()-1) {
            val pom = dane[i].asInt
            when (i){
                0 -> {info +="\nDOL\n"; poz = i+1}
                6-> {info +="\nGORA\n"; poz = i-5}
                13-> {info +="\nKOTLOWNIA\n"; poz=1}
                in 1..5 -> poz = i+1;
                in 7..12 -> poz = i -5;
            }
            info += "Zbiornik $poz : $pom\n"
        }
        val wynik = dialog.findViewById<TextView>(R.id.tvSzczegolWynik)
        wynik.text = info

        val btn = dialog.findViewById<Button>(R.id.btnSzczegolZamknij)
        btn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


}