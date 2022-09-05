package com.example.lazywriter

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    lateinit var btn_login : Button
    lateinit var btn_reg : Button
    lateinit var dbHelper: dbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = dbHelper()
        btn_login = findViewById<Button>(R.id.btn_login)
         btn_reg = findViewById<Button>(R.id.btn_reg)
        btn_reg.setOnClickListener {
            val i = Intent(this,reg_Activity::class.java)
            this.startActivity(i)
        }


        btn_login.setOnClickListener {

         onLoginClick()

        }
    }

    private fun onLoginClick() {
        btn_login.isEnabled = false
        btn_reg.isEnabled = false

        val emailEditText = findViewById<EditText>(R.id.InputEmailLog)
        val passwordEditText = findViewById<EditText>(R.id.InputPasswordLog)
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        if (email.isEmpty()) {
            emailEditText.error = "Enter email"
            return
        }
        if (password.isEmpty()) {
            passwordEditText.error = "Enter password"
            return
        }
        val result =dbHelper.loginUser(email, password)
        result.addOnCompleteListener(this)
        {
            if (result.isSuccessful) {
                //val UID = auth.currentUser?.uid
                val intent = Intent(this, Main_Menu::class.java)
                startActivity(intent)

                finish()
            } else {
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

