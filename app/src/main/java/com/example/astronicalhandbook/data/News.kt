package com.example.astronicalhandbook.data

data class News(
    val id: Int,
    val title: String,
    val description: String,
    var likes: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is News) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
