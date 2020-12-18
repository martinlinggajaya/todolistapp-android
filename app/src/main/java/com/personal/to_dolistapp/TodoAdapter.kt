package com.personal.to_dolistapp

import android.content.Context
import com.personal.to_dolistapp.R
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.item_todo.view.*
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class TodoAdapter(
        options: FirestoreRecyclerOptions<Todo>
) : FirestoreRecyclerAdapter<Todo, TodoAdapter.ListViewHolder>(options) {

    var listener : RecyclerViewClickListener? = null

    interface RecyclerViewClickListener {
        fun openTodo(view: View, todo: Todo)
        fun getScale(): Float
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTodoName: CheckBox = itemView.cbTodoCheckbox
        var tvTodoDue: TextView = itemView.tvTodoDue
        var ivLabelColor: ImageView = itemView.ivLabelColor
        var cvTodo: CardView = itemView.cvTodo
        var layout: ConstraintLayout = itemView.itemLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int, todo: Todo) {

        todo.id = snapshots.getSnapshot(position).id

        Log.d("cek", todo.toString())

        holder.tvTodoName.text = todo.name
        if (todo.due == null) {
            holder.tvTodoDue.visibility = View.GONE
            // Set margin if no date
            val scale: Int = listener?.getScale()?.roundToInt()!!
            val constraintSet = ConstraintSet()
            constraintSet.clone(holder.layout)
//            constraintSet.connect(holder.tvTodoName.id, ConstraintSet.START, holder.layout.id, ConstraintSet.START, 16 * scale)
            constraintSet.connect(holder.tvTodoName.id, ConstraintSet.BOTTOM, holder.layout.id, ConstraintSet.BOTTOM, 12 * scale)
            constraintSet.applyTo(holder.layout)
        }
        else {
            val formatter = SimpleDateFormat("hh:mm a EEE, d MMM yyyy", Locale.ENGLISH)
            holder.tvTodoDue.visibility = View.VISIBLE
            holder.tvTodoDue.text = formatter.format(todo.due!!)
        }
        if (todo.labelName == null) {
            holder.ivLabelColor.visibility = View.GONE
        }
        else {
            var circleResource = 0
            when (todo.labelColor) {
                "red" -> circleResource = R.drawable.circle_label_red
                "orange" -> circleResource = R.drawable.circle_label_orange
                "yellow" -> circleResource = R.drawable.circle_label_yellow
                "green" -> circleResource = R.drawable.circle_label_green
                "blue" -> circleResource = R.drawable.circle_label_blue
                "purple" -> circleResource = R.drawable.circle_label_purple
                "pink" -> circleResource = R.drawable.circle_label_pink
            }
            holder.ivLabelColor.visibility = View.VISIBLE
            holder.ivLabelColor.setImageResource(circleResource)
        }

        holder.cvTodo.setOnClickListener {
            Log.d("cek", "clicked")
            listener?.openTodo(it, todo)
        }
    }

}