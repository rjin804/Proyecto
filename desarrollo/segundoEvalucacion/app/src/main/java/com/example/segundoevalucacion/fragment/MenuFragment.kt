package com.example.segundoevalucacion.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.databinding.FragmentMenuBinding
import com.example.segundoevalucacion.interfaz.OnFragmentsActionListener

/**
 * Menu fragment
 *
 * @constructor Create empty Menu fragment
 */
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding?=null
    private val binding get() = _binding!!
    private var miBoton = 500
    private var listener: OnFragmentsActionListener?=null
    private val botonMenu = arrayListOf<Int>(R.id.img2_sonido,R.id.img2_brillo,R.id.img2_play,R.id.img2_busqueda)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var boton: ImageView
        if (arguments!=null){
            miBoton = requireArguments().getInt("BOTON")
        }
        for (i in 0 until botonMenu.size){
            boton = view.findViewById(botonMenu[i])
            if(miBoton==i){
                val num = botonMenu.get(i)
            }

            boton.setOnClickListener {
                listener?.onClickBotonMenu(i)


            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentsActionListener) listener=context
    }

    override fun onDetach() {
        super.onDetach()
        listener=null
    }

    companion object {

        fun newInstance(): MenuFragment {
            return MenuFragment()
        }
    }
}