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
        var todayOpenIssueCount = 0
        var todayOpenPullRequestCount = 0

        logger.log("Start Calculating Today's Commit Count")

        // 이벤트 목록을 조회한다. push 이벤트의 경우 커밋을 조회하기 위해 별도로 저장한다.
        while (eventFetchPage < EVENT_FETCH_MAX_PAGE) {
            logger.log("Start Fetching GitHub Event. page: $eventFetchPage")
            val response: String? = fetchUserEvents(page = eventFetchPage)
            val events: List<GitHubPublicEventOfaUser> = deserializeToEvents(response)
            events.forEach { event ->
                when {
                    event.isTodayPushEvent() -> todayPushEvents.add(event)
                    event.isTodayOpenIssuesEvent() -> todayOpenIssueCount++
                    event.isTodayOpenPullRequestEvent() -> todayOpenPullRequestCount++
                }
            }

            val todayEventCounts = events.filter { it.isToday() }.size
            if (todayEventCounts < EVENT_PER_PAGE) {
                break
            } else {
                eventFetchPage++
            }
        }
        if (eventFetchPage == EVENT_FETCH_MAX_PAGE) logger.log("Event Fetch Page is over the limit: $EVENT_FETCH_MAX_PAGE")

        // 커밋을 repository 별로 그룹화하고 커밋을 조회하여 오늘 커밋한 커밋을 찾는다.
        var todayCommitCount = 0
        val repositoryNames: Set<String> = getRepositoryNames(todayPushEvents)
        for (repositoryName in repositoryNames) {
            logger.log("Start Fetching commits of '$repositoryName'")

            val todayRepoCommits: Set<GitHubCommit> = getGitHubTodayRepoCommits(repositoryName, logger)

            todayCommitCount += todayRepoCommits.size
            logger.log("today's '$repositoryName' commit count: ${todayRepoCommits.size}")
        }

        logger.log("today's commit count: $todayCommitCount")
        logger.log("today's issue count: $todayOpenIssueCount")
        logger.log("today's pull request count: $todayOpenPullRequestCount")
        logger.log("End of Calculating Today's Commit Count")

        return TodayGitHubContributes(
            username = username,
            commit = todayCommitCount,
            openIssues = todayOpenIssueCount,
            openPullRequests = todayOpenPullRequestCount,
        )
    }

    private fun getRepositoryNames(todayPushEvents: Set<GitHubPublicEventOfaUser>): Set<String> {
        val repoNames = mutableSetOf<String>()
        todayPushEvents.forEach { it.getRepositoryName()?.let { repoNames.add(it) } }

        return repoNames
    }

    private fun getGitHubTodayRepoCommits(
        repositoryName: String,
        logger: Logger,
    ): Set<GitHubCommit> {
        var page = 1
        val todayCommits = mutableSetOf<GitHubCommit>()

        while (page < COMMIT_FETCH_MAX_PAGE) {
            logger.log("Fetching commits of '$repositoryName' page: $page")
            val response: String? = fetchUserCommits(repositoryName = repositoryName, page = page)
            val todayCommitsFromResponse = deserializeToCommits(response).filter { it.isToday() }
            todayCommits.addAll(todayCommitsFromResponse)

            if (todayCommitsFromResponse.size < COMMIT_PER_PAGE) {
                break
            } else {
                page++
            }
        }

        if (page == COMMIT_FETCH_MAX_PAGE) logger.log("Commit Fetch Page is over the limit: $COMMIT_FETCH_MAX_PAGE")

        return todayCommits
    }

    private fun deserializeToEvents(response: String?): List<GitHubPublicEventOfaUser> {
        val itemType = object : TypeToken<List<GitHubPublicEventOfaUser>>() {}.type
        val events: List<GitHubPublicEventOfaUser> = gson.fromJson(response, itemType)

        return events
    }

    private fun deserializeToCommits(response: String?): Set<GitHubCommit> {
        val itemType = object : TypeToken<Set<GitHubCommit>>() {}.type
        val events: Set<GitHubCommit> = gson.fromJson(response, itemType)

        return events
    }

    private fun fetchUserCommits(
        repositoryName: String,
        page: Int,
    ): String? {
        val request = buildFetchCommitsRequest(repositoryName, page)

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

    private fun buildFetchCommitsRequest(
        repositoryName: String,
        page: Int,
    ): Request {
        return Request.Builder()
            .url("https://api.github.com/repos/$repositoryName/commits?page=$page&per_page=$COMMIT_PER_PAGE")
            .header("Accept", "application/vnd.github.v3+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("Authorization", "token $token")
            .build()
    }

    private fun fetchUserEvents(page: Int): String? {
        val request = buildFetchUserEventRequest(page = page)

        return okHttpClient.newCall(request).execute().use { response ->
            if (response.code == 401) {
                throw RuntimeException("Unauthorized: Check your GitHub token")
            } else if (!response.isSuccessful) {
                throw RuntimeException("Failed to fetch user events: ${response.code} ${response.message}")
            }

            response.body?.string()
        }
    }

    private fun buildFetchUserEventRequest(page: Int): Request {
        return Request.Builder()
            .url("https://api.github.com/users/$username/events?page=$page&per_page=$EVENT_PER_PAGE")
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
    val contributes = client.getTodayContributes(logger = ConsoleLogger())

    println("=====================================")
    println(
        """
        |Today's GitHub Contributes
        |=======================
        |Commits: ${contributes.commit}
        |Open Issues: ${contributes.openIssues}
        |Open Pull Requests: ${contributes.openPullRequests}
        |=======================
        |Total: ${contributes.total}
        """.trimMargin(),
    )
}
