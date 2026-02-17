package com.example.astronicalhandbook.data

data class News(
    val id: Int,
    val title: String,
    val description: String,
    var likes: Int = 0
)
