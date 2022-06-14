package com.example.segundoevalucacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.segundoevalucacion.adapter.MensajeAdapter
import com.example.segundoevalucacion.databinding.ActivityChatBinding
import com.example.segundoevalucacion.modelo.Mensaje
import com.example.segundoevalucacion.notificacion.AppConstants
import com.example.segundoevalucacion.notificacion.NotificacionModel
import com.example.segundoevalucacion.notificacion.PushNotification
import com.example.segundoevalucacion.notificacion.RetrofitInstante
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson

import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
/**
 * Chat activity
 *
 * @constructor Create empty Chat activity
 */
class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    lateinit var messageAdapter: MensajeAdapter
    lateinit var messageList:ArrayList<Mensaje>
    lateinit var reference : DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    var receiverRoom: String=""
    var senderRoom: String=""
    var senderUid =""
    var receiverUid =""
    var mensajeId =""
    var topic = ""
    var nombre =""

    private var hisId: String? = null
    private var chatId: String? = null
    private var myName: String? = null

    lateinit var storageReference: StorageReference
    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                if (it.data!=null){
                    var uri: Uri? = it.data!!.data
                    val time = System.currentTimeMillis()
                    var imagenref=storageReference.child("Mensaje").child(time.toString())
                    subirImagen(imagenref, uri)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setSupportActionBar(binding.toolbar)
        storageReference= Firebase.storage.reference
        val bundle =intent.extras
        nombre = bundle?.getString("NOMBRE").toString()
        receiverUid = bundle?.getString("UID").toString()
        //token = bundle?.getString("TOKEN").toString()

        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE)
        chatId = bundle?.getString("chatId").toString()
        hisId = bundle?.getString("hisId").toString()


        val img = bundle?.getString("IMG").toString()
        Log.d("uid>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", chatId+"***0"+hisId)
        Picasso.get()
            .load(img)
            .into(binding.imgPerfil2)

        binding.txtContactor.text= nombre

        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference = FirebaseDatabase.getInstance().getReference()
        senderRoom = receiverUid+senderUid
        receiverRoom = senderUid+receiverUid

        messageList = ArrayList()
        messageAdapter = MensajeAdapter(this,messageList,senderRoom,receiverRoom)

        binding.recycle.layoutManager = LinearLayoutManager(this)

        binding.recycle.adapter = messageAdapter

        reference.child("chat").child(senderRoom).child("mensaje").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (sna in snapshot.children){

                    val message = sna.getValue<Mensaje>(Mensaje::class.java)
                    message?.mensajeId = sna.key.toString()
                    messageList.add(message!!)

                    mensajeId = sna.key.toString()
                }

                binding.recycle.scrollToPosition(messageList.size-1)
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        presencia()
        setUp()
        estado()


    }

    private fun estado() {
        val handler = Handler()
        binding.txtMensaje.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                reference.child("presence").child(senderUid).setValue("Escribiendo...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping,1000)
            }
            var userStoppedTyping = Runnable {
                reference.child("presence").child(senderUid).setValue("Online")
            }


        })
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun presencia() {
        reference.child("presence").child(receiverUid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val estado = snapshot.getValue<String>(String::class.java)
                    if(estado=="Offline"){
                        binding.txtPresencia.visibility= View.GONE
                    }else{
                        binding.txtPresencia.setText(estado)
                        binding.txtPresencia.visibility=View.VISIBLE
                    }
                    //Log.d("ESTADO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><", estado!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun setUp() {
        binding.ivSend.setOnClickListener {
            enviarMensaje()

        }
        binding.imgSubir.setOnClickListener {
            enviarImagen()
        }
        binding.btnVolver5.setOnClickListener {
            onBackPressed()
        }


    }

    private fun enviarImagen() {
        var i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.setType("image/*")
        responseLauncher.launch(i)


    }

    private fun subirImagen(imagen: StorageReference, ruta: Uri?) {
        imagen.putFile(ruta!!).addOnCompleteListener{task ->
            if(task.isSuccessful){
                imagen.downloadUrl.addOnSuccessListener {it->
                    val file = it.toString()
                    val messaje = "Has subido una imagen"
                    val time = System.currentTimeMillis()

                    val messageObject = Mensaje(messaje, senderUid!!, time)
                    messageObject.imagen = file
                    messageObject.aux=false

                    val lastMsj = HashMap<String,Any>()
                    lastMsj["lastMsg"] = messageObject.mensaje
                    lastMsj["lastMsgTime"] = messageObject.fecha

                    reference.child("chat").updateChildren(lastMsj)
                    reference.child("chat").child(senderRoom).updateChildren(lastMsj)

                    reference.child("chat").child(senderRoom).child("mensaje").push()
                        .setValue(messageObject).addOnSuccessListener {
                            reference.child("chat").child(receiverRoom).child("mensaje").push()
                                .setValue(messageObject).addOnSuccessListener {
                                }
                        }
                    getToken(messaje)
                    binding.txtMensaje.setText("")

                }
            }

        }
    }


    private fun enviarMensaje() {
        val messaje = binding.txtMensaje.text.toString()
        val time = System.currentTimeMillis()
        val messageObject = Mensaje(messaje, senderUid!!, time)
        messageObject.aux=false

        val lastMsj = HashMap<String,Any>()
        lastMsj["lastMsg"] = messageObject.mensaje
        lastMsj["lastMsgTime"] = messageObject.fecha

        reference.child("chat").updateChildren(lastMsj)
        reference.child("chat").child(senderRoom).updateChildren(lastMsj)

        reference.child("chat").child(senderRoom).child("mensaje").push()
            .setValue(messageObject).addOnSuccessListener {
                reference.child("chat").child(receiverRoom).child("mensaje").push()
                    .setValue(messageObject).addOnSuccessListener {

                    }
            }

        /* topic = "/tipocs/$receiverUid"
         FirebaseMessaging.getInstance().subscribeToTopic(topic)
         PushNotification(NotificacionModel( nombre,messaje),
             token).also {
             sendNotification(it)
         }*/
        topic = "/tipocs"
        FirebaseMessaging.getInstance().subscribeToTopic( "/topics/news")
        getToken(messaje)
        // Log.d("TOKEN>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",messaje)
        binding.txtMensaje.setText("")


    }

    private fun getToken(message: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("usuarioPerfil").child(receiverUid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val token = snapshot.child("token").value.toString()


                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("hisId", receiverUid)
                    data.put("title", nombre)
                    data.put("message", message)
                    data.put("chatId", senderRoom)

                    to.put("to", token)
                    to.put("data", data)
                    sendNotification(to)
                    Log.d("TOKEN>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>",hisId + " **"+
                            nombre+"***"+message+"***"+chatId+"***"+token)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendNotification(to: JSONObject) {

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            AppConstants.BASE_URL,
            to,
            Response.Listener { response: JSONObject ->

                Log.d("TAG>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", "onResponse: $response")
            },
            Response.ErrorListener {

                Log.d("TAG", "onError: $it")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)

    }
}