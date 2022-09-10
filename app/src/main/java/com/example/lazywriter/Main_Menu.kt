package com.example.lazywriter

import android.Manifest
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment


class Main_Menu : AppCompatActivity()  {

    //elementi della view
    lateinit var addBtn : ImageButton
    lateinit var delBtn : ImageButton
    lateinit var copBtn : ImageButton
    lateinit var whBtn :   ImageButton
    lateinit var editBtn: ImageButton
    lateinit var outBtn : Button
    lateinit var adapter: CustomAdapter
    lateinit var dialog: ProgressDialog
    lateinit var username : String
    lateinit var check : CheckBox
    var coutindex = 0

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


        check = findViewById<CheckBox>(R.id.posbox)
        check.setOnClickListener{
           positionattachment(check.isChecked)
        }

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
                state1()
                addfragment = addfragment()
                addfragment.setview()
                transaction(addfragment)
                coutindex= 1
            }else {
                state0()
                transaction(listfragment)
                coutindex= 0
            }

        }

        editBtn.setOnClickListener {

            addfragment = addfragment()
            addfragment.setview()
            transaction(addfragment)
            coutindex= 2
            state2()
        }

        delBtn.setOnClickListener {
            listfragment = listfragment()
            dbHelper.delete(keyList[selected])
            if(!fragstate)
            {
                coutindex = 0
            }
         state0()
        }

        whBtn.setOnClickListener {
            var testo = createtext()
            sendMessage(testo)
        }


        dbHelper = dbHelper()
        frgm = this.supportFragmentManager
        dbHelper.menu = this


        if(savedInstanceState != null)
        {
            data = savedInstanceState.getSerializable("data") as ArrayList<Preset>
            keyList = savedInstanceState.getSerializable("key") as ArrayList<String>
            selected= savedInstanceState.getInt("sel")
            username = savedInstanceState.getString("username").toString()
            coutindex =    savedInstanceState.getInt("cout")

            if(coutindex == 0)
            {
                state0()
            }
            if(coutindex == 1)
            {
                state1()
                addfragment.setview()

            }

            if(coutindex == 2)
            {
                state2()
                transaction(addfragment)
                addfragment.setview()

            }


            MenunotifyUpdate(username)
            dbHelper.retrivedata()

        }else{
            state0()
            startprocd("Caricamento informazioni")
            dbHelper.retrivedata()
            dbHelper.retriveusername()

        }
    }



    override fun onResume() {
        super.onResume()
        if(check.isChecked)
        {
            positionattachment(check.isChecked)
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("cout", coutindex)
        outState.putString("username",username)
        outState.putSerializable("data", data)
        outState.putSerializable("key",keyList)
        outState.putInt("sel",selected)
    }

    override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
    ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    this.recreate()
    }

//metodi per gli stati possibili della UI
  private  fun state0()
    {
        delBtn.isVisible= false
        copBtn.isVisible= false
        whBtn.isVisible= false
        editBtn.isVisible= false
        addBtn.setImageResource(R.drawable.add_baseline)

        fragstate = true
        addfragment.edit= false
    }

  private  fun state1()
    {

        delBtn.isVisible= false
        copBtn.isVisible= false
        whBtn.isVisible= false
        editBtn.isVisible= false
        addBtn.setImageResource(R.drawable.back_baseline)

        fragstate= false
        addfragment.edit= false

    }
  private  fun state2()
    {
        delBtn.isVisible= true
        copBtn.isVisible= true
        whBtn.isVisible= true
        editBtn.isVisible= false
        addBtn.setImageResource(R.drawable.back_baseline)

        fragstate= false
        addfragment.edit= true

    }




    //salvataggio del preset
    fun savePreset(titolo: String, testo: String) {

      val pres = Preset(titolo,testo)
      dbHelper.save(pres)
      if(::adapter.isInitialized)
      {
          adapter.notifyDataSetChanged()
      }else{ transaction(listfragment)}
      state0()
      coutindex= 0
    }

    //modifica del preset
    fun ChangePreset(titolo: String, testo: String, chiave: String) {

        val pres = Preset(titolo,testo)
        dbHelper.change(pres,chiave)
        transaction(listfragment)
        state0()
        coutindex = 0


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


//imposta i dati restituiti da dbHelper.retriveUsername
    fun MenunotifyUpdate(username : String)
    {
        this.username = username
        val tag = findViewById<TextView>(R.id.welcome_tag)
        tag.setText("Bentornato "+ username)
    }

    //imposta i dati restituiti da dbHelper.retrivedata
    fun notifyData(post: ArrayList<Preset>, keys : ArrayList<String>) {

        data= post
        keyList = keys

        val text = findViewById<TextView>(R.id.quantity_text)
        text.setText(data.size.toString() + "/" + "20")
        if(::adapter.isInitialized) {
            transaction(listfragment)
            adapter.notifyDataSetChanged()
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

        var service = false
        val lm = this.getSystemService(LOCATION_SERVICE) as LocationManager
        service= lm.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(service) {
       // locationup = !locationup
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
        }else {
            Toast.makeText(
                this,
                "Devi prima attivare la geolocalizzazione!",
                Toast.LENGTH_SHORT
            )
                .show()
            check.isChecked=false
        }

    }

 //metodo che genera un dialog di caricamento(allo startup dell'applicazione
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

    //metodo che gestisce le transaction tra i fragment
    fun transaction(fragment: Fragment)
    {

            val transaction = frgm.beginTransaction()
            transaction.replace(R.id.fgv, fragment)
            transaction.commit()

    }

    //metodo che genera il testo per la clipboard
    fun createtext(): String
    {
        var testo: String
        if(!addfragment.edit) {
            testo = data[selected].text
        }else{  testo = addfragment.testo.text.toString()}
        if(check.isChecked)  testo = testo + "\n" +
                "latitudine: "+  mService.getLat().toString()+ "; longitudine: "+ mService.getLong().toString()+
                " http://maps.google.com/?ie=UTF8&hq=&ll="+mService.getLat().toString()+","+mService.getLong().toString()+"&z=13"

        return testo
    }

}




