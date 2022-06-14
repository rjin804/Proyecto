package com.example.segundoevalucacion.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.interfaz.Sonido

/**
 * Sonido fragment
 *
 * @constructor Create empty Sonido fragment
 */
class SonidoFragment : Fragment() {

    var enceder = true
    private var son : Sonido?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sonido, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val boton: ImageView = view.findViewById(R.id.img_sonido)

        boton.setOnClickListener {
            if(enceder){

                son?.sonido(true)
                enceder=false

            }else{
                son?.sonido(true)
                enceder=true
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Sonido) son=context
    }

    override fun onDetach() {
        super.onDetach()
        son=null
    }


    companion object {

        fun newInstance():SonidoFragment {
            return SonidoFragment()
        }
    }
}