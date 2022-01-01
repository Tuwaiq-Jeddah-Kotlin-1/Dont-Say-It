package com.shahad.dontsayit.data.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.database.DatabaseReference
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.model.UserSuggestions
import com.shahad.dontsayit.data.model.Word
import kotlinx.coroutines.launch

class ViewModel(context: Application) : AndroidViewModel(context) {

    private val repo = Repo(context)

    fun userRequests(gameVal : UserSuggestions){
        viewModelScope.launch {
            try {
                repo.userRequests(gameVal)
            }catch (e: Throwable){
                Log.e("Mock API...","Problem AT user requests ${e.localizedMessage}")
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
            }//Unable to resolve host "61af4ca33e2aba0017c49187.mockapi.io": No address associated with hostname
        } catch (w: Throwable) {
            Log.i("viewmodel", w.message.toString())
        }
        return pictureList
    }

    fun getUserById(id: String)=    repo.getUserById(id)

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

    fun signUp(pic:String,email: String, pass: String, username: String, findNavController: NavController) {
        viewModelScope.launch {
            repo.createUser(pic,username, email, pass, findNavController)
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
    fun checkConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            else -> false
        }
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