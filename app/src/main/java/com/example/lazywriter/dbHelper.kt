package com.example.lazywriter

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class dbHelper(val menu: Main_Menu) {





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
    val keyList = ArrayList<String>()
    val preList = ArrayList<Preset>()
    val data = HashMap<String,Preset>()

    val presetdatabase = FirebaseDatabase
        .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Users").child(UID).child("Presets")

    val presetListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            preList.clear()
            keyList.clear()
            try {
                val post = dataSnapshot.getValue(object :
                    GenericTypeIndicator<HashMap<String, Preset>>() {}) as HashMap<String, Preset>
                for (childKey in post.keys) {

                    post.get(childKey)?.let { preList.add(it) }
                    keyList.add(childKey)

                }



            }catch(e: Exception)
            {

            }
            menu.notifyData(preList, keyList)


        }

        override fun onCancelled(error: DatabaseError) {



        }
    }
    presetdatabase.addValueEventListener(presetListener)



}

    fun save(pres: Preset) {

        val presdarabse=     FirebaseDatabase
            .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users").child(UID).child("Presets").push()
        presdarabse.setValue(pres)

    }

    fun delete(s: String) {
        val presdarabse=     FirebaseDatabase
            .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users").child(UID).child("Presets").child(s)
        presdarabse.removeValue()
    }


}

