package com.example.segundoevalucacion.perfil

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.segundoevalucacion.ChatActivity
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.databinding.UserLayoutBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.lang.String
/**
 * Perfil adapter
 *
 * @property user
 * @constructor Create empty Perfil adapter
 */
class PerfilAdapter (var user:ArrayList<Perfil>):RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder>(){
    lateinit var context: Context
    //lateinit var reference : DatabaseReference
    class PerfilViewHolder(v: View): RecyclerView.ViewHolder(v)  {
        val  binding= UserLayoutBinding.bind(v)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerfilViewHolder {
        context=parent.context
        val layoutInflater= LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(R.layout.user_layout, parent, false)
        return PerfilViewHolder(v)
    }

    override fun onBindViewHolder(holder: PerfilViewHolder, position: Int) {
        val usuario = user[position]
        if(usuario.nombre.equals("")){
            holder.binding.txtNom.text = usuario.email
        }else{
            holder.binding.txtNom.text=usuario.nombre
        }

        if (usuario.img.equals(""))
            usuario.img="https://firebasestorage.googleapis.com/v0/b/segundoevalucion.appspot.com/o/User%2Fperfil.png?alt=media&token=b0e4fd1f-04b6-4fb3-9fa5-848a8fb4b88d"
        Picasso.get().load(usuario.img).resize(150,150).centerCrop().into(holder.binding.imgPerfil1)
        //var estado =""

        /* FirebaseDatabase.getInstance().reference.child("presence").child(usuario.uid!!).get().addOnCompleteListener(object:
             OnCompleteListener<DataSnapshot> {
             override fun onComplete(p0: Task<DataSnapshot>) {
                 if (p0.isSuccessful){
                     if(p0.getResult().exists()){
                         val dataSnapshot = p0.getResult()
                         holder.binding.txtEstado.text = String.valueOf(dataSnapshot.value)
                         estado = String.valueOf(dataSnapshot.value)
                         if(estado.equals("Offline")){
                             holder.binding.txtEstado.visibility = View.INVISIBLE
                         }

                     }
                 }
             }

         })*/


        val receiverRoom = usuario.uid + FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseDatabase.getInstance().reference.child("chat").child(receiverRoom).get().addOnCompleteListener(object:
            OnCompleteListener<DataSnapshot> {
            override fun onComplete(p0: Task<DataSnapshot>) {
                if (p0.isSuccessful){
                    if(p0.getResult().exists()){
                        val dataSnapshot = p0.getResult()
                        holder.binding.txtMensaje1.text = String.valueOf(dataSnapshot.child("lastMsg").value)
                        if(String.valueOf(dataSnapshot.child("lastMsg").value)==null){
                            holder.binding.txtMensaje1.visibility = View.INVISIBLE
                        }

                    }
                }
            }

        })


        holder.binding.cardView2.setOnClickListener {
            val i = Intent(context, ChatActivity::class.java).apply {
                putExtra("NOMBRE",usuario.nombre)
                //putExtra("EMAIL",usuario.email)
                putExtra("IMG",usuario.img)
                putExtra("UID",usuario.uid)
            }
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return user.count()
    }
}