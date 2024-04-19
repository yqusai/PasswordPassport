package com.example.passwordpassport

data class PasswordEntry(
    val id: Int,
    val websiteName: String,
    val password: String,
    val passwordType: String,
    val iv: String
)
