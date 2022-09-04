package com.example.lazywriter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class reg_Activity : AppCompatActivity() {
    //private lateinit var auth: FirebaseAuth
    val dbHelper = dbHelper()

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg2)
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

       val result =  dbHelper.createUser(email,password)
        result.addOnCompleteListener(this){
            if (result.isSuccessful) {
                dbHelper.initUser(userName)
                finish()
            }   else {
                val builder = AlertDialog.Builder(this)
                with(builder)
                {
                    setTitle("Authentication failed")
                    setMessage(result.exception?.message)
                    setPositiveButton("OK", null)
                    show()
                }
        }

    }


    }


    }


