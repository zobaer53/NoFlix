package com.zobaer53.zedmovies.data.repository

import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.common.result.isFailure
import com.zobaer53.zedmovies.data.common.result.isSuccess
import com.zobaer53.zedmovies.data.datastore.PreferencesDataStoreDataSource
import com.zobaer53.zedmovies.data.mapper.asCertificationsModel
import com.zobaer53.zedmovies.data.mapper.asWatchProvidersModel
import com.zobaer53.zedmovies.data.network.source.FilterOptionsNetworkDataSource
import com.zobaer53.zedmovies.data.util.Constants
import com.zobaer53.zedmovies.domain.model.FilterOptionsModel
import com.zobaer53.zedmovies.domain.repository.FilterOptionsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterOptionsRepositoryImpl @Inject constructor(
    private val networkDataSource: FilterOptionsNetworkDataSource,
    private val preferencesDataStoreDataSource: PreferencesDataStoreDataSource
) : FilterOptionsRepository {
    private val mutex = Mutex()
    private var cached: FilterOptionsModel? = null

    // These lists rarely change, so one fetch per process is enough.
    override suspend fun getFilterOptions(): ZedMoviesResult<FilterOptionsModel> = mutex.withLock {
        cached?.let { return ZedMoviesResult.success(it) }
        val region = Locale.getDefault().country.ifBlank { FALLBACK_REGION }
        val language = preferencesDataStoreDataSource.getContentLanguage().first()
        coroutineScope {
            val certifications = async { networkDataSource.getMovieCertifications() }
            val providers = async { fetchWatchProviders(region, language) }
            val certificationsResult = certifications.await()
            val providersResult = providers.await()
            when {
                certificationsResult.isFailure() -> ZedMoviesResult.failure(certificationsResult.error)
                providersResult.isFailure() -> ZedMoviesResult.failure(providersResult.error)
                certificationsResult.isSuccess() && providersResult.isSuccess() -> {
                    val options = FilterOptionsModel(
                        certifications = certificationsResult.value
                            .asCertificationsModel(region, FALLBACK_REGION),
                        watchProviders = providersResult.value
                    )
                    cached = options
                    ZedMoviesResult.success(options)
                }
                else -> error("${Constants.Messages.UNHANDLED_STATE} $certificationsResult $providersResult")
            }
        }
    }

    /** Movie and TV services merged; the US list when TMDB has none for [region]. */
    private suspend fun fetchWatchProviders(region: String, language: String) =
        fetchWatchProvidersIn(region, language).let { result ->
            if (result.isSuccess() && result.value.providers.isEmpty() && region != FALLBACK_REGION) {
                fetchWatchProvidersIn(FALLBACK_REGION, language)
            } else {
                result
            }
        }

    private suspend fun fetchWatchProvidersIn(region: String, language: String) = coroutineScope {
        val movie = async { networkDataSource.getMovieWatchProviders(region, language) }
        val tv = async { networkDataSource.getTvWatchProviders(region, language) }
        val movieResult = movie.await()
        val tvResult = tv.await()
        when {
            movieResult.isFailure() -> ZedMoviesResult.failure(movieResult.error)
            tvResult.isFailure() -> ZedMoviesResult.failure(tvResult.error)
            movieResult.isSuccess() && tvResult.isSuccess() -> ZedMoviesResult.success(
                (movieResult.value.results + tvResult.value.results).asWatchProvidersModel(region)
            )
            else -> error("${Constants.Messages.UNHANDLED_STATE} $movieResult $tvResult")
        }
    }
}

private const val FALLBACK_REGION = "US"
