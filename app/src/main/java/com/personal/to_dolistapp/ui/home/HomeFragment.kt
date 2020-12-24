package com.personal.to_dolistapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.personal.to_dolistapp.*
import com.personal.to_dolistapp.ui.todo.AddTodoActivity
import com.personal.to_dolistapp.ui.todo.TodoDetailActivity
import com.tiper.MaterialSpinner
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), TodoAdapter.RecyclerViewClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var todoAdapter: TodoAdapter? = null

    private lateinit var todoList: RecyclerView

    var labelSelectedName: String? = null
    val labelNameList = arrayListOf("All")

    // Listener for spinner
    private val spinnerListener by lazy {
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                labelSelectedName = labelNameList[position]
                val newQuery: Query
                if (labelSelectedName == "All") {
                    newQuery = db.collection("users").document(auth.currentUser!!.email!!)
                            .collection("todos").whereEqualTo("done", false)
                }
                else {
                    newQuery = db.collection("users").document(auth.currentUser!!.email!!)
                            .collection("todos").whereEqualTo("done", false).whereEqualTo("labelName", labelSelectedName)
                }
                val newOptions = FirestoreRecyclerOptions.Builder<Todo>()
                        .setQuery(newQuery, Todo::class.java)
                        .build()
                todoAdapter?.updateOptions(newOptions)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

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

        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(context, AddTodoActivity::class.java)
            startActivity(intent)
        }

        // Set up spinner --------------------------------------------------------------------------
        val spinner: Spinner = view.findViewById(R.id.spFilter)
        db.collection("users").document(auth.currentUser!!.email!!)
                .collection("labels").get()
                .addOnSuccessListener {
                    for (label in it) {
                        labelNameList.add(label.data["name"].toString())
                    }
//                    Log.d("cek", labelNameList.toString())
                    val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, labelNameList)
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.apply {
                        adapter = arrayAdapter
                        onItemSelectedListener = spinnerListener
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("cek", e.toString())
                }
    }

    private fun setupRecyclerView() {
        // Initialize adapter for rvTodo
        val query = db.collection("users").document(auth.currentUser!!.email!!)
                .collection("todos").whereEqualTo("done", false)
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

    override fun checkTodo(todo: Todo) {
        db.collection("users").document(auth.currentUser!!.email!!)
                .collection("todos").document(todo.id!!)
                .update("done", true)
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