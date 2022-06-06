package com.example.segundoevalucacion.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.databinding.DeleteLayoutBinding
import com.example.segundoevalucacion.databinding.RecibeLayoutBinding
import com.example.segundoevalucacion.databinding.SendLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class MensajeAdapter(var context:Context, messages: ArrayList<Mensaje>?, senderRoom:String, receiverRoom:String)
    :RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    lateinit var messages: ArrayList<Mensaje>
    val ITEM_SENT=1
    val ITEM_RECIBE = 2
    var senderRoom:String
    var receiverRoom:String


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if(viewType==ITEM_SENT){
            val v = LayoutInflater.from(context).inflate(R.layout.send_layout,parent,false)
            SentMsgHolder(v)
        }else{
            val v = LayoutInflater.from(context).inflate(R.layout.recibe_layout,parent,false)
            ReceiveMsgHolder(v)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messages = messages[position]
        return if(FirebaseAuth.getInstance().uid == messages.sendId){
            ITEM_SENT
        }else{
            ITEM_RECIBE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messages= messages[position]
        if(holder.javaClass == SentMsgHolder::class.java){
            val view = holder as SentMsgHolder
            if (messages.msj.equals("foto")){
                view.binding.imageSend.visibility=View.VISIBLE
                Picasso.get().load(messages.img).resize(150,150).centerCrop().into(view.binding.imageSend)

            }
            view.binding.txtSend.text = messages.msj

            //view.binding.txtFecha3.text = convertirFecha(messages.fecha)

            view.itemView.setOnLongClickListener {

                val v = LayoutInflater.from(context).inflate(R.layout.delete_layout,null)
                val binding:DeleteLayoutBinding = DeleteLayoutBinding.bind(v)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Eliminar Mensaje")
                    .setView(binding.root)
                    .create()

                binding.txtEliminar.setOnClickListener {
                    messages.msj = "Este mensaje ha sido eliminado"
                    messages.mensajeId.let { it->
                        FirebaseDatabase.getInstance().reference.child("mensajesChat")
                            .child(senderRoom)
                            .child("mensaje")
                            .child(it).setValue(messages)
                    }
                    messages.mensajeId.let { it1->
                        FirebaseDatabase.getInstance().reference.child("mensajesChat")
                            .child(receiverRoom)
                            .child("mensaje")
                            .child(it1).setValue(messages)
                    }
                    dialog.dismiss()
                }

                binding.txtEliminarmi.setOnClickListener {

                    messages.mensajeId.let { it2->
                        FirebaseDatabase.getInstance().reference.child("mensajesChat")
                            .child(senderRoom)
                            .child("mensaje")
                            .child(it2).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.txtCancelar.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                false
            }

            }else{
                val view = holder as ReceiveMsgHolder
            if (messages.msj.equals("foto")){
                view.binding.imageRecibe.visibility=View.VISIBLE
                Picasso.get().load(messages.img).resize(150,150).centerCrop().into(view.binding.imageRecibe)

            }
            view.binding.txtRecibe.text = messages.msj
            //view.binding.txtFecha2.text=convertirFecha(messages.fecha)
            view.itemView.setOnLongClickListener {

                val v = LayoutInflater.from(context).inflate(R.layout.delete_layout,null)
                val binding:DeleteLayoutBinding = DeleteLayoutBinding.bind(v)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Eliminar Mensaje")
                    .setView(binding.root)
                    .create()

                binding.txtEliminar.setOnClickListener {
                    messages.msj = "Este mensaje ha sido eliminado"
                    messages.mensajeId.let { it->
                        FirebaseDatabase.getInstance().reference.child("mensajesChat")
                            .child(senderRoom)
                            .child("mensaje")
                            .child(it).setValue(messages)
                    }
                    messages.mensajeId.let { it1->
                        FirebaseDatabase.getInstance().reference.child("mensajesChat")
                            .child(receiverRoom)
                            .child("mensaje")
                            .child(it1).setValue(messages)
                    }
                    dialog.dismiss()
                }

                binding.txtEliminarmi.setOnClickListener {

                    messages.mensajeId.let { it2->
                        FirebaseDatabase.getInstance().reference.child("mensajesChat")
                            .child(senderRoom)
                            .child("mensaje")
                            .child(it2).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.txtCancelar.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                false
            }

        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
    class SentMsgHolder(v:View):RecyclerView.ViewHolder(v){
        var binding:SendLayoutBinding = SendLayoutBinding.bind(v)
    }
    class ReceiveMsgHolder(v:View):RecyclerView.ViewHolder(v){
        var binding:RecibeLayoutBinding = RecibeLayoutBinding.bind(v)
    }


    init {
        if (messages != null){
            this.messages = messages
        }
        this.senderRoom=senderRoom
        this.receiverRoom=receiverRoom

    }
    private fun convertirFecha(f : Long): String{
        val date = Date(f)
        val format = SimpleDateFormat("HH:mm dd/mm/yyyy")
        return format.format(date)
    }



}