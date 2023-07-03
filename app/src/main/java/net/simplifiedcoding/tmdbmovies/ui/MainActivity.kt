package net.simplifiedcoding.tmdbmovies.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.AndroidEntryPoint
import net.simplifiedcoding.tmdbmovies.data.network.Resource
import net.simplifiedcoding.tmdbmovies.ui.common.FullScreenProgressbar
import net.simplifiedcoding.tmdbmovies.ui.common.toast
import net.simplifiedcoding.tmdbmovies.ui.movies.MovieList
import net.simplifiedcoding.tmdbmovies.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val movies = viewModel.movies.collectAsState()

            AppTheme {
                movies.value?.let {
                    when (it) {
                        is Resource.Failure -> {
                            context.toast(it.exception.message!!)
                        }
                        Resource.Loading -> {
                            FullScreenProgressbar()
                        }
                        is Resource.Success -> {
                            MovieList(it.result)
                        }
                    }
                }
            }
        }
    }
}