package com.example.segundoevalucacion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.segundoevalucacion.databinding.ActivityAnimalBinding
import com.example.segundoevalucacion.databinding.ActivityLikeBinding
import com.example.segundoevalucacion.preferencia.Prefs
import com.example.segundoevalucacion.publicacion.Publicacion
import com.example.segundoevalucacion.publicacion.PublicacionAdapter
import com.example.segundoevalucacion.viewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LikeActivity : AppCompatActivity() {
    lateinit var binding: ActivityLikeBinding
    private lateinit var viewModel: MainViewModel
    var email=""
    var img =""
    lateinit var prefs: Prefs
    var imagen=""

    private val database = Firebase.database
    var web=""

    private lateinit var db: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikeBinding.inflate(layoutInflater)
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
                    if (datosMensaje!= null ){
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
        reference = db.getReference("like").child(FirebaseAuth.getInstance().uid!!)
    }



    private fun rellenarLayout(lista: ArrayList<Publicacion> ) {
        // val linearLayoutManager = LinearLayoutManager(this)
        // binding.recycler.layoutManager = linearLayoutManager


        binding.recyclerLike.adapter = PublicacionAdapter(lista)
        binding.recyclerLike.scrollToPosition(lista.size-1)




    }
}