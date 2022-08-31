package com.example.lazywriter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
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
                    /**post[0].text


                for(i in 1.. post.size )
                {
                    data[i] = post[i]
                }
                    **/
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
                val adapter = CustomAdapter(data)
                recyclerview.adapter = adapter

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        presetdatabase.addValueEventListener(presetListener)


        // getting the recyclerview by its id





    }

}