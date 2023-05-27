package com.markdown_notepad.add_tags_features

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.markdown_notepad.R
import com.markdown_notepad.room.MyPair
import com.markdown_notepad.room.entities.Tag

class AddTagAdapter(var pairList: List<MyPair>, var function: (MyPair)->Unit): RecyclerView.Adapter<AddTagAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView : TextView = view.findViewById(R.id.tag_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_tag_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pairList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = pairList[position].first.tagName
        if (pairList[position].second)
            holder.textView.setBackgroundResource(R.drawable.rounded_corner_checked)
        else
            holder.textView.setBackgroundResource(R.drawable.rounded_corner_unchecked)

        holder.itemView.setOnClickListener {
            function(pairList[position])
            notifyDataSetChanged()
        }
    }
}