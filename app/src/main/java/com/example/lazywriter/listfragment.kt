package com.example.lazywriter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [listfragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class listfragment : Fragment() {

    //private var param1: String? = null
    //private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        **/


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


      var click =   (activity as Main_Menu).setClickList()


        val view = inflater.inflate(R.layout.fragment_listfragment, container, false)
        // Inflate the layout for this fragment
        val recyclerview = view.findViewById<RecyclerView>(R.id.rcvp)
        recyclerview.layoutManager = LinearLayoutManager(view.context)



        val presetdatabase = FirebaseDatabase
            .getInstance("https://lazywriter-fe624-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("Presets")

        val presetListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue(object: GenericTypeIndicator<ArrayList<Preset>>(){}) as ArrayList<Preset>
                // val tag = findViewById<TextView>(R.id.welcome_tag)

                // getting the recyclerview by its id

                val adapter = CustomAdapter(post,click)
                recyclerview.adapter = adapter

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        presetdatabase.addValueEventListener(presetListener)


        return view

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment listfragment.

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            listfragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
              }

    }
         */
    }
}