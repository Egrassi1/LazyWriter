package com.example.lazywriter

import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.*
import java.io.Serializable



class dbHelper()
{

    lateinit var menu : Main_Menu
    lateinit var UID : String
    private lateinit var auth: FirebaseAuth

    init{
        auth = FirebaseAuth.getInstance()

    }

    fun createUser(email: String, password: String): Task<AuthResult> {

           val result = auth.createUserWithEmailAndPassword(email, password)

        return result

        }

    fun initUser(userName: String) {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        val userMap = Preset("name", userName)

        val database = FirebaseDatabase
            .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users").child(uid)
        database.setValue(userMap)

        val preset1 = Preset("prova", "testo di prova")
        val presdatabse = FirebaseDatabase
            .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users").child(uid).child("Presets").push()

        presdatabse.setValue(preset1)


}

     fun loginUser(email: String, password: String): Task<AuthResult> {

         val res = auth.signInWithEmailAndPassword(email, password)
            return  res
             //return "OK"

            }

fun retriveusername(){


    val database = FirebaseDatabase
        .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Users").child(UID)
    val postListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            val post = dataSnapshot.getValue(Preset::class.java) as Preset
            menu.stoprpcd()
            menu.MenunotifyUpdate(post.text)
        }

        override fun onCancelled(error: DatabaseError) {

        }
    }
    database.addListenerForSingleValueEvent(postListener)
}

fun retrivedata ()
{
    UID = FirebaseAuth.getInstance().currentUser!!.uid
    val keyList = ArrayList<String>()
    val preList = ArrayList<Preset>()
    val data = HashMap<String,Preset>()

    val presetdatabase = FirebaseDatabase
        .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("Users").child(UID).child("Presets")

    val presetListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if(menu.frgm.isDestroyed)
            {
                presetdatabase.removeEventListener(this)
                return
            }
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


    fun change(pres: Preset, chiave: String) {
        val presdarabse=     FirebaseDatabase
            .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users").child(UID).child("Presets").child(chiave)
        presdarabse.setValue(pres)

    }

    fun isLogged(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    fun singout() {
        auth.signOut()
    }





}

