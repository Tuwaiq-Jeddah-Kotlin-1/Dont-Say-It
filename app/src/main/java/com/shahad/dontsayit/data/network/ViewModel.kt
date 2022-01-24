package com.shahad.dontsayit.data.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.animation.Animation
import android.widget.ImageButton
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.google.firebase.database.DatabaseReference
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.model.UserSuggestions
import com.shahad.dontsayit.data.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewModel(context: Application) : AndroidViewModel(context) {


    private val repo = Repo(context)


    fun userRequests(gameVal: UserSuggestions) {
        viewModelScope.launch {
            try {
                repo.userRequests(gameVal)
            } catch (e: Throwable) {
                Log.e("Mock API...", "Problem AT user requests ${e.localizedMessage}")
            }
        }
    }

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

    fun getProfilePictures(): LiveData<List<ProfilePicture>> {
        var pictureList = MutableLiveData<List<ProfilePicture>>()
        try {
            viewModelScope.launch {
                pictureList.postValue(repo.getProfilePictures())
            }
        } catch (w: Throwable) {
            Log.i("viewmodel", w.message.toString())
        }
        return pictureList
    }

    fun getUserById(id: String) = repo.getUserById(id)

    fun updateUsername(username: String) {
        viewModelScope.launch {
            repo.updateUsername(username)
        }
    }

    fun updateProfilePic(pic: String) {
        viewModelScope.launch {
            repo.updateProfilePic(pic)
        }
    }

    fun signUp(
        pic: String,
        email: String,
        pass: String,
        username: String,
        findNavController: NavController
    ) {
        viewModelScope.launch {
            repo.createUser(pic, username, email, pass, findNavController)
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

            score.postValue(it.value as Long)
            Log.i("getScore ", it.value.toString())
        }
        return score
    }

    fun getRound(roomRef: DatabaseReference): MutableLiveData<Long> {
        val round = MutableLiveData<Long>()
        roomRef.get().addOnSuccessListener {
            round.postValue(it.value as Long)
            Log.i("getRound", it.value.toString())
        }
        return round
    }

    fun checkConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun resetState(
        stateRef: DatabaseReference,
        playersList: MutableSet<String>
    ): MutableLiveData<Any> {
        val newState = mutableMapOf<String, String>()
        val stateList = MutableLiveData<Any>()

        playersList.forEach { newState[it] = "in" }
        stateRef.updateChildren(newState as Map<String, String>)
        stateRef.get().addOnSuccessListener {
            Log.e("resetState", "${it.value}")
            stateList.postValue(it.value)
        }
        return stateList
    }
}