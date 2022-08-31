package com.example.lazywriter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val mList: List<Preset>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>()
{
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