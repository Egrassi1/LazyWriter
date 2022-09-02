package com.example.lazywriter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class dbHelper(val menu: Main_Menu) {


    val preList = ArrayList<Preset>()
    val data = HashMap<String,Preset>()


    val UID = FirebaseAuth.getInstance().currentUser!!.uid





fun retriveusername(){
    val database = FirebaseDatabase
        .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Users").child(UID)
    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            val post = dataSnapshot.getValue(Preset::class.java) as Preset
            menu.MenunotifyUpdate(post.text)
        }

        override fun onCancelled(error: DatabaseError) {

        }
    }
    database.addValueEventListener(postListener)
}

fun retrivedata ()
{

    val presetdatabase = FirebaseDatabase
        .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Users").child(UID).child("Presets")

    val presetListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
           // val post = dataSnapshot.getValue(Preset::class.java) as Preset


            val post = dataSnapshot.getValue(object : GenericTypeIndicator<HashMap<String,Preset>>(){}) as HashMap<String,Preset>

            for (childKey in post.keys) {
                //childKey is your "-LQka.. and so on"
                //Your current object holds all the variables in your picture.
                post.get(childKey)?.let { preList.add(it) }

                //You can access each variable like so: String variableName = (String) currentLubnaObject.get("INSERT_VARIABLE_HERE"); //data, description, taskid, time, title
            }
            //preList.add(post)

             menu.notifyData(preList)
            //menu.MenunotifyUpdate(preList[0].text)


        }

        override fun onCancelled(error: DatabaseError) {

        }
    }
    presetdatabase.addValueEventListener(presetListener)



}

}