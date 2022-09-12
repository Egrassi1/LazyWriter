package com.example.lazywriter

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible

class addfragment : Fragment() {
    lateinit var titolo: TextView
    lateinit var testo: TextView
    lateinit var btnAdd: Button
    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_addfragment, container, false)

             titolo = view.findViewById<TextView>(R.id.Title_text)
             testo = view.findViewById<TextView>(R.id.preset_text)
             btnAdd = view.findViewById<Button>(R.id.save_btn)


        setview()

        return view
    }

    fun setview() {

        if (::btnAdd.isInitialized) {


            if(edit) {
                btnAdd.setText("Modifica")
                titolo.setText((activity as Main_Menu).data[(activity as Main_Menu).selected].title)
                testo.setText((activity as Main_Menu).data[(activity as Main_Menu).selected].text)
                btnAdd.setOnClickListener {
                    (activity as Main_Menu).ChangePreset(
                        titolo.text.toString(),
                        testo.text.toString(),
                        (activity as Main_Menu).keyList[(activity as Main_Menu).selected]
                    )
                }
            } else {

                btnAdd.setOnClickListener {

                    if((activity as Main_Menu).data.size <20) {
                        var check = true
                        if (titolo.text.toString().isEmpty()) {
                            titolo.error = "Inserisci un titolo per il preset"
                            check = false
                        }
                        if (testo.text.toString().isEmpty()) {
                            testo.error = "inserisci un testo per il preset"
                            check = false
                        }

                        if (check) {

                            (activity as Main_Menu).savePreset(
                                titolo.text.toString(),
                                testo.text.toString()
                            )
                            titolo.setText("")
                            testo.setText("")
                        }


                    } else{  val builder = AlertDialog.Builder(activity as Main_Menu)
                        with(builder)
                        {
                            setTitle("Impossibile salvare il preset")
                            setMessage("hai raggiunto il limite massimo di 20 preset!")
                            setPositiveButton("OK", null)
                            show()
                        }


                }
            }
        }
    }
    }
}


