package com.agndesarrollos.pruebabolsiyo.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.agndesarrollos.pruebabolsiyo.R
import com.google.firebase.auth.FirebaseAuth
import java.util.HashMap

class PopUps : AppCompatActivity() {
    var fg: FuncionesGenerales? = null
    fun popUpConfirmar(contex: Context, inflater: LayoutInflater, datos: HashMap<String, String>,Prefs: SharedPreferences) {
        val ruta = "com.agndesarrollos.pruebabolsiyo." + datos.get("Clase")
        val popUp = inflater.inflate(R.layout.popup_confirmacion, null)
        val TVTitulo = popUp.findViewById<TextView>(R.id.TVTitulo)
        val TVMensaje = popUp.findViewById<TextView>(R.id.TVTextoconf)
        val cancel = popUp.findViewById<Button>(R.id.buttonConfNo)
        val conf = popUp.findViewById<Button>(R.id.buttonConfSi)
        TVTitulo.text = datos.get("Titulo")
        TVMensaje.text = datos.get("Mensaje")
        cancel.text = datos.get("TextoBTNo")
        conf.text = datos.get("TextoBTSi")
        val popupWindow = PopupWindow(popUp, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true)

        //En caso de confirmar la salida.
        conf.setOnClickListener {
            var sigClass: Class<*>? = null
            try {
                sigClass = Class.forName(ruta)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } finally {
                //Se cierra sesion en Firebase
                FirebaseAuth.getInstance().signOut()

                //Se limpian los valores de las preferencias
                val pref = Prefs.edit()
                pref.clear()
                pref.apply()

                //Segun la clase recibida se dirije a esa pantalla.
                val intento = Intent(contex, sigClass)
                intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                contex.startActivity(intento)
                popupWindow.dismiss()
                finish()
            }
        }
        //En caso de cancelar la salida.
        cancel.setOnClickListener { popupWindow.dismiss() }
        popupWindow.height = ViewGroup.LayoutParams.MATCH_PARENT
        popupWindow.showAtLocation(popUp, Gravity.CENTER, 0, 0)
    }
}