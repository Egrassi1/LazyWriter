package com.example.lazywriter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible


class LoadingAct : AppCompatActivity() {
    lateinit var btn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading2)
        btn = findViewById<Button>(R.id.loadbtn)
        btn.setOnClickListener{

            Loading()
        }
        Loading()

        }

    fun Loading()
    {
        if (isNetworkAvailable()) {
            val dbHelper = dbHelper()
            if (dbHelper.isLogged()) {
                val intent = Intent(this, Main_Menu::class.java)
                startActivity(intent)
                finish()
            } else {

                val intent = Intent(this, login_activity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            val builder = AlertDialog.Builder(this)
            with(builder)
            {
                setTitle("CONTROLLA LA TUA CONNESSIONE")
                setMessage("il tuo telefono non è connesso")
                setPositiveButton("ok", null)

                setPositiveButton("OK", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        btn = findViewById<Button>(R.id.loadbtn)
                        btn.isVisible = true


                    }
                })
                show()
            }
        }

    }


            fun isNetworkAvailable(): Boolean {
                //if (context == null) return false
                val connectivityManager =
                    this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val actinfo =
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    if (actinfo!= null) {
                        return true
                    }
                }
            else {
                    val actinfo = connectivityManager.activeNetworkInfo
                    if (actinfo != null && actinfo.isConnected) {
                        return true
                    }
                }
                return false
            }

    }
