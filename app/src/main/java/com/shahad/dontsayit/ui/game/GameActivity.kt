package com.shahad.dontsayit.ui.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.shahad.dontsayit.R

class GameActivity : AppCompatActivity() {
    private lateinit var recyclerview: RecyclerView

    private lateinit var tvTimer:TextView
    private lateinit var imgBtnPlayers: ImageButton
    private lateinit var btnStart: Button
    private lateinit var btnReset: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        findView()
        recyclerview.layoutManager =   GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false)
    }

    private fun findView() {
        recyclerview = findViewById(R.id.recyclerview)
        tvTimer = findViewById(R.id.tvtimer)
        imgBtnPlayers = findViewById(R.id.imgbtnplayers)
        btnStart = findViewById(R.id.btnStart)
        btnReset = findViewById(R.id.btnReset)
    }
}