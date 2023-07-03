package net.simplifiedcoding.tmdbmovies.data.repository

import io.ktor.client.*
import io.ktor.client.request.*
import net.simplifiedcoding.tmdbmovies.data.models.Movie
import net.simplifiedcoding.tmdbmovies.data.models.PopularMovies
import net.simplifiedcoding.tmdbmovies.data.network.BASE_URL
import net.simplifiedcoding.tmdbmovies.data.network.Resource
import javax.inject.Inject

private const val POPULAR_MOVIES = "${BASE_URL}/popular"

class MoviesRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : MoviesRepository {

    override suspend fun getPopularMovies(): Resource<List<Movie>> {
        return try {
            Resource.Success(
                httpClient.get<PopularMovies> {
                    url(POPULAR_MOVIES)
                }.movies
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}