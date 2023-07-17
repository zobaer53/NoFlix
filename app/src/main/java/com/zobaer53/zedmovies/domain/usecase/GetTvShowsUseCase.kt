
package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.domain.model.MediaTypeModel
import com.zobaer53.zedmovies.domain.repository.TvShowRepository
import javax.inject.Inject

class GetTvShowsUseCase @Inject constructor(private val repository: TvShowRepository) {
    operator fun invoke(mediaTypeModel: MediaTypeModel.TvShow) =
        repository.getByMediaType(mediaTypeModel)
}
