package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.data.storage.dao.DBExampleDataDao
import ru.rznnike.demokmp.data.storage.entity.toDBExampleData
import ru.rznnike.demokmp.data.storage.entity.toDBExampleDataEntity
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.DBExampleGateway
import ru.rznnike.demokmp.domain.model.db.DBExampleData

class DBExampleGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val dbExampleDataDao: DBExampleDataDao,
    private val mainDB: MainDB
) : DBExampleGateway {
    override suspend fun get(id: Long): DBExampleData? = withContext(dispatcherProvider.io) {
        dbExampleDataDao.get(id = id)?.toDBExampleData()
    }

    override suspend fun getAll(): List<DBExampleData> = withContext(dispatcherProvider.io) {
        dbExampleDataDao.getAll()
            .map { it.toDBExampleData() }
    }

    override suspend fun add(data: DBExampleData) = withContext(dispatcherProvider.io) {
        dbExampleDataDao.add(data.toDBExampleDataEntity())
    }

    override suspend fun delete(data: DBExampleData) = withContext(dispatcherProvider.io) {
        dbExampleDataDao.delete(data.id)
    }

    override suspend fun deleteAll() = withContext(dispatcherProvider.io) {
        dbExampleDataDao.deleteAll()
    }

    override suspend fun closeDB() = withContext(dispatcherProvider.io) {
        mainDB.close()
    }
}