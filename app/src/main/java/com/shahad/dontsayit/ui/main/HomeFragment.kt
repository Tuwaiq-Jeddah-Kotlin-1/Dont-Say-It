package com.shahad.dontsayit.ui.main

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.shahad.dontsayit.*
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.network.ViewModel
import com.shahad.dontsayit.ui.game.GameActivity

class HomeFragment : Fragment() {
    private lateinit var btnCreateLobby: Button
    private lateinit var btnJoinLobby: Button
    private lateinit var btnHow: Button
    private lateinit var imgBtnShare: ImageButton
    private lateinit var imgBtnSettings: ImageButton
    private val appUrl = "https://github.com/Tuwaiq-Jeddah-Kotlin-1/Dont-Say-It"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: ViewModel

    private var playerName = ""
    private var roomName = ""
    private var host = ""
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var roomRef: DatabaseReference
    private lateinit var roomsRef: DatabaseReference
    private var roomCreateJoin: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findView(view)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val uId = sharedPreferences.getString(UID, null)
        uId?.let { fillShared(it) }
       // playerName = sharedPreferences.getString(PLAYER_NAME, "") ?: ""


        roomsRef = database.getReference("rooms")

        btnCreateLobby.setOnClickListener {
            //startActivity(Intent(requireContext(), GameActivity::class.java))
            createRoomDialog()
        }
        btnJoinLobby.setOnClickListener {
            joinRoomDialog()
        }
        btnHow.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_howToPlayFragment)
        }
        imgBtnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
        imgBtnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, appUrl)//change to url
            intent.type = "text/plain"
            startActivity(intent)
        }
    }

    private fun fillShared(uId: String) {
        viewModel.getUserById(uId).observe(viewLifecycleOwner,{
            sharedPreferences.edit().putString(USERNAME, it.username).apply()
            playerName = sharedPreferences.getString(USERNAME,"")?:""
        })
    }

    private fun findView(view: View) {
        btnCreateLobby = view.findViewById(R.id.btnCreateLobby)
        btnJoinLobby = view.findViewById(R.id.btnJoinLobby)
        btnHow = view.findViewById(R.id.btnHow)
        imgBtnShare = view.findViewById(R.id.imgBtnShare)
        imgBtnSettings = view.findViewById(R.id.imgBtnSettings)

    }

    private fun createRoomDialog() {
        var chosen = 2

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog)
        var edKeyword: EditText = dialog.findViewById(R.id.keyword)
        var btnCreate: Button = dialog.findViewById(R.id.btnCreate)
        var btnCancel: Button = dialog.findViewById(R.id.btnCancel)
        var numPick: NumberPicker = dialog.findViewById(R.id.numberPicker)
        numPick.maxValue = 6;
        numPick.minValue = 2;
        numPick.wrapSelectorWheel = false;
        numPick.setOnValueChangedListener() { numPicker, _, _ ->

            chosen = numPicker.value
        }
        btnCreate.setOnClickListener {
            roomCreateJoin = true

            if (edKeyword.text.toString() != "") {
                btnCreateLobby.text = getString(R.string.creating_room_txt)
                btnCreateLobby.isEnabled = false
                roomName = edKeyword.text.toString()
                roomRef = database.getReference("rooms/${roomName}/players/${playerName}")
                roomRef.setValue("$playerName word?")
                roomsRef.child(roomName).child("playersNum").setValue((chosen) - 1)
                host = playerName
                //just added
                roomRef = database.getReference("rooms/${roomName}/host")
                roomRef.setValue(playerName)
                dialog.dismiss()
                addRoomEventListener()

            }
        }
        btnCancel.setOnClickListener {
            edKeyword.text.clear()
            chosen = 2
        }


        dialog.show()
    }

    private fun joinRoomDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog)
        var edKeyword: EditText = dialog.findViewById(R.id.keyword)
        var tvPlayers: TextView = dialog.findViewById(R.id.tvPlayers)
        var btnJoin: Button = dialog.findViewById(R.id.btnCreate)
        var btnCancel: Button = dialog.findViewById(R.id.btnCancel)
        var numPick: NumberPicker = dialog.findViewById(R.id.numberPicker)
        numPick.isVisible = false
        tvPlayers.isVisible = false
        btnJoin.text = getString(R.string.join_room_btn)
        btnJoin.setOnClickListener {
            roomCreateJoin = true

            roomName = edKeyword.text.toString()
            //check players number before adding another player

            checkPlayersNum()
            dialog.dismiss()

        }
        btnCancel.setOnClickListener {
            edKeyword.text.clear()

        }


        dialog.show()
    }

    private fun checkPlayersNum() {
        roomRef = database.getReference("rooms/${roomName}/playersNum")

        roomRef.get().addOnCompleteListener {
            it.addOnSuccessListener { num ->
              //  Log.i("players number ", num.value.toString())
               if (num.value!=null){
                if (num.value.toString().toInt() - 1 >= 0) {//change value and let player in
                    roomRef = database.getReference("rooms/${roomName}/players/${playerName}")
                    roomRef.setValue("$playerName word?")//can set this to random word from api?
                    roomRef = database.getReference("rooms/${roomName}/playersNum")
                    roomRef.setValue(num.value.toString().toInt() - 1)
                    addRoomEventListener()
                    Log.i("players number IF", "YOU'RE IN")
                } else {
                    Log.i("players number ELSE ROOM FULL", num.value.toString())
                }}
            }
            it.addOnFailureListener { e ->
                Log.i("players number", e.message.toString())
            }
        }
    }

    private fun addRoomEventListener() {

        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (roomCreateJoin) {// if true then the change is  creating/joining room

                    //join the room
                    btnCreateLobby.text = getString(R.string.create_room_btn)
                    btnCreateLobby.isEnabled = true

                    val intent = Intent(requireContext(), GameActivity::class.java)
                    intent.putExtra(ROOM_NAME, roomName)
                    intent.putExtra(HOST_NAME, host)
                    startActivity(intent)
                    roomCreateJoin = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //error
                btnCreateLobby.text = getString(R.string.create_room_btn)
                btnCreateLobby.isEnabled = true
                Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show()
            }

        })
    }

}