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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.shahad.dontsayit.*
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.model.UserSuggestions
import com.shahad.dontsayit.data.network.ViewModel
import com.shahad.dontsayit.ui.game.GameActivity

class HomeFragment : Fragment() {
    private lateinit var btnCreateLobby: ImageButton
    private lateinit var btnJoinLobby: ImageButton
    private lateinit var btnHow: ImageButton
    private lateinit var imgBtnShare: ImageButton
    private lateinit var imgbtnsuggest: ImageButton
    private lateinit var imgBtnSettings: ImageButton
    private val appUrl = "https://github.com/Tuwaiq-Jeddah-Kotlin-1/Dont-Say-It"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: ViewModel

    private var playerName = ""
    private var profilePic = ""
    private var roomName = ""
    private var host = ""
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var roomRef: DatabaseReference
    private lateinit var roomsRef: DatabaseReference
    var roomsList: MutableList<String> = mutableListOf()
    private var roomCreateJoin: Boolean = false
    private lateinit var roomListener: ValueEventListener
    private lateinit var roomsListener: ValueEventListener


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findView(view)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val uId = sharedPreferences.getString(UID, null)
        uId?.let { fillShared(it) }

        roomsRef = database.getReference("rooms")


        btnCreateLobby.setOnClickListener {
            if (viewModel.checkConnection(requireContext())) {
                createRoomDialog()
            } else {
                Toast.makeText(requireContext(), "No Internet Connect", Toast.LENGTH_SHORT).show()
            }
        }
        btnJoinLobby.setOnClickListener {
            if (viewModel.checkConnection(requireContext())) {
                joinRoomDialog()
            } else {
                Toast.makeText(requireContext(), "No Internet Connect", Toast.LENGTH_SHORT).show()
            }
        }
        imgbtnsuggest.setOnClickListener {
            if (viewModel.checkConnection(requireContext())) {
            suggestionDialog()
            }else{
                Toast.makeText(requireContext(), "No Internet Connect", Toast.LENGTH_SHORT).show()
            }
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
        addRoomsEventListener()
    }

    private fun suggestionDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.suggestion_dialog)
        val etSuggest: EditText = dialog.findViewById(R.id.etSuggestion)
        val btnSuggest: Button = dialog.findViewById(R.id.btnSuggestion)

        btnSuggest.setOnClickListener {
            if (etSuggest.text.toString().isNotEmpty()) {
                saveToAPI(etSuggest.text.toString())
            }
        }

        dialog.show()

    }

    private fun fillShared(uId: String) {
        viewModel.getUserById(uId).observe(viewLifecycleOwner, {
            sharedPreferences.edit().putString(USERNAME, it.username).apply()
            playerName = it.username
            sharedPreferences.edit().putString(PIC, it.profilePic).apply()
            profilePic = it.profilePic




        })
    }

    private fun findView(view: View) {
        btnCreateLobby = view.findViewById(R.id.btnCreateLobby)
        btnJoinLobby = view.findViewById(R.id.btnJoinLobby)
        btnHow = view.findViewById(R.id.btnHow)
        imgBtnShare = view.findViewById(R.id.imgBtnShare)
        imgBtnSettings = view.findViewById(R.id.imgBtnSettings)
        imgbtnsuggest = view.findViewById(R.id.imgbtnsuggest)

    }

    private fun setPlayersNum(chosen: Int) {
        //minus one player
        roomsRef.child(roomName).child("playersNum").setValue((chosen) - 1)
        host = playerName
    }

    private fun setHost() {
        //add host name
        roomRef = database.getReference("rooms/${roomName}/host")
        roomRef.setValue(playerName)
    }

    private fun setRound() {
        //add host name
        roomRef = database.getReference("rooms/${roomName}/round")
        roomRef.setValue(0)
    }

    private fun addPlayer() {
        //add player
        roomRef = database.getReference("rooms/${roomName}/players/${playerName}")
        roomRef.setValue("$playerName word?")
        roomRef = database.getReference("rooms/${roomName}/state/${playerName}")
        roomRef.setValue("in")
        roomRef = database.getReference("rooms/${roomName}/score/${playerName}")
        roomRef.setValue(0)
        roomRef = database.getReference("rooms/${roomName}/picture/${playerName}")
        roomRef.setValue(profilePic)

    }

    private fun reducePlayersNum(num: DataSnapshot) {
        //player minus one
        roomRef = database.getReference("rooms/${roomName}/playersNum")
        roomRef.setValue(num.value.toString().toInt() - 1)
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
              //  btnCreateLobby.text = getString(R.string.create_lobby_btn)
                btnCreateLobby.isEnabled = false
                roomName = edKeyword.text.toString()

                if (!roomsList.contains(roomName)) {
                    addPlayer()
                    /* //add player
                roomRef = database.getReference("rooms/${roomName}/players/${playerName}")
                roomRef.setValue("$playerName word?")*/


                    /* //add player state
                 roomRef = database.getReference("rooms/${roomName}/state/${playerName}")
                 roomRef.setValue("in")
 */

                    setPlayersNum(chosen)
                    /*//minus one player
                roomsRef.child(roomName).child("playersNum").setValue((chosen) - 1)
                host = playerName*/

                    setHost()
                    setRound()
                    /*//add host name
                roomRef = database.getReference("rooms/${roomName}/host")
                roomRef.setValue(playerName)*/


                    dialog.dismiss()
                    addRoomEventListener()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "room key is taken choose another key",
                        Toast.LENGTH_LONG
                    ).show()
                    btnCreateLobby.isEnabled = true

                }
            }
        }
        btnCancel.setOnClickListener {
            edKeyword.text.clear()
            chosen = 2
            dialog.dismiss()

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
        btnJoin.text = getString(R.string.join_lobby_btn)
        btnJoin.setOnClickListener {
            roomCreateJoin = true

            roomName = edKeyword.text.toString()
            btnJoinLobby.isEnabled = false

            //check if room exists
            if (roomsList.contains(roomName)) {
                //check players number before adding another player

                checkPlayersNum(dialog)

            } else {
                Toast.makeText(requireContext(), "room doesn't exists", Toast.LENGTH_LONG).show()
                btnJoinLobby.isEnabled = true
            }


        }
        btnCancel.setOnClickListener {
            edKeyword.text.clear()
            dialog.dismiss()


        }


        dialog.show()
    }

    private fun checkPlayersNum(dialog: Dialog) {
        roomRef = database.getReference("rooms/${roomName}/playersNum")

        roomRef.get().addOnCompleteListener {
            it.addOnSuccessListener { num ->
                //  Log.i("players number ", num.value.toString())
                if (num.value != null) {
                    if (num.value.toString().toInt() - 1 >= 0) {//change value and let player in


                        addPlayer()
                        /*//add player
                        roomRef = database.getReference("rooms/${roomName}/players/${playerName}")
                        roomRef.setValue("$playerName word?")//can set this to random word from api?*/
/*

                    //add state
                    roomRef = database.getReference("rooms/${roomName}/state/${playerName}")
                    roomRef.setValue("in")//can set this to random word from api?
*/

                        reducePlayersNum(num)
                        /*//player minus one
                        roomRef = database.getReference("rooms/${roomName}/playersNum")
                        roomRef.setValue(num.value.toString().toInt() - 1)*/
                        dialog.dismiss()

                        addRoomEventListener()
                        // Log.i("players number IF", "YOU'RE IN")

                    } else {
                        //   Log.i("players number ELSE ROOM FULL", num.value.toString())
                        Toast.makeText(requireContext(), "this room is full", Toast.LENGTH_LONG)
                            .show()
                        btnJoinLobby.isEnabled = true

                    }
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("onStop", "removingListeners")
        if (this::roomRef.isInitialized) {
            roomRef.removeEventListener(roomListener)
            Log.i("onStop", "roomRef isInitialized")

        }
        if (this::roomsRef.isInitialized) {
            roomsRef.removeEventListener(roomsListener)
            Log.i("onStop", "roomsRef isInitialized")

        }
    }

    private fun addRoomEventListener() {

        roomListener = roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("addRoomEventListener", "roomListener")

                if (roomCreateJoin) {// if true then the change is  creating/joining room

                    //join the room
                    /* btnCreateLobby.text = getString(R.string.create_lobby_btn)*/
                    btnCreateLobby.isEnabled = true
                    btnJoinLobby.isEnabled = true

                    val intent = Intent(requireContext(), GameActivity::class.java)
                    intent.putExtra(ROOM_NAME, roomName)
                    intent.putExtra(HOST_NAME, host)
                    startActivity(intent)
                    roomCreateJoin = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //error
                /* btnCreateLobby.text = getString(R.string.create_lobby_btn)*/
                btnCreateLobby.isEnabled = true
                btnJoinLobby.isEnabled = true
                Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addRoomsEventListener() {
        // roomsRef = database.getReference("rooms")
        roomsListener = roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("addRoomsEventListener", "roomsListener")

                //show list of rooms
                roomsList.clear()
                val rooms: Iterable<DataSnapshot> = snapshot.children
                for (room in rooms) {
                    roomsList.add(room.key!!)

                    /*   val adapter: ArrayAdapter<String> = ArrayAdapter(
                           requireContext(),
                           android.R.layout.simple_list_item_1,
                           roomsList
                       )
                       listview.adapter = adapter*/


                }
                //  Log.i("ROOMS data change", "$playerName: $roomDeletedFlag")

            }

            override fun onCancelled(error: DatabaseError) {
                //error - nothing
            }

        })
    }


    private fun saveToAPI(value: String) {
        val suggestions = UserSuggestions()
        suggestions.suggestion = value
        viewModel.userRequests(suggestions)
        Toast.makeText(
            context,
            "Thank you for being a part of this game \uD83E\uDD73",
            Toast.LENGTH_LONG
        ).show()
    }
}