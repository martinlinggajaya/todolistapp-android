package com.personal.to_dolistapp

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Parcelize
data class Todo (
        var id: String? = null,
        var name: String = "",
        var notes: String = "",
        @ServerTimestamp
        var due: Date? = null,
        var labelName: String? = null,
        var labelColor: String? = null,
        var done: Boolean = false
) : Parcelable