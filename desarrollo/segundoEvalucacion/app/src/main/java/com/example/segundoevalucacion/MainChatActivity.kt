package com.example.segundoevalucacion

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.segundoevalucacion.databinding.ActivityMainChatBinding
import com.example.segundoevalucacion.notificacion.Notificacion
import com.example.segundoevalucacion.perfil.Perfil
import com.example.segundoevalucacion.perfil.PerfilAdapter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.*
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.String

class MainChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainChatBinding
    //lateinit var adapter: PageAdater
    var user:ArrayList<Perfil>?=null
    var userAdater:PerfilAdapter?=null
    val titulo = arrayListOf("Chat","Calls")
    var email =""
    var id =""
    var usuario: Perfil?=null
    lateinit var reference : DatabaseReference
    var img = ""
    var uid=""
    var  token =""

    lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*Notificacion.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            token = task.result
            Notificacion.token = token
            Log.d("token>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><",token)
        })*/

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("usuarioPerfil")
                        .child(FirebaseAuth.getInstance().uid!!)

                val map: MutableMap<kotlin.String, Any> = HashMap()
                map["token"] = token!!
                databaseReference.updateChildren(map)
            }
        })




        initDB()
        cogerDatos()
        recycler("")
        setUp()

    }

    private fun setUp() {
        binding.btnVolver2.setOnClickListener {
            onBackPressed()
        }

        binding.imgGrupo.setOnClickListener {
            val i = Intent(this, GrupoActivity::class.java).apply {
                putExtra("IMG",img)
                putExtra("UID", uid)
                putExtra("TOKEN", token)
            }
            startActivity(i)
        }
        binding.seachContacro.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: kotlin.String?): Boolean {

                return true
            }

            override fun onQueryTextChange(p0: kotlin.String?): Boolean {
                recycler(p0)
                return true
            }

        })
    }



    private fun cogerDatos() {
        val bundle = intent.extras
        email=bundle?.getString("EMAIL").toString()
        img = bundle?.getString("IMG").toString()

    }

    private fun recycler(p0 : kotlin.String?) {
        user = ArrayList<Perfil>()
        userAdater = PerfilAdapter(user!!)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerChat.layoutManager = linearLayoutManager
        binding.recyclerChat.adapter=userAdater

        var userid = FirebaseAuth.getInstance().uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")

        db.reference.child("usuarioPerfil").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                usuario = snapshot.getValue(Perfil::class.java)
                userAdater!!.notifyDataSetChanged()
            }


            override fun onCancelled(error: DatabaseError) {

            }

        })


        db.reference.child("usuarioPerfil").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user!!.clear()
                for (snapshot1 in snapshot.children){
                    val user1:Perfil? = snapshot1.getValue(Perfil::class.java)

                    if (!p0.equals("") && p0!= null){
                        if(!user1!!.uid.equals(FirebaseAuth.getInstance().uid!!) && user1.nombre.equals(p0)){
                            user!!.add(user1)
                        }
                    }else{
                        if(!user1!!.uid.equals(FirebaseAuth.getInstance().uid!!)){
                            user!!.add(user1)
                            uid = user1.uid.toString()

                        }
                    }

                }
                userAdater!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

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