package com.example.segundoevalucacion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.segundoevalucacion.databinding.ActivityRegistrarBinding
import com.example.segundoevalucacion.perfil.Perfil
import com.example.segundoevalucacion.preferencia.Prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Registrar activity
 *
 * @constructor Create empty Registrar activity
 */
class RegistrarActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegistrarBinding

    lateinit var prefs: Prefs

    lateinit var db: FirebaseDatabase


    var email =""

    var contrasena=""

    var contrasena1 =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = Prefs(this)
        setUp()
        comprobarSesion()
    }

    private fun comprobarSesion() {
        val e = prefs.leerEmail()

        if(!e.isNullOrEmpty() ){
            irApp(e)
        }
    }
    private fun irApp(s: String) {
        val i = Intent(this, AnimalActivity::class.java).apply {
            putExtra("EMAIL",s)
        }
        startActivity(i)
    }

    private fun setUp() {
        binding.bntRegistrar1.setOnClickListener {
            registrar()
        }

    }

    private fun registrar() {
        if (!cogerDato()) return
        //crea y conctar a firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,contrasena).addOnCompleteListener {
            if (it.isSuccessful){

                irApp(it.result?.user?.email?:"")


            }else{
                Log.d("Error>>>>>>>>>>>>>>>>>>>>",it.exception.toString())
                //dialogo
                mostrarError()
            }
        }

    }
    private fun cogerDato(): Boolean {
        email = binding.edEmail.text.toString().trim()
        contrasena=binding.edContrasena.text.toString().trim()
        contrasena1 = binding.edContrasena1.text.toString().trim()


        if(email.length==0){
            binding.edEmail.setError("Rellena este campo!!!")
            return false
        }
        if (contrasena.length==0){
            binding.edContrasena.setError("Rellena este campo!!!")
            return false
        }
        if (contrasena1.length==0 && contrasena.equals(contrasena1)  ){
            binding.edContrasena1.setError("Rellena este campo!!!")
            return false
        }
        return true
    }

    private fun mostrarError() {
        val alerta = AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Ha producido un error")
            .setPositiveButton("Aceptar",null)
            .create()
            .show()
    }
}