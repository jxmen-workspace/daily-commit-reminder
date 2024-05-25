package org.example.github

import org.example.support.logger.Logger

abstract class GitHubApiClient(
    protected var page: Int = 1,
    protected val perPage: Int = 30,
    protected val username: String,
    protected val token: String,
) {
    companion object {
        const val MAX_PAGE = 10
    }

    abstract fun getTodayCommitCount(logger: Logger): Int
}
