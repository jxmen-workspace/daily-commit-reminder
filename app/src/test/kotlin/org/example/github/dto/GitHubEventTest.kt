package org.example.github.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class GitHubEventTest : DescribeSpec({

    describe("isTodayOpenIssueEvent") {
        context("오늘 생성한 Issue 이벤트일 경우") {

            it("true를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubEvent(
                        type = "IssuesEvent",
                        createdAt = date,
                        payload = GitHubEventPayload(action = "opened"),
                    )

                event.isTodayOpenIssuesEvent(date) shouldBe true
            }
        }

        context("오늘 생성하지 않은 Issue 이벤트일 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubEvent(
                        type = "IssuesEvent",
                        createdAt = date.minusDays(1),
                        payload =
                            GitHubEventPayload(
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
                    GitHubEvent(
                        type = "IssuesEvent",
                        createdAt = date,
                        payload =
                            GitHubEventPayload(
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
                    GitHubEvent(
                        type = "PullRequestEvent",
                        createdAt = date,
                        payload = GitHubEventPayload(action = "opened"),
                    )

                event.isTodayOpenPullRequestEvent(date) shouldBe true
            }
        }

        context("오늘 생성하지 않은 pull request 이벤트일 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubEvent(
                        type = "PullRequestEvent",
                        createdAt = date,
                        payload = GitHubEventPayload(action = "opened"),
                    )

                event.isTodayOpenPullRequestEvent(date.minusNanos(1)) shouldBe false
            }
        }

        context("오늘 생성한 pull request 이지만 closed 상태일 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubEvent(
                        type = "PullRequestEvent",
                        createdAt = date,
                        payload =
                            GitHubEventPayload(
                                action = "closed",
                            ),
                    )

                event.isTodayOpenPullRequestEvent(date) shouldBe false
            }
        }
    }

    describe("getRepositoryName") {
        context("pushEvent이면서 repository와 name이 있을 경우") {
            it("repository 이름을 리턴한다") {
                val event =
                    GitHubEvent(
                        type = "PushEvent",
                        repo = GitHubEventRepository(name = "jxmen/til"),
                    )

                event.getRepositoryName() shouldBe "jxmen/til"
            }
        }
        context("type이 pushEvent가 아닐 경우") {
            it("IllegalStateException을 던진다") {
                val types = listOf("IssuesEvent", "PullRequestEvent", "CreateEvent")

                types.forEach { type ->
                    val event =
                        GitHubEvent(
                            type = type,
                            repo = GitHubEventRepository(name = "jxmen/til"),
                        )

                    runCatching { event.getRepositoryName() }.exceptionOrNull() shouldBe
                        IllegalStateException("이벤트가 pushEvent type이 아니면 이 함수를 호출해서는 안됩니다. 타입: $type")
                }
            }
        }
    }

    describe("isTodayCreateRepositoryEvent") {

        context("오늘 생성하고 Repository를 생성한 이벤트일 경우") {

            it("true를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubEvent(
                        type = GitHubEventType.CreateEvent,
                        createdAt = date,
                        payload = GitHubEventPayload(refType = GtiHubEventPayloadRefType.Repository),
                    )

                event.isTodayCreateRepositoryEvent(date) shouldBe true
                event.isTodayCreateRepositoryEvent(date.plusHours(23).plusMinutes(59)) shouldBe true
            }
        }

        context("오늘 생성하지 않은 Repository를 생성한 이벤트일 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubEvent(
                        type = GitHubEventType.CreateEvent,
                        createdAt = date,
                        payload = GitHubEventPayload(refType = GtiHubEventPayloadRefType.Repository),
                    )

                event.isTodayCreateRepositoryEvent(date.minusSeconds(1)) shouldBe false
                event.isTodayCreateRepositoryEvent(date.plusDays(1)) shouldBe false
            }
        }

        context("오늘 생성했지만 Repository 생성이 아닌 이벤트일 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event =
                    GitHubEvent(
                        type = GitHubEventType.CreateEvent,
                        createdAt = date,
                        payload = GitHubEventPayload(refType = GtiHubEventPayloadRefType.Branch),
                    )

                event.isTodayCreateRepositoryEvent(date) shouldBe false
            }
        }
    }

    describe("isTodayForkEvent") {

        context("오늘 생성한 ForkEvent일 경우") {

            it("true를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event = GitHubEvent(type = GitHubEventType.ForkEvent, createdAt = date)

                event.isTodayForkEvent(date) shouldBe true
                event.isTodayForkEvent(
                    date.plusHours(23).plusMinutes(59).plusSeconds(59),
                ) shouldBe true
            }
        }

        context("오늘 생성하지 않은 ForkEvent일 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                val event = GitHubEvent(type = GitHubEventType.ForkEvent, createdAt = date)

                event.isTodayForkEvent(date.minusNanos(1)) shouldBe false
                event.isTodayForkEvent(date.plusDays(1)) shouldBe false
            }
        }

        context("오늘 생성했으나 ForkEvent가 아닐 경우") {

            it("false를 리턴한다") {
                val date = LocalDateTime.of(2024, 5, 26, 0, 0, 0)

                GitHubEventType.entries.filterNot { it == GitHubEventType.ForkEvent }.forEach { type ->
                    println(type)
                    val event = GitHubEvent(type = type, createdAt = date)
                    event.isTodayForkEvent(date) shouldBe false
                }
            }
        }
    }
})
