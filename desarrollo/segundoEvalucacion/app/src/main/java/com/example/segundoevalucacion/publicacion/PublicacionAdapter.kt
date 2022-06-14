package com.example.segundoevalucacion.publicacion

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.segundoevalucacion.AnimalActivity
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.UpdateActivity
import com.example.segundoevalucacion.databinding.ActivityAddBinding.bind
import com.example.segundoevalucacion.databinding.AnimalLayoutBinding
/**
 * Publicacion adapter
 *
 * @property lista
 * @constructor Create empty Publicacion adapter
 */
class PublicacionAdapter (private val lista:ArrayList<Publicacion>):
    RecyclerView.Adapter<PublicacionViewHolder>() {
    lateinit var context: Context



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        context=parent.context
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.animal_layout, parent, false)
        return PublicacionViewHolder(v)
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val pub = lista[position]
        holder.render(pub,context)


    }

    override fun getItemCount(): Int {
        return lista.size
    }
}