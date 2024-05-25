package org.example.github.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class GitHubPublicEventOfaUserTest : DescribeSpec({
    describe("hasSameCommitSha") {
        context("commit sha가 같다면") {
            it("true를 반환한다") {
                val gitHubPublicEventOfaUser =
                    GitHubPublicEventOfaUser(
                        id = "1",
                        type = "PushEvent",
                        createdAt = LocalDateTime.of(2024, 5, 21, 0, 0, 0),
                        payload =
                            GitHubPublicEventOfaUserPayload(
                                commits =
                                    listOf(
                                        GitHubPublicEventPayloadCommit(
                                            sha = "1234",
                                            message = "commit message",
                                        ),
                                    ),
                            ),
                    )

                gitHubPublicEventOfaUser.hasSameCommitSha("1234") shouldBe true
            }
        }

        context("commit sha가 다르다면") {
            it("false를 반환한다") {
                val gitHubPublicEventOfaUser =
                    GitHubPublicEventOfaUser(
                        id = "1",
                        type = "PushEvent",
                        createdAt = LocalDateTime.of(2024, 5, 21, 0, 0, 0),
                        payload =
                            GitHubPublicEventOfaUserPayload(
                                commits =
                                    listOf(
                                        GitHubPublicEventPayloadCommit(
                                            sha = "1234",
                                            message = "commit message",
                                        ),
                                    ),
                            ),
                    )

                gitHubPublicEventOfaUser.hasSameCommitSha("5678") shouldBe false
            }
        }
    }
})
