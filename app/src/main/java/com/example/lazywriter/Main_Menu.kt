package com.example.lazywriter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        val extras = intent.extras
        val UID = extras?.get("UID")
        var data = ArrayList<Preset>()

        addBtn = findViewById<ImageButton>(R.id.AddButton)
        delBtn = findViewById<ImageButton>(R.id.DeleteButton)
        copBtn = findViewById<ImageButton>(R.id.CopyButton)
        whBtn = findViewById<ImageButton>(R.id.WhaButton)


        delBtn.isVisible= false
        copBtn.isVisible= false
        whBtn.isVisible= false



        addBtn.setOnClickListener {
            if (fragstate) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fgv, addfragment)
                transaction.commit()
                fragstate= false
                addBtn.setImageResource(R.drawable.back_baseline)
            }else {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fgv, listfragment)
                transaction.commit()
                fragstate= true
                addBtn.setImageResource(R.drawable.add_baseline)

            }

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
         }
     }
     return click
 }

 fun MenunotifyUpdate(username : String)
 {
     val tag = findViewById<TextView>(R.id.welcome_tag)
     tag.setText("Bentornato "+ username)
 }

    fun notifyData(post: ArrayList<Preset>) {
        data= post
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


}

