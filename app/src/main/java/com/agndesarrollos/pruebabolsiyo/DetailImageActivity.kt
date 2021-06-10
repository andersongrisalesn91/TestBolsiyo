package com.agndesarrollos.pruebabolsiyo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.agndesarrollos.pruebabolsiyo.utils.CircleTransform
import com.agndesarrollos.pruebabolsiyo.utils.FuncionesGenerales
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_image.*
import kotlinx.android.synthetic.main.activity_detail_image.NameView_TextView
import kotlinx.android.synthetic.main.activity_list_images.*

class DetailImageActivity : AppCompatActivity() {
    var imgid:String? = null
    var imgurl:String? = null
    var imgtags:String? = null
    var imgviews:String? = null
    var imglikes:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)
        instance = this
        //Se cargan los parametros almacenados en SQLite en la pantalla de lista y se guardan en el Map param
        val bundle = intent.extras
        imgid = bundle?.getString("imgid",null)
        imgurl = bundle?.getString("imgurl",null)
        imgtags = bundle?.getString("imgtags",null)
        imgviews = bundle?.getString("imgviews",null)
        imglikes = bundle?.getString("imglikes",null)
    }

    override fun onStart() {
        super.onStart()
        LoadDetail()
    }

    fun LoadDetail(){
        if(imgid != null && imgurl != null && imgtags != null && imgviews != null && imglikes != null){
            //Se carga la informacion a mostrar (Titulo,Imagen,Tags,Vistas y Me Gusta)
            NameView_TextView.text = "IMAGEN : " + imgid.toString()
            Picasso.get()
                    .load(imgurl.toString().toUri())
                    .centerCrop()
                    .transform(CircleTransform(15, 3))
                    .fit()
                    .into(ImgPreview_imageView)
            Tags_textView.text = imgtags.toString()
            Views_textView.text = imgviews.toString()
            Likes_textView.text = imglikes.toString()
        }else{
            Toast.makeText(baseContext, "Error al cargar el detalle. " , Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

    }

    companion object {
        var instance: DetailImageActivity? = null
    }
}