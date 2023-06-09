package com.d3if3059.taskify.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    val name: String,
    val important: Boolean =false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis()

):Parcelable{
    val createdDateFormatted: String
    get() = DateFormat.getDateTimeInstance().format(created)
}
