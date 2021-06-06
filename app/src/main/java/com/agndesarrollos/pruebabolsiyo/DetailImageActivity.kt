package com.agndesarrollos.pruebabolsiyo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.agndesarrollos.pruebabolsiyo.utils.CircleTransform
import com.agndesarrollos.pruebabolsiyo.utils.FuncionesGenerales
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_image.*

class DetailImageActivity : AppCompatActivity() {

    var fg: FuncionesGenerales? = null
    var param: Map<String, Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)
        instance = this
        //Se cargan los parametros almacenados en SQLite en la pantalla de lista y se guardan en el Map param
        fg = FuncionesGenerales(baseContext)
        param = fg?.getparametros()
    }

    override fun onStart() {
        super.onStart()
        LoadDetail()
    }

    fun LoadDetail(){
        //Se carga la informacion a mostrar (Titulo,Imagen,Tags,Vistas y Me Gusta)
        NameView_TextView.text = "IMAGEN : " + param?.get("imgid").toString()
        Picasso.get()
                .load(param?.get("imgurl").toString().toUri())
                .centerCrop()
                .transform(CircleTransform(15, 3))
                .fit()
                .into(ImgPreview_imageView)
        Tags_textView.text = param?.get("imgtags").toString()
        Views_textView.text = param?.get("imgviews").toString()
        Likes_textView.text = param?.get("imglikes").toString()
    }

    companion object {
        var instance: DetailImageActivity? = null
    }
}