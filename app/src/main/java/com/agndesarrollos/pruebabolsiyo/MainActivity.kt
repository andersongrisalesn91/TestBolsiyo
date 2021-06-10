package com.agndesarrollos.pruebabolsiyo

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.WindowManager
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.widget.Toast
import android.content.Intent
import com.agndesarrollos.pruebabolsiyo.Login
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val permissions = arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
    var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread.sleep(2000)
        setTheme(R.style.AppThemeNoBar)
        instance = this
    }

    override fun onStart() {
        super.onStart()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        revisarPermisos()
    }

    fun revisarPermisos() {
        //Verifica si los permisos establecidos se encuentran concedidos
        if (ActivityCompat.checkSelfPermission(this@MainActivity, permissions[0]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this@MainActivity, permissions[1]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this@MainActivity, permissions[2]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this@MainActivity, permissions[3]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this@MainActivity, permissions[4]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this@MainActivity, permissions[5]) != PackageManager.PERMISSION_GRANTED) {
            //Si alguno de los permisos no esta concedido lo solicita
            try {
                ActivityCompat.requestPermissions(this@MainActivity, permissions, MULT_PERMISOS)
            } catch (e: Exception) {
                Toast.makeText(baseContext, "Recuerde otorgar todos los permisos, de lo contrario la aplicación fallara.", Toast.LENGTH_LONG).show()
                revisarPermisos()
            } finally {
                revisarPermisos()
            }
        } else {
            IrLogin()
        }
    }

    fun IrLogin() {
        val p: Intent
        p = Intent(this, Login::class.java)
        startActivity(p)
    }

    companion object {
        const val MULT_PERMISOS = 4
        var instance: MainActivity? = null
    }
}