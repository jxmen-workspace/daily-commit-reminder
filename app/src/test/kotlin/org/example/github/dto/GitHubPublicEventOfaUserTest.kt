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

    describe("isTodayOpenIssueEvent") {
        context("오늘 생성한 Issue 이벤트일 경우") {

            it("true를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubPublicEventOfaUser(
                        type = "IssuesEvent",
                        createdAt = date,
                        payload = GitHubPublicEventOfaUserPayload(action = "opened"),
                    )

                event.isTodayOpenIssuesEvent(date) shouldBe true
            }
        }

        context("오늘 생성하지 않은 Issue 이벤트일 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubPublicEventOfaUser(
                        type = "IssuesEvent",
                        createdAt = date.minusDays(1),
                        payload =
                            GitHubPublicEventOfaUserPayload(
                                action = "opened",
                            ),
                    )

                event.isTodayOpenIssuesEvent(date) shouldBe false
            }
        }

        context("오늘 생성한 Issue 이지만 closed 상태일 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubPublicEventOfaUser(
                        type = "IssuesEvent",
                        createdAt = date,
                        payload =
                            GitHubPublicEventOfaUserPayload(
                                action = "closed",
                            ),
                    )

                event.isTodayOpenIssuesEvent(date) shouldBe false
            }
        }
    }

    describe("isTodayOpenPullRequestEvent") {
        context("오늘 생성한 Pull Request 이벤트일 경우") {

            it("true를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubPublicEventOfaUser(
                        type = "PullRequestEvent",
                        createdAt = date,
                        payload = GitHubPublicEventOfaUserPayload(action = "opened"),
                    )

                event.isTodayOpenPullRequestEvent(date) shouldBe true
            }
        }

        context("오늘 생성하지 않은 pull request 이벤트일 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubPublicEventOfaUser(
                        type = "PullRequestEvent",
                        createdAt = date.minusDays(1),
                        payload =
                            GitHubPublicEventOfaUserPayload(
                                action = "opened",
                            ),
                    )

                event.isTodayOpenPullRequestEvent(date) shouldBe false
            }
        }

        context("오늘 생성한 pull request 이지만 closed 상태일 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubPublicEventOfaUser(
                        type = "PullRequestEvent",
                        createdAt = date,
                        payload =
                            GitHubPublicEventOfaUserPayload(
                                action = "closed",
                            ),
                    )

                event.isTodayOpenPullRequestEvent(date) shouldBe false
            }
        }
    }
})
