package com.example.lazywriter

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.database.*


class Main_Menu : AppCompatActivity() {
    lateinit var     addBtn : ImageButton
    lateinit var   delBtn : ImageButton
    lateinit var  copBtn : ImageButton
    lateinit var whBtn :   ImageButton
    lateinit var adapter: CustomAdapter
    val dbHelper = dbHelper(this)
    private var listfragment  = listfragment()
    private val addfragment = addfragment()
    private var fragstate = true
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

            val testo = data[selected].text
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


}

