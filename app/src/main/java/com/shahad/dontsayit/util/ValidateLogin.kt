package com.shahad.dontsayit.util

import java.util.regex.Pattern


private val hasUppercase = Pattern.compile("[A-Z]")
private val hasLowercase = Pattern.compile("[a-z]")
private val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
fun checkIfEmpty(str: String): String? {
    return if (str.isEmpty()) {
        "Required"
    } else {
        null
    }
    
}

fun validateMail(email: String): String? {
    return if (email.isEmpty()) {
        "Required"
    } else if (!emailPattern.matcher(email).matches()) {
        "Invalid"
    } else {
        null
    }
}

fun validatePasswords(password: String): String? {
    return if (password.isEmpty()) {
        "Required"
    } else if (password.length < 6) {
        "Too short, Needs at least 6 characters"
    } else if (!hasUppercase.matcher(password).find()) {
        "Need at least one upper case letter"
    } else if (!hasLowercase.matcher(password).find()) {
        "Need at least one lower case letter"
    } else {
        null
    }
}

fun validateUsername(userName: String): String? {
    return when {
        userName.isEmpty() -> {
            "Required"
        }
        userName.length > 10 -> {
            "Too long. Max 10 characters"
        }
        userName.length < 6 -> {
            "Too short, Needs at least 6 characters"
        }
        else -> {
            null
        }
    }
}