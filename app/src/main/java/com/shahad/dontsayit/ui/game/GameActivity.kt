package com.shahad.dontsayit.ui.game

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.shahad.dontsayit.*
import com.shahad.dontsayit.R
import com.shahad.dontsayit.data.model.Word
import com.shahad.dontsayit.data.network.ViewModel

class GameActivity : AppCompatActivity() {
    private lateinit var recyclerview: RecyclerView
    private lateinit var tvTimer: TextView
    private lateinit var imgBtnPlayers: ImageButton
    private lateinit var btnStart: Button

    // private lateinit var btnReset: Button
    private lateinit var viewModel: ViewModel
    private lateinit var wordObjsList: List<Word>
    private var arWordList: MutableList<String> = mutableListOf()
    private var enWordList: MutableList<String> = mutableListOf()
    private lateinit var shortenList: List<String>

    //
    /*  lateinit var btnSend: Button
      lateinit var etmessage: EditText
      lateinit var tvmessage: TextView
      lateinit var tvPlayers: TextView*/
    private var playerName = ""
    private var roomName = ""
    private var hostName = ""
    private var role = ""

    //private var message = ""
    private val roomContentMap = mutableMapOf<String, String>()
    private val playerMapWord = mutableMapOf<Int, MutableList<String>>()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()


    //private lateinit var messageRef: DatabaseReference
    private lateinit var hostRef: DatabaseReference
    private lateinit var wordRef: DatabaseReference
    private lateinit var roomRef: DatabaseReference
    private lateinit var preference: SharedPreferences
    private var playersList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findView()
        recyclerview.layoutManager = GridLayoutManager(this, 3)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        observeWord()
        playersList = ArrayList()
        preference = this.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        playerName = preference.getString(USERNAME, "") ?: ""



        val extra = intent.extras
        if (extra != null) {
            roomName = extra.getString(ROOM_NAME).toString()
            hostName = extra.getString(HOST_NAME).toString()
        }

        if (hostName == playerName) {//if the room is by his name then hes the host
            role = "host"
            btnStart.isVisible = true
            //   btnReset.isVisible = true
        } else {
            role = "guest"
            btnStart.isVisible = false
            //   btnReset.isVisible = false
            // etmessage.isVisible = false
        }

        roomRef = database.getReference("rooms")
        wordRef = database.getReference("rooms/${roomName}/players")

        imgBtnPlayers.setOnClickListener {
            playersListDialog()
        }

        btnStart.setOnClickListener {//show recycler view with random words,start timer
            assignWord()
            addRoomEventListener()
            //recyclerview.adapter = Adapter(playerMapWord, playerName, roomName)
        }

        hostRef = database.getReference("rooms/${roomName}/host")

        //listen for incoming messages
        /*messageRef = database.getReference("rooms/${roomName}/message")
        message = "$role :entered!"
        messageRef.setValue(message)
        tvmessage.text = message
        addMessageEventListener()*/

        addRoomEventListener()
        addPlayersListener()
        addWordEventListener()
        //recyclerview.adapter = Adapter(playerMapWord, playerName, roomName)

    }

    private fun assignWord() {

        val arShuffled = arWordList.shuffled()
        val enShuffled = enWordList.shuffled()

        //send assigned map word/name to adapter and hide the player ones
        wordRef = database.getReference("rooms/${roomName}/players")
        //Log.i("players", playersList.toString())

        for (i in 0 until playersList.size) {
            //Log.i("filling players data with words $playerName", playersList[i])

            //check lang
            wordRef.child(playersList[i]).setValue(arShuffled[i])
            //  playerMapWord[i] = "${playersList[i]}:${arShuffled[i]}"
            //roomRef.child(playersList[i]).setValue(enShuffled[i])
        }
        //addWordEventListener()
        //recyclerview.adapter = Adapter(playerMapWord, playerName, roomName)
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

    private fun findView() {
        recyclerview = findViewById(R.id.recyclerview)
        tvTimer = findViewById(R.id.tvtimer)
        imgBtnPlayers = findViewById(R.id.imgbtnplayers)
        btnStart = findViewById(R.id.btnStart)
        //  btnReset = findViewById(R.id.btnReset)
    }

    override fun onBackPressed() {

        Log.d("onBack", "Fragment back pressed invoked")

        roomRef = database.getReference("rooms/${roomName}/players/${playerName}")
        //Log.i("BACK", "$playerName == $hostName")
        if (playerName == hostName) {
            /* message = "host just left"
             messageRef.setValue(message)*/

            hostRef = database.getReference("rooms/${roomName}/host")
            //if host left change the value to "gone" and check if that value is changed then move home cuz host left
            hostRef.setValue("gone")

            roomRef = database.getReference("rooms/${roomName}")
            roomRef.removeValue()//room
        } else {
            roomRef.removeValue()//player

            addToPlayersNum()//add one to players number

        }
        super.onBackPressed()
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
        roomRef.child(roomName).addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key != playerName && snapshot.key != "message") {
                    roomContentMap[snapshot.key!!] = snapshot.value.toString()
                }
                // playersList.add(map.toString())
                //   //Log.i("Map", playerMapWord.toString())

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key != playerName && snapshot.key != "message") {
                    roomContentMap[snapshot.key!!] = snapshot.value.toString()
                }
                //playersList.add(map.toString())
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if (snapshot.key != playerName && snapshot.key != "message") {
                    roomContentMap.remove(snapshot.key!!)
                  //  recyclerview.adapter = Adapter(playerMapWord, playerName, roomName)//.notifyItemRemoved()
                }
//                playersList.add(map.toString())

                if (snapshot.key == playerName) {//player left
                    //Log.i("onChildRemoved", "${snapshot.key} player left")
                    finish()
                }


                if (snapshot.key == "host") {//if host left and deleted "host" key
                    //Log.i("onChildRemoved", "Room been deleted $playerName")
                    finish()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.key != playerName && snapshot.key != "message") {
                    roomContentMap[snapshot.key!!] = snapshot.value.toString()
                }
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
        roomRef = database.getReference("rooms/$roomName")
        roomRef.child("players").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        //        if (snapshot.key.toString() != playerName) {
                    playersList.add(snapshot.key.toString())
                    //Log.i("player add", snapshot.key.toString())
        //        }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                recyclerview.adapter = Adapter(playerMapWord, playerName, roomName)
                //  if (role == "guest") {//remove this?
                //Log.i(" onChildChanged playersList:", playersList.toString())
                shortenList = playersList.minus(playerName)
                //Log.i(" onChildChanged shortenList:", shortenList.toString())

                //   for (i in 0 until playersList.size) {
                if(snapshot.key!=playerName) {
                    playerMapWord[shortenList.indexOf(snapshot.key.toString())] =
                        mutableListOf(snapshot.key.toString(),snapshot.value.toString())
                }
                //    }

                //    }

                //Log.i("player change $playerName", playerMapWord.toString())
                //  //Log.i("player change $playerName", "${snapshot.key.toString()} ${snapshot.value.toString()}")

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                playersList.remove(snapshot.key.toString())
                //Log.i("player delete playersList", playersList.toString())

                playerMapWord.remove(shortenList.indexOf(snapshot.key.toString()))
                    //Log.i( "player delete playerMapWord",it.toString())
                    Log.i( "player delete shortenList.indexOf", "${shortenList.indexOf(snapshot.key.toString())} ${snapshot.key.toString()}")


                recyclerview.adapter!!.notifyItemRemoved(shortenList.indexOf(snapshot.key.toString()))
                shortenList=playersList.minus(playerName)
                //Log.i("player delete shortenList", playersList.toString())


                //playerMapWord.remove(playersList.indexOf(snapshot.key.toString()))
                //Log.i("player delete", snapshot.key.toString())


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
            //Log.i("observeWord wordList: ", wordList.toString())
            wordList.forEach { word ->
                arWordList.add(word.wordAr)
                enWordList.add(word.wordEn)
            }

            //  recyclerview.adapter = Adapter(it, playersList as ArrayList<String>,playerName,roomName)
        })

        return wordList
    }

    private fun addWordEventListener() {
        wordRef.addValueEventListener(object : ValueEventListener {
            //when button clicked message changes and this is activated
            override fun onDataChange(snapshot: DataSnapshot) {
//message received
                recyclerview.adapter = Adapter(playerMapWord, playerName, roomName)

            }

            override fun onCancelled(error: DatabaseError) {
//error = retry
                recyclerview.adapter = Adapter(playerMapWord, playerName, roomName)

            }

        })
    }
}
