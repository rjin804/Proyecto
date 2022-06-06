package com.example.segundoevalucacion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.segundoevalucacion.databinding.ActivityAnimalBinding
import com.example.segundoevalucacion.databinding.ActivityMiPublicacionBinding
import com.example.segundoevalucacion.preferencia.Prefs
import com.example.segundoevalucacion.publicacion.Publicacion
import com.example.segundoevalucacion.publicacion.PublicacionAdapter
import com.example.segundoevalucacion.viewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class MiPublicacionActivity : AppCompatActivity() {
    lateinit var binding: ActivityMiPublicacionBinding


    private lateinit var viewModel: MainViewModel
    var email=""
    var img =""
    var imagen=""

    private lateinit var db: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMiPublicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initDB()
        ponerListenerDB()
    }
    private fun ponerListenerDB() {
        val postListener =  object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista : ArrayList<Publicacion> = ArrayList()
                for (data in snapshot.children){
                    val datosMensaje = data.getValue<Publicacion>(Publicacion::class.java)
                    if (datosMensaje!= null && datosMensaje.uid.equals(FirebaseAuth.getInstance().uid)){
                        lista.add(datosMensaje)
                    }else continue
                }
                //Ordeno esta lista por marca de tiempo
                lista.sortBy { mensaje -> mensaje.fecha }
                rellenarLayout(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error:>>>>>>", error.message.toString())
            }

        }
        reference.addValueEventListener(postListener)
    }

    private fun initDB() {
        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")
        reference = db.getReference("publicacion")
    }



    private fun rellenarLayout(lista: ArrayList<Publicacion> ) {
        // val linearLayoutManager = LinearLayoutManager(this)
        // binding.recycler.layoutManager = linearLayoutManager


        binding.recycle2.adapter = PublicacionAdapter(lista)
        binding.recycle2.scrollToPosition(lista.size-1)




    }


}