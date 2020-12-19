package com.personal.to_dolistapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.personal.to_dolistapp.R
import com.personal.to_dolistapp.Todo
import com.personal.to_dolistapp.TodoAdapter
import com.personal.to_dolistapp.ui.auth.LoginActivity
import com.personal.to_dolistapp.ui.todo.TodoDetailActivity
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), TodoAdapter.RecyclerViewClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var todoAdapter: TodoAdapter? = null

    private lateinit var todoList: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        todoList = view.findViewById(R.id.rvTodo)
        if (auth.currentUser != null) {
            setupRecyclerView()
        }

        todoAdapter?.listener = this

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupRecyclerView() {
        // Initialize adapter for rvTodo
        val query = db.collection("users").document(auth.currentUser!!.email!!).collection("todos")
        val options = FirestoreRecyclerOptions.Builder<Todo>()
                    .setQuery(query, Todo::class.java)
                    .build()
        todoAdapter = TodoAdapter(options)
        todoAdapter!!.notifyDataSetChanged()
        // Initialize rvTodo
        todoList.layoutManager = LinearLayoutManager(context)
        todoList.adapter = todoAdapter
    }

    override fun openTodo(view: View, todo: Todo) {
        Log.d("cek", "Todo to Detail: $todo")
        val intent = Intent(context, TodoDetailActivity::class.java).apply {
            putExtra(TodoDetailActivity.DATA, todo)
        }
        startActivity(intent)
    }

    override fun getScale(): Float {
        return resources.displayMetrics.density
    }

    override fun onStart() {
        super.onStart()
        todoAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        todoAdapter?.stopListening()
    }
}