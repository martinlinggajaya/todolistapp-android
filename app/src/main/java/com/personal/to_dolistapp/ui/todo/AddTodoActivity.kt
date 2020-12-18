package com.personal.to_dolistapp.ui.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.personal.to_dolistapp.Label
import com.personal.to_dolistapp.R
import com.tiper.MaterialSpinner
import kotlinx.android.synthetic.main.activity_add_todo.*
import kotlinx.android.synthetic.main.item_todo.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class AddTodoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    var labelSelected : Label? = null
    val labelList = arrayListOf<Label>()

    // Listener for spinner
    private val spinnerListener by lazy {
        object : MaterialSpinner.OnItemSelectedListener {
            override fun onItemSelected(parent: MaterialSpinner, view: View?, position: Int, id: Long) {
                labelSelected = labelList[position - 1] // position 0 = no label
                Log.v("MaterialSpinner", "onItemSelected parent=${parent.id}, position=$position")
//                parent.focusSearch(View.FOCUS_UP)?.requestFocus()
            }
            override fun onNothingSelected(parent: MaterialSpinner) {
                Log.v("MaterialSpinner", "onNothingSelected parent=${parent.id}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_todo)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Set up spinner --------------------------------------------------------------------------
        db.collection("users").document(auth.currentUser!!.email!!)
                .collection("labels").get()
                .addOnSuccessListener {
                    for (label in it) {
                        val newLabel = Label(label.data["name"].toString(),
                                label.data["color"].toString())
                        labelList.add(newLabel)
                    }
                    val labelNames = arrayListOf<String>("No Label")
                    labelList.forEach {
                        label -> labelNames.add(label.name)
                    }
                    val spinner = findViewById<MaterialSpinner>(R.id.spAddLabel)
                    Log.d("cek", labelNames.toString())
                    val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labelNames)
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.apply {
                        adapter = arrayAdapter
                        onItemSelectedListener = spinnerListener
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("cek", e.toString())
                }

        // Set up switch ---------------------------------------------------------------------------
        var withDate = false
        etAddDate.visibility = View.GONE
        etAddTime.visibility = View.GONE
        ivAddCalendar.visibility = View.GONE
        ivAddClock.visibility = View.GONE
        swAddDateTime.setOnCheckedChangeListener { _, isChecked ->
            withDate = isChecked
            if (!isChecked) {
                etAddDate.visibility = View.GONE
                etAddTime.visibility = View.GONE
                ivAddCalendar.visibility = View.GONE
                ivAddClock.visibility = View.GONE
            }
            else {
                etAddDate.visibility = View.VISIBLE
                etAddTime.visibility = View.VISIBLE
                ivAddCalendar.visibility = View.VISIBLE
                ivAddClock.visibility = View.VISIBLE
            }
        }

        // Set up date and time picker -------------------------------------------------------------
        val tvDate: TextView = findViewById(R.id.etAddDate)
        tvDate.text = SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis())
        val tvTime: TextView = findViewById(R.id.etAddTime)
        tvTime.text = SimpleDateFormat("HH : mm").format(System.currentTimeMillis())

        var cal = Calendar.getInstance()
        Log.d("cek", "cal.time = " + cal.time.toString())
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.TAIWAN)
            tvDate.text = sdf.format(cal.time)
        }
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            tvTime.text = SimpleDateFormat("HH:mm").format(cal.time)
        }
        tvDate.setOnClickListener {
            DatePickerDialog(
                this@AddTodoActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        tvTime.setOnClickListener {
            TimePickerDialog(
                this@AddTodoActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                    Calendar.MINUTE
                ), true
            ).show()
        }

        // Set up buttons --------------------------------------------------------------------------
        btnAddSimpan.setOnClickListener{
            var due: Calendar? = cal
            if (!withDate) {
                due = null
            }
            val datasimpan = hashMapOf(
                    "name" to etAddJudul.text.toString(),
                    "due" to due?.time,
                    "notes" to etAddCatatan.text.toString(),
                    "labelName" to labelSelected?.name,
                    "labelColor" to labelSelected?.color,
                    "done" to false,
            )
            db.collection("users").document(auth.currentUser?.email.toString())
                    .collection("todos")
                    .add(datasimpan)
                    .addOnSuccessListener {
                        Toast.makeText(this@AddTodoActivity, "Sukses", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@AddTodoActivity, "Gagal", Toast.LENGTH_SHORT).show()
                        Log.d("cek", e.toString())
                    }
        }

        btnAddCancel.setOnClickListener {
            finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position != 0) {
            labelSelected = labelList[position]
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}