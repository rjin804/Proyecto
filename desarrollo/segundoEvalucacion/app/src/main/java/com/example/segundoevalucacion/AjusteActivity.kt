package com.example.segundoevalucacion

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.MediaController
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.segundoevalucacion.databinding.ActivityAjusteBinding
import com.example.segundoevalucacion.fragment.*
import com.example.segundoevalucacion.interfaz.Brillo
import com.example.segundoevalucacion.interfaz.OnFragmentsActionListener
import com.example.segundoevalucacion.interfaz.Sonido
import com.example.segundoevalucacion.interfaz.Video
import java.lang.Exception

/**
 * Ajuste activity
 *
 * @constructor Create empty Ajuste activity
 */
class AjusteActivity : AppCompatActivity(),OnFragmentsActionListener,Brillo, Sonido,Video {
    lateinit var binding: ActivityAjusteBinding

    var posicion= 0
    var boton = 0
    var idCamara = ""
    var camara: CameraManager? =null
    val fragments = arrayListOf<Fragment>(
        SonidoFragment(),
        BrilloFragment(),
        VideoFragment(),
        WebFragment()
    )
    lateinit var mediaControler: MediaController
    @TargetApi(21)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAjusteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaControler= MediaController(this)
        title="Ajuste"

    }

    private fun permiso(c: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(c)) {

            } else {
                var i = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                c.startActivity(i)
            }
        }
    }

    override fun onClickBotonMenu(btn: Int) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.contenedor_fragments,fragments[btn])

        transaction.addToBackStack(null)
        transaction.commit()


    }



    override fun brillo(ac: Boolean) {
        permiso(this)
        val barra: SeekBar = binding.seekBar
        barra.max=255
        barra.min=1
        if(ac){
            barra.isVisible=true
            barra.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    android.provider.Settings.System.putInt(contentResolver,Settings.System.SCREEN_BRIGHTNESS,p1)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })

        }else{
            barra.isVisible=false
        }
    }

    override fun sonido(ac: Boolean) {
        val barra: SeekBar = binding.seekBar
        val audio: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        barra.min=0
        barra.max=audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        if(ac){
            barra.isVisible=true
            barra.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC,p1,0)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })

        }else{
            barra.isVisible=false
        }

    }

    override fun video(ac: Boolean) {
        if (ac) {
            binding.videoView.isVisible=true
            var rutaVideo = "android.resource://"+packageName+"/"+R.raw.video
            Log.d("RUTA DEL VIDEO->>>>>>>>",rutaVideo)
            val uri = Uri.parse(rutaVideo)
            try {
                binding.videoView.setVideoURI(uri)
                binding.videoView.requestFocus()
            }catch (ex:Exception){
                Log.e("ERROR->>>>>>",ex.message.toString())
            }
            //le ponemos el control para el video parte 2
            binding.videoView.setMediaController(mediaControler)
            mediaControler.setAnchorView(binding.videoView)

            //el video esta cargado lo iniciamos
            //para la parte 3
            if (posicion==0){
                binding.videoView.start()
            }else{
                binding.videoView.pause()
            }
        }else{
            binding.videoView.isVisible=false
        }
    }

}
