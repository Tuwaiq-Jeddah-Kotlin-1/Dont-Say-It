package com.shahad.dontsayit

import android.util.Patterns
import android.widget.EditText
import java.util.regex.Pattern


const val PREFERENCE = "preference"
const val EMAIL = "EMAIL"
const val CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
const val NOTIFICATION_NAME = "NOTIFICATION"

private val hasUppercase = Pattern.compile("[A-Z]")
private val hasLowercase = Pattern.compile("[a-z]")
private val hasNumber = Pattern.compile("\\d")

fun EditText.checkIfEmpty(str: String): Boolean {
    return if (str.isEmpty()) {
        this.error = "Empty field"
        this.requestFocus()
        false
    } else {
        true
    }
}

fun EditText.validateMail(email: String): Boolean {
    return this.checkIfEmpty(email) || if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        this.error = "Please enter a valid email address"
        this.requestFocus()
        false
    } else {
        true
    }
}

fun EditText.validatePasswords(password: String): Boolean {
    if (password.isEmpty()) {
        this.error = "Empty field"
        this.requestFocus()
        return false
    }
    if (password.length <= 8) {
        this.error = "Password is too short. Needs to have 7 characters"
        this.requestFocus()
        return false
    }
    if (!hasUppercase.matcher(password).find()) {
        this.error = "Password needs to have at least one upper case letter"
        this.requestFocus()
        return false
    }
    if (!hasLowercase.matcher(password).find()) {
        this.error = "Password needs to have at least one lower case letter"
        this.requestFocus()
        return false
    }
    if (!hasNumber.matcher(password).find()) {
        this.error = "Password needs to have at least one number"
        this.requestFocus()
        return false
    }
    return true

}

fun EditText.validateUsername(userName: String): Boolean {
    return when {
        userName.isEmpty() -> {
            this.error = "Provide your username please"
            this.requestFocus()
            false
        }
        userName.length > 15 -> {
            this.error = "Username too long"
            false
        }
        userName.length < 6 -> {
            this.error = "Username too short"
            false
        }
        else -> {
            true
        }
    }
}