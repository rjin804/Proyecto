package com.example.segundoevalucacion.adapter

/*class MensajeViewHolder(v:View):RecyclerView.ViewHolder(v) {


    private val binding = MensajeLayoutBinding.bind(v)

    public fun render(mensaje: Mensaje){
        binding.txtContenido.text=mensaje.msj
        binding.txt5Nombre.text=mensaje.nombre
        binding.txtFecha.text=convertirFecha(mensaje.fecha)
        if (mensaje.img.equals(""))
            mensaje.img="https://firebasestorage.googleapis.com/v0/b/segundoevalucion.appspot.com/o/User%2Fperfil.png?alt=media&token=b0e4fd1f-04b6-4fb3-9fa5-848a8fb4b88d"
        Picasso.get().load(mensaje.img).resize(150,150).centerCrop().into(binding.img)

        if (mensaje.ac==true && mensaje.url.length!=0 ){
            binding.imgSubida.visibility=View.VISIBLE
            Picasso.get().load(mensaje.url).resize(150,150).centerCrop().into(binding.imgSubida)
            mensaje.ac = false
            mensaje.url=""
        }else{
            binding.imgSubida.visibility=View.GONE

        }
    }

    private fun convertirFecha(f : Long): String{
        val date = Date(f)
        val format = SimpleDateFormat("HH:mm dd/mm/yyyy")
        return format.format(date)
    }


}*/