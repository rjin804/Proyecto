package com.example.segundoevalucacion.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.publicacion.Publicacion
import com.example.segundoevalucacion.publicacion.PublicacionAdapter
import com.google.firebase.database.*


class HomeFragment : Fragment() {
    private lateinit var db: FirebaseDatabase
    private lateinit var reference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var recycler:RecyclerView = view.findViewById(R.id.recycler)

        initDB()
        val postListener =  object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista : ArrayList<Publicacion> = ArrayList()
                for (data in snapshot.children){
                    val datosMensaje = data.getValue<Publicacion>(Publicacion::class.java)
                    if (datosMensaje!= null){
                        lista.add(datosMensaje)
                    }else continue
                }
                //Ordeno esta lista por marca de tiempo
                lista.sortBy { mensaje -> mensaje.fecha }
                //rellenarLayout(lista)
                recycler.adapter = PublicacionAdapter(lista)
                recycler.scrollToPosition(lista.size-1)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error:>>>>>>", error.message.toString())
            }

        }
        reference.addValueEventListener(postListener)
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }


    private fun initDB() {
        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")
        reference = db.getReference("publicacion")
    }

}