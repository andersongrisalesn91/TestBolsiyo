package com.agndesarrollos.pruebabolsiyo

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agndesarrollos.pruebabolsiyo.utils.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_logeo.*
import java.lang.Exception


class Login : AppCompatActivity() {

    var userTXT: String = ""
    var passTXT: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logeo)
        instance = this
    }

    override fun onStart() {
        super.onStart()
        val ivlogox = findViewById<ImageView>(R.id.Logo_ImageView)
        ivlogox.isSelected = true
        Login_button.setOnClickListener() {
            login()
        }
        Registro_button.setOnClickListener() {
            registro()
        }
        sesion()
    }

    private fun login() {
        val connex = VerificarConex.revisarconexion(baseContext)
        if (connex) {
            login_online()
        } else {
            Toast.makeText(baseContext, "No hay conexion , intentelo nuevamente",
                    Toast.LENGTH_SHORT).show()
        }
    }

    fun login_online() {
        if (ValidarDatos()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(userTXT, StringEncryption.SHA1(passTXT)).addOnCompleteListener {
                if (it.isSuccessful) {
                    IngresoCompletado()
                } else {
                    Toast.makeText(baseContext, "Logeo Fallido, verifique los datos e intentelo nuevamente, si no esta registrado, registrese primero.",
                            Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun registro() {
        if (ValidarDatos()) {
            val connex = VerificarConex.revisarconexion(baseContext)
            if (connex) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(userTXT, StringEncryption.SHA1(passTXT)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        IngresoCompletado()
                    } else {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(userTXT, StringEncryption.SHA1(passTXT)).addOnCompleteListener() {
                            if (it.isSuccessful) {
                                IngresoCompletado()
                            } else {
                                Toast.makeText(baseContext, "Registro Fallido, intentelo nuevamente.",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(baseContext, "Registro Fallido, verifique su conexion.",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun ValidarDatos(): Boolean {
        var fg = FuncionesGenerales(this)
        userTXT = User_EditText.text.toString().trim()
        passTXT = Pass_EditText.text.toString().trim()
        User_EditText.setText(userTXT)
        Pass_EditText.setText(passTXT)
        if (User_EditText.text.isEmpty() || Pass_EditText.text.isEmpty()) {
            Toast.makeText(this, "Debe Ingresar el Usuario y la contraseña para ingresar", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!fg.validarEmail(User_EditText.text.toString())) {
            Toast.makeText(this, "Correo invalido", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun IngresoCompletado() {
        val prefs = getSharedPreferences(getString(R.string.Prefs_File),Context.MODE_PRIVATE).edit()
        prefs.putString("email",userTXT)
        prefs.putString("passsha1",StringEncryption.SHA1(passTXT))
        prefs.apply()
        Toast.makeText(this, "Bienvenido:" + userTXT, Toast.LENGTH_SHORT).show()
        irListImage()
    }

    fun sesion(){
        try{
            val prefs = getSharedPreferences(getString(R.string.Prefs_File),Context.MODE_PRIVATE)
            val email = prefs.getString("email",null)
            val pass = prefs.getString("passsha1",null)
            if (email != null && pass != null){
                irListImage()
            }
        }catch (e:Exception){
            Log.d("Error:","Error de sesion:"+e.message)
        }

    }

    fun irListImage(){
        val intlogeo = Intent(this, ListImagesActivity::class.java)
        intlogeo.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intlogeo, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        instance?.finishAfterTransition()
    }

    companion object {
        var instance: Login? = null
    }
}