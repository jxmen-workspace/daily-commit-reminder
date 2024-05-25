package org.example.github.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class GitHubPublicEventPayloadCommit(
    val sha: String,
    val message: String,
)

data class GitHubPublicEventOfaUserPayload(val commits: List<GitHubPublicEventPayloadCommit>?)

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
    constructor(id: String, type: String, createdAt: LocalDateTime) : this(
        id = id,
        type = type,
        repo = null,
        payload = null,
        createdAt = createdAt,
    )

    constructor(id: String, type: String, payload: GitHubPublicEventOfaUserPayload, createdAt: LocalDateTime) : this(
        id = id,
        type = type,
        repo = null,
        payload = payload,
        createdAt = createdAt,
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

    fun isTodayIssuesEvent(): Boolean {
        return isToday(LocalDateTime.now()) && type == "IssuesEvent"
    }

    fun isTodayPullRequestEvent(): Boolean {
        return isToday(LocalDateTime.now()) && type == "PullRequestEvent"
    }

    fun hasSameCommitSha(sha: String): Boolean {
        return payload?.commits?.any { it.sha == sha } == true
    }
}
