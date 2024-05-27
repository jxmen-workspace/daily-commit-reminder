package org.example.github.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class GitHubPublicEventPayloadCommit(
    val sha: String,
    val message: String,
)

data class GitHubPublicEventOfaUserPayload(
    val commits: List<GitHubPublicEventPayloadCommit>?,
    val action: String?,
) {
    constructor(action: String) : this(
        commits = null,
        action = action,
    )
}

data class GitHubPublicEventOfaUserRepository(
    val name: String, // 'jxmen/til' 형태로 불러와짐
)

data class GitHubPublicEventOfaUser(
    val id: String,
    val type: String,
    val repo: GitHubPublicEventOfaUserRepository?,
    val payload: GitHubPublicEventOfaUserPayload?,
    @SerializedName("created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    constructor(type: String, createdAt: LocalDateTime, payload: GitHubPublicEventOfaUserPayload) : this(
        id = null.toString(),
        type = type,
        createdAt = createdAt,
        repo = null,
        payload = payload,
    )

    fun isToday(date: LocalDateTime = LocalDateTime.now()): Boolean {
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

    fun isTodayPushEvent(date: LocalDateTime = LocalDateTime.now()): Boolean {
        return isToday(date) && type == "PushEvent"
    }

    fun isTodayOpenPullRequestEvent(date: LocalDateTime = LocalDateTime.now()): Boolean {
        return isToday(date) &&
            type == "PullRequestEvent" &&
            payload?.action == "opened"
    }

    fun isTodayOpenIssuesEvent(date: LocalDateTime = LocalDateTime.now()): Boolean {
        return isToday(date) &&
            type == "IssuesEvent" &&
            payload?.action == "opened"
    }
}
