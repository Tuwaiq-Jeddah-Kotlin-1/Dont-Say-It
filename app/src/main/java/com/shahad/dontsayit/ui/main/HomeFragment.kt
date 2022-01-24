package com.shahad.dontsayit.ui.main

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.shahad.dontsayit.HOST_NAME
import com.shahad.dontsayit.R
import com.shahad.dontsayit.ROOM_NAME
import com.shahad.dontsayit.data.model.UserSuggestions
import com.shahad.dontsayit.data.network.ViewModel
import com.shahad.dontsayit.databinding.FragmentHomeBinding
import com.shahad.dontsayit.ui.game.GameActivity
import com.shahad.dontsayit.util.checkConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var viewModel: ViewModel
    private lateinit var shake: Animation
    private lateinit var scaleUp: Animation
    private lateinit var scaleDown: Animation
    private val clipboard: ClipboardManager by lazy {
        requireContext().getSystemService(
            CLIPBOARD_SERVICE
        ) as ClipboardManager
    }
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
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser!!
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        loadAnimation()
        playerName = user.displayName ?: ""
        profilePic = user.photoUrl.toString()
        roomsRef = database.getReference("rooms")



        binding.btnCreateLobby.setOnClickListener {
            lifecycleScope.launch {
                binding.btnCreateLobby.startAnimation(scaleDown)
                delay(100)

                if (checkConnection(
                        requireContext(),
                        viewModel.checkConnection(requireContext())
                    )
                ) {
                    createRoomDialog()
                }

            }
        }
        binding.btnJoinLobby.setOnClickListener {
            lifecycleScope.launch {
                binding.btnJoinLobby.startAnimation(scaleDown)
                delay(100)

                if (checkConnection(
                        requireContext(),
                        viewModel.checkConnection(requireContext())
                    )
                ) {
                    joinRoomDialog()
                }
            }
        }
        binding.btnHow.setOnClickListener {
            lifecycleScope.launch {
                binding.btnHow.startAnimation(scaleDown)
                delay(100)
                findNavController().navigate(R.id.action_homeFragment_to_howToPlayFragment)

            }
        }
        binding.imgbtnsuggest.setOnClickListener {
            lifecycleScope.launch {
                binding.imgbtnsuggest.startAnimation(scaleDown)
                delay(100)

                if (checkConnection(
                        requireContext(),
                        viewModel.checkConnection(requireContext())
                    )
                ) {
                    suggestionDialog()
                }
            }
        }
        binding.imgBtnSettings.setOnClickListener {
            lifecycleScope.launch {
                binding.imgBtnSettings.startAnimation(scaleUp)
                delay(100)
                findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
            }
        }

        addRoomsEventListener()
    }


    private fun suggestionDialog() {
        val dialog = Dialog(requireContext())

        dialog.setContentView(R.layout.suggestion_dialog)
        val etSuggest: EditText = dialog.findViewById(R.id.etSuggestion)
        val btnSuggest: ImageButton = dialog.findViewById(R.id.btnSuggestion)

        btnSuggest.setOnClickListener {
            lifecycleScope.launch {
                btnSuggest.startAnimation(scaleDown)
                delay(100)

                if (etSuggest.text.toString().isNotEmpty()) {
                    saveToAPI(etSuggest.text.toString())
                    dialog.dismiss()

                }
            }
        }

        dialog.show()

    }


    private fun loadAnimation() {
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
            lifecycleScope.launch {
                btnCreate.startAnimation(scaleDown)
                delay(100)

                roomCreateJoin = true

                binding.btnCreateLobby.isEnabled = false
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
                        R.string.roomcodetaken,
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnCreateLobby.isEnabled = true

                }
            }
        }
        btncopy.setOnClickListener {
            lifecycleScope.launch {
                btncopy.startAnimation(scaleDown)
                delay(100)

                val clip: ClipData = ClipData.newPlainText("Room code", tvKeyword.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "code Copied", Toast.LENGTH_SHORT).show();
            }
        }
        btnshare.setOnClickListener {
            lifecycleScope.launch {
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
            lifecycleScope.launch {
                btnJoin.startAnimation(scaleDown)
                delay(100)

                roomCreateJoin = true
                roomName = edKeyword.text.toString()
                binding.btnJoinLobby.isEnabled = false
                //check if room exists
                if (roomsList.contains(roomName)) {
                    //check players number before adding another player

                    checkPlayersNum(dialog)

                } else {
                    Toast.makeText(requireContext(), R.string.nolobby, Toast.LENGTH_LONG)
                        .show()
                    binding.btnJoinLobby.isEnabled = true
                }
            }
        }
        btnPaste.setOnClickListener {
            lifecycleScope.launch {
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
                        Toast.makeText(requireContext(), R.string.fulllobby, Toast.LENGTH_LONG)
                            .show()
                        binding.btnJoinLobby.isEnabled = true

                    }
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("onDestroy", "HomeFragment removingListeners")
        if (this::roomRef.isInitialized) {
            roomRef.removeEventListener(roomListener)

        }
        if (this::roomsRef.isInitialized) {
            roomsRef.removeEventListener(roomsListener)
        }
    }

    private fun addRoomEventListener() {

        roomListener = roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (roomCreateJoin) {// if true then the change is  creating/joining room

                    //join the room
                    binding.btnCreateLobby.isEnabled = true
                    binding.btnJoinLobby.isEnabled = true

                    val intent = Intent(requireContext(), GameActivity::class.java)
                    intent.putExtra(ROOM_NAME, roomName)
                    intent.putExtra(HOST_NAME, host)
                    startActivity(intent)
                    roomCreateJoin = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //error
                binding.btnCreateLobby.isEnabled = true
                binding.btnJoinLobby.isEnabled = true
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
        if (checkConnection(requireContext(), viewModel.checkConnection(requireContext()))) {
            viewModel.userRequests(suggestions)
            Toast.makeText(
                context,
                "Thank you for being a part of this game \uD83E\uDD73",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}