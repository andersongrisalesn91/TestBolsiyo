package com.agndesarrollos.pruebabolsiyo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agndesarrollos.pruebabolsiyo.jsonclass.ResponseList
import com.agndesarrollos.pruebabolsiyo.utils.*
import com.apptakk.http_request.HttpRequest
import com.apptakk.http_request.HttpRequestTask
import com.apptakk.http_request.HttpResponse
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_list_images.*
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList

class ListImagesActivity : AppCompatActivity() {

    internal var context: Context? = null
    lateinit var recyclerImages: RecyclerView
    internal lateinit var ListImg: ArrayList<ImageModel>
    internal var ARid = mutableListOf<String>()
    internal var ARUrls = mutableListOf<String>()
    internal var ARDownloads = mutableListOf<String>()
    internal var ARUser = mutableListOf<String>()
    internal var ARTags = mutableListOf<String>()
    internal var ARViews = mutableListOf<String>()
    internal var ARLikes = mutableListOf<String>()
    lateinit var RelativeLAS: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_images)
        instance = this
    }

    internal inner class Imagesc(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPrev: ImageView
        var idImage: TextView
        var dlImage: TextView
        var usImage: TextView
        var cImage: ConstraintLayout
        init {
            this.imgPrev = itemView.findViewById(R.id.Preview_ImageView)
            this.idImage = itemView.findViewById(R.id.ID_TextView)
            this.dlImage = itemView.findViewById(R.id.Downloads_TextView)
            this.usImage = itemView.findViewById(R.id.User_TextView)
            this.cImage = itemView.findViewById(R.id.Container_ConstraintLayout)
        }
    }

    internal inner class ImageModel
    (var idimg: String, var dwimg: String, var usimg: String, var urlimg: String)


    internal inner class ImageAdapter(var ListImg: ArrayList<ImageModel>) : RecyclerView.Adapter<Imagesc>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Imagesc {
            return Imagesc(LayoutInflater.from(parent.context).inflate(R.layout.ver_image, parent, false))
        }

        override fun onBindViewHolder(holder: Imagesc, position: Int) {
            //Para cada item en la lista se asignan los valores de Imagen,ID,Cantidad de descargas y Nombre del usuario creador
            val opcion = ListImg[position]
            holder.idImage.text = "ID Imagen : " + opcion.idimg
            holder.dlImage.text = "Descargas : " + opcion.dwimg
            holder.usImage.text = "Usuario Creador : " + opcion.usimg
            Picasso.get()
                    .load(opcion.urlimg.toUri())
                    .centerCrop()
                    .transform(CircleTransform(50, 0))
                    .fit()
                    .into(holder.imgPrev)
            //Se asigna el evento OnClic a cada elemento de la lista Enviando la posicion, para consultar posteriormente su detalle
            holder.imgPrev.setOnClickListener { IrImage(position) }
            holder.idImage.setOnClickListener { IrImage(position) }
            holder.dlImage.setOnClickListener { IrImage(position) }
            holder.usImage.setOnClickListener { IrImage(position) }
            holder.cImage.setOnClickListener { IrImage(position) }
        }

        override fun getItemCount(): Int {
            return ListImg.size
        }
    }


    private fun IrImage(position: Int) {

        //Se Ejecuta el intent a la pantalla de detalle y se envia la informacion por medio de un extra
        val intentDetalle = Intent(this, DetailImageActivity::class.java)
        intentDetalle.putExtra("imgid",ARid[position])
        intentDetalle.putExtra("imgurl",ARUrls[position])
        intentDetalle.putExtra("imgtags",ARTags[position])
        intentDetalle.putExtra("imgviews",ARViews[position])
        intentDetalle.putExtra("imglikes",ARLikes[position])
        startActivity(intentDetalle)
    }

    override fun onBackPressed() {
        CerrarSesion()
    }

    override fun onStart() {
        super.onStart()

        //Definicion relative Images y asignacion del recicler view
        val inflaterImg = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val VImg = inflaterImg.inflate(R.layout.ver_recycler, null)
        recyclerImages = VImg.findViewById(R.id.Generic_ReciclerView)
        RelativeLAS = findViewById(R.id.ListImages_RelativeLayout)
        RelativeLAS.addView(VImg)

        //Se asigna el manejador de LinearLayout dentro del recicler para cargar posteriormente los items personalizados en el
        val LinearLayoutManagerImg = LinearLayoutManager(this)
        LinearLayoutManagerImg.orientation = LinearLayoutManager.VERTICAL
        recyclerImages.layoutManager = LinearLayoutManagerImg

        //Se carga la informacion de los Spinner
        LoadSpiners()

        //Se Asignan los eventos OnClick Segun Corresponden
        Buscar_button.setOnClickListener() {
            DescargarImages()
        }

        CerrarSesion_imagebutton.setOnClickListener() {
            CerrarSesion()
        }
    }

    fun LoadSpiners() {
        val spinnerCategories: Spinner = findViewById(R.id.Category_spinner)
        // Crea un ArrayAdapter usando el recurso de categories.xml y un default spinner layout
        ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategories.adapter = adapter
        }
        val spinnerResult: Spinner = findViewById(R.id.Results_spinner)
        // Crea un ArrayAdapter usando el recurso de results.xml y un default spinner layout
        ArrayAdapter.createFromResource(
                this,
                R.array.results_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerResult.adapter = adapter
        }
    }

    fun DescargarImages() {
        //Se verifica que haya conexion , si el resultado es igual a true se ejecuta la peticion al servidor
        if(Consulta_textView.text.length>100){
            Toast.makeText(baseContext, "El texto a buscar no puede Exceder los 100 caracteres, Longitud Actual : " + Consulta_textView.text.length, Toast.LENGTH_SHORT).show()
            return
        }
        val connex = VerificarConex.revisarconexion(baseContext)
        if (connex) {
            try {
                HttpRequestTask(
                        HttpRequest(ObtenerURL(), HttpRequest.GET, null),
                        object : HttpRequest.Handler {
                            override fun response(response: HttpResponse) {
                                //Si la peticion es positiva se verifica si contiene informacion o no
                                //Si contiene informacion se envia el Response a la funcion CargarImagenes
                                if (response.body !== "" && response.body != null) {
                                    Log.d(this.javaClass.toString(), "Request successful!")
                                    CargarImages(response.body)
                                } else {
                                    Toast.makeText(baseContext, "No hay Imagenes para mostrar ", Toast.LENGTH_SHORT).show()
                                    Log.d(this.javaClass.toString(), "Request unsuccessful: $response")
                                }
                            }
                        }).execute()
            } catch (e: JSONException) {
                Toast.makeText(baseContext, "Error al descargar las imagenes : " + e.message, Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            Toast.makeText(baseContext, "No hay conexion, revisela a intente nuevamente. ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ObtenerURL(): String {
        // Se valida que parametros se envian en la url ty se retorna la cadena completa para realizar la peticion HTTP
        var urlfull = ConstantRestApi.ROOT_URL + ConstantRestApi.API_KEY

        if (Consulta_textView.text.toString().length > 0) {
            urlfull += ConstantRestApi.KEY_GET_Q + Consulta_textView.text.toString().replace(' ', '+')
        }
        if (Category_spinner.selectedItemPosition > 1) {
            urlfull += ConstantRestApi.KEY_GET_CATEGORY + Category_spinner.selectedItem.toString()
        }
        urlfull += ConstantRestApi.KEY_GET_PER_PAGE + Results_spinner.selectedItem.toString()

        return urlfull
    }

    fun CargarImages(ResponseSTR: String) {
        //Se Limpian los valores previos de la lista
        ListImg = ArrayList()
        //Se pasa el valor de la respuesta HTTP por el Gson Parser para convertirlo en un Objeto de tipo ResponseList
        val gson = Gson()
        val objGson = gson.fromJson(ResponseSTR, ResponseList::class.java)
        //Se valida si hay items para mostrar y se rellenan los ArrayList con la informacion del Objeto Json
        if (objGson.totalHits.toInt() > 0) {
            var i = 0
            while (i < objGson.hits!!.count()) {
                ARid.add(objGson.hits?.get(i)?.id.toString())
                ARDownloads.add(objGson.hits?.get(i)?.downloads.toString())
                ARUser.add(objGson.hits?.get(i)?.user.toString())
                ARUrls.add(objGson.hits?.get(i)?.previewURL.toString())
                ARTags.add(objGson.hits?.get(i)?.tags.toString())
                ARViews.add(objGson.hits?.get(i)?.views.toString())
                ARLikes.add(objGson.hits?.get(i)?.likes.toString())
                //Se envia la informacion al modelo (ID,Download,usuario,UrlPreview) y este se añade a la Lista
                val modelAC = ImageModel(ARid[i], ARDownloads[i], ARUser[i], ARUrls[i])
                ListImg.add(modelAC)
                i++
            }
            //Al finalizar de recorrer toda la lista, se carga dentro de ImageAdapter y esta se asigna al recylcer como adapter
            val maAC = ImageAdapter(ListImg)
            recyclerImages.adapter = maAC
        } else {
            val maAC = ImageAdapter(ListImg)
            recyclerImages.adapter = maAC
            Toast.makeText(baseContext, "La busqueda no arrojo ningun resultado. ", Toast.LENGTH_SHORT).show()
        }
    }

    fun CerrarSesion() {

        //Se envian los parametros para crear alerta personalizada, (cerrar la sesion del usuario)
        val mapPop = HashMap<String, String>()
        mapPop.put("Titulo", "Cerrar Sesion")
        mapPop.put("Mensaje", "Desea Cerrar Sesion:")
        mapPop.put("TextoBTNo", "Cancelar")
        mapPop.put("TextoBTSi", "Confirmar")
        mapPop.put("Clase", "MainActivity")
        val prefs = getSharedPreferences(getString(R.string.Prefs_File),Context.MODE_PRIVATE)
        PopUps().popUpConfirmar(baseContext, getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, mapPop,prefs)
    }

    companion object {
        var instance: ListImagesActivity? = null
    }
}
