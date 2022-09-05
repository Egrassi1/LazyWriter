package com.example.lazywriter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible


class LoadingAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading2)

        Loading()


        }

    fun Loading()
    {
        if (isNetworkAvailable(this)) {
            val dbHelper = dbHelper()
            if (dbHelper.isLogged()) {
                val intent = Intent(this, Main_Menu::class.java)
                startActivity(intent)
                finish()
            } else {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            val builder = AlertDialog.Builder(this)
            with(builder)
            {
                setTitle("check internet connection")
                setMessage("your phone is not connectd")
                setPositiveButton("ok", null)

                setPositiveButton("OK", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        val btn = findViewById<Button>(R.id.loadbtn)
                        btn.isVisible = true
                        btn.setOnClickListener{

                            Loading()
                        }

                    }
                })
                show()
            }
        }

    }


            fun isNetworkAvailable(context: Context?): Boolean {
                if (context == null) return false
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val capabilities =
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    if (capabilities != null) {
                        when {
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                                return true
                            }
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                                return true
                            }
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                                return true
                            }
                        }
                    }
                } else {
                    val activeNetworkInfo = connectivityManager.activeNetworkInfo
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                        return true
                    }
                }
                return false
            }

    }
