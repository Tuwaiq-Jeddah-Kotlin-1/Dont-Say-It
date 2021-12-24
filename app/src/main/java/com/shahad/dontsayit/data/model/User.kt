package com.shahad.dontsayit.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(val username:String="",val email:String="") : Parcelable
