package com.personal.to_dolistapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Label (
    var name: String,       // Pake sebagai ID juga
    var color: String
) : Parcelable