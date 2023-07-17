
package com.zobaer53.zedmovies.domain.usecase

import com.zobaer53.zedmovies.core.domain.model.MediaTypeModel
import com.zobaer53.zedmovies.core.domain.repository.TvShowRepository
import javax.inject.Inject

class GetTvShowsPagingUseCase @Inject constructor(private val repository: TvShowRepository) {
    operator fun invoke(mediaTypeModel: MediaTypeModel.TvShow) =
        repository.getPagingByMediaType(mediaTypeModel)
}
