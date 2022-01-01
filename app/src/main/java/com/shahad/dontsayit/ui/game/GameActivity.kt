package com.shahad.dontsayit.ui.game

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.shahad.dontsayit.*
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.model.Word
import com.shahad.dontsayit.data.network.ViewModel

class GameActivity : AppCompatActivity() {
    private lateinit var recyclerview: RecyclerView
    private lateinit var tvKeyword: TextView
    private lateinit var tvRoundTitle: TextView

    // private lateinit var imgBtnPlayers: ImageButton
    private lateinit var imgBtnScore: ImageButton
    private lateinit var btnStart: ImageButton

    private lateinit var viewModel: ViewModel
    private lateinit var preference: SharedPreferences
    private var arWordList: MutableList<String> = mutableListOf()
    private var enWordList: MutableList<String> = mutableListOf()

    private var playerName = ""
    private var roomName = ""
    private var hostName = ""
    private var role = ""

    private val roomContentMap = mutableMapOf<String, String>()
    private val playersMapWithWords = mutableMapOf<Int, MutableList<String>>()
    private val playersStateListWithoutCurrentPlayer: MutableList<String> = mutableListOf()
    private val playersPicListWithoutCurrentPlayer: MutableList<String> = mutableListOf()
    private var listOfStateInPlayerName: MutableList<String> = mutableListOf()
    private val playersScore: MutableList<Int> = mutableListOf()
    private var playersListWithoutCurrentPlayer: List<String> = listOf()

    private var playersList: MutableList<String> = mutableListOf()
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
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findView()
        recyclerview.layoutManager = GridLayoutManager(this, 2)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        observeWord()
        playersList = ArrayList()
        listOfStateInPlayerName = ArrayList()
        preference = this.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        playerName = preference.getString(USERNAME, "") ?: ""


        val extra = intent.extras
        if (extra != null) {
            roomName = extra.getString(ROOM_NAME).toString()
            hostName = extra.getString(HOST_NAME).toString()
        }
        tvKeyword.text = roomName
        if (hostName == playerName) {//if the room is by his name then hes the host
            role = "host"
            btnStart.isVisible = true
        } else {
            role = "guest"
            btnStart.isVisible = false
        }

        //roomRef = database.getReference("rooms")
        roomRef = database.getReference("rooms/$roomName")
        wordRef = database.getReference("rooms/${roomName}/players")
        stateRef = database.getReference("rooms/${roomName}/state")
        picRef = database.getReference("rooms/${roomName}/picture")
        roundRef = database.getReference("rooms/${roomName}/round")
        hostRef = database.getReference("rooms/${roomName}/host")
        scoreRef = database.getReference("rooms/${roomName}/score")

        /* imgBtnPlayers.setOnClickListener {
             playersListDialog()
         }*/

        imgBtnScore.setOnClickListener {
            scoreDialog()
        }

        btnStart.setOnClickListener {//show recycler view with random words,start timer
            assignWord()
            addRoomEventListener()
            if (playersList.size < 2) {
                Toast.makeText(this, "not enough players to start the game", Toast.LENGTH_LONG)
                    .show()
                btnStart.isEnabled = true

            }
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
            tvRoundTitle.text = "Round ${it.toInt()}"
        })
    }

    private fun scoreDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.score_dialog)
        val recyclerviewScore: RecyclerView = dialog.findViewById(R.id.recyclerviewScore)
        recyclerviewScore.layoutManager = LinearLayoutManager(this)
        recyclerviewScore.adapter = ScoreAdapter(playersList, playersScore)
        dialog.show()
    }

    private fun assignWord() {

        viewModel.getRound(roundRef).observe(this, {
            roundRef.setValue(it.toInt() + 1)
            // tvRoundTitle.text = "Round ${it.toInt()+1}"
        })
        viewModel.resetState(stateRef, playersList).observe(this, {
            val dataSnap = it
            Log.i("dataSnap ", dataSnap.toString())

            listOfStateInPlayerName.clear()
            for (i in 0 until playersList.size) {
                listOfStateInPlayerName.add(playersList[i])
            }
            Log.i("reset ", listOfStateInPlayerName.toString())

        })


        val shuffled: List<String>
        if (preference.getString(LANG, "en") == "ar") {
            shuffled = arWordList.shuffled()
        } else {
            shuffled = enWordList.shuffled()
        }


        //send assigned map word/name to adapter and hide the player ones
        //wordRef = database.getReference("rooms/${roomName}/players")
        for (i in 0 until playersList.size) {
            //if (stateRef.child(playersList[i]).get().result?.value=="in") {
            wordRef.child(playersList[i]).setValue(shuffled[i])
            //don't change word of out players
            Log.i("new word", playersList[i])
            //}
        }
        btnStart.isEnabled = false
    }

    private fun playersListDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.player_list_dialog)
        val listview: ListView = dialog.findViewById(R.id.listview)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            playersList
        )
        //Log.i("player list", playersList.size.toString())

        listview.adapter = adapter

        dialog.show()
    }

    fun endOfRoundDialog(winner: String) {
        val endOfRoundDialog = AlertDialog.Builder(this)
        endOfRoundDialog.setTitle("$winner Won!")
        endOfRoundDialog.setMessage("better luck next round!")
        endOfRoundDialog.show()
        btnStart.isEnabled = true
    }

    private fun findView() {
        recyclerview = findViewById(R.id.recyclerview)
        tvKeyword = findViewById(R.id.tvRoomKey)
        tvRoundTitle = findViewById(R.id.tvRoundTitle)
        // imgBtnPlayers = findViewById(R.id.imgbtnplayers)
        imgBtnScore = findViewById(R.id.imgbtnscore)
        btnStart = findViewById(R.id.btnStart)
        //  btnReset = findViewById(R.id.btnReset)
    }

    override fun onBackPressed() {

        Log.d("onBack", "Fragment back pressed invoked")
        // roomRef = database.getReference("rooms/${roomName}/players/${playerName}")

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
        //  roomRef = database.getReference("rooms/${roomName}/players/${playerName}")
        // roomRef.removeValue()//player
        wordRef.child(playerName).removeValue()//player

        //    roomRef = database.getReference("rooms/${roomName}/state/${playerName}")
        stateRef.child(playerName).removeValue()//state
        //    roomRef = database.getReference("rooms/${roomName}/score/${playerName}")
        scoreRef.child(playerName).removeValue()//score
        addToPlayersNum()//add one to players number
    }

    private fun addToPlayersNum() {
        roomRef = database.getReference("rooms/${roomName}/playersNum")
        roomRef.get().addOnCompleteListener {
            it.addOnSuccessListener { num ->
                //Log.i("players number ", num.value.toString())

                roomRef.setValue(num.value.toString().toInt() + 1)
                //Log.i("players number IF", "YOU'RE OUT")

            }
            it.addOnFailureListener { e ->
                //Log.i("players number", e.message.toString())
            }
        }
    }

    private fun addRoomEventListener() {
        roomListener = roomRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //     if (snapshot.key != playerName && snapshot.key != "message") {
                roomContentMap[snapshot.key!!] = snapshot.value.toString()
                //   }
                // playersList.add(map.toString())
                //   //Log.i("Map", playerMapWord.toString())

                /*  if (snapshot.key=="score"){
                      // playersScore[playersList.indexOf(snapshot.key)] = snapshot.value.toString().toInt()
                       Log.e("score ", snapshot.toString())
                      *//* Log.e("key ", snapshot.key.toString())
                     Log.e("value ", snapshot.value.toString())*//*
                    val dbscorelist= snapshot.value as String
                    val list= dbscorelist.split(",")
                    Log.e("dbscorelist ", dbscorelist.toString())
                }*/

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //    if (snapshot.key != playerName && snapshot.key != "message") {
                roomContentMap[snapshot.key!!] = snapshot.value.toString()
                //    }
/*

                Log.e("onChildChanged ", snapshot.toString())*/
                /*  Log.e("key ", snapshot.key.toString())
                  Log.e("value ", snapshot.value.toString())*/


                if (snapshot.key == "round") {
                    roundNumberObserver()
                    Log.e("round ", snapshot.toString())
                }

                /*   if (snapshot.key=="score"){
                       val dbscorelist= snapshot.key as List<String>
                    // playersScore[playersList.indexOf(snapshot.key)] = snapshot.value.toString().toInt()
                       Log.e("dbscorelist ", dbscorelist.toString())
                       Log.e("score ", snapshot.toString())
                   }*/

                //playersList.add(map.toString())
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //      if (snapshot.key != playerName) {
                roomContentMap.remove(snapshot.key!!)
                //      }
//                playersList.add(map.toString())


                if (snapshot.key == "host") {//if host left and deleted "host" key
                    Toast.makeText(this@GameActivity, "Host closed the lobby", Toast.LENGTH_LONG)
                        .show()

                    /* if (dialog.isShowing) {//lateinit property dialog has not been initialized
                         dialog.dismiss()
                     }*/

                    finish()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //   if (snapshot.key != playerName && snapshot.key != "message") {
                roomContentMap[snapshot.key!!] = snapshot.value.toString()
                //   }
//                playersList.add(map.toString())
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
                playersList.add(snapshot.key.toString())
                playersScore.add(0)

                Log.i("playersList ADD", playersList.toString())
                Log.i("playersScore ADD", playersScore.toString())
                listOfStateInPlayerName.add(snapshot.key.toString())
                playersListWithoutCurrentPlayer = playersList.minus(playerName)

                if (snapshot.key != playerName) {
                    playersStateListWithoutCurrentPlayer.add(
                        playersListWithoutCurrentPlayer.indexOf(snapshot.key.toString()), "in"


                    )
                    Log.i(
                        "playersStateListWithoutCurrentPlayer ADD",
                        playersStateListWithoutCurrentPlayer.toString()
                    )

                }

                if (hostName == playerName) {
                    btnStart.isEnabled = true
                }
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {//this gets activated when a player leave


                if (snapshot.key != playerName) {
                    playersMapWithWords[playersListWithoutCurrentPlayer.indexOf(snapshot.key.toString())] =
                        mutableListOf(snapshot.key.toString(), snapshot.value.toString())

                }


            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                playersScore.removeAt(playersList.indexOf(snapshot.key))
                playersList.remove(snapshot.key.toString())
                listOfStateInPlayerName.remove(snapshot.key.toString())
                playersMapWithWords.remove(playersListWithoutCurrentPlayer.indexOf(snapshot.key.toString()))
                if (snapshot.key != playerName) {
                    playersStateListWithoutCurrentPlayer.removeAt(
                        playersListWithoutCurrentPlayer.indexOf(
                            snapshot.key.toString()
                        )
                    )//java.lang.ArrayIndexOutOfBoundsException: length=10; index=-1

                    playersPicListWithoutCurrentPlayer.removeAt(
                        playersListWithoutCurrentPlayer.indexOf(
                            snapshot.key.toString()
                        )
                    )
                }
                /* Log.i(
                     "INDEX ERROR playerMapState $playerName=${snapshot.key}",
                     playersStateListWithoutCurrentPlayer.toString()
                 )
                 Log.i(
                     "INDEX ERROR shortenList $playerName=${snapshot.key}",
                     playersListWithoutCurrentPlayer.indexOf(snapshot.key.toString()).toString()
                 )*/

                recyclerview.adapter!!.notifyItemRemoved(
                    playersListWithoutCurrentPlayer.indexOf(
                        snapshot.key.toString()
                    )
                )
                playersListWithoutCurrentPlayer = playersList.minus(playerName)


                //Toast.makeText(this@GameActivity, "${snapshot.key} just left", Toast.LENGTH_LONG).show()
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
                //   Log.i("addWordEventListener", snapshot.toString())
                recyclerview.adapter = GameAdapter(
                    playersMapWithWords,
                    roomName,
                    playerName,
                    playersStateListWithoutCurrentPlayer,
                    playersPicListWithoutCurrentPlayer,
                    this@GameActivity
                )

            }

            override fun onCancelled(error: DatabaseError) {
                //error = retry
                recyclerview.adapter = GameAdapter(
                    playersMapWithWords,
                    roomName,
                    playerName,
                    playersStateListWithoutCurrentPlayer,
                    playersPicListWithoutCurrentPlayer,
                    this@GameActivity
                )

            }

        })
    }

    private fun addStateEventListener() {
        stateListener = stateRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                /*if (snapshot.key != playerName) {
                    playersStateListWithoutCurrentPlayer.add(
                        playersListWithoutCurrentPlayer.indexOf(snapshot.key.toString()),
                        snapshot.value.toString()
                    )*/

                //}
                //   Log.i("playerMapWord state", playersStateListWithoutCurrentPlayer.toString())

                //   Log.i("state", snapshot.toString())
                Log.e(
                    "$playerName after state onChildAdded PLAYERS",
                    playersListWithoutCurrentPlayer.toString()
                )
                Log.e(
                    "$playerName after state onChildAdded",
                    playersStateListWithoutCurrentPlayer.toString()
                )

                recyclerview.adapter = GameAdapter(
                    playersMapWithWords,
                    roomName,
                    playerName,
                    playersStateListWithoutCurrentPlayer,
                    playersPicListWithoutCurrentPlayer,
                    this@GameActivity
                )

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                if (snapshot.key != playerName) {
                    playersStateListWithoutCurrentPlayer[playersListWithoutCurrentPlayer.indexOf(
                        snapshot.key.toString()
                    )] =
                        snapshot.value.toString()
                    //   Log.i("playerMapWord state", playersStateListWithoutCurrentPlayer.toString())

                }

                //if change to out delete player name
                if (snapshot.value == "out") {
                    listOfStateInPlayerName.remove(snapshot.key.toString())
                }

                //if change to in add player name back
                if (snapshot.value == "in") {
                    listOfStateInPlayerName.add(snapshot.key.toString())
                }
                //if length of listOfInPlayers is one declare winner
                if (listOfStateInPlayerName.size == 1) {
                    stateRef.child(listOfStateInPlayerName[0]).setValue("winner")
                }

                if (snapshot.value == "winner") {
                    //   recyclerview.isClickable = false
                    // Toast.makeText(this@GameActivity,"winner is ${snapshot.key}",Toast.LENGTH_SHORT).show()
                    endOfRoundDialog(snapshot.key.toString())

                    Log.e(
                        "${playerName} winner ",
                        scoreRef.child(snapshot.key.toString()).toString()
                    )


                    viewModel.getScore(scoreRef.child(listOfStateInPlayerName[0])).observe(
                        this@GameActivity, {
                            //   Log.i("$hostName guest ", it.toString())

                            if (hostName == playerName) {
                                //   Log.i("Host ", it.toString())
                                scoreRef.child(listOfStateInPlayerName[0]).setValue(it.toInt() + 1)
                                //   Log.i("Host ", it.toString())
                                //  playersScore[playersList.indexOf(snapshot.key)] = it.toInt() + 1

                            }
                        })

                }


                //check state list if these one in
                //declare winner
                //make start btn enabled

                Log.e(
                    "$playerName after state onChildChanged PLAYERS",
                    playersListWithoutCurrentPlayer.toString()
                )
                Log.e(
                    "$playerName after state onChildChanged",
                    playersStateListWithoutCurrentPlayer.toString()
                )

                //  Log.i("state", snapshot.toString())
                recyclerview.adapter = GameAdapter(
                    playersMapWithWords,
                    roomName,
                    playerName,
                    playersStateListWithoutCurrentPlayer,
                    playersPicListWithoutCurrentPlayer,
                    this@GameActivity
                )
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //       Log.i("state", snapshot.toString())
                //playersStateListWithoutCurrentPlayer.remove(snapshot.value.toString())
                Log.e(
                    "$playerName before state onChildRemoved PLAYERS",
                    playersListWithoutCurrentPlayer.toString()
                )

                //     playersStateListWithoutCurrentPlayer.remove(snapshot.value.toString())
                Log.e(
                    "$playerName after state onChildRemoved",
                    playersStateListWithoutCurrentPlayer.toString()
                )


            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

 private fun addPicEventListener() {
        profilePicListener = picRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
             if (playerName!=snapshot.key) {
                 Log.e("$playerName add pic for ${snapshot.key}","with value of ${snapshot.value}")
                 playersPicListWithoutCurrentPlayer.add(
                     playersListWithoutCurrentPlayer.indexOf(snapshot.key.toString()),
                     snapshot.value.toString()
                 )
             }
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
                playersScore[playersList.indexOf(snapshot.key)] = snapshot.value.toString().toInt()

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
        if (this::roomRef.isInitialized) {
            roomRef.removeEventListener(roomListener)
            Log.i("onDestroy Home", "roomRef isInitialized")

        }
        if (this::wordRef.isInitialized) {
            wordRef.removeEventListener(playersListener)
            Log.i("onDestroy Game", "wordRef isInitialized")

        }
        if (this::wordRef.isInitialized) {
            wordRef.removeEventListener(wordListener)
            Log.i("onDestroy Game", "wordRef isInitialized")

        }
        if (this::scoreRef.isInitialized) {
            scoreRef.removeEventListener(scoreListener)
            Log.i("onDestroy Game", "scoreRef isInitialized")

        }
        if (this::stateRef.isInitialized) {
            stateRef.removeEventListener(stateListener)
            Log.i("onDestroy Game", "stateRef isInitialized")

        }
    if (this::picRef.isInitialized) {
        picRef.removeEventListener(profilePicListener)
            Log.i("onDestroy Game", "picRef isInitialized")

        }
        //viewModel.getRound().removeObserver()

// check if room still in db and remove it


    }
}
