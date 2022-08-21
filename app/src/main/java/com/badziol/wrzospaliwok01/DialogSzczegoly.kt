import android.app.AlertDialog
import android.content.Context

/**
 *  Wersja stara , przerobka okna alert na potrzeby wypisania szczegolowych danych
 *  dotyczacych wskazanego pomiaru
 */
class DialogSzczegoly(context: Context) : AlertDialog.Builder(context) {

     fun show(title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
         builder.setIcon(android.R.drawable.ic_dialog_alert)
         builder.setPositiveButton("ZAMKNIJ") { _, _ ->

         }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

}