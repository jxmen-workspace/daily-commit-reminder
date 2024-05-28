package org.example.github.dto

data class TodayGitHubContributes(
    val username: String, // github username
    val commit: Int,
    val openPullRequests: Int,
    val openIssues: Int,
    val createRepository: Int,
    val fork: Int,
    val total: Int = (commit + openPullRequests + openIssues + createRepository + fork),
)
