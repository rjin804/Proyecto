package com.example.segundoevalucacion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.segundoevalucacion.databinding.ActivityMainChatBinding
import com.example.segundoevalucacion.perfil.Perfil
import com.example.segundoevalucacion.perfil.PerfilAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainChatBinding
    //lateinit var adapter: PageAdater
    var user:ArrayList<Perfil>?=null
    var userAdater:PerfilAdapter?=null
    val titulo = arrayListOf("Chat","Calls")
    var email =""
    var id =""
    var usuario: Perfil?=null

    lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*binding.toolbar.title="Chat"
        setSupportActionBar(binding.toolbar)

        adapter = PageAdater(this)
        binding.viewPage.adapter=adapter
        TabLayoutMediator(binding.tablayout, binding.viewPage){
            tab,posicion->
            tab.text=titulo[posicion]
        }.attach()*/
        initDB()
        cogerDatos()
        recycler()

    }

    private fun cogerDatos() {
        val bundle = intent.extras
        email=bundle?.getString("EMAIL").toString()

    }

    private fun recycler() {
        user = ArrayList<Perfil>()
        userAdater = PerfilAdapter(user!!)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerChat.layoutManager = linearLayoutManager

        db.reference.child("usuarioPerfil").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                usuario = snapshot.getValue(Perfil::class.java)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        binding.recyclerChat.adapter=userAdater
        db.reference.child("usuarioPerfil").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user!!.clear()
                for (snapshot1 in snapshot.children){
                    val user1:Perfil? = snapshot1.getValue(Perfil::class.java)
                    if(!user1!!.uid.equals(FirebaseAuth.getInstance().uid!!)){
                        user!!.add(user1)
                    }
                }
                userAdater!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun initDB() {

        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        db.reference.child("presence").child(currentId!!).setValue("Online")

    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        db.reference.child("presence").child(currentId!!).setValue("Offline")
    }

}