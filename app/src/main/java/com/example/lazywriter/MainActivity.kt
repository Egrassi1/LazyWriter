package com.example.lazywriter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val btn_reg = findViewById<Button>(R.id.btn_reg)
        btn_reg.setOnClickListener {
            val i = Intent(this,reg_Activity::class.java)
            this.startActivity(i)
        }

        val btn_login = findViewById<Button>(R.id.btn_login)
        btn_login.setOnClickListener {

         onLoginClick()

        }
    }

    private fun onLoginClick() {
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
        loginUser(email, password)
    }
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val UID = auth.currentUser?.uid
                    val intent = Intent(this, Main_Menu::class.java)
                    intent.putExtra("UID",UID)
                    startActivity(intent)
                    finish()
                } else {
                    val builder = AlertDialog.Builder(this)
                    with(builder)
                    {
                        setTitle("Authentication failed")
                        setMessage(task.exception?.message)
                        setPositiveButton("OK", null)
                        show()
                    }
                }
            }
    }

}