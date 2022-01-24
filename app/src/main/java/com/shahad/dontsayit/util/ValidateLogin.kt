package com.shahad.dontsayit.util

import android.content.res.Resources
import com.shahad.dontsayit.R
import java.util.regex.Pattern


private val hasUppercase = Pattern.compile("[A-Z]")
private val hasLowercase = Pattern.compile("[a-z]")
private val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
fun checkIfEmpty(str: String): String? {
    return if (str.isEmpty()) {
        Resources.getSystem().getString(R.string.required)
    } else {
        null
    }
}

fun validateMail(email: String): String? {
    return if (email.isEmpty()) {
        Resources.getSystem().getString(R.string.required)
    } else if (!emailPattern.matcher(email).matches()) {
        Resources.getSystem().getString(R.string.Invalid)
    } else {
        null
    }
}

fun validatePasswords(password: String): String? {
    return if (password.isEmpty()) {
        Resources.getSystem().getString(R.string.required)
    } else if (password.length < 6) {
        Resources.getSystem().getString(R.string.tooshort)
    } else if (!hasUppercase.matcher(password).find()) {
        Resources.getSystem().getString(R.string.oneupper)
    } else if (!hasLowercase.matcher(password).find()) {
        Resources.getSystem().getString(R.string.onelower)
    } else {
        null
    }
}

fun validateUsername(userName: String): String? {
    return when {
        userName.isEmpty() -> {
            Resources.getSystem().getString(R.string.required)
        }
        userName.length > 10 -> {
            Resources.getSystem().getString(R.string.toolong)
        }
        userName.length < 6 -> {
            Resources.getSystem().getString(R.string.tooshort)
        }
        else -> {
            null
        }
    }
}