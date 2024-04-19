package com.example.passwordpassport


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var buttonGoHome: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        dbHelper = DatabaseHelper(this)

        usernameInput = findViewById(R.id.editTextUsername)
        emailInput = findViewById(R.id.editTextEmail)
        passwordInput = findViewById(R.id.editTextPassword)
        registerButton = findViewById(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()


            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {

                dbHelper.addUser(username, email, password)
                Toast.makeText(this, "Registration successful", Toast.LENGTH_LONG).show()

                usernameInput.text.clear()
                emailInput.text.clear()
                passwordInput.text.clear()
                finish()
            }
            
        }

    }
}
