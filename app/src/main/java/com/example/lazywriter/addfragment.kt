package com.example.lazywriter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [addfragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class addfragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_addfragment, container, false)
        val titolo = view.findViewById<TextView>(R.id.Title_text)
        val testo = view.findViewById<TextView>(R.id.preset_text)
        val btnAdd = view.findViewById<Button>(R.id.save_btn)
        btnAdd.setOnClickListener {
            (activity as Main_Menu).savePreset(titolo.text.toString(),testo.text.toString())
        }
        return view
    }

}