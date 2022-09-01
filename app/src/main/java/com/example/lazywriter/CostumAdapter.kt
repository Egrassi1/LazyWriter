package com.example.lazywriter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.collection.LLRBNode

class CustomAdapter(private val mList: List<Preset>, var onclick: OnListClickInterface ) : RecyclerView.Adapter<CustomAdapter.ViewHolder>()
{

    lateinit var lastsel: ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // qui ci dovrebbe essere la lista di task recuperata
        val ItemsViewModel = mList[position]
        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.title
        holder.textView2.text = ItemsViewModel.text
        holder.itemView.setOnClickListener {
            if(::lastsel.isInitialized)
            {
                lastsel.itemView.setBackgroundColor(0xFFFFFF)
            }
            holder.itemView.setBackgroundColor(0x6B3A3838)
            lastsel = holder
            onclick.OnClick(position)

        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.Pic)
        val textView: TextView = itemView.findViewById(R.id.Name)
        val textView2: TextView = itemView.findViewById(R.id.textView3)


    }




}