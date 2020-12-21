package com.personal.to_dolistapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.personal.to_dolistapp.ui.auth.LoginActivity
import com.personal.to_dolistapp.ui.todo.AddTodoActivity
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_label_list, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Initialize auth and db
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        // Set up navigation header text
        if (auth.currentUser != null) {
            var name = ""
            val email = auth.currentUser!!.email!!
            db.collection("users").document(email)
                    .get()
                    .addOnSuccessListener {
                        val data = it?.data as MutableMap<String, *>
                        name = data.getValue("name").toString()
                        tvName.text = "Welcome, $name"
                        tvEmailAddress.text = email
                    }
                    .addOnFailureListener {
                        e -> Log.d("cek", e.toString())
                    }
        }

        // Set up logout button
        navView.menu.findItem(R.id.nav_signout).setOnMenuItemClickListener {
            auth.signOut()
            Log.d("cek", "Signed out")
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in
        val currentUser = auth.currentUser
        Log.d("cek", currentUser.toString())
        if (currentUser == null) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
}