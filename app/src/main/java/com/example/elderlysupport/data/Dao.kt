package com.example.elderlysupport.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmacoDao {

    @Insert
    suspend fun inserisci(farmaco: Farmaco): Long

    @Update
    suspend fun aggiorna(farmaco: Farmaco)

    @Delete
    suspend fun elimina(farmaco: Farmaco)

    @Query("SELECT * FROM farmaco ORDER BY nome ASC")
    fun getTutti(): Flow<List<Farmaco>>
}

@Dao
interface PromemoriaDao {

    @Insert
    suspend fun inserisci(promemoria: Promemoria): Long

    @Delete
    suspend fun elimina(promemoria: Promemoria)

    @Query("SELECT * FROM promemoria WHERE farmacoId = :farmacoId")
    fun getPerFarmaco(farmacoId: Long): Flow<List<Promemoria>>

    @Query("SELECT * FROM promemoria")
    suspend fun getTutti(): List<Promemoria>
}

@Dao
interface ContattoDao {

    @Insert
    suspend fun inserisci(contatto: ContattoEmergenza): Long

    @Delete
    suspend fun elimina(contatto: ContattoEmergenza)

    @Query("SELECT * FROM contatto_emergenza ORDER BY nome ASC")
    fun getTutti(): Flow<List<ContattoEmergenza>>
}