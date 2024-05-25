package org.example.github.dto

import java.time.LocalDateTime

data class Author(val date: LocalDateTime)

data class Commit(val author: Author)

data class GitHubCommit(
    val sha: String,
    val commit: Commit,
) {
    fun isToday(date: LocalDateTime = LocalDateTime.now()): Boolean {
        return commit.author.date.toLocalDate() == date.toLocalDate()
    }
}
