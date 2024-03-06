package salcedo.mario.listatareas

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Tarea::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): TareaDAO
}