package com.example.lazywriter

import android.Manifest
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment


class Main_Menu : AppCompatActivity()  {

    //elementi della view
    lateinit var     addBtn : ImageButton
    lateinit var   delBtn : ImageButton
    lateinit var  copBtn : ImageButton
    lateinit var whBtn :   ImageButton
    lateinit var editBtn: ImageButton
    lateinit var outBtn : Button
    lateinit var adapter: CustomAdapter
    lateinit var dialog: ProgressDialog



  //gestione dei fragment
    private var listfragment  = listfragment()
    private var addfragment = addfragment()
    var frgm =  supportFragmentManager
    private var fragstate = true
    var freshstart = true

    // servizio di geolocalizzazione
    private var location: Location? = null
    lateinit var mService : LocationService
    val servcConn = object : ServiceConnection {
        override fun onServiceDisconnected(compName: ComponentName?) { }
        override fun onServiceConnected(compName: ComponentName?, binder: IBinder?) {
            mService = (binder as MyBinder).getService()
        }
        override fun onBindingDied(compName: ComponentName) {}
    }
    var locationup = false

    //oggetto per l'accesso a firebase
    lateinit var  dbHelper : dbHelper

    //dati sull'istanza corrente
    var data = ArrayList<Preset>()
    var keyList = ArrayList<String>()
    var selected = -1


    override fun onCreate(savedInstanceState: Bundle?) {
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
            val i = Intent(this,login_activity::class.java)
            startActivity(i)
            finish()
        }

        copBtn.setOnClickListener {
            var testo = createtext()
            val clipboard = applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", testo)
            clipboard.setPrimaryClip(clip)
        }

        addBtn.setOnClickListener {
            if (fragstate) {
                addfragment = addfragment()
                addfragment.edit= false
                addfragment.setview()
                transaction(addfragment)

                addBtn.setImageResource(R.drawable.back_baseline)
                delBtn.isVisible= false
                copBtn.isVisible= false
                whBtn.isVisible= false
                editBtn.isVisible= false

                fragstate= false

            }else {
                transaction(listfragment)
                addfragment.edit = false
                addBtn.setImageResource(R.drawable.add_baseline)
                delBtn.isVisible= false
                copBtn.isVisible= false
                whBtn.isVisible= false
                editBtn.isVisible= false
                fragstate= true
            }

        }

        editBtn.setOnClickListener {
            editBtn.isVisible= false
            addfragment = addfragment()
            addfragment.edit= true
            addfragment.setview()
            transaction(addfragment)
            addBtn.setImageResource(R.drawable.back_baseline)
            fragstate= false
        }

        delBtn.setOnClickListener {
            locationup = false
            //listfragment = listfragment()
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
            var testo = createtext()
            sendMessage(testo)
        }

    }


    override fun onResume() {
        super.onResume()
        if(freshstart) {
            dbHelper = dbHelper()
            frgm = this.supportFragmentManager
            dbHelper.menu = this

            startprocd("Caricamento informazioni")
            dbHelper.retriveusername()
            dbHelper.retrivedata()
        }else{recreate()}
    }


    override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
    ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    this.recreate()
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
        transaction(listfragment)
        adapter.notifyDataSetChanged()
    }

  //salvataggio del preset
    fun savePreset(titolo: String, testo: String) {

        val pres = Preset(titolo,testo)
        adapter.notifyDataSetChanged()
        addBtn.setImageResource(R.drawable.add_baseline)
        fragstate = !fragstate
        dbHelper.save(pres)
        transaction(listfragment)
    }

    //modifica del preset
    fun ChangePreset(titolo: String, testo: String, chiave: String) {
        adapter.notifyDataSetChanged()
        addBtn.setImageResource(R.drawable.add_baseline)
        delBtn.isVisible= false
        copBtn.isVisible= false
        whBtn.isVisible= false
        fragstate = !fragstate
        val pres = Preset(titolo,testo)
        dbHelper.change(pres,chiave)

    }

    //metodo che apre whatsapp per inoltrare il testo selezionato
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
                "Devi installare whatsapp prima!",
                Toast.LENGTH_SHORT)
                .show();
            return;
        }

    }

    //metodo che imposta il clicklistener per il viewholder
    fun setClickList(): OnListClickInterface
    {
        var click = object : OnListClickInterface{
            override fun OnClick(pos: Int) {
                delBtn.isVisible= true
                copBtn.isVisible= true
                whBtn.isVisible= true
                editBtn.isVisible= true
                selected = adapter.pos
            }
        }
        return click
    }

    //metodo che ritorna l'adapter per il listfragment
    fun retriveAdapter(): CustomAdapter {
         val click = setClickList()
         adapter = CustomAdapter(data,click)
         return adapter
    }

    //metodo che imposta e gestisce il service di posizione
    fun positionattachment(checked: Boolean) {

        locationup = !locationup
        if (checked) {
            val intentBg = Intent(this, LocationService::class.java)
            bindService(intentBg, servcConn, BIND_AUTO_CREATE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {


                val requestcode = 0
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    requestcode
                )



            }

            startService(intentBg)
        } else {
            val intentBg = Intent(this, LocationService::class.java)
            unbindService(servcConn)
            stopService(intentBg)
        }

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

    fun transaction(fragment: Fragment)
    {

        //if (!frgm.isDestroyed)
        //{
            val transaction = frgm.beginTransaction()
            transaction.replace(R.id.fgv, fragment)
            transaction.commit()
            adapter.notifyDataSetChanged()
       // }

    }

    fun createtext(): String
    {
        var testo: String
        if(!addfragment.edit) {
            testo = data[selected].text
        }else{  testo = addfragment.testo.text.toString()}
        if(locationup)  testo = testo + "\n" +
                "latitudine: "+  mService.getLat().toString()+ "; longitudine: "+ mService.getLong().toString()
        return testo
    }

}




