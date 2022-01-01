package com.shahad.dontsayit.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shahad.dontsayit.*
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.model.User
import com.shahad.dontsayit.data.model.UserSuggestions
import com.shahad.dontsayit.data.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repo(val context: Context) {
    private lateinit var sharedPreferences: SharedPreferences
    private val collection = "user"
    private val tAG = "repo"
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val api = MockBuilder.mockAPI //created instance

    suspend fun userRequests(gameValue: UserSuggestions) = withContext(Dispatchers.IO) {
        api.userRequests(gameValue)
    }

    suspend fun getWords(): List<Word> = withContext(Dispatchers.IO) {
        api.getWords()
    }

    suspend fun getProfilePictures(): List<ProfilePicture> = withContext(Dispatchers.IO) {
        api.getProfilePictures()
    }

    suspend fun createUser(
        pic: String,
        username: String,
        email: String,
        pass: String,
        findNavController: NavController
    ) =
        withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    savePersist(username, email, pic)
                    storeUserData(User(username, email, pic), findNavController)
                    Log.d(tAG, "createUserWithEmail:success")

                } else {
                    Log.w(tAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private fun savePersist(username: String, email: String, pic: String) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(EMAIL, email)
        editor.putString(UID, auth.uid!!)
        editor.putString(USERNAME, username)
        editor.putString(PIC, pic)
        editor.apply()
        Log.d("$tAG: sharedPreferences: ", "savePersist:success")
        Toast.makeText(context, "Info saved! $username", Toast.LENGTH_LONG).show()
    }

    private fun storeUserData(user: User, findNavController: NavController) {
        db.collection(collection).document(auth.uid!!).set(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(tAG, "storeUserData:success")
                findNavController.navigate(R.id.action_signupFragment_to_homeFragment)
            } else {
                Log.w(tAG, "storeUserData:failure", task.exception)
                Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun getUserById(id: String): MutableLiveData<User> {
        val user = MutableLiveData<User>()
        db.collection(collection).document(id).get().addOnCompleteListener {

            it.addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("Firebase", "DocumentSnapshot data: ${document.data}")
                    user.postValue(document.toObject(User::class.java))
                } else {
                    Log.d("Firebase", "No such document")
                }
            }
        }.addOnFailureListener { exception ->
            Log.d("Firebase", "get failed with ", exception)
        }
        return user
    }

    suspend fun updateUsername(username: String) = withContext(Dispatchers.IO) {
        db.collection(collection).document(auth.currentUser!!.uid).update("username", username)
            .addOnCompleteListener {
                it.addOnSuccessListener {
                    Log.d("updateUser", "success")
                }
                it.addOnFailureListener { exception ->
                    Log.d("updateUser", exception.message.toString())
                }

            }
    }

    suspend fun updateProfilePic(picId: String) = withContext(Dispatchers.IO) {
        db.collection(collection).document(auth.currentUser!!.uid).update("profilePic", picId)
            .addOnCompleteListener {
                it.addOnSuccessListener {
                    Log.d("updateUser", "success")
                }
                it.addOnFailureListener { exception ->
                    Log.d("updateUser", exception.message.toString())
                }

            }
    }

    suspend fun signIn(email: String, pass: String, findNavController: NavController) =
        withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tAG, "signInWithEmail:success")
                    //get data from database and store in shared preference
                    /*val user = getUserById(auth.currentUser!!.uid)
                    savePersist(user!!.username, user.email)*/
                    savePersist("default", email,"default")
                    findNavController.navigate(R.id.action_loginFragment_to_homeFragment)

                } else {
                    Log.w(tAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT)
                        .show()

                }

            }
        }

}