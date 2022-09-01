package com.example.lazywriter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Main_Menu : AppCompatActivity() {
    lateinit var     addBtn : ImageButton
    lateinit var   delBtn : ImageButton
    lateinit var  copBtn : ImageButton
    lateinit var whBtn :   ImageButton
    private val listfragment = listfragment()
    private val addfragment = addfragment()
    private var fragstate = true


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



        //val recyclerview = findViewById<RecyclerView>(R.id.rcvp)
        //recyclerview.layoutManager = LinearLayoutManager(this)

        lateinit var username : String
        var data = ArrayList<Preset>()
        val database = FirebaseDatabase
            .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users").child(UID.toString())
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue(Preset::class.java) as Preset
                val tag = findViewById<TextView>(R.id.welcome_tag)
                username = post.text
                tag.setText("Bentornato "+ username)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
     database.addValueEventListener(postListener)



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




    }

