package com.getrepo.presentation.listRepo

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ListScreen(
    vm: ListScreenViewModel = hiltViewModel()
) {
    val repositories = vm.getRepositories().collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, end = 16.dp, start = 16.dp)
    ) {
        items(repositories) { gitRepository ->
            gitRepository?.let {
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
        }

        when (repositories.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    Row(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    ListScreenError(
                        Modifier.fillParentMaxSize(),
                        "Não foi possível carregar os repositórios"
                    ) {
                        repositories.retry()
                    }
                }
            }
            else -> Unit
        }

        when (repositories.loadState.append) {
            is LoadState.Loading -> {
                item {
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

            is LoadState.Error -> {
                item {
                    ListScreenError(
                        Modifier.fillMaxWidth(),
                        "Não foi possível carregar mais repositórios"
                    ) {
                        repositories.retry()
                    }
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun ListScreenError(
    modifier: Modifier = Modifier,
    contentText: String,
    buttonText: String = "Tentar novamente",
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = contentText,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onClick() },
            modifier = Modifier
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(
                text = buttonText,
                color = Color.White,
            )
        }
    }
}