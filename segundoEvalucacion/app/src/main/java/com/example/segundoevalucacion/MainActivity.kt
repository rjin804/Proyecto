package com.example.segundoevalucacion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.segundoevalucacion.databinding.ActivityMainBinding
import com.example.segundoevalucacion.preferencia.Prefs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private val reponseLauncher=
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode== RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val cuenta = task.getResult(ApiException::class.java)
                    if(cuenta!=null){
                        val credenciales = GoogleAuthProvider.getCredential(cuenta.idToken,null)
                        FirebaseAuth.getInstance().signInWithCredential(credenciales).addOnCompleteListener {
                            if(it.isSuccessful){

                                irApp(cuenta.email?:"")
                            }else{
                                Toast.makeText(this,"ERROR DE LOGIN", Toast.LENGTH_SHORT)
                            }
                        }
                    }
                }catch (e: ApiException){
                    Log.d("ERROR >>>>>>>>>>>>>>>>>>>>>>",e.message.toString())
                }
            }
        }


    lateinit var binding: ActivityMainBinding
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Registrarse"

        setUp()
        comprobarSesion()
    }
    private fun comprobarSesion() {
        prefs= Prefs(this)
        val email=prefs.leerEmail()
        if (!email.isNullOrEmpty())
            irApp(email)
    }

    private fun setUp() {
        binding.btnLogin.setOnClickListener{
            iniciarSesion()
        }
    }

    private fun iniciarSesion() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("313865763898-pflnf35ojhra6e55v6i5klag9i04ss5s.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(this, gso)
        googleClient.signOut()
        reponseLauncher.launch(googleClient.signInIntent)

    }
    private fun irApp(s: String) {
        val i = Intent(this, AnimalActivity::class.java).apply {
            putExtra("EMAIL",s)
        }
        startActivity(i)
    }
}