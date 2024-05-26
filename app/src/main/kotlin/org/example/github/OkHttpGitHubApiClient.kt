package org.example.github

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import org.example.github.dto.GitHubCommit
import org.example.github.dto.GitHubPublicEventOfaUser
import org.example.github.dto.TodayGitHubContributes
import org.example.support.logger.ConsoleLogger
import org.example.support.logger.Logger
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class OkHttpGitHubApiClient(
    username: String,
    token: String,
) : GitHubApiClient(username = username, token = token) {
    companion object {
        private val okHttpClient = OkHttpClient()
        private val gson: Gson = createGson()

        private fun createGson(): Gson {
            val gsonBuilder = GsonBuilder()

            gsonBuilder.registerTypeAdapter(
                LocalDateTime::class.java,
                object : JsonDeserializer<LocalDateTime> {
                    override fun deserialize(
                        json: JsonElement,
                        typeOfT: Type,
                        context: JsonDeserializationContext,
                    ): LocalDateTime {
                        // NOTE: 한국 시간대로 변환해야 커밋 개수가 정확하게 나온다.
                        val instant = Instant.parse(json.asString)
                        return LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"))
                    }
                },
            )

            return gsonBuilder.create()
        }
    }

    override fun getTodayContributes(logger: Logger): TodayGitHubContributes {
        val todayPushEvents = mutableSetOf<GitHubPublicEventOfaUser>()
        var todayOpenIssueCounts = 0
        var todayOpenPullRequestCounts = 0

        logger.log("Start Calculating Today's Commit Count")

        // 이벤트 목록을 조회한다. push 이벤트의 경우 커밋을 조회하기 위해 별도로 저장한다.
        while (page < MAX_PAGE) {
            logger.log("Fetching GitHub API page: $page")
            val response: String? = fetchUserEvents(page = page, perPage = perPage)
            val events: List<GitHubPublicEventOfaUser> = deserializeToEvents(response)
            events.forEach { event ->
                when {
                    event.isTodayPushEvent() -> todayPushEvents.add(event)
                    event.isTodayOpenIssuesEvent() -> todayOpenIssueCounts++
                    event.isTodayOpenPullRequestEvent() -> todayOpenPullRequestCounts++
                }
            }

            val todayEventCounts = events.filter { it.isToday() }.size
            if (todayEventCounts < perPage) {
                break
            } else {
                page++
            }
        }
        logger.log("today's issue count: $todayOpenIssueCounts")
        logger.log("today's pull request count: $todayOpenPullRequestCounts")

        var todayCommitCounts = 0

        // 커밋을 repository 별로 그룹화하고 커밋을 조회하여 오늘 커밋한 커밋을 찾는다.
        val repositoryCommitsMap = groupCommitByRepository(todayPushEvents)
        for ((repositoryName, pushEvents) in repositoryCommitsMap) {
            logger.log("Fetching commits of '$repositoryName'")
            val response: String? = fetchUserCommits(repositoryName)
            val commits: List<GitHubCommit> = deserializeToCommits(response)

            val todayCommits =
                commits.filter { it.isToday() }
                    .filter { commit -> pushEvents.any { pushEvent -> pushEvent.hasSameCommitSha(commit.sha) } }

            todayCommitCounts += todayCommits.size
            logger.log("today's '$repositoryName' commit count: ${todayCommits.size}")
        }

        logger.log("today's commit count: $todayCommitCounts")
        logger.log("today's issue count: $todayOpenIssueCounts")
        logger.log("today's pull request count: $todayOpenPullRequestCounts")
        logger.log("End of Calculating Today's Commit Count")

        return TodayGitHubContributes(
            username = username,
            commit = todayCommitCounts,
            openIssues = todayOpenIssueCounts,
            openPullRequests = todayOpenPullRequestCounts,
        )
    }

    private fun deserializeToEvents(response: String?): List<GitHubPublicEventOfaUser> {
        val itemType = object : TypeToken<List<GitHubPublicEventOfaUser>>() {}.type
        val events: List<GitHubPublicEventOfaUser> = gson.fromJson(response, itemType)

        return events
    }

    private fun groupCommitByRepository(
        todayPushEvents: Set<GitHubPublicEventOfaUser>,
    ): MutableMap<String, Set<GitHubPublicEventOfaUser>> {
        val repositoryCommitsMap = mutableMapOf<String, Set<GitHubPublicEventOfaUser>>()
        todayPushEvents.forEach {
            val repositoryName = it.repo!!.name
            if (repositoryCommitsMap.containsKey(repositoryName)) {
                repositoryCommitsMap[repositoryName] = repositoryCommitsMap[repositoryName]!!.plus(it)
            } else {
                repositoryCommitsMap[repositoryName] = setOf(it)
            }
        }

        return repositoryCommitsMap
    }

    private fun deserializeToCommits(response: String?): List<GitHubCommit> {
        val itemType = object : TypeToken<List<GitHubCommit>>() {}.type
        val events: List<GitHubCommit> = gson.fromJson(response, itemType)

        return events
    }

    private fun fetchUserCommits(repositoryName: String): String? {
        val request = buildFetchCommitsRequest(repositoryName)

        val response =
            okHttpClient.newCall(request).execute().use { response ->
                if (response.code == 401) {
                    throw RuntimeException("Unauthorized: Check your GitHub token")
                } else if (!response.isSuccessful) {
                    throw RuntimeException("Failed to fetch commits: ${response.code} ${response.message}")
                }

                response.body?.string()
            }

        return response
    }

    private fun buildFetchCommitsRequest(repositoryName: String): Request {
        return Request.Builder()
            .url("https://api.github.com/repos/$repositoryName/commits")
            .header("Accept", "application/vnd.github.v3+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("Authorization", "token $token")
            .build()
    }

    private fun fetchUserEvents(
        page: Int,
        perPage: Int,
    ): String? {
        val request = buildFetchUserEventRequest(page = page, perPage = perPage)

        return okHttpClient.newCall(request).execute().use { response ->
            if (response.code == 401) {
                throw RuntimeException("Unauthorized: Check your GitHub token")
            } else if (!response.isSuccessful) {
                throw RuntimeException("Failed to fetch user events: ${response.code} ${response.message}")
            }

            response.body?.string()
        }
    }

    private fun buildFetchUserEventRequest(
        page: Int,
        perPage: Int,
    ): Request {
        return Request.Builder()
            .url("https://api.github.com/users/$username/events?page=$page&per_page=$perPage")
            .header("Accept", "application/vnd.github.v3+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("Authorization", "token $token")
            .build()
    }
}

fun main() {
    val client =
        OkHttpGitHubApiClient(
            username = System.getenv("GITHUB_USERNAME"),
            token = System.getenv("GITHUB_TOKEN"),
        )
    val activity = client.getTodayContributes(logger = ConsoleLogger())

    println("=====================================")
    println(
        """
        |Today's GitHub Activity
        |=======================
        |Commits: ${activity.commit}
        |Open Issues: ${activity.openIssues}
        |Open Pull Requests: ${activity.openPullRequests}
        """.trimMargin(),
    )
}
