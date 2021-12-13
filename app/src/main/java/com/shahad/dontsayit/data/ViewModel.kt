package com.shahad.dontsayit.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewModel(context: Application) : AndroidViewModel(context)  {

    private val repo = Repo(context)

    fun signUp(email: String, pass: String, username: String) {
        viewModelScope.launch {
            repo.createUser(email, pass)
        }
    }

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            repo.signIn(email, pass)
        }
    }
}