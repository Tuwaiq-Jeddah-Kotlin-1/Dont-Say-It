package com.shahad.dontsayit.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfilePicture( val id:String, val picture:String) : Parcelable
