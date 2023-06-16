package com.d3if3059.taskify.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.d3if3059.taskify.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback(){

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // db operations
           val dao =  database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task(name = "Hai, ayo taruh task pertamamu! "))
                dao.insert(Task(name = "Task yang sudah selesai akan tampak seperti ini \uD83D\uDCAA ", completed = true))
                dao.insert(Task(name = "Swipe ke kanan atau ke kiri untuk menghapus \uD83D\uDC48 \uD83D\uDC49 "))
                dao.insert(Task(name = "Kamu bisa klik task untuk mengedit ✍\uD83C\uDFFB "))
                dao.insert(Task(name = "Task penting akan berada di urutan paling atas", important = true))
                dao.insert(Task(name = "Ahmad Abdul Fatah\uD83C\uDFFB "))
            }



        }
    }
}