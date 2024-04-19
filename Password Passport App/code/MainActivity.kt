package com.example.passwordpassport

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var buttonGoToSignup: Button
    private lateinit var buttonGoToLogin: Button
    private lateinit var buttonGoToManager: Button
    private lateinit var logoutButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonGoToSignup = findViewById(R.id.buttonGoToSignup)
        buttonGoToLogin = findViewById(R.id.buttonGoToLogin)
        buttonGoToManager = findViewById(R.id.buttonGoToManager)
        logoutButton = findViewById(R.id.logoutButton)



        buttonGoToSignup.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }

        buttonGoToLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        buttonGoToManager.setOnClickListener {
            val intent = Intent(this, ManagerActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }
        }


    private fun logoutUser() {
        val dbHelper = DatabaseHelper(this)
        dbHelper.clearSession()
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()
        finish()
    }

}
