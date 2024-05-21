package org.example.github.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class GitHubPublicEventOfaUser(
    val id: String,
    val type: String,
    @SerializedName("created_at") val createdAt: LocalDateTime,
) {
    fun isTodayCommitEvent(date: LocalDateTime = LocalDateTime.now()): Boolean {
        return isToday(date) && isCommitEvent()
    }

    private fun isCommitEvent(): Boolean {
        return when (type) {
            "PushEvent" -> true
            "PullRequestEvent" -> true
            "IssuesEvent" -> true
            else -> false
        }
    }

    private fun isToday(date: LocalDateTime): Boolean {
        if (createdAt.year != date.year) {
            return false
        }

        if (createdAt.month != date.month) {
            return false
        }

        if (createdAt.dayOfMonth != date.dayOfMonth) {
            return false
        }

        return true
    }
}
