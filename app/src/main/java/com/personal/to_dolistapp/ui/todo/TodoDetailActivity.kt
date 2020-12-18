package com.personal.to_dolistapp.ui.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.personal.to_dolistapp.Label
import com.personal.to_dolistapp.R
import com.personal.to_dolistapp.Todo
import com.tiper.MaterialSpinner
import kotlinx.android.synthetic.main.activity_add_todo.*
import kotlinx.android.synthetic.main.activity_todo_detail.*
import java.text.SimpleDateFormat
import java.util.*

class TodoDetailActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    var todo: Todo? = Todo()

    var labelSelected : Label? = null
    val labelList = arrayListOf<Label>()

    // Listener for spinner
    private val spinnerListener by lazy {
        object : MaterialSpinner.OnItemSelectedListener {
            override fun onItemSelected(parent: MaterialSpinner, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    labelSelected = null
                }
                else {
                    labelSelected = labelList[position - 1] // position 0 = no label
                }
                Log.v("MaterialSpinner", "onItemSelected parent=${parent.id}, position=$position")
//                parent.focusSearch(View.FOCUS_UP)?.requestFocus()
            }
            override fun onNothingSelected(parent: MaterialSpinner) {
                Log.v("MaterialSpinner", "onNothingSelected parent=${parent.id}")
            }
        }
    }

    companion object {
        const val DATA = "todo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_detail)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        todo = intent.getParcelableExtra(DATA)
        Log.d("cek", "Todo: $todo")

        // Set up textfields -----------------------------------------------------------------------
        etEditJudul.setText(todo?.name)
        etEditCatatan.setText(todo?.notes)

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
                    val spinner = findViewById<MaterialSpinner>(R.id.spEditLabel)
//                    Log.d("cek", labelNames.toString())
                    val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, labelNames)
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.apply {
                        adapter = arrayAdapter
                        onItemSelectedListener = spinnerListener
                    }
                    spinner.selection = labelNames.indexOf(todo?.labelName)
                }
                .addOnFailureListener { e ->
                    Log.d("cek", e.toString())
                }

        // Set up switch ---------------------------------------------------------------------------
        var withDate = true
        if (todo?.due == null) {
            withDate = false
            swEditDateTime.isChecked = false
            etEditDate.visibility = View.GONE
            etEditTime.visibility = View.GONE
            ivEditCalendar.visibility = View.GONE
            ivEditClock.visibility = View.GONE
        }
        else {
            swEditDateTime.isChecked = true
        }
        swEditDateTime.setOnCheckedChangeListener { _, isChecked ->
            withDate = isChecked
            if (!isChecked) {
                etEditDate.visibility = View.GONE
                etEditTime.visibility = View.GONE
                ivEditCalendar.visibility = View.GONE
                ivEditClock.visibility = View.GONE
            }
            else {
                etEditDate.visibility = View.VISIBLE
                etEditTime.visibility = View.VISIBLE
                ivEditCalendar.visibility = View.VISIBLE
                ivEditClock.visibility = View.VISIBLE
            }
        }

        // Set up calendar and time ----------------------------------------------------------------
        val tvDate: TextView = findViewById(R.id.etEditDate)
        val tvTime: TextView = findViewById(R.id.etEditTime)
        if (todo?.due != null) {
            tvDate.text = SimpleDateFormat("dd/MM/yyyy").format(todo!!.due)
            tvTime.text = SimpleDateFormat("HH : mm").format(todo!!.due)
        }
        else {
            tvDate.text = SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis())
            tvTime.text = SimpleDateFormat("HH : mm").format(System.currentTimeMillis())
        }

        val cal = Calendar.getInstance()
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
            DatePickerDialog(this@TodoDetailActivity, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        tvTime.setOnClickListener {
            TimePickerDialog(this@TodoDetailActivity, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(
                    Calendar.MINUTE), true).show()
        }

        // Set up buttons --------------------------------------------------------------------------
        btnEditSimpan.setOnClickListener {
            var due: Calendar? = cal
            if (!withDate) {
                due = null
            }
            val datasimpan = hashMapOf(
                    "name" to etEditJudul.text.toString(),
                    "due" to due?.time,
                    "notes" to etEditCatatan.text.toString(),
                    "labelName" to labelSelected?.name,
                    "labelColor" to labelSelected?.color,
                    "done" to false,
            )
            db.collection("users").document(auth.currentUser?.email.toString())
                    .collection("todos").document(todo!!.id!!)
                    .set(datasimpan)
                    .addOnSuccessListener {
                        Toast.makeText(this@TodoDetailActivity, "Sukses", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@TodoDetailActivity, "Gagal", Toast.LENGTH_SHORT).show()
                        Log.d("cek", e.toString())
                    }
        }

        btnDelete.setOnClickListener {
            db.collection("users").document(auth.currentUser?.email.toString())
                .collection("todos").document(todo!!.id!!)
                .delete()
                .addOnSuccessListener { Log.d("Jadwal", "Delete Sukses")
                    Toast.makeText(this@TodoDetailActivity, "Delete Sukses", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e -> Log.d("Jadwal", e.toString()) }
        }

        btnEditCancel.setOnClickListener {
            finish()
        }

    }
}