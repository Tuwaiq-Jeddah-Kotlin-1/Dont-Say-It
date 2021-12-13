package com.shahad.dontsayit.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.shahad.dontsayit.EMAIL
import com.shahad.dontsayit.PREFERENCE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repo(val context: Context) {
    private lateinit var sharedPreferences: SharedPreferences

    private val TAG = "repo"
    private val auth = FirebaseAuth.getInstance()

    suspend fun createUser(
        email: String,
        pass: String,
    ) = withContext(
        Dispatchers.IO
    ) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                savePersist(email)

                Log.d(TAG, "createUserWithEmail:success")

            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun savePersist(email: String) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(EMAIL, email)
        editor.apply()
        Log.d("$TAG: sharedPreferences: ", "savePersist:success")
        Toast.makeText(context, "Info saved!", Toast.LENGTH_LONG).show()

    }
    suspend fun signIn(email: String, pass: String) = withContext(Dispatchers.IO) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
            } else {
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT)
                    .show()

            }

        }
    }

}