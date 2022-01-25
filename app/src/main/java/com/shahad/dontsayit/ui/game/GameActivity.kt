package com.shahad.dontsayit.ui.game

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.shahad.dontsayit.*
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.model.Player
import com.shahad.dontsayit.data.model.Word
import com.shahad.dontsayit.data.network.ViewModel
import com.shahad.dontsayit.databinding.ActivityGameBinding
import com.shahad.dontsayit.util.checkConnection

class GameActivity : AppCompatActivity(), GameAdapter.ItemListener {

    private lateinit var viewModel: ViewModel
    private lateinit var preference: SharedPreferences
    private var arWordList: MutableList<String> = mutableListOf()
    private var enWordList: MutableList<String> = mutableListOf()

    private var playerName = ""
    private var roomName = ""
    private var hostName = ""
    private var role = ""
    private lateinit var shuffled: List<String>

    private var playersListObj: MutableMap<String, Player> = mutableMapOf()


    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var hostRef: DatabaseReference
    private lateinit var wordRef: DatabaseReference
    private lateinit var roomRef: DatabaseReference
    private lateinit var stateRef: DatabaseReference
    private lateinit var roundRef: DatabaseReference
    private lateinit var scoreRef: DatabaseReference
    private lateinit var picRef: DatabaseReference

    private lateinit var roomListener: ChildEventListener
    private lateinit var playersListener: ChildEventListener
    private lateinit var wordListener: ValueEventListener
    private lateinit var scoreListener: ChildEventListener
    private lateinit var stateListener: ChildEventListener
    private lateinit var profilePicListener: ChildEventListener
    private lateinit var binding: ActivityGameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerview.layoutManager = GridLayoutManager(this, 2)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        observeWord()

        preference = this.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        playerName = preference.getString(USERNAME, "") ?: ""


        val extra = intent.extras
        if (extra != null) {
            roomName = extra.getString(ROOM_NAME).toString()
            hostName = extra.getString(HOST_NAME).toString()
        }

        if (hostName == playerName) {//if the room is by his name then hes the host
            role = "host"
            binding.btnStart.isVisible = true
        } else {
            role = "guest"
            binding.btnStart.isVisible = false
        }

        roomRef = database.getReference("rooms/$roomName")
        wordRef = database.getReference("rooms/${roomName}/players")
        stateRef = database.getReference("rooms/${roomName}/state")
        picRef = database.getReference("rooms/${roomName}/picture")
        roundRef = database.getReference("rooms/${roomName}/round")
        hostRef = database.getReference("rooms/${roomName}/host")
        scoreRef = database.getReference("rooms/${roomName}/score")


        binding.imgbtnscore.setOnClickListener {
            scoreDialog()
        }

        binding.btnStart.setOnClickListener {//show recycler view with random words
            if (checkConnection(this, viewModel.checkConnection(this))) {
                if (playersListObj.size < 2) {
                    Toast.makeText(this, R.string.notenough, Toast.LENGTH_LONG)
                        .show()
                    binding.btnStart.isEnabled = true
                    binding.btnStart.background =
                        ContextCompat.getDrawable(this, R.drawable.play_button)

                } else {
                    assignWord()
                    addRoomEventListener()
                }
            }

        }
        binding.close.setOnClickListener {
            onBackPressed()
        }
        roundNumberObserver()
        addRoomEventListener()
        addPlayersListener()
        addWordEventListener()
        addStateEventListener()
        addScoreEventListener()
        addPicEventListener()

    }

    private fun roundNumberObserver() {
        viewModel.getRound(roundRef).observe(this, {
            if (it.toInt() == 0) {
                binding.tvRoundTitle.text = resources.getText(R.string.round)
            } else {
                binding.tvRoundTitle.text = "${it.toInt()}"

            }
        }

        )
    }

    private fun scoreDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.score_dialog)
        val recyclerviewScore: RecyclerView = dialog.findViewById(R.id.recyclerviewScore)
        val code: TextView = dialog.findViewById(R.id.keyword)
        code.text = roomName
        recyclerviewScore.layoutManager = LinearLayoutManager(this)
        recyclerviewScore.adapter = ScoreAdapter(playersListObj)
        dialog.show()
    }

    private fun resetState() {
        viewModel.resetState(stateRef, playersListObj.keys).observe(this, {
            Log.i("$playerName resetState $it", playersListObj.toString())

        })
    }

    private fun shuffleWords() {
        shuffled = if (preference.getString(LANG, "en") == "ar") {
            arWordList.shuffled()
        } else {
            enWordList.shuffled()
        }
    }

    private fun assignWord() {

        viewModel.getRound(roundRef).observe(this, {
            roundRef.setValue(it.toInt() + 1)
        })

        resetState()
        shuffleWords()


        //send assigned map word/name to adapter and hide the player ones

        for (i in 0 until playersListObj.size) {

            wordRef.child(playersListObj.keys.elementAt(i)).setValue(shuffled[i])
            Log.i("new word", playersListObj.keys.elementAt(i))

        }
        binding.btnStart.isEnabled = false
        binding.btnStart.background =
            ContextCompat.getDrawable(this, R.drawable.gray_play_button)
    }

    fun endOfRoundDialog(winner: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.winner_dialog)
        val title: TextView = dialog.findViewById(R.id.tvwinner)
        val message: TextView = dialog.findViewById(R.id.tvmessage)

        title.text = "$winner ${resources.getText(R.string.Won)}"

        message.text = resources.getText(R.string.nextround)
        dialog.show()
        binding.btnStart.isEnabled = true
        binding.btnStart.background =
            ContextCompat.getDrawable(this, R.drawable.play_button)
    }


    override fun onBackPressed() {

        Log.d("onBack", "Fragment back pressed invoked")

        if (playerName == hostName) {
            deleteRoom()
        } else {
            playerLeft()
        }
        super.onBackPressed()
    }

    private fun deleteRoom() {
        hostRef = database.getReference("rooms/${roomName}/host")
        //if host left change the value to "gone" and check if that value is changed then move home cuz host left
        hostRef.setValue("gone")
        roomRef = database.getReference("rooms/${roomName}")
        roomRef.removeValue()//room

    }

    private fun playerLeft() {
        wordRef.child(playerName).removeValue()//player
        stateRef.child(playerName).removeValue()//state
        scoreRef.child(playerName).removeValue()//score
        picRef.child(playerName).removeValue()//pic
        addToPlayersNum()//add one to players number
    }

    private fun addToPlayersNum() {
        roomRef = database.getReference("rooms/${roomName}/playersNum")
        roomRef.get().addOnCompleteListener {
            it.addOnSuccessListener { num ->
                roomRef.setValue(num.value.toString().toInt() + 1)
            }
        }
    }

    private fun addRoomEventListener() {
        roomListener = roomRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key == "round") {
                    roundNumberObserver()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

                if (snapshot.key == "host") {//if host left and deleted "host" key
                    Toast.makeText(this@GameActivity, R.string.hostleft, Toast.LENGTH_LONG)
                        .show()

                    finish()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GameActivity, "${error.message} Error!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        )
    }

    private fun addPlayersListener() {
        playersListener = wordRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                playersListObj[snapshot.key.toString()] =
                    Player(snapshot.key.toString(), "in", 0)//nedd to add word and pic
                Log.i("$playerName playersListObj addPlayersListener", playersListObj.toString())

                if (hostName == playerName) {
                    binding.btnStart.isEnabled = true
                    binding.btnStart.background =
                        ContextCompat.getDrawable(this@GameActivity, R.drawable.play_button)
                }
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {//this gets activated when a player leave

                playersListObj[snapshot.key.toString()]!!.word = snapshot.value.toString()
                Log.i("$playerName playersListObj addPlayersListener", playersListObj.toString())
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.i("$playerName playersListObj onChildRemoved ${snapshot.key}", playersListObj.toString())

                binding.recyclerview.adapter!!.notifyItemRemoved(
                    playersListObj.minus(playerName).keys.indexOf(
                        snapshot.key.toString()
                    )
                )
                playersListObj.keys.remove(snapshot.key)

                if (snapshot.key == playerName) {//player left
                    finish()
                }

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun observeWord(): List<Word> {
        var wordList: List<Word> = listOf()
        viewModel.getWords().observe(this, {
            wordList = it
            wordList.forEach { word ->
                arWordList.add(word.wordAr)
                enWordList.add(word.wordEn)
            }
        })

        return wordList
    }

    private fun addWordEventListener() {
        wordListener = wordRef.addValueEventListener(object : ValueEventListener {
            //when button clicked words changes and this is activated
            override fun onDataChange(snapshot: DataSnapshot) {
                //change received
                playersListObj[snapshot.key]?.word = snapshot.value.toString()
                //  playersListObj[snapshot.key]?.state = "in"// when new words generated change state to in
                Log.i("$playerName playersListObj addWordEventListener", playersListObj.toString())
                binding.recyclerview.adapter = GameAdapter(
                    playersListObj.minus(playerName),
                    this@GameActivity
                )

            }

            override fun onCancelled(error: DatabaseError) {
                //error = retry
            }

        })
    }

    private fun addStateEventListener() {
        stateListener = stateRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                binding.recyclerview.adapter = GameAdapter(
                    playersListObj.minus(playerName),
                    this@GameActivity
                )
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                playersListObj[snapshot.key.toString()]!!.state = snapshot.value.toString()
                Log.i("$playerName playersListObj addStateEventListener", playersListObj.toString())

                if (playersListObj.values.none { it.state == "winner" } && snapshot.value != "in") {
                    var remainingPlayer = playersListObj.filter {

                        it.value.state == "in"
                    }
                    remainingPlayer.values.forEach {
                        Log.i("$playerName remainingPlayer", "${it.name} = ${it.state}")
                    }

                    if (remainingPlayer.size == 1) {
                        Log.i("winner", "$playerName found ${remainingPlayer.keys.first()} winner")
                        if (playerName == hostName) {
                            stateRef.child(remainingPlayer.keys.first()).setValue("winner")
                            increaseScore(remainingPlayer.keys.first())
                        }
                        playersListObj[remainingPlayer.keys.first()]!!.state = "winner"


                        endOfRoundDialog(remainingPlayer.keys.first())

                    }
                }






                binding.recyclerview.adapter = GameAdapter(
                    playersListObj.minus(playerName),
                    this@GameActivity

                )
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {


            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun increaseScore(player: String) {
        viewModel.getScore(scoreRef.child(player)).observe(this@GameActivity, {
            if (hostName == playerName) {
                scoreRef.child(player).setValue(it.toInt() + 1)
            }

        })
    }

    private fun addPicEventListener() {
        profilePicListener = picRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                playersListObj[snapshot.key]!!.pic = snapshot.value.toString()
                Log.i("$playerName playersListObj addPicEventListener", playersListObj.toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {


            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun addScoreEventListener() {
        scoreListener = scoreRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                playersListObj[snapshot.key]!!.score = snapshot.value.toString().toInt()
                Log.i("$playerName playersListObj addScoreEventListener", playersListObj.toString())

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("onDestroy", "GameActivity removingListeners")
        removeListeners()

        //viewModel.getRound().removeObserver()

// check if room still in db and remove it


    }

    private fun removeListeners() {
        if (this::roomRef.isInitialized) {
            roomRef.removeEventListener(roomListener)
        }
        if (this::wordRef.isInitialized) {
            wordRef.removeEventListener(playersListener)
        }
        if (this::wordRef.isInitialized) {
            wordRef.removeEventListener(wordListener)
        }
        if (this::scoreRef.isInitialized) {
            scoreRef.removeEventListener(scoreListener)
        }
        if (this::stateRef.isInitialized) {
            stateRef.removeEventListener(stateListener)
        }
        if (this::picRef.isInitialized) {
            picRef.removeEventListener(profilePicListener)
        }
    }

    override fun onItemClick(item: Player?) {
        item?.let {
            if (playersListObj.values.none { it.state == "winner" }) {

                if (item.state == "in") {
                    stateRef.child(item.name).setValue("out")
                    playersListObj[item.name]!!.state = "out"
                    Log.i("$playerName onItemClick", "out")

                } else if (item.state == "out") {//in case winner or out,  maybe make winner unchangeable
                    stateRef.child(item.name).setValue("in")
                    playersListObj[item.name]!!.state = "in"
                    Log.i("$playerName onItemClick", "in")
                }
                binding.recyclerview.adapter!!.notifyItemChanged(playersListObj.keys.indexOf(item.name))
            }
        }
    }
}

