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

enum class GitHubEventType {
    CreateEvent,
    PushEvent,
    PullRequestEvent,
    IssuesEvent,
    DeleteEvent,
    WatchEvent,
}

data class GitHubPublicEventOfaUser(
    val id: String,
    val type: GitHubEventType,
    val repo: GitHubPublicEventOfaUserRepository?,
    val payload: GitHubPublicEventOfaUserPayload? = null,
    @SerializedName("created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    constructor(type: String, createdAt: LocalDateTime, payload: GitHubPublicEventOfaUserPayload) : this(
        id = null.toString(),
        type = GitHubEventType.valueOf(type),
        createdAt = createdAt,
        repo = null,
        payload = payload,
    )

    constructor(type: String, repo: GitHubPublicEventOfaUserRepository) : this(
        id = null.toString(),
        type = GitHubEventType.valueOf(type),
        repo = repo,
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
        return isToday(date) && type == GitHubEventType.PushEvent
    }

    fun isTodayOpenPullRequestEvent(date: LocalDateTime = LocalDateTime.now()): Boolean {
        return isToday(date) &&
            type == GitHubEventType.PullRequestEvent &&
            payload?.action == "opened"
    }

    fun isTodayOpenIssuesEvent(date: LocalDateTime = LocalDateTime.now()): Boolean {
        return isToday(date) &&
            type == GitHubEventType.IssuesEvent &&
            payload?.action == "opened"
    }

    fun getRepositoryName(): String? {
        return when (type) {
            GitHubEventType.PushEvent -> repo?.name
            else -> error("이벤트가 pushEvent type이 아니면 이 함수를 호출해서는 안됩니다. 타입: $type")
        }
    }
}
