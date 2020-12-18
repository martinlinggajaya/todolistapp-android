package com.personal.to_dolistapp.ui.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.personal.to_dolistapp.R
import com.personal.to_dolistapp.Todo
import kotlinx.android.synthetic.main.activity_todo_detail.*
import java.text.SimpleDateFormat
import java.util.*

class TodoDetailActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    var todo: Todo? = Todo()

    companion object {
        const val DATA = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_detail)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        todo = intent.getParcelableExtra("DATA")
        Log.d("cek", todo.toString())

        // Set up textfields -----------------------------------------------------------------------
        etEditJudul.setText(todo?.name)
        etEditCatatan.setText(todo?.notes)

        // Set up switch -----------------------------------------------------------------------
        var withDate = true
        if (todo?.due == null) {
            withDate = false
            etEditDate.visibility = View.GONE
            etEditTime.visibility = View.GONE
            ivEditCalendar.visibility = View.GONE
            ivEditClock.visibility = View.GONE
        }

        // Set up calendar and time ----------------------------------------------------------------
        val tvDate: TextView = findViewById(R.id.etEditDate)
        tvDate.text = SimpleDateFormat("dd/MM/yyyy").format(todo?.due)
        val tvTime: TextView = findViewById(R.id.etEditTime)
        tvTime.text = SimpleDateFormat("HH : mm").format(todo?.due)

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
        }

        btnDelete.setOnClickListener {
            db.collection("users").document(auth.currentUser?.email.toString())
                .collection("todos")
                .document(etEditJudul.text.toString())
                .delete()
                .addOnSuccessListener { Log.d("Jadwal", "Delete Sukses")
                    Toast.makeText(this@TodoDetailActivity, "Delete Sukses", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e -> Log.d("Jadwal", e.toString()) }
        }

        btnEditCancel.setOnClickListener {
            finish()
        }

    }
}