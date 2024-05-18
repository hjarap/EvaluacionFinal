package cl.hectorjara.EvaluacionFinal.BD

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Reading::class], version = 1, exportSchema = false)
abstract class ReadingDatabase : RoomDatabase() {

    // Método abstracto que devuelve el DAO asociado a la entidad Reading
    abstract fun readingDao(): ReadingDao

    companion object {
        @Volatile
        private var INSTANCE: ReadingDatabase? = null

        // Método estático para obtener una instancia de la base de datos
        fun getDatabase(context: Context): ReadingDatabase {

            // Utiliza el patrón de diseño Singleton para garantizar que solo haya una instancia de la base de datos
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReadingDatabase::class.java,
                    "reading_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}