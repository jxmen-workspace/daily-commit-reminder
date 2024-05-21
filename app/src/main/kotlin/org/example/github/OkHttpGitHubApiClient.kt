package org.example.github

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import org.example.github.dto.GitHubPublicEventOfaUser
import org.example.support.logger.ConsoleLogger
import org.example.support.logger.Logger
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val okHttpClient = OkHttpClient()

class OkHttpGitHubApiClient(
    username: String,
    token: String,
) : GitHubApiClient(username = username, token = token) {
    private var gson: Gson // for deserializing JSON

    init {
        val gsonBuilder = GsonBuilder()

        gsonBuilder.registerTypeAdapter(
            LocalDateTime::class.java,
            object : JsonDeserializer<LocalDateTime> {
                override fun deserialize(
                    json: JsonElement,
                    typeOfT: Type,
                    context: JsonDeserializationContext,
                ): LocalDateTime {
                    return LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_DATE_TIME)
                }
            },
        )

        gson = gsonBuilder.create()
    }

    override fun getTodayCommitCount(logger: Logger): Int {
        // init about api request variables
        var todayCommitEventCounts = 0

        logger.log("Start Fetching GitHub API")
        while (true) {
            logger.log("Fetching GitHub API page: $page")
            val events: List<GitHubPublicEventOfaUser> = fetchUserEvents(page = page, perPage = perPage)
            val thisPageTodayCommitEvents = events.filter { it.isTodayCommitEvent() }
            todayCommitEventCounts += thisPageTodayCommitEvents.size

            if (thisPageTodayCommitEvents.size < perPage) {
                break
            } else {
                page++
            }
        }
        logger.log("Fetched all GitHub API")
        logger.log("Today's commit count: $todayCommitEventCounts")

        return todayCommitEventCounts
    }

    private fun fetchUserEvents(
        page: Int,
        perPage: Int,
    ): List<GitHubPublicEventOfaUser> {
        val request = buildAUserEventsRequest(page = page, perPage = perPage)
        val responseString =
            okHttpClient.newCall(request).execute().use { response ->
                if (response.code == 401) {
                    throw RuntimeException("Unauthorized: Check your GitHub token")
                } else if (!response.isSuccessful) {
                    throw RuntimeException("Failed to fetch user events: ${response.code} ${response.message}")
                }

                response.body?.string()
            }
        val itemType = object : TypeToken<List<GitHubPublicEventOfaUser>>() {}.type
        val events: List<GitHubPublicEventOfaUser> = gson.fromJson(responseString, itemType)

        return events
    }

    private fun buildAUserEventsRequest(
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
    val commitCount = client.getTodayCommitCount(logger = ConsoleLogger())

    println(commitCount)
}
