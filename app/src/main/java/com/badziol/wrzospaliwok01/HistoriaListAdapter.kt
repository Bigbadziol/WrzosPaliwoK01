package com.badziol.wrzospaliwok01

import DialogSzczegoly
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.google.gson.JsonArray

/*
    Realnie , zastanowic sie czy w ogole nie przegazywac danych jako  JsonObject, bo
    teraz to mocno  zagwatmane
 */
data class HistoriaListDataModel(var  czas : String , var pomiar : String , var timestamp : Long,var dane :JsonArray)


class HistoriaListAdapter (private val  context: Activity,
                           var items : ArrayList<HistoriaListDataModel>,
                           ) :
    ArrayAdapter<HistoriaListDataModel>(context , R.layout.pomiar_wiersz , items){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater: LayoutInflater = LayoutInflater.from(context)
            val view: View = layoutInflater.inflate(R.layout.pomiar_wiersz, null)

            val tenCzas : TextView = view.findViewById(R.id.tvPomiarCzas)
            val tenPomiar : TextView = view.findViewById(R.id.tvPomiarWartosc)
            val btn : ImageButton = view.findViewById(R.id.ibPomiarSzczegoly)

            tenCzas.text = items[position].czas
            tenPomiar.text = items[position].pomiar
            btn.setOnClickListener {
/*
                //kod potrzebny do odpalenia pierwszej wersji roboczej
                //zostawic moze sie do czegos przydac w przyszlosci jako podglad
                var teDane = items[position].dane
                var info =""
                for (i in 0 until teDane.size()) {
                    val pom = teDane[i].asInt
                    when (i){
                        0 -> info +="\nDOL\n"
                        6-> info +="\nGORA\n"
                        13->info +="\nKOTLOWNIA\n"
                    }
                    info +="Zbiornik " +(i+1)+ " :"+pom+"\n"
                }
                DialogSzczegoly(context).show("Szczegoly",info)
 */
                val teDane = items[position].dane
                val d2 = DialogSzczegoly2(context,teDane)
                d2.przygotuj()
            }
            return view
        }
    }