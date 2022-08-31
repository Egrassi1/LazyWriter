package com.example.lazywriter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class Main_Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        val extras = intent.extras
        val UID = extras?.get("UID")


        val recyclerview = findViewById<RecyclerView>(R.id.rcv)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val addBtn = findViewById<ImageButton>(R.id.AddButton)
        val delBtn = findViewById<ImageButton>(R.id.DeleteButton)
        val copBtn = findViewById<ImageButton>(R.id.CopyButton)
        val whBtn = findViewById<ImageButton>(R.id.WhaButton)

        var click = object : OnListClickInterface{
            override fun OnClick(pos: Int) {
                delBtn.isVisible= true
                copBtn.isVisible= true
                whBtn.isVisible= true

            }
        }

        delBtn.isVisible= false
        copBtn.isVisible= false
        whBtn.isVisible= false






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




        val presetdatabase = FirebaseDatabase
            .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users").child(UID.toString()).child("Presets")

        val presetListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue(object: GenericTypeIndicator<ArrayList<Preset>>(){}) as ArrayList<Preset>
               // val tag = findViewById<TextView>(R.id.welcome_tag)
                data = post
                
                // getting the recyclerview by its id
                val adapter = CustomAdapter(data,click)
                recyclerview.adapter = adapter

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        presetdatabase.addValueEventListener(presetListener)






       }




    }

