package com.example.lazywriter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class listfragment() : Fragment() {

    lateinit var recyclerview : RecyclerView
    lateinit var adapter: CustomAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_listfragment, container, false)
        recyclerview = view.findViewById<RecyclerView>(R.id.rcvp)
        recyclerview.layoutManager = LinearLayoutManager(view.context)

            val adapter = (activity as Main_Menu).retriveAdapter()
            recyclerview.adapter = adapter


        return view

    }

    fun setView(adapter: CustomAdapter)
    {
        recyclerview.adapter = adapter

    }



}