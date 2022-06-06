package com.example.segundoevalucacion.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import com.example.segundoevalucacion.R
import com.example.segundoevalucacion.interfaz.Sonido
import com.example.segundoevalucacion.interfaz.Video
import java.lang.Exception


class VideoFragment : Fragment() {

    var enceder=true
    private var vi: Video?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val boton: Button = view.findViewById(R.id.btn_empiezar)
        boton.setOnClickListener {
            if(enceder){

                vi?.video(true)
                enceder=false

            }else{
                vi?.video(false)
                enceder=true
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Video) vi=context
    }

    override fun onDetach() {
        super.onDetach()
        vi=null
    }

    companion object {
        fun newInstance(): VideoFragment {
            return VideoFragment()
        }
    }
}