package cl.hectorjara.EvaluacionFinal.BD

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {

    // Consulta para obtener todas las lecturas ordenadas por fecha de manera ascendente
    @Query("SELECT * FROM reading_table ORDER BY date ASC")
    fun getAllReadings(): Flow<List<Reading>>

    // Inserta una nueva lectura en la base de datos
    // Ignora la lectura si ya existe una con el mismo ID
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reading: Reading)
}