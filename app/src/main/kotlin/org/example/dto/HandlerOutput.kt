package org.example.dto

import org.example.github.dto.TodayGitHubContributes

data class HandlerOutput(
    val message: String,
    val errorMessage: String?,
    val contributes: TodayGitHubContributes?,
) {
    constructor(message: String, errorMessage: String?) : this(
        message = message,
        errorMessage = errorMessage,
        contributes = null,
    )

    constructor(
        message: String,
        contributes: TodayGitHubContributes,
    ) : this(
        message = message,
        errorMessage = null,
        contributes = contributes,
    )
}
