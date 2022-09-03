package com.example.lazywriter

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.*
import java.lang.Exception


class Main_Menu : AppCompatActivity()  {
    lateinit var     addBtn : ImageButton
    lateinit var   delBtn : ImageButton
    lateinit var  copBtn : ImageButton
    lateinit var whBtn :   ImageButton
    lateinit var adapter: CustomAdapter
    val dbHelper = dbHelper(this)
    private var listfragment  = listfragment()
    private val addfragment = addfragment()
    private var fragstate = true

    private var location: Location? = null
    val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    val UPDATE_INTERVAL: Long = 5000
    val FASTEST_INTERVAL: Long = 5000
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    var locationup = false

    private var fusedLocationClient: FusedLocationProviderClient? = null
   //private var locationRequest: LocationRequest? = null
    //private var locationCallback: LocationCallback? = null

    var latitude = 0.0
    var longitude = 0.0



    var data = ArrayList<Preset>()
    var keyList = ArrayList<String>()
    var selected = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        val extras = intent.extras
        val UID = extras?.get("UID")



        addBtn = findViewById<ImageButton>(R.id.AddButton)
        delBtn = findViewById<ImageButton>(R.id.DeleteButton)
        copBtn = findViewById<ImageButton>(R.id.CopyButton)
        whBtn = findViewById<ImageButton>(R.id.WhaButton)


        delBtn.isVisible= false
        copBtn.isVisible= false
        whBtn.isVisible= false




        copBtn.setOnClickListener {



            var testo = data[selected].text
            if(locationup)  testo = testo + "/n    "+ "latitudine: "+ latitude.toString()+ "; longitudione: "+ longitude.toString()
            val clipboard =
                applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", testo)
            clipboard.setPrimaryClip(clip)
        }


        addBtn.setOnClickListener {
            if (fragstate) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fgv, addfragment)
                transaction.commit()
                fragstate= false
                addBtn.setImageResource(R.drawable.back_baseline)
                delBtn.isVisible= false
                copBtn.isVisible= false
                whBtn.isVisible= false
            }else {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fgv, listfragment)
                transaction.commit()
                fragstate= true
                addBtn.setImageResource(R.drawable.add_baseline)

            }

        }

        delBtn.setOnClickListener {

            data.clear()
            adapter.notifyDataSetChanged()



            dbHelper.delete(keyList[selected])

        }

        whBtn.setOnClickListener {
            var testo = data[selected].text
            if(locationup)  testo = testo + "/n    "+ "latitudine: "+ latitude.toString()+ "; longitudione: "+ longitude.toString()
            sendMessage(testo)

        }


        dbHelper.retriveusername()
        dbHelper.retrivedata()


        locationRequest = buildLocationRequest()
         locationCallback =  buildLocationCallBack()
        LocationServices.getFusedLocationProviderClient(this).also { fusedLocationClient = it }


       }


 fun setClickList(): OnListClickInterface
 {
     var click = object : OnListClickInterface{
         override fun OnClick(pos: Int) {
             delBtn.isVisible= true
             copBtn.isVisible= true
             whBtn.isVisible= true
             selected = adapter.pos
             val text = findViewById<TextView>(R.id.quantity_text)
             text.setText(data.size.toString() + "/" + selected.toString())
         }
     }
     return click
 }

 fun MenunotifyUpdate(username : String)
 {
     val tag = findViewById<TextView>(R.id.welcome_tag)
     tag.setText("Bentornato "+ username)
 }

    fun notifyData(post: ArrayList<Preset>, keys : ArrayList<String>) {

        data= post
        keyList = keys

        val text = findViewById<TextView>(R.id.quantity_text)
        text.setText(data.size.toString() + "/" + "20")

        adapter.notifyDataSetChanged()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fgv, listfragment)
        transaction.commit()

    }

    fun retriveAdapter(): CustomAdapter {
         val click = setClickList()
         adapter = CustomAdapter(data,click)
         return adapter
    }

    fun savePreset(titolo: String, testo: String) {

        val pres = Preset(titolo,testo)
        //data.clear()
        adapter.notifyDataSetChanged()
        addBtn.setImageResource(R.drawable.add_baseline)
        delBtn.isVisible= false
        copBtn.isVisible= false
        whBtn.isVisible= false
        fragstate = !fragstate
        dbHelper.save(pres)
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

    fun positionattachment(checked: Boolean) {

        locationup = !locationup
        if (checked)
        {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

            }
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } else{
            fusedLocationClient?.removeLocationUpdates(locationCallback)
        }

    }

    fun sendMessage(message:String){

        try {
            val intent = Intent(Intent.ACTION_SEND)

            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, message)
            sendIntent.type = "text/plain"

            sendIntent.setPackage("com.whatsapp")


            startActivity(sendIntent)
        }
        catch(e: ActivityNotFoundException){
            Toast.makeText(
                this,
                "Please install whatsapp first.",
                Toast.LENGTH_SHORT)
                .show();
            return;
        }

            }

    }




