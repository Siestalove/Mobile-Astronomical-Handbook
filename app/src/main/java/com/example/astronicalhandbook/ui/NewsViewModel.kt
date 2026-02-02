package com.example.astronicalhandbook.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astronicalhandbook.data.News
import com.example.astronicalhandbook.data.NewsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class NewsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<List<News>>(emptyList())
    val uiState: StateFlow<List<News>> = _uiState.asStateFlow()


    private val currentNews = Array<News?>(4) { null }

    init {

        val initialIndices = (NewsRepository.newsList.indices).shuffled().take(4)
        initialIndices.forEachIndexed { index, newsIndex ->
            currentNews[index] = NewsRepository.newsList[newsIndex].copy()
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

        val slotToReplace = Random.nextInt(4)
        

        val randomNewsIndex = Random.nextInt(NewsRepository.newsList.size)
        val newNews = NewsRepository.newsList[randomNewsIndex].copy()

        currentNews[slotToReplace] = newNews
        updateUiState()
    }

    fun onLikeClicked(index: Int) {
        if (index in 0 until 4) {
            currentNews[index]?.let {
                val updatedNews = it.copy(likes = it.likes + 1)
                currentNews[index] = updatedNews
                updateUiState()
            }
        }
    }
}
