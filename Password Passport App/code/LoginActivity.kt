package com.example.passwordpassport

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        emailInput = findViewById(R.id.editTextEmail)
        passwordInput = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()



            if (dbHelper.validateUser(email, password)) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                val username = dbHelper.getUsernameByEmail(email)
                if (username != null) {
                    dbHelper.startSession(username, email)
                } else {
                    Toast.makeText(this, "Error: User not found.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}
