package com.personal.to_dolistapp.ui.label

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.personal.to_dolistapp.R
import com.tiper.MaterialSpinner
import kotlinx.android.synthetic.main.activity_add_label.*

class AddLabelActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val labelColors = arrayListOf<String>("Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Pink")
    private var selectedColor: String = ""

    // Listener for spinner
    private val spinnerListener by lazy {
        object : MaterialSpinner.OnItemSelectedListener {
            override fun onItemSelected(parent: MaterialSpinner, view: View?, position: Int, id: Long) {
                selectedColor = labelColors[position] // position 0 = no label
                selectedColor = selectedColor.toLowerCase()
//                Log.v("MaterialSpinner", "onItemSelected parent=${parent.id}, position=$position")
                when (selectedColor) {
                    "red" -> ivLabelIndicator.setImageResource(R.drawable.circle_label_red)
                    "orange" -> ivLabelIndicator.setImageResource(R.drawable.circle_label_orange)
                    "yellow" -> ivLabelIndicator.setImageResource(R.drawable.circle_label_yellow)
                    "green" -> ivLabelIndicator.setImageResource(R.drawable.circle_label_green)
                    "blue" -> ivLabelIndicator.setImageResource(R.drawable.circle_label_blue)
                    "purple" -> ivLabelIndicator.setImageResource(R.drawable.circle_label_purple)
                    "pink" -> ivLabelIndicator.setImageResource(R.drawable.circle_label_pink)
                }
            }
            override fun onNothingSelected(parent: MaterialSpinner) {
//                Log.v("MaterialSpinner", "onNothingSelected parent=${parent.id}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_label)

        // Set up auth and db
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set up spinner --------------------------------------------------------------------------
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labelColors)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spAddLabelColor.apply {
            adapter = arrayAdapter
            onItemSelectedListener = spinnerListener
        }

        // Set up button ---------------------------------------------------------------------------
        btnAddLabel.setOnClickListener {
            val labelName = etAddLabelName.text.toString()
            val data = hashMapOf(
                    "name" to labelName,
                    "color" to selectedColor
            )
            db.collection("users").document(auth.currentUser!!.email!!)
                    .collection("labels").document(labelName).set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        e -> Log.d("cek", e.toString())
                        Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
                    }
        }

        btnLabelCancel.setOnClickListener {
            finish()
        }

    }
}