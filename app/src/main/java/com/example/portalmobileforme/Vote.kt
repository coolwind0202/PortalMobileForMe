package com.example.portalmobileforme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.portalmobileforme.ui.VoteRequestError
import com.example.portalmobileforme.ui.VoteViewModel
import com.example.portalmobileforme.ui.theme.PortalMobileForMeTheme
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth

class Vote : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PortalMobileForMeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VoteScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Preview
@Composable
fun VoteContentPreview() {
    VoteContent(count = Ok(0))
}

@Composable
fun VoteScreen(modifier: Modifier = Modifier, voteViewModel: VoteViewModel = viewModel()) {
    val voteUiState by voteViewModel.uiState.collectAsState()
    VoteContent(
        modifier = modifier,
        count = voteUiState.count,
        onVoteButtonClicked = { voteViewModel.vote() })
}

@Composable
fun VoteContent(
    modifier: Modifier = Modifier,
    count: Result<Int, VoteRequestError>,
    onVoteButtonClicked: () -> Unit = {}
) {
    val text = count
        .mapBoth(
            success = { it.toString() },
            failure = {
                when (it) {
                    is VoteRequestError.ServerError -> it.reason
                    is VoteRequestError.TimeoutError -> "リクエストがタイムアウトしました"
                }
            }
        )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        modifier = modifier
    ) {
        Text(text = text)

        Button(onClick = { onVoteButtonClicked() }) {
            Text("投票！")
        }
    }
}