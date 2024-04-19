package com.example.passwordpassport

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec



class AddPasswordActivity : AppCompatActivity() {

    private lateinit var editTextApplicationName: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var spinnerPasswordType: Spinner
    private lateinit var buttonSavePassword: Button
    private lateinit var dbHelper: DatabaseHelper  // Declare dbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_password)

        dbHelper = DatabaseHelper(this)

        editTextApplicationName = findViewById(R.id.editTextApplicationName)
        editTextPassword = findViewById(R.id.editTextPassword)
        spinnerPasswordType = findViewById(R.id.spinnerPasswordType)
        buttonSavePassword = findViewById(R.id.buttonSavePassword)



        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.password_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPasswordType.adapter = adapter

        buttonSavePassword.setOnClickListener {
            val appName = editTextApplicationName.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val passwordType = spinnerPasswordType.selectedItem.toString()


            if (appName.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }

            val username = dbHelper.getUsernamefromSession()
            val encryptionKey = generateKey()
            val iv = generateIv()
            val encryptedPassword = encryptData(password, encryptionKey, iv)

            if (username != null) {
                dbHelper.createUserPasswordTable(username)
                dbHelper.addPassword(username,appName,encryptedPassword, passwordType, encryptionKey)
                Toast.makeText(this, "Password saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Session not found, please log in again.", Toast.LENGTH_LONG).show()
            }
        }
    }


    fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    fun generateIv(): IvParameterSpec {
        val random = java.security.SecureRandom()
        val ivBytes = ByteArray(16)
        random.nextBytes(ivBytes)
        return IvParameterSpec(ivBytes)
    }

    fun encryptData(data: String, key: SecretKey, iv: IvParameterSpec): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(iv.iv + encrypted, Base64.DEFAULT)
    }

}






