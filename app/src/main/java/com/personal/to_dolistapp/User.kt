package com.personal.to_dolistapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var email: String,      // Pake sebagai ID juga
    var name: String,
    var todos: ArrayList<Todo> = arrayListOf(),
    var labels: ArrayList<Label> = arrayListOf()
) : Parcelable