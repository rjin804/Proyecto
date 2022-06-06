package com.example.segundoevalucacion.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.UpdateActivity
import com.example.segundoevalucacion.databinding.AnimalLayoutBinding
import com.example.segundoevalucacion.model.Animal
import com.example.segundoevalucacion.viewModel.MainViewModel
import com.squareup.picasso.Picasso


class AnimalAdapter: RecyclerView.Adapter<AnimalAdapter.AnimalHolder>(){
    lateinit var context: Context
    private var lista = emptyList<Animal>()
    private lateinit var viewModel: MainViewModel
    class AnimalHolder (v: View): RecyclerView.ViewHolder(v)  {
        val  binding=AnimalLayoutBinding.bind(v)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalHolder {
        context=parent.context
        val layoutInflater= LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(R.layout.animal_layout, parent, false)
        return AnimalHolder(v)
    }

    override fun onBindViewHolder(holder: AnimalHolder, position: Int) {
        val elemento = lista[position]
        holder.binding.txtAutor.text=elemento.autor
        holder.binding.txtDescripcion.text=elemento.descripcion
        holder.binding.txtTitulo.text=elemento.titulo

        Picasso.get()
            .load(elemento.imagen)
            .resize(100, 100) //tamaño
            .centerCrop()//centrar
            .into(holder.binding.imgView)


        holder.binding.cardView.setOnClickListener {
            val i = Intent(context, UpdateActivity::class.java).apply {
                putExtra("ID",elemento.id)
                putExtra("TITULO",elemento.titulo)
                putExtra("AUTOR", elemento.autor)
                putExtra("DESCRIPCION", elemento.descripcion)
            }
            context.startActivity(i)
        }

    }

    override fun getItemCount(): Int {
        return lista.count()
    }

    fun setData(dato: List<Animal>){
        this.lista=dato
        notifyDataSetChanged()
    }



}


