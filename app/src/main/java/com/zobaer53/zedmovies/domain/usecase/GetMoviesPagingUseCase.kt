
package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.domain.model.MediaTypeModel
import com.zobaer53.zedmovies.domain.repository.MovieRepository
import javax.inject.Inject

class GetMoviesPagingUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(mediaTypeModel: MediaTypeModel.Movie) =
        repository.getPagingByMediaType(mediaTypeModel)
}
