package com.personal.to_dolistapp.ui.label

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.personal.to_dolistapp.*
import kotlinx.android.synthetic.main.fragment_label_list.*

class LabelListFragment : Fragment(), LabelAdapter.RecyclerViewClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var labelAdapter: LabelAdapter? = null
    private lateinit var labelList: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_label_list, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        labelList = view.findViewById(R.id.rvLabel)
        if (auth.currentUser != null) {
            setupRecyclerView()
        }

        labelAdapter?.listener = this

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddLabel.setOnClickListener {
            val intent = Intent(context, AddLabelActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        // Initialize adapter for rvLabel
        val query = db.collection("users").document(auth.currentUser!!.email!!).collection("labels")
        val options = FirestoreRecyclerOptions.Builder<Label>()
                .setQuery(query, Label::class.java)
                .build()
        labelAdapter = LabelAdapter(options)
        labelAdapter!!.notifyDataSetChanged()
        // Initialize rvLabel
        labelList.layoutManager = LinearLayoutManager(context)
        labelList.adapter = labelAdapter
    }

    override fun deleteLabel(label: Label) {
        db.collection("users").document(auth.currentUser!!.email!!)
                .collection("labels").document(label.name).delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Label terhapus", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    e -> Log.d("cek", e.toString())
                    Toast.makeText(context, "Label gagal dihapus", Toast.LENGTH_SHORT).show()
                }
    }

    override fun onStart() {
        super.onStart()
        labelAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        labelAdapter?.stopListening()
    }
}