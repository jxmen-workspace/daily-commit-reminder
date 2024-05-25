package org.example.github.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class GitHubPublicEventOfaUserTest : DescribeSpec({
    describe("todayCommitEvent") {
        context("날짜가 같고 commit 종류의 이벤트라면") {
            it("true를 반환한다") {

                val gitHubPublicEventOfaUser =
                    GitHubPublicEventOfaUser(
                        id = "1",
                        type = "PushEvent",
                        createdAt = LocalDateTime.of(2024, 5, 21, 0, 0, 0),
                    )

                gitHubPublicEventOfaUser.isTodayCommitEvent(LocalDateTime.of(2024, 5, 21, 0, 0, 0)) shouldBe true
            }
        }

        context("날짜가 다르다면") {
            it("false를 반환한다") {
                val gitHubPublicEventOfaUser =
                    GitHubPublicEventOfaUser(
                        id = "1",
                        type = "PushEvent",
                        createdAt =
                            LocalDateTime.of(2024, 5, 21, 0, 0, 0),
                    )

                gitHubPublicEventOfaUser.isTodayCommitEvent(LocalDateTime.of(2024, 5, 22, 0, 0, 0)) shouldBe false
            }
        }

        context("commit이 아닌 종류의 이벤트라면") {
            it("false를 반환한다") {
                val gitHubPublicEventOfaUser =
                    GitHubPublicEventOfaUser(
                        id = "1",
                        type = "WatchEvent",
                        createdAt =
                            LocalDateTime.of(2024, 5, 21, 0, 0, 0),
                    )

                gitHubPublicEventOfaUser.isTodayCommitEvent(LocalDateTime.of(2024, 5, 21, 0, 0, 0)) shouldBe false
            }
        }
    }

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
