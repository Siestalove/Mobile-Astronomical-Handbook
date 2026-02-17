package com.example.astronicalhandbook.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class Prefs(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("news_prefs", Context.MODE_PRIVATE)

    fun saveLikes(id: Int, count: Int) {
        preferences.edit { putInt("likes_$id", count) }
    }

    fun getLikes(id: Int): Int {
        return preferences.getInt("likes_$id", -1) 
    }
}
