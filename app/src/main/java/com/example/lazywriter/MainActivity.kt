package com.example.lazywriter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btn_reg = findViewById<Button>(R.id.btn_reg)
        btn_reg.setOnClickListener {
            val i = Intent(this,reg_Activity::class.java)
            this.startActivity(i)
        }
    }
}