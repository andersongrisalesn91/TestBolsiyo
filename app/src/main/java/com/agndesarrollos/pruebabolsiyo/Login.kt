package com.agndesarrollos.pruebabolsiyo

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agndesarrollos.pruebabolsiyo.utils.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_logeo.*


class Login : AppCompatActivity() {
    var context: Context? = null
    var userTXT:String = ""
    var passTXT:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logeo)
        instance = this
        try {
            val cons = ConsultaGeneral()
            val queryUP = "SELECT va FROM PA WHERE pa=?"
            val pant = cons.queryObjeto(baseContext, queryUP, arrayOf("ultP"))
            if (pant != null) {
                val ultima = pant[0][0]
                Ir(ultima)
            } else {
                Toast.makeText(baseContext, "No se puede acceder a la base de datos", Toast.LENGTH_LONG).show()
            }
        } catch (c: Exception) {
            Log.i("Error ocreate:", c.toString())
        }
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
    }

    private fun login() {
        val connex = VerificarConex.revisarconexion(baseContext)
        if (connex) {
            login_online()
        } else {
            login_offline()
        }
    }

    fun login_online() {
        if (ValidarDatos()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(userTXT, StringEncryption.SHA1(passTXT)).addOnCompleteListener {
                if (it.isSuccessful) {
                    val sqluser = "insert or ignore into users (email , encrypted_password,login_online)" +
                            " values ('" + userTXT + "' , " +
                            "'" + StringEncryption.SHA1(passTXT) + "' ,1);"
                    val operaciones = OperacionesBDInterna(this)
                    operaciones.queryNoData(sqluser)
                    IngresoCompletado(1)
                } else {
                    Toast.makeText(baseContext, "Logeo Fallido, verifique los datos e intentelo nuevamente, si no esta registrado, registrese primero.",
                            Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun login_offline() {
        if (ValidarDatos()){
            val cGeneral = ConsultaGeneral()
            val query = "SELECT encrypted_password FROM users WHERE email='" + userTXT + "'"
            val obj = cGeneral.queryObjeto2val(baseContext, query, null)
            if (obj == null) {
                User_EditText.setText("")
                Pass_EditText.setText("")
                Toast.makeText(this, "Usuario no encontrado, por favor registrese o verifique la informacion ingresada, si esta intentando realizar el logeo offline, recuerde que debe haber realizado login online al menos 1 vez", Toast.LENGTH_LONG).show()
            } else {
                if (obj[0][0] == StringEncryption.SHA1(passTXT)) {
                    IngresoCompletado(2)
                } else {
                    Pass_EditText.setText("")
                    Toast.makeText(this, "Contraseña incorrecta, intentelo nuevamente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun registro() {
        if (ValidarDatos()){
            val connex = VerificarConex.revisarconexion(baseContext)
            if (connex) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(userTXT, StringEncryption.SHA1(passTXT)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val sqluser = "insert or ignore into users (email , encrypted_password,login_online)" +
                                " values ('" + userTXT + "' , " +
                                "'" + StringEncryption.SHA1(passTXT) + "' ,1);"
                        val operaciones = OperacionesBDInterna(this)
                        operaciones.queryNoData(sqluser)
                        IngresoCompletado(1)
                    } else {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(userTXT, StringEncryption.SHA1(passTXT)).addOnCompleteListener() {
                            if (it.isSuccessful) {
                                IngresoCompletado(1)
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

    fun ValidarDatos():Boolean{
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

    fun IngresoCompletado(tipologeo: Int) {
        val fg = FuncionesGenerales(baseContext)
        val operaciones = OperacionesBDInterna(this)
        fg.ultimaPantalla("Asteroides")
        fg.actparam("userid", fg.getQ1("select id from users where email='" + userTXT + "';"))
        fg.actparam("user", userTXT)
        fg.actparam("pass", StringEncryption.SHA1(passTXT))
        fg.actparam("login_online", tipologeo.toString())
        fg.actparam("sesion", "1")
        operaciones.queryNoData("UPDATE users SET login_online=" + tipologeo.toString() + " WHERE email='" + userTXT + "'")
        Toast.makeText(this, "Bienvenido:" + userTXT, Toast.LENGTH_SHORT).show()
        val intlogeo = Intent(this, ListImagesActivity::class.java)
        intlogeo.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intlogeo, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        instance?.finishAfterTransition()
    }

    fun Ir(pantalla: String?) {
        val p: Intent
        when (pantalla) {
            "Login" -> {
                try {
                    FirebaseAuth.getInstance().signOut()
                } catch (e: java.lang.Exception) {
                    Log.d("Error:", e.message.toString())
                }
                return
            }
            "MisImagenes" -> {
                p = Intent(this, ListImagesActivity::class.java)
                startActivity(p)
            }
            "DetalleImagen" -> {
                p = Intent(this, ListImagesActivity::class.java)
                startActivity(p)
            }
        }
    }

    companion object {
        var instance: Login? = null
    }
}