package org.example.github.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class GitHubEventPayloadCommit(
    val sha: String,
    val message: String,
)

enum class GitHubEventPayloadAction(val str: String) {
    Opened("opened"), ;

    companion object {
        fun findByName(name: String?): GitHubEventPayloadAction {
            return entries.find { it.str == name }
                ?: error("해당하는 값이 없습니다. value: $name")
        }
    }
}

data class GitHubEventPayload(
    val commits: List<GitHubEventPayloadCommit>?,
    val action: String?,
    @SerializedName("ref_type") val refType: GtiHubEventPayloadRefType?,
) {
    fun isRepositoryRefType(): Boolean {
        return refType == GtiHubEventPayloadRefType.Repository
    }

    constructor(action: String) : this(
        action = action,
        commits = null,
        refType = null,
    )

    constructor(refType: GtiHubEventPayloadRefType) : this(
        commits = null,
        action = null,
        refType = refType,
    )
}

enum class GtiHubEventPayloadRefType(val str: String) {
    Branch("branch"),
    Repository("repository"),
    ;

    companion object {
        fun findByName(value: String): GtiHubEventPayloadRefType {
            return entries.find { it.str == value }
                ?: error("해당하는 값이 없습니다. value: $value")
        }
    }
}

data class GitHubEventRepository(
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

data class GitHubEvent(
    val id: String,
    val type: GitHubEventType,
    val repo: GitHubEventRepository?,
    val payload: GitHubEventPayload? = null,
    @SerializedName("created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    constructor(type: String, createdAt: LocalDateTime, payload: GitHubEventPayload) : this(
        id = null.toString(),
        type = GitHubEventType.valueOf(type),
        createdAt = createdAt,
        repo = null,
        payload = payload,
    )

    constructor(type: String, repo: GitHubEventRepository) : this(
        id = null.toString(),
        type = GitHubEventType.valueOf(type),
        repo = repo,
    )

    constructor(type: GitHubEventType, createdAt: LocalDateTime, payload: GitHubEventPayload) : this(
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

    fun isTodayCreateRepositoryEvent(date: LocalDateTime = LocalDateTime.now()): Boolean {
        return isToday(date) &&
            type == GitHubEventType.CreateEvent &&
            payload?.isRepositoryRefType() == true
    }
}
