package com.getrepo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class ItemEntity(
    @PrimaryKey val id: Int? = null,
    val repositoryName: String,
    val ownerName: String,
    val ownerAvatarUrl: String,
    val amountOfStars: Int,
    val amountOfForks: Int
)
