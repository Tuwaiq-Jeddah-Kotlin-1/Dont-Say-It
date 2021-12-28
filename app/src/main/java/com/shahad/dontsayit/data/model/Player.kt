package com.shahad.dontsayit.data.model

data class Player(private val name:String="", private val playerData: PlayerData)

data class PlayerData(private val word:String="word",private var state:String="in",private var score:Int=0)
