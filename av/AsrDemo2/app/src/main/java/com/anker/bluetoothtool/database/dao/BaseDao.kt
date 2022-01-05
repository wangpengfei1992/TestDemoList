package com.anker.bluetoothtool.database.dao

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg element: T)

    @Delete
    suspend fun delete(element: T)

    @Delete
    suspend fun deleteList(elements: List<T>)

    @Delete
    suspend fun deleteSome(vararg elements: T)

    @Update
    fun update(element: T): Int

}