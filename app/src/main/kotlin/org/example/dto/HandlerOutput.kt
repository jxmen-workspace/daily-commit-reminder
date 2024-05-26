package org.example.dto

import org.example.github.dto.TodayGitHubContributes

data class HandlerOutput(
    val message: String,
    val errorMessage: String?,
    val activity: TodayGitHubContributes?,
) {
    constructor(message: String, errorMessage: String?) : this(
        message = message,
        errorMessage = errorMessage,
        activity = null,
    )

    constructor(
        message: String,
        contributes: TodayGitHubContributes,
    ) : this(
        message = message,
        errorMessage = null,
        activity = contributes,
    )
}
