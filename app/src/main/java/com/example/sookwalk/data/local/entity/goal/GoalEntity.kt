package com.example.sookwalk.data.local.entity.goal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // 로컬 관리용 ID (Room)

    val remoteId: String = "", // 파이어베이스 문서 ID (동기화용)

    @ColumnInfo(name = "title")
    val title: String,

    val targetSteps: Int,
    val currentSteps: Int,

    @ColumnInfo(name = "startDate")
    val startDate: String,

    @ColumnInfo(name = "endDate")
    val endDate: String,

    @ColumnInfo(name = "memo")
    val memo: String = "",

    @ColumnInfo(name = "isDone")
    val isDone: Boolean = false,
)