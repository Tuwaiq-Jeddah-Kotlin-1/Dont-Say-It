package com.shahad.dontsayit.data.network

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.model.ProfilePicture
import com.shahad.dontsayit.data.model.User
import com.shahad.dontsayit.data.model.UserSuggestions
import com.shahad.dontsayit.data.model.Word
import com.shahad.dontsayit.util.savePersist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repo(val context: Context) {
    private val collection = "user"
    private val tAG = "repo"
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val api = MockBuilder.mockAPI

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
                    updateUserProfile(username, Uri.parse(pic))
                    savePersist(username, email, pic, auth.uid!!, context)
                    storeUserData(User(username, email, pic), findNavController)
                    Log.d(tAG, "createUserWithEmail:success")

                } else {
                    Log.w(tAG, "createUserWithEmail:failure", task.exception)

                }
            }
        }

    fun updateUserProfile(
        displayname: String = auth.currentUser!!.displayName ?: "",
        pic: Uri = auth.currentUser!!.photoUrl ?: Uri.parse("")
    ) {
        auth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(displayname)
                .setPhotoUri(pic)
                .build()
        )
    }

    suspend fun signIn(email: String, pass: String, findNavController: NavController) =
        withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tAG, "signInWithEmail:success")
                    savePersist(
                        auth.currentUser!!.displayName!!,
                        email,
                        auth.currentUser!!.photoUrl!!.toString(),
                        auth.uid!!,
                        context
                    )

                    findNavController.navigate(R.id.action_loginFragment_to_homeFragment)

                } else {
                    Log.w(tAG, "signInWithEmail:failure", task.exception)
                }

            }
        }

    private fun storeUserData(user: User, findNavController: NavController) {
        db.collection(collection).document(auth.uid!!).set(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                findNavController.navigate(R.id.action_signupFragment_to_homeFragment)
                Log.d(tAG, "storeUserData:success")
            } else {
                Log.w(tAG, "storeUserData:failure", task.exception)

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
                    updateUserProfile(username)
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
                    updateUserProfile(auth.currentUser!!.displayName!!, Uri.parse(picId))
                    Log.d("updateUser", "success")
                }
                it.addOnFailureListener { exception ->
                    Log.d("updateUser", exception.message.toString())
                }

            }
    }


}