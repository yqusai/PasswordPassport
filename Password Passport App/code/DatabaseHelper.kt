package com.example.passwordpassport

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.crypto.SecretKey
import java.security.MessageDigest
import android.content.ContentValues
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import android.util.Log


class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "UserManager.db"
        private const val AES_MODE = "AES/CBC/PKCS5Padding"
        private const val TABLE_USERS = "users"
        private const val TABLE_SESSION = "UserSession"
        private const val KEY_ID = "id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_APPNAME = "app_name"
        private const val KEY_PASS = "password"
        private const val KEY_PASSTYPE = "password_type"
        private const val KEY_IV = "iv"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USERS_TABLE = """
            CREATE TABLE $TABLE_USERS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                email TEXT,
                password TEXT
            )
        """.trimIndent()
        db.execSQL(CREATE_USERS_TABLE)

        val CREATE_SESSION_TABLE = """
            CREATE TABLE $TABLE_SESSION (
                $KEY_USERNAME TEXT,
                $KEY_EMAIL TEXT
            )
        """.trimIndent()
        db.execSQL(CREATE_SESSION_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SESSION")
        onCreate(db)
    }


    fun clearSession() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_SESSION")
    }

    fun createUserPasswordTable(username: String) {
        val db = this.writableDatabase
        val tableName = username

        val CREATE_PASSWORD_TABLE = """
        CREATE TABLE IF NOT EXISTS $tableName (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            app_name TEXT,
            password TEXT,
            password_type TEXT,
            iv TEXT
        )
    """.trimIndent()

        db.execSQL(CREATE_PASSWORD_TABLE)
    }


    fun encrypt(key: String, data: String): Pair<String, String> {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = Base64.encodeToString(cipher.iv, Base64.DEFAULT)
        val encryptedData = Base64.encodeToString(cipher.doFinal(data.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        return Pair(encryptedData, iv)
    }


    private fun decrypt(key: String, encryptedDataWithIv: String): String {
        val ivAndEncryptedData = Base64.decode(encryptedDataWithIv, Base64.DEFAULT)
        val iv = ivAndEncryptedData.copyOfRange(0, 16)
        val encryptedData = ivAndEncryptedData.copyOfRange(16, ivAndEncryptedData.size)
        val cipher = Cipher.getInstance(AES_MODE)
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val original = cipher.doFinal(encryptedData)
        return String(original, Charsets.UTF_8)
    }


    fun addPassword(username: String, websiteName: String, encryptedPassword: String, passwordType: String, iv: SecretKey) {
        val tableName = username
        val db = this.writableDatabase
        val iv = Base64.encodeToString(iv.encoded, Base64.DEFAULT)
        val values = ContentValues().apply {
            put(KEY_APPNAME, websiteName)
            put(KEY_PASS, encryptedPassword)
            put(KEY_PASSTYPE, passwordType)
            put(KEY_IV, iv)
        }


        db.insert(tableName, null, values)
        db.close()
    }

    fun addUser(username: String, email: String, password: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_USERNAME, username)
            put(KEY_EMAIL, email)
            put(KEY_PASSWORD, hashPassword(password))
        }
        db.insert(TABLE_USERS, null, values)
        db.close()
    }

    fun getPasswords(tableName: String): List<PasswordEntry> {
        val db = this.readableDatabase
        val passwordList = mutableListOf<PasswordEntry>()
        val cursor = db.rawQuery("SELECT * FROM $tableName", null)
        val idIndex = cursor.getColumnIndex("id")
        val websiteNameIndex = cursor.getColumnIndex("app_name")
        val passwordIndex = cursor.getColumnIndex("password")
        val passwordTypeIndex = cursor.getColumnIndex("password_type")
        val ivIndex = cursor.getColumnIndex("iv")

        if (idIndex == -1 || websiteNameIndex == -1 || passwordIndex == -1 ||
            passwordTypeIndex == -1 || ivIndex == -1) {

            throw IllegalStateException("One or more columns are missing in the database table: $tableName")
        }

        while (cursor.moveToNext()) {
            val id = cursor.getInt(idIndex)
            val websiteName = cursor.getString(websiteNameIndex)
            val encryptedPassword = cursor.getString(passwordIndex)
            val passwordType = cursor.getString(passwordTypeIndex)
            val iv = cursor.getString(ivIndex)

            val password = decrypt(getEncryptionKey(), encryptedPassword)
            passwordList.add(PasswordEntry(id, websiteName, password, passwordType,iv))
        }

        cursor.close()
        db.close()
        return passwordList
    }

    fun getEncryptionKey(): String {
        val hardcodedKeyBase64 = "58CpQ1tM6/t5+GKSeIIVxbLNWF4N+0oU+ZkdD1pufhI=\n"
        return hardcodedKeyBase64
    }



    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }





    fun validateUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(KEY_PASSWORD),
            "$KEY_EMAIL =?",
            arrayOf(email),
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            val storedPassword = cursor.getString(0)
            cursor.close()
            db.close()
            return storedPassword == hashPassword(password)
        }
        cursor.close()
        db.close()
        return false
    }


    fun getUsernameByEmail(email: String): String {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(KEY_USERNAME),
            "$KEY_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        val username: String = if (cursor.moveToFirst()) {
            val indexUsername = cursor.getColumnIndex(KEY_USERNAME)
            if (indexUsername != -1) {
                cursor.getString(indexUsername)
            } else {
                cursor.close()
                db.close()
                throw IllegalStateException("Username column index not found")
            }
        } else {
            cursor.close()
            db.close()
            throw IllegalStateException("Email not found: $email")
        }
        cursor.close()
        db.close()
        return username
    }


    @SuppressLint("Range")
    fun getUsernamefromSession(): String? {
        val db = this.readableDatabase
        val selectQuery = "SELECT $KEY_USERNAME FROM $TABLE_SESSION LIMIT 1"
        db.rawQuery(selectQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(KEY_USERNAME))
            }
        }
        return null
    }

    fun startSession(username: String, email: String) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_SESSION")
        val values = ContentValues().apply {
            put(KEY_USERNAME, username)
            put(KEY_EMAIL, email)
        }
        db.insert(TABLE_SESSION, null, values)
    }

}


