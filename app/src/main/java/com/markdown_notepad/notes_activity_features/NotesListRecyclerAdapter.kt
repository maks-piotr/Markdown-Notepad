package com.markdown_notepad.notes_activity_features

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.markdown_notepad.R
import com.markdown_notepad.NotesListActivity

class NotesListRecyclerAdapter(notesListSquareList: List<NotesListSquare>, private val listener_in: NotesListActivity.SquareListener) : RecyclerView.Adapter<NotesListRecyclerAdapter.ViewHolder>() {
    var squareList : List<NotesListSquare> = notesListSquareList
    val listener : NotesListActivity.SquareListener = listener_in
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
            listener.squareClick(boundSquare)
            //notifySquareChange(position)
        }
    }

    fun passGalleryState() {
        if (squareList.isNotEmpty())
            Log.i("mylogs", "pierwszy element: " + squareList[0].toString())
        notifyDataSetChanged()
    }
    fun notifySquareChange(position: Int) {
        notifyItemChanged(position)
    }
    fun notifyMultipleSquareChanges(positions : List<Int>) {
        for (e in positions) {
            notifyItemChanged(e)
        }
    }
    override fun getItemCount(): Int {
        return squareList.size
    }

}