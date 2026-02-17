package com.example.astronicalhandbook.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.astronicalhandbook.data.News
import com.example.astronicalhandbook.data.NewsRepository
import com.example.astronicalhandbook.data.Prefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<List<News>>(emptyList())
    val uiState: StateFlow<List<News>> = _uiState.asStateFlow()
    private val prefs = Prefs(application)

    private val currentNews: MutableList<News?> = MutableList(4) { null }
    private val likedNews = HashSet<Int>()

    init {
        val initialIndices = (NewsRepository.newsList.indices).shuffled().take(4)
        initialIndices.forEachIndexed { index, newsIndex ->
            val news = NewsRepository.newsList[newsIndex].copy()
            val savedLikes = prefs.getLikes(news.id)
            if (savedLikes != -1) {
                news.likes = savedLikes
            }
            currentNews[index] = news
        }
        updateUiState()
        startTimer()
    }

    private fun updateUiState() {
        _uiState.value = currentNews.filterNotNull().toList()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                delay(5000)
                replaceRandomNews()
            }
        }
    }

    private fun replaceRandomNews() {
        val maxRetries = 10
        var retryCount = 0
        val slotToReplace = Random.nextInt(4)

        while (retryCount < maxRetries) {
            val randomNewsIndex = Random.nextInt(NewsRepository.newsList.size)
            val newNews = NewsRepository.newsList[randomNewsIndex].copy()

            if (!currentNews.contains(newNews)) {
                val savedLikes = prefs.getLikes(newNews.id)
                if (savedLikes != -1) {
                    newNews.likes = savedLikes
                }

                currentNews[slotToReplace] = newNews
                updateUiState()
                return
            }
            retryCount++

        }
    }

    fun onLikeClicked(index: Int) {
        if (currentNews[index] == null || likedNews.contains(currentNews[index]?.id ?: 0))
            return
        if (index in 0 until 4) {
            currentNews[index]?.let {
                val updatedNews = it.copy(likes = it.likes + 1)
                prefs.saveLikes(updatedNews.id, updatedNews.likes)
                currentNews[index] = updatedNews
                likedNews.add(currentNews[index]?.id ?: 0)
                updateUiState()
            }
        }
    }
}
