package com.example.segundoevalucacion

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

/**
 * Maps activity
 *
 * @constructor Create empty Maps activity
 */
class MapsActivity : AppCompatActivity() ,  OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    lateinit var map: GoogleMap
    val LOCA_PERM_CODE = 100
    var coordenada = ""
    var longitud = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        crearFragment()
    }


    private fun crearFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        map.uiSettings.isZoomControlsEnabled = true
        cogerDatos()

        enableLocation()
    }

    private fun cogerDatos() {
        val dato = intent.extras
        coordenada = dato?.getString("LATITUD").toString().trim()
        longitud = dato?.getString("LOGITUD").toString().trim()
        if(coordenada.length!=0 && longitud.length!=0){
            crarMarcadores(coordenada,longitud)
        }else{
            dialogo()
        }


    }

    private fun dialogo() {
        val alertDialog= AlertDialog.Builder(this)
            .setTitle("Marcar tu casa")
            .setPositiveButton("Aceptar") { _,_ ->

            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isPermisosConcedido()) {
            map.isMyLocationEnabled = true
        } else {
            pedirPermisos()
        }
    }

    private fun isPermisosConcedido(): Boolean {
        return (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }


    private fun pedirPermisos() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            Toast.makeText(this, "vaya a ajusta y activa los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCA_PERM_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCA_PERM_CODE -> {
                if (grantResults.isNotEmpty() &&
                    (grantResults[0]) == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    map.isMyLocationEnabled = true
                } else {
                    Toast.makeText(this, "No seas rata de permiso", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {

            }
        }
    }


    private fun crarMarcadores(lat: String, lon: String) {

        val cord = LatLng(lat.toDouble(), lon.toDouble())
        val marcador = MarkerOptions().position(cord).title("CASA")
        map.addMarker(marcador)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(cord, 15f), 6000, null
        )
    }


    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!isPermisosConcedido()) {
            if (!::map.isInitialized) return
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Permisos necesario para la app, activalos", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Boton localizacion pulsado", Toast.LENGTH_SHORT).show()
        return false
    }



    override fun onMyLocationClick(p0: Location) {
        crarMarcadores(p0.latitude.toString(),p0.longitude.toString())
        Toast.makeText(this, "Coodenadas de mi ubicacion: (${p0.latitude}, ${p0.longitude})",Toast.LENGTH_SHORT).show()

    }

}