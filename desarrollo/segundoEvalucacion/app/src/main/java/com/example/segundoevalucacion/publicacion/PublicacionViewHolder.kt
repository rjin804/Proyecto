package com.example.segundoevalucacion.publicacion

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.segundoevalucacion.ComentarioActivity
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.UpdateActivity
import com.example.segundoevalucacion.databinding.AnimalLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
/**
 * Publicacion view holder
 *
 * @constructor
 *
 * @param v
 */
class PublicacionViewHolder(v: View): RecyclerView.ViewHolder(v) {

    private val binding = AnimalLayoutBinding.bind(v)
    lateinit var db: FirebaseDatabase

    public fun render(pub:Publicacion, c:Context){
        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")

        binding.txtTitulo.text = pub.titulo
        binding.txtDescripcion.text=pub.descripcion
        binding.txtFecha1.text = convertirFecha(pub.fecha)

        if(pub.autor.equals("")){
            binding.txtAutor.text = pub.email
        }else{
            binding.txtAutor.text = pub.autor
        }
        if(!pub.img.equals("")){
            Picasso.get().load(pub.img).resize(150,150).centerCrop().into(binding.imgView)
            binding.imgView.visibility = View.VISIBLE
        }

        if (pub.imgPerfil.equals("")){
            pub.imgPerfil="https://firebasestorage.googleapis.com/v0/b/segundoevalucion.appspot.com/o/User%2Fperfil.png?alt=media&token=b0e4fd1f-04b6-4fb3-9fa5-848a8fb4b88d"

        }
        binding.imgNolike.setOnClickListener {
            binding.imgNolike.visibility=View.INVISIBLE
            binding.imgLike.visibility=View.VISIBLE
            db.getReference("like").child(FirebaseAuth.getInstance().uid!!).child(pub.id).setValue(pub)

        }

        binding.imgLike.setOnClickListener {
            binding.imgLike.visibility=View.INVISIBLE
            binding.imgNolike.visibility=View.VISIBLE
            db.getReference("like").child(FirebaseAuth.getInstance().uid!!).child(pub.id).removeValue()

        }

        Picasso.get().load(pub.imgPerfil).resize(150,150).centerCrop().into(binding.imgAutor)

        binding.cardView.setOnClickListener {
            val i = Intent(c, UpdateActivity::class.java).apply {
                putExtra("TITULO", pub.titulo)
                putExtra("DESCRIPCION",pub.descripcion)
                putExtra("AUTOR",pub.autor)
                putExtra("EMAIL",pub.email)
                putExtra("ID",pub.id)
                putExtra("IMG",pub.img)
                putExtra("IMGPERFIL",pub.imgPerfil)
                putExtra("UID",pub.uid)
            }
            c.startActivity(i)
        }
        binding.imgComentario.setOnClickListener {
            val i = Intent(c, ComentarioActivity::class.java).apply {
                putExtra("IMGPERFIL",pub.imgPerfil)
                putExtra("UID",pub.uid)
                putExtra("ID",pub.id)
            }
            c.startActivity(i)
        }

    }

    private fun convertirFecha(f : Long): String{
        val date = Date(f)
        val format = SimpleDateFormat("HH:mm dd/mm/yyyy")
        return format.format(date)
    }


}