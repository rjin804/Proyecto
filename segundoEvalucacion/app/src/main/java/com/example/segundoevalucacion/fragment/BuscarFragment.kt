package com.example.segundoevalucacion.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.publicacion.Publicacion
import com.example.segundoevalucacion.publicacion.PublicacionAdapter
import com.google.firebase.database.*


class BuscarFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_buscar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val seach: SearchView = view.findViewById(R.id.search1_view)
        var recycler: RecyclerView = view.findViewById(R.id.recycler_buscar)
        initDB()

        seach.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                ponerListener(recycler,p0)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                return true
            }

        })
    }

    private fun ponerListener(recycler: RecyclerView, p0: String?) {
        val postListener =  object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista : ArrayList<Publicacion> = ArrayList()
                //var lista1 : ArrayList<Publicacion> = ArrayList()
                for (data in snapshot.children){
                    val datosMensaje = data.getValue<Publicacion>(Publicacion::class.java)
                    if (datosMensaje!= null && (datosMensaje.titulo.equals(p0) || datosMensaje.autor.equals(p0))){
                        lista.add(datosMensaje)
                    }else continue
                }
                if(lista.size==0){
                    Toast.makeText(context, "No existe con ese titulo o autor", Toast.LENGTH_LONG).show()
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
        fun newInstance(): BuscarFragment {
            return BuscarFragment()
        }
    }
    private fun initDB() {
        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")
        reference = db.getReference("publicacion")
    }
}