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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.getrepo.R

@Composable
fun ListScreen(
    vm: ListScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val repositories = vm.getRepositories().collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("lazyColumn")
            .padding(top = 16.dp, end = 16.dp, start = 16.dp)
    ) {
        itemsIndexed(repositories) { index, gitRepository ->
            gitRepository?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray)
                        .padding(8.dp)
                        .testTag("itemId${index + 1}")
                        .semantics { "itemId${index + 1}" },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(gitRepository.ownerAvatarUrl)
                            .size(Size.ORIGINAL)
                            .crossfade(true)
                            .placeholder(R.drawable.baseline_error_outline_24)
                            .placeholder(R.drawable.baseline_error_outline_24)
                            .build()
                    )

                    when (painter.state) {
                        is AsyncImagePainter.State.Loading ->
                            Column(
                                modifier = Modifier.size(100.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                            }
                        else -> {
                            Image(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Gray),
                                painter = painter,
                                contentDescription = gitRepository.ownerName,
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = context.getString(
                                R.string.text_repo,
                                gitRepository.repositoryName
                            ),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = context.getString(
                                R.string.text_owner,
                                gitRepository.ownerName
                            ),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = context.getString(
                                R.string.text_stars,
                                gitRepository.amountOfStars.toString()
                            ),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = context.getString(
                                R.string.text_forks,
                                gitRepository.amountOfForks.toString()
                            ),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        item {
            when {
                repositories.loadState.refresh is LoadState.Loading -> {
                    if (repositories.loadState.refresh is LoadState.Error)
                        Row(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .testTag("progressFirstPage"),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator()
                        }
                }
                repositories.loadState.refresh is LoadState.Error -> {
                    ListScreenError(
                        modifier = if (repositories.itemCount != 0) Modifier
                            .fillMaxWidth()
                            .testTag("errorWithItems") else Modifier
                            .fillParentMaxSize()
                            .testTag("errorFirstPage"),
                        contentText = context.getString(R.string.text_error_loading_first_page),
                        buttonText = context.getString(R.string.text_btn_try_again)
                    ) {
                        repositories.retry()
                    }
                }
                repositories.loadState.append is LoadState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("progressWithItems"),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                    }
                }
                repositories.loadState.append is LoadState.Error -> {
                    ListScreenError(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("errorWithItems"),
                        contentText = context.getString(R.string.text_error_loading_more_items),
                        buttonText = context.getString(R.string.text_btn_try_again)
                    ) {
                        repositories.retry()
                    }
                }
            }
        }
    }
}

@Composable
private fun ListScreenError(
    modifier: Modifier = Modifier,
    contentText: String,
    buttonText: String,
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