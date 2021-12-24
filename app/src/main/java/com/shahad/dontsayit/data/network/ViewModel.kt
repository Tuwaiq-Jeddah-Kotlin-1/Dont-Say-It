package com.shahad.dontsayit.data.network

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.shahad.dontsayit.data.model.User
import com.shahad.dontsayit.data.model.Word
import kotlinx.coroutines.launch

class ViewModel(context: Application) : AndroidViewModel(context) {

    private val repo = Repo(context)

    fun getWords(): LiveData<List<Word>> {
        var wordList = MutableLiveData<List<Word>>()
        try {
            viewModelScope.launch {
                wordList.postValue(repo.getWords())
            }
        } catch (w: Throwable) {
            Log.i("viewmodel", w.message.toString())
        }
        return wordList
    }

    fun getUserById(id: String)= repo.getUserById(id)


    fun updateUser(username: String) {
        viewModelScope.launch {
            repo.updateUser(username)
        }
    }

    fun signUp(email: String, pass: String, username: String, findNavController: NavController) {
        viewModelScope.launch {
            repo.createUser(username, email, pass, findNavController)
        }
    }

    fun signIn(email: String, pass: String, findNavController: NavController) {
        viewModelScope.launch {
            repo.signIn(email, pass, findNavController)

        }
    }
}