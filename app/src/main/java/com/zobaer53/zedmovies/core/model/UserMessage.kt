
package com.zobaer53.zedmovies.core.model

import androidx.annotation.StringRes

class UserMessage(
    @StringRes val messageResourceId: Int,
    vararg val args: Any
)
