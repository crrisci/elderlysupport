package com.example.elderlysupport.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "farmaco")
data class Farmaco(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val codiceAic: String? = null,
    val scadenza: String? = null,
    val fotoPath: String? = null
)

@Entity(
    tableName = "promemoria",
    foreignKeys = [
        ForeignKey(
            entity = Farmaco::class,
            parentColumns = ["id"],
            childColumns = ["farmacoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["farmacoId"])]
)
data class Promemoria(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val farmacoId: Long,
    val ora: Int,
    val minuto: Int,
    val giorni: String
)

@Entity(tableName = "contatto_emergenza")
data class ContattoEmergenza(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val telefono: String? = null,
    val email: String? = null
)