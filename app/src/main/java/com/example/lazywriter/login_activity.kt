package com.example.lazywriter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class login_activity : AppCompatActivity() {

    lateinit var btn_login : Button
    lateinit var btn_reg : Button
    lateinit var dbHelper: dbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult() ) { result ->
            if (result.resultCode == Activity.RESULT_OK){
                val mail = result.data?.getStringExtra(("email"))
                val pass = result.data?.getStringExtra(("password"))
                onLoginClick(mail!! ,pass!!)
            }
        }

        dbHelper = dbHelper()
        btn_login = findViewById<Button>(R.id.btn_login)
        btn_reg = findViewById<Button>(R.id.btn_reg)
        btn_reg.setOnClickListener {
            val i = Intent(this,reg_Activity::class.java)
                resultLauncher.launch(i)
        }

        btn_login.setOnClickListener {

            val emailEditText = findViewById<EditText>(R.id.InputEmailLog)
            val passwordEditText = findViewById<EditText>(R.id.InputPasswordLog)
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            var check = true

            if (email.isEmpty()) {
                emailEditText.error = "Inserisci la mail"
                check = false
            }
            if (password.isEmpty()) {
                passwordEditText.error = "Inserisci la password"
                check = false
            }
            if(check)
            {
             onLoginClick(email,password)
            }

        }
    }

    private fun onLoginClick(email: String, password: String) {
        btn_login.isEnabled = false
        btn_reg.isEnabled = false
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
                    setTitle("Login non riuscito")
                    setMessage(result.exception?.message)
                    setPositiveButton("OK", null)
                    show()
                }
            }
        }

    }

    }

