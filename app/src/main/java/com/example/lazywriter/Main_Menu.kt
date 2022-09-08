package com.example.lazywriter

import android.app.ProgressDialog
import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment


class Main_Menu : AppCompatActivity()  {
    lateinit var     addBtn : ImageButton
    lateinit var   delBtn : ImageButton
    lateinit var  copBtn : ImageButton
    lateinit var whBtn :   ImageButton
    lateinit var editBtn: ImageButton
    lateinit var outBtn : Button
    lateinit var adapter: CustomAdapter
    lateinit var mService : LocationService
   var frgm =  supportFragmentManager

    lateinit var dialog: ProgressDialog

   lateinit var  dbHelper : dbHelper

    private var listfragment  = listfragment()
    private var addfragment = addfragment()
    private var fragstate = true

    private var location: Location? = null



    val servcConn = object : ServiceConnection {
        override fun onServiceDisconnected(compName: ComponentName?) { }
        override fun onServiceConnected(compName: ComponentName?, binder: IBinder?) {
            mService = (binder as MyBinder).getService()
        }
        override fun onBindingDied(compName: ComponentName) {}
    }

    var locationup = false

    var data = ArrayList<Preset>()
    var keyList = ArrayList<String>()
    var selected = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        dbHelper= dbHelper()

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
            var testo : String
            if(!addfragment.edit) {
                 testo = data[selected].text
            }else{  testo = addfragment.testo.text.toString()}
            if(locationup)  testo = testo + "\n    "+ "latitudine: "+  mService.getLat().toString()+ "; longitudione: "+ mService.getLong().toString()
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
                fragstate= false
                addBtn.setImageResource(R.drawable.back_baseline)
                delBtn.isVisible= false
                copBtn.isVisible= false
                whBtn.isVisible= false
                editBtn.isVisible= false
            }else {
                transaction(listfragment)
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
            transaction(addfragment)
            fragstate= false
            addBtn.setImageResource(R.drawable.back_baseline)
        }

        delBtn.setOnClickListener {
            locationup = false
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
            if(locationup)   testo = testo + "\n    "+ "latitudine: "+  mService.getLat().toString()+ "; longitudione: "+ mService.getLong().toString()
            sendMessage(testo)

        }

       }

    override fun onResume() {
        super.onResume()
        frgm = this.supportFragmentManager
        dbHelper.menu = this
        dbHelper.retriveusername()
        dbHelper.retrivedata()
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
        transaction(listfragment)
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
        fragstate = !fragstate
        dbHelper.save(pres)
        transaction(listfragment)
    }


    fun positionattachment(checked: Boolean) {

        locationup = !locationup
        if (checked)
        {

            val intentBg = Intent(this, LocationService::class.java)
            bindService(intentBg, servcConn, BIND_AUTO_CREATE)
            startService(intentBg)
            }
        else{
        val intentBg = Intent(this, LocationService::class.java)
            unbindService(servcConn)
        stopService(intentBg)

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

    fun transaction(fragment: Fragment)
    {

        if (!frgm.isDestroyed)
        {
            val transaction = frgm.beginTransaction()
            transaction.replace(R.id.fgv, fragment)
            transaction.commit()
            adapter.notifyDataSetChanged()
        }
    }



}




