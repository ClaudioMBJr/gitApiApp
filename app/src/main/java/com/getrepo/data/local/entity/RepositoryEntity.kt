package com.getrepo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repository")
data class RepositoryEntity(
    @PrimaryKey
    @ColumnInfo("repository_id")
    val repositoryId: Int,
    @ColumnInfo("repository_name")
    val repositoryName: String,
    @ColumnInfo("owner_name")
    val ownerName: String,
    @ColumnInfo("owner_avatar_url")
    val ownerAvatarUrl: String,
    @ColumnInfo("stars")
    val amountOfStars: Int,
    @ColumnInfo("forks")
    val amountOfForks: Int,
    var page: Int
)
