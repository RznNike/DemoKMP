package ru.rznnike.demokmp.data.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.rznnike.demokmp.domain.model.db.DBExampleData

@Entity
data class DBExampleDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)

fun DBExampleDataEntity.toDBExampleData() = DBExampleData(
    id = id,
    name = name
)

fun DBExampleData.toDBExampleDataEntity() = DBExampleDataEntity(
    id = id,
    name = name
)