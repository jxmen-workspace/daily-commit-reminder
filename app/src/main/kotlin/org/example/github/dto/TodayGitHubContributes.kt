package org.example.github.dto

data class TodayGitHubContributes(
    val username: String,
    val commit: Int,
    val openPullRequests: Int,
    val openIssues: Int,
    val total: Int = commit + openPullRequests + openIssues,
)
