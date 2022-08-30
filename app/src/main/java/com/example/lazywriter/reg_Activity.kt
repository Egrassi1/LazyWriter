package com.example.lazywriter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class reg_Activity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg2)
        auth = FirebaseAuth.getInstance()
        val btn_reg_sub = findViewById<Button>(R.id.btn_reg_sub)
        btn_reg_sub.setOnClickListener {
            onSignUp()
        }

    }

    private fun onSignUp() {
        val usernameEditText = findViewById<EditText>(R.id.InputUsername)
        val emailEditText = findViewById<EditText>(R.id.InputEmail)
        val passwordEditText = findViewById<EditText>(R.id.InputPassword)
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val userName = usernameEditText.text.toString()
        if (userName.isEmpty()) {
            usernameEditText.error = "Enter userName"
            return
        }
        if (email.isEmpty()) {
            emailEditText.error = "Enter email"
            return
        }
        if (password.isEmpty()) {
            passwordEditText.error = "Enter password"
            return
        }
        val u = User(userName,email,password)
        createUser(u)

    }


    private fun createUser(user:User) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currenyUser = auth.currentUser
                    val uid = currenyUser!!.uid
                    val userMap = HashMap<String, String>()
                    userMap["name"] = user.username
                    val database = FirebaseDatabase.getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(uid)
                    database.setValue(userMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //   val intent = Intent(applicationContext, MainActivity::class.java)
                            //  startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    Toast.makeText(
                        baseContext, task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }
    }

