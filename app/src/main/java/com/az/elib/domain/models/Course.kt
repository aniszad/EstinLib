package com.az.elib.domain.models

data class Course(
    val courseName: String,
    val courseYear : Int,
    val courseSemester: Int,
    val courseDriveFileId: String,
    val courseImage: Int,
    val specialization : String
) {}
