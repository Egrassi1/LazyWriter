package com.example.lazywriter

import android.Manifest
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.*


class Main_Menu : AppCompatActivity()  {
    lateinit var     addBtn : ImageButton
    lateinit var   delBtn : ImageButton
    lateinit var  copBtn : ImageButton
    lateinit var whBtn :   ImageButton
    lateinit var editBtn: ImageButton
    lateinit var outBtn : Button
    lateinit var adapter: CustomAdapter

    lateinit var dialog: ProgressDialog

    val  dbHelper = dbHelper()

    private var listfragment  = listfragment()
    private var addfragment = addfragment()
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
        dbHelper.menu = this
        dbHelper.retriveusername()
        dbHelper.retrivedata()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)


        outBtn = findViewById<Button>(R.id.outBtn)
        addBtn = findViewById<ImageButton>(R.id.AddButton)
        editBtn = findViewById<ImageButton>(R.id.EditButton)
        delBtn = findViewById<ImageButton>(R.id.DeleteButton)
        copBtn = findViewById<ImageButton>(R.id.CopyButton)
        whBtn = findViewById<ImageButton>(R.id.WhaButton)

        delBtn.isVisible= false
        copBtn.isVisible= false
        whBtn.isVisible= false
        editBtn.isVisible= false

        outBtn.setOnClickListener()
        {
            dbHelper.singout()
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }

        copBtn.setOnClickListener {
            var testo : String
            if(!addfragment.edit) {
                 testo = data[selected].text
            }else{  testo = addfragment.testo.text.toString()}
            if(locationup)  testo = testo + "/n    "+ "latitudine: "+ latitude.toString()+ "; longitudione: "+ longitude.toString()
            val clipboard = applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", testo)
            clipboard.setPrimaryClip(clip)
        }

        addBtn.setOnClickListener {
            if (fragstate) {
                addfragment = addfragment()
                addfragment.edit= false
                addfragment.setview()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fgv, addfragment)
                transaction.commit()
                //addfragment.setview(false)
                fragstate= false
                addBtn.setImageResource(R.drawable.back_baseline)
                delBtn.isVisible= false
                copBtn.isVisible= false
                whBtn.isVisible= false
                editBtn.isVisible= false
            }else {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fgv, listfragment)
                transaction.commit()
                addfragment.edit = false
                fragstate= true
                addBtn.setImageResource(R.drawable.add_baseline)

            }

        }

        editBtn.setOnClickListener {
            editBtn.isVisible= false
            addfragment = addfragment()
            addfragment.edit= true
            addfragment.setview()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fgv, addfragment)
            transaction.commit()
            fragstate= false
            addBtn.setImageResource(R.drawable.back_baseline)
        }

        delBtn.setOnClickListener {
            listfragment = listfragment()
            dbHelper.delete(keyList[selected])
            adapter.notifyDataSetChanged()
            if(!fragstate)
            {
                addfragment.edit = false
                fragstate= true
                addBtn.setImageResource(R.drawable.add_baseline)
            }
            delBtn.isVisible= false
            copBtn.isVisible= false
            whBtn.isVisible= false
            editBtn.isVisible= false
        }

        whBtn.setOnClickListener {
            var testo : String
            if(!addfragment.edit) {
                testo = data[selected].text
            }else{  testo = addfragment.testo.text.toString()}
            if(locationup)  testo = testo + "/n    "+ "latitudine: "+ latitude.toString()+ "; longitudione: "+ longitude.toString()
            sendMessage(testo)

        }

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
             editBtn.isVisible= true
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
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fgv, listfragment)
        transaction.commit()
        adapter.notifyDataSetChanged()


    }

    fun retriveAdapter(): CustomAdapter {
         val click = setClickList()
         adapter = CustomAdapter(data,click)
         return adapter
    }

    fun savePreset(titolo: String, testo: String) {

        val pres = Preset(titolo,testo)
        adapter.notifyDataSetChanged()
        addBtn.setImageResource(R.drawable.add_baseline)
       // delBtn.isVisible= false
       // copBtn.isVisible= false
       // whBtn.isVisible= false
        fragstate = !fragstate
        dbHelper.save(pres)
        //addfragment = addfragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fgv, listfragment)
        transaction.commit()
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
            startprocd("Recupero della posizione")
           val result =  fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
            result?.addOnCompleteListener{
                if (result.isSuccessful)
                {
                    stoprpcd()

                }else{
                    stoprpcd()

                }
            }



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

    fun ChangePreset(titolo: String, testo: String, chiave: String) {
        adapter.notifyDataSetChanged()
        addBtn.setImageResource(R.drawable.add_baseline)
        delBtn.isVisible= false
        copBtn.isVisible= false
        whBtn.isVisible= false
        fragstate = !fragstate
        //addfragment = addfragment()
        val pres = Preset(titolo,testo)
        dbHelper.change(pres,chiave)

    }

    fun startprocd(message: String )
    {
        dialog = ProgressDialog(this)
        dialog.setMessage(message)
        dialog.setCancelable(false)
        dialog.setInverseBackgroundForced(false)
        dialog.show()
    }
    fun stoprpcd()
    {
        dialog.hide()
    }





}




