package org.example.github

import org.example.github.dto.TodayGitHubContributes
import org.example.support.logger.Logger

abstract class GitHubApiClient(
    protected var eventFetchPage: Int = 1,
    protected val username: String,
    protected val token: String,
) {
    companion object {
        const val EVENT_FETCH_MAX_PAGE = 10
        const val EVENT_PER_PAGE = 30

        const val COMMIT_FETCH_MAX_PAGE = 10
        const val COMMIT_PER_PAGE = 30
    }

    abstract suspend fun getTodayContributes(logger: Logger): TodayGitHubContributes
}
