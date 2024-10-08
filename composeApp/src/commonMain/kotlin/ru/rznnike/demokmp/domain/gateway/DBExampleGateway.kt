package ru.rznnike.demokmp.domain.gateway

import ru.rznnike.demokmp.domain.model.dbexample.DBExampleData

interface DBExampleGateway {
    suspend fun get(id: Long): DBExampleData?

    suspend fun getAll(): List<DBExampleData>

    suspend fun add(data: DBExampleData)

    suspend fun delete(data: DBExampleData)

    suspend fun deleteAll()

    suspend fun closeDB()
}