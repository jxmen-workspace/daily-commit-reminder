package org.example.messenger

import org.example.github.dto.TodayGitHubContributes
import org.example.support.logger.Logger

interface Messenger {
    fun sendMessage(
        text: String,
        logger: Logger,
    )

    fun sendGitHubContributesMessage(
        contributes: TodayGitHubContributes,
        logger: Logger,
    )
}
