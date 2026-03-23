package com.example.astronicalhandbook.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astronicalhandbook.data.News
import com.example.astronicalhandbook.opengl.OpenGLActivity

@Composable
fun NewsScreen(viewModel: NewsViewModel = viewModel()) {
    val newsList by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout)
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(bottom = 60.dp) 
        ) {
        Row(modifier = Modifier.weight(1f)) {
            Quadrant(news = newsList.getOrNull(0), onLikeClick = { viewModel.onLikeClicked(0) }, modifier = Modifier.weight(1f))
            Quadrant(news = newsList.getOrNull(1), onLikeClick = { viewModel.onLikeClicked(1) }, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.weight(1f)) {
            Quadrant(news = newsList.getOrNull(2), onLikeClick = { viewModel.onLikeClicked(2) }, modifier = Modifier.weight(1f))
            Quadrant(news = newsList.getOrNull(3), onLikeClick = { viewModel.onLikeClicked(3) }, modifier = Modifier.weight(1f))
        }
        }
        
        Button(
            onClick = {
                context.startActivity(Intent(context, OpenGLActivity::class.java))
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Text("Куб")
        }
    }
}

@Composable
fun Quadrant(news: News?, onLikeClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .background(Color.LightGray)
    ) {
        if (news != null) {
            Column(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.9f)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = news.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = news.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f)
                        .background(Color.Gray.copy(alpha = 0.2f))
                        .clickable { onLikeClick() }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Лайки: ${news.likes}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
