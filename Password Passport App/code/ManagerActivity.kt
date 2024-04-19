package com.example.passwordpassport

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ManagerActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var noPasswordsText: TextView
    private lateinit var addPasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        listView = findViewById(R.id.lvPasswords)
        noPasswordsText = findViewById(R.id.tvNoPasswords)
        addPasswordButton = findViewById(R.id.btnAddPassword)

        addPasswordButton.setOnClickListener {
            val intent = Intent(this, AddPasswordActivity::class.java)
            startActivity(intent)
        }

        loadPasswords()
    }

    private fun loadPasswords() {
        val dbHelper = DatabaseHelper(this)
        val username = dbHelper.getUsernamefromSession()

        if (username == null) {
            Toast.makeText(this, "No session found. Please log in.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            val passwordEntries = dbHelper.getPasswords(username)
            if (passwordEntries.isEmpty()) {
                noPasswordsText.visibility = View.VISIBLE
                listView.visibility = View.GONE
            } else {
                noPasswordsText.visibility = View.GONE
                listView.visibility = View.VISIBLE
                val items = passwordEntries.map { entry ->
                    val decryptedPassword = decryptData(entry.password, stringToSecretKey(entry.iv))
                    "${entry.websiteName} - $decryptedPassword"
                }
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)
                listView.adapter = adapter
            }
        } catch (e: Exception) {
            noPasswordsText.visibility = View.VISIBLE
            listView.visibility = View.GONE
            Toast.makeText(this, "Failed to load passwords. Please try again later.", Toast.LENGTH_LONG).show()
        }
    }
    fun stringToSecretKey(encodedKey: String): SecretKey {
        val decodedKey = Base64.decode(encodedKey, Base64.DEFAULT)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }

    fun decryptData(encryptedData: String, key: SecretKey): String? {
        val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)
        val iv = IvParameterSpec(decodedData.take(16).toByteArray())
        val encryptedText = decodedData.drop(16).toByteArray()

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val decrypted = cipher.doFinal(encryptedText)

        return decrypted.toString(Charsets.UTF_8)
    }}