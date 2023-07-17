
package com.zobaer53.zedmovies.data.model

import androidx.annotation.StringRes

class UserMessage(
    @StringRes val messageResourceId: Int,
    vararg val args: Any
)
