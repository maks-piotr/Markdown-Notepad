package com.markdown_notepad.notes_activity_features

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.markdown_notepad.R

class NotesListRecyclerAdapter(var squareList: List<NotesListSquare>, private val listener: (NotesListSquare)->Unit ) : RecyclerView.Adapter<NotesListRecyclerAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView : TextView = view.findViewById(R.id.note_name)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_list_square,parent,false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val boundSquare = squareList[position]
        holder.textView.text = boundSquare.file_name
        Log.i("mylogs", "file bound: ${boundSquare.file_name}")
        holder.itemView.setOnClickListener {
            listener(boundSquare)
            //notifySquareChange(position)
        }
    }

    fun passGalleryState() {
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return squareList.size
    }

}