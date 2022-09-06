package com.example.lazywriter

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class LocationService : Service() {

    val binder = MyBinder(this)
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    //var locationup = false
    private var fusedLocationClient: FusedLocationProviderClient? = null

    var latitude = 0.0
    var longitude = 0.0


    override fun onBind(intent: Intent): IBinder? {

    return binder

    }

    override fun onCreate() {
        super.onCreate()
        locationRequest = buildLocationRequest()
        locationCallback =  buildLocationCallBack()
        LocationServices.getFusedLocationProviderClient(this).also { fusedLocationClient = it }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
           //?? popup per consentire le autorizzazioni
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    fun getLat(): Double
    {
        return latitude
    }

    fun getLong(): Double{
        return longitude
    }

    private fun buildLocationRequest() : LocationRequest{
        val locationRequest = LocationRequest()
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest!!.setInterval(100)
        locationRequest!!.setFastestInterval(100)
        locationRequest!!.setSmallestDisplacement(1f)
        return locationRequest
    }

    private fun buildLocationCallBack() : LocationCallback {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    latitude = location.latitude
                    longitude = location.longitude

                }

            }
        }
        return locationCallback
    }

}