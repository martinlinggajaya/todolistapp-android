package com.personal.to_dolistapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.item_label.view.*

class LabelAdapter (options: FirestoreRecyclerOptions<Label>
) : FirestoreRecyclerAdapter<Label, LabelAdapter.ListViewHolder>(options) {

    var listener : LabelAdapter.RecyclerViewClickListener? = null

    interface RecyclerViewClickListener {
        fun deleteLabel(label: Label)
    }

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvLabelName: TextView = itemView.tvLabelItemName
        var ivLabelColor: ImageView = itemView.ivLabelItemColor
        var btnLabelDelete: ImageButton = itemView.btnLabelItemDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelAdapter.ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_label, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: LabelAdapter.ListViewHolder, position: Int, label: Label) {
        holder.tvLabelName.text = label.name

        var circleResource = 0
        when (label.color) {
            "red" -> circleResource = R.drawable.circle_label_red
            "orange" -> circleResource = R.drawable.circle_label_orange
            "yellow" -> circleResource = R.drawable.circle_label_yellow
            "green" -> circleResource = R.drawable.circle_label_green
            "blue" -> circleResource = R.drawable.circle_label_blue
            "purple" -> circleResource = R.drawable.circle_label_purple
            "pink" -> circleResource = R.drawable.circle_label_pink
        }
        holder.ivLabelColor.setImageResource(circleResource)

        holder.btnLabelDelete.setOnClickListener {
            listener?.deleteLabel(label)
        }
    }

}