package cl.hectorjara.EvaluacionFinal.BD

import androidx.room.Entity
import androidx.room.PrimaryKey

// Definición de la entidad de base de datos para almacenar mediciones
@Entity(tableName = "reading_table")
data class Reading(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Clave primaria autoincremental
    val type: String, // Tipo de medición (Agua, Luz, Gas, etc.)
    val value: Double, // Valor de la medición
    val date: String // Fecha de la medición
)

