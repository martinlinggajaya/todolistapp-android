package com.personal.to_dolistapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.personal.to_dolistapp.MainActivity
import com.personal.to_dolistapp.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString()
            val password = etLoginPassword.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        Log.d("cek", "Sign in success")
                        finish()
                        val mainIntent = Intent(this, MainActivity::class.java)
                        startActivity(mainIntent)   // start to main
                    } else {
                        Log.w("cek", "Sign in failed", task.exception)
                        Toast.makeText(baseContext, "Failed to login.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnToRegister.setOnClickListener {
            finish()
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        ibLoginShowPassword.setOnClickListener {
            if (etLoginPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ibLoginShowPassword.setImageResource(R.drawable.ic_visibility_hide);
                //Show Password
                etLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ibLoginShowPassword.setImageResource(R.drawable.ic_visibility_show);
                //Hide Password
                etLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
}