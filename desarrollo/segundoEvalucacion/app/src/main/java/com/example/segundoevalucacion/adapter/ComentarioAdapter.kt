package com.example.segundoevalucacion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.databinding.DeleteLayoutBinding
import com.example.segundoevalucacion.databinding.RecibeLayoutBinding
import com.example.segundoevalucacion.databinding.SendLayoutBinding
import com.example.segundoevalucacion.modelo.Mensaje
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class ComentarioAdapter (val context: Context, val messageList: ArrayList<Mensaje>,
                         val senderRoom:String, val receiverRoom :String)
    : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    val ITEM_RECEIVE = 1;
    val ITEM_SENT = 2;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            val v = LayoutInflater.from(context).inflate(R.layout.recibe_layout, parent, false)
            return ReceiveViewHolder(v)
        }else{
            val v = LayoutInflater.from(context).inflate(R.layout.send_layout, parent, false)
            return SentViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java) {
            val v = holder as SentViewHolder
            holder.binding.txtSend.text = currentMessage.mensaje
            holder.binding.txtFecha5.text=convertirFecha(currentMessage.fecha)
            if(!currentMessage.imagen.equals("")){
                Picasso.get().load(currentMessage.imagen).resize(150,150).centerCrop().into(holder.binding.imageSend)
                holder.binding.imageSend.visibility= View.VISIBLE
            }else{
                holder.binding.imageSend.visibility= View.GONE
            }
            if (currentMessage.aux==true && !currentMessage.imagenPerfil.equals("")){
                Picasso.get().load(currentMessage.imagenPerfil).into(holder.binding.imgPerfil4)
                holder.binding.imgPerfil4.visibility= View.VISIBLE
            }else{
                holder.binding.imgPerfil4.visibility= View.GONE

            }
        } else {
            val v = holder as ReceiveViewHolder
            holder.binding.txtFecha4.text = convertirFecha(currentMessage.fecha)
            holder.binding.txtRecibe.text = currentMessage.mensaje
            if(!currentMessage.imagen.equals("")){
                Picasso.get().load(currentMessage.imagen).resize(100,100).centerCrop().into(holder.binding.imageRecibe)
                holder.binding.imageRecibe.visibility= View.VISIBLE
            }else{
                holder.binding.imageRecibe.visibility= View.GONE
            }
            if (currentMessage.aux==true && !currentMessage.imagenPerfil.equals("")){
                Picasso.get().load(currentMessage.imagenPerfil).into(holder.binding.imgPerfil3)
                holder.binding.imgPerfil3.visibility= View.VISIBLE
            }else{
                holder.binding.imgPerfil3.visibility= View.GONE
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.sendId)){
            return ITEM_SENT
        }else{
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return  messageList.size
    }

    class  SentViewHolder(v : View): RecyclerView.ViewHolder(v){
        //val sentMessage = v.findViewById<TextView>(R.id.txt_send)
        val binding = SendLayoutBinding.bind(v)
    }
    class  ReceiveViewHolder(v : View): RecyclerView.ViewHolder(v){
        //val receiveMessage = v.findViewById<TextView>(R.id.txt_recibe)
        val binding = RecibeLayoutBinding.bind(v)
    }

    private fun convertirFecha(f : Long): String{
        val date = Date(f)
        val format = java.text.SimpleDateFormat("HH:mm dd/mm/yyyy")
        return format.format(date)
    }
}