package com.example.segundoevalucacion.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.interfaz.Brillo



class BrilloFragment : Fragment() {

    var encender = true
    private var bri:Brillo?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_brillo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boton:ImageView = view.findViewById(R.id.img_brillo)


        boton.setOnClickListener {
            if (encender){

                bri?.brillo(true)
                encender=false
            }else{

                 bri?.brillo(false)
                 encender=true

            }

        }

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Brillo) bri=context
    }

    override fun onDetach() {
        super.onDetach()
        bri=null
    }


    companion object {

        fun newInstance():BrilloFragment{
            return BrilloFragment()
        }
    }



}