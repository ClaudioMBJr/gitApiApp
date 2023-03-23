package com.getrepo.presentation.listRepo

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ListScreen(
    vm: ListScreenViewModel = hiltViewModel()
) {
    val listScreenState = vm.listScreenState
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        vm.getRepositories()
    }

    if (vm.listScreenState.isLoading)
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
        }

    if (vm.listScreenState.hasError) {
        if (vm.listScreenState.items.isEmpty())
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Não foi possível carregar os repositórios",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )
            }
        else
            Toast.makeText(context, "Não foi possível carregar mais notícias", Toast.LENGTH_SHORT)
                .show()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, end = 16.dp, start = 16.dp)
    ) {
        items(listScreenState.items) { gitRepository ->
            if (vm.canRequestMoreItems()) {
                val itemIndex = listScreenState.items.indexOf(gitRepository)
                if (itemIndex >= listScreenState.items.size - 1) vm.getMoreItems()
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray),
                    painter = rememberImagePainter(
                        data = gitRepository.ownerAvatarUrl,
                    ),
                    contentDescription = gitRepository.ownerName,
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Repo: ${gitRepository.repositoryName}",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Owner: ${gitRepository.ownerName}",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Stars: ${gitRepository.amountOfStars}",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Forks: ${gitRepository.amountOfForks}",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            if (vm.listScreenState.isLoadingMoreItems)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
        }
    }
}