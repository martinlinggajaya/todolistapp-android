package com.personal.to_dolistapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.personal.to_dolistapp.MainActivity
import com.personal.to_dolistapp.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etRegisterName.requestFocus()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnRegister.setOnClickListener {
            val name = etRegisterName.text.toString()
            val email = etRegisterEmail.text.toString()
            val password = etRegisterPassword.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Register success
                        Log.d("cek", "Register success")
                        // Add new user to database
                        val newUser = hashMapOf(
                            "email" to email,
                            "name" to name
                        )
                        db.collection("users").document(email)
                            .set(newUser)

                        // Redirect to main
                        finish()
                        val mainIntent = Intent(this, MainActivity::class.java)
                        startActivity(mainIntent)
                    }
                    else {
                        // Register failed
                        Log.w("cek", "Register failed", task.exception)
                        Toast.makeText(baseContext, "Failed to register.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnToLogin.setOnClickListener {
            finish()
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        ibRegisterShowPassword.setOnClickListener {
            if (etRegisterPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ibRegisterShowPassword.setImageResource(R.drawable.ic_visibility_hide);
                //Show Password
                etRegisterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ibRegisterShowPassword.setImageResource(R.drawable.ic_visibility_show);
                //Hide Password
                etRegisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
}