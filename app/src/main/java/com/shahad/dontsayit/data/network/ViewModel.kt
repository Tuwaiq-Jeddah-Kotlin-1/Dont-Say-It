package com.shahad.dontsayit.data.network

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.database.DatabaseReference
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

    fun getUserById(id: String) = repo.getUserById(id)


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

    fun getScore(playerScoreRef: DatabaseReference): MutableLiveData<Long> {
        val score = MutableLiveData<Long>()
        playerScoreRef.get().addOnSuccessListener {
            Log.i("getScore 1  :", it.value.toString())
            score.postValue(it.value as Long)//java.lang.NullPointerException: null cannot be cast to non-null type kotlin.Long

            Log.i("getScore 2  :", it.value.toString())
        }
        return score
    }

    fun getRound(roomRef: DatabaseReference): MutableLiveData<Long> {
        val round = MutableLiveData<Long>()
        roomRef.get().addOnSuccessListener {
                  Log.i("getScore 1  :",it.value.toString())
            round.postValue(it.value as Long)
            //      Log.i("getScore 2  :",it.value.toString())
        }
        return round
    }

    fun resetState(stateRef: DatabaseReference, playersList: List<String>): MutableLiveData<Any> {
        val newState = mutableMapOf<String, String>()
        val stateList = MutableLiveData<Any>()

        playersList.forEach { newState[it] = "in" }
        stateRef.updateChildren(newState as Map<String, Any>)
        stateRef.get().addOnSuccessListener {
            stateList.postValue(it.value)
        }
        return stateList
    }
}