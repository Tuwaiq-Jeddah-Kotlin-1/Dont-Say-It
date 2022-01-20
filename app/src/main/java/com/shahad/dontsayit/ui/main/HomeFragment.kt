package com.shahad.dontsayit.ui.main

import android.app.Dialog
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.shahad.dontsayit.*
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.model.UserSuggestions
import com.shahad.dontsayit.data.network.ViewModel
import com.shahad.dontsayit.ui.game.GameActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.util.*

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
    private lateinit var shake: Animation
    private lateinit var scaleUp: Animation
    private lateinit var scaleDown: Animation
    private val clipboard: ClipboardManager by lazy { requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager }
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        roomsRef = database.getReference("rooms")

        btnCreateLobby.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                btnCreateLobby.startAnimation(scaleDown)
                delay(100)


                if (viewModel.checkConnection(requireContext())) {
                    createRoomDialog()
                } else {
                    Toast.makeText(requireContext(), "No Internet Connect", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
        btnJoinLobby.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                btnJoinLobby.startAnimation(scaleDown)
                delay(100)

                if (viewModel.checkConnection(requireContext())) {
                    joinRoomDialog()
                } else {
                    Toast.makeText(requireContext(), "No Internet Connect", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        btnHow.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                btnHow.startAnimation(scaleDown)
                delay(100)
                findNavController().navigate(R.id.action_homeFragment_to_howToPlayFragment)

            }
        }
        imgbtnsuggest.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                imgbtnsuggest.startAnimation(scaleDown)
                // btnCreateLobby.startAnimation(bounce)
                delay(100)

                if (viewModel.checkConnection(requireContext())) {
                    suggestionDialog()
                } else {
                    Toast.makeText(requireContext(), "No Internet Connect", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        imgBtnSettings.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                imgBtnSettings.startAnimation(scaleUp)
                delay(100)
                findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
            }
        }
        /* imgBtnShare.setOnClickListener {
             GlobalScope.async(Dispatchers.Main) {
                 imgBtnShare.startAnimation(scaleDown)
                 // btnCreateLobby.startAnimation(bounce)
                 delay(100)

                 val intent = Intent(Intent.ACTION_SEND)
                 intent.putExtra(Intent.EXTRA_TEXT, appUrl)//change to url
                 intent.type = "text/plain"
                 startActivity(intent)
             }
         }*/
        addRoomsEventListener()
    }

    private fun suggestionDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.suggestion_dialog)
        val etSuggest: EditText = dialog.findViewById(R.id.etSuggestion)
        val btnSuggest: ImageButton = dialog.findViewById(R.id.btnSuggestion)

        btnSuggest.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                btnSuggest.startAnimation(scaleDown)
                // btnCreateLobby.startAnimation(bounce)
                delay(100)

                if (etSuggest.text.toString().isNotEmpty()) {
                    saveToAPI(etSuggest.text.toString())
                    dialog.dismiss()

                }
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
//        imgBtnShare = view.findViewById(R.id.imgBtnShare)
        imgBtnSettings = view.findViewById(R.id.imgBtnSettings)
        imgbtnsuggest = view.findViewById(R.id.imgbtnsuggest)
        shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
        scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)
        scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
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
        dialog.setContentView(R.layout.create_dialog)
        var tvKeyword: TextView = dialog.findViewById(R.id.keyword)
        var btncopy: ImageButton = dialog.findViewById(R.id.btnCopy)
        var btnshare: ImageButton = dialog.findViewById(R.id.imgBtnShare)
        var btnCreate: ImageButton = dialog.findViewById(R.id.btnJoin)
        var numPick: NumberPicker = dialog.findViewById(R.id.numberPicker)
        tvKeyword.text = UUID.randomUUID().toString()
        numPick.maxValue = 6;
        numPick.minValue = 2;
        numPick.wrapSelectorWheel = false;
        numPick.setOnValueChangedListener() { numPicker, _, _ ->

            chosen = numPicker.value
        }
        btnCreate.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                btnCreate.startAnimation(scaleDown)
                delay(100)

                roomCreateJoin = true

                btnCreateLobby.isEnabled = false
                roomName = tvKeyword.text.toString()

                if (!roomsList.contains(roomName)) {
                    addPlayer()
                    setPlayersNum(chosen)
                    setHost()
                    setRound()
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
        btncopy.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                btncopy.startAnimation(scaleDown)
                delay(100)

                val clip: ClipData = ClipData.newPlainText("Room password", tvKeyword.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Password Copied", Toast.LENGTH_SHORT).show();
            }
        }
        btnshare.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                btnshare.startAnimation(scaleDown)
                delay(100)
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, tvKeyword.text)
                intent.type = "text/plain"
                startActivity(intent)
            }
        }


        dialog.show()
    }

    private fun joinRoomDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.join_dialog)

        var edKeyword: EditText = dialog.findViewById(R.id.keyword)
        var btnJoin: ImageButton = dialog.findViewById(R.id.btnJoin)
        var btnPaste: ImageButton = dialog.findViewById(R.id.btnPaste)

        btnJoin.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                btnJoin.startAnimation(scaleDown)
                // btnCreateLobby.startAnimation(bounce)
                delay(100)

                roomCreateJoin = true
                roomName = edKeyword.text.toString()
                btnJoinLobby.isEnabled = false
                //check if room exists
                if (roomsList.contains(roomName)) {
                    //check players number before adding another player

                    checkPlayersNum(dialog)

                } else {
                    Toast.makeText(requireContext(), "room doesn't exists", Toast.LENGTH_LONG)
                        .show()
                    btnJoinLobby.isEnabled = true
                }
            }
        }
        btnPaste.setOnClickListener {
            GlobalScope.async(Dispatchers.Main) {
                btnPaste.startAnimation(scaleDown)
                delay(100)
                val clipData: ClipData? = clipboard.primaryClip

                clipData?.apply {
                    val textToPaste: String = this.getItemAt(0).text.toString().trim()
                    edKeyword.setText(textToPaste)
                }
            }

        }


        dialog.show()
    }

    private fun checkPlayersNum(dialog: Dialog) {
        roomRef = database.getReference("rooms/${roomName}/playersNum")
        roomRef.get().addOnCompleteListener {
            it.addOnSuccessListener { num ->
                if (num.value != null) {
                    if (num.value.toString().toInt() - 1 >= 0) {//change value and let player in
                        addPlayer()
                        reducePlayersNum(num)
                        dialog.dismiss()
                        addRoomEventListener()

                    } else {
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
        Log.i("onDestroy", "removingListeners")
        if (this::roomRef.isInitialized) {
            roomRef.removeEventListener(roomListener)
            Log.i("onDestroy", "roomRef isInitialized")

        }
        if (this::roomsRef.isInitialized) {
            roomsRef.removeEventListener(roomsListener)
            Log.i("onDestroy", "roomsRef isInitialized")

        }
    }

    private fun addRoomEventListener() {

        roomListener = roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (roomCreateJoin) {// if true then the change is  creating/joining room

                    //join the room
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
                btnCreateLobby.isEnabled = true
                btnJoinLobby.isEnabled = true
                Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addRoomsEventListener() {
        roomsListener = roomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //show list of rooms
                roomsList.clear()
                val rooms: Iterable<DataSnapshot> = snapshot.children
                for (room in rooms) {
                    roomsList.add(room.key!!)

                }

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