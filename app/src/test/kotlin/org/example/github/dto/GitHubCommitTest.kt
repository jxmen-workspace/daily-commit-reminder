package org.example.github.dto

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class GitHubCommitTest : DescribeSpec({

    describe("GitHubCommit") {
        val date = LocalDateTime.of(2024, 5, 25, 0, 0, 0)

        context("동일한 날짜일 경우") {
            it("true를 반환한다") {
                val commit = GitHubCommit(sha = "sha", commit = Commit(author = Author(date = date)))

                commit.isToday(date = date) shouldBe true
                commit.isToday(date = date.plusHours(23).plusMinutes(59)) shouldBe true
            }
        }

        context("다른 날짜일 경우") {
            it("false를 반환한다") {
                val commit = GitHubCommit(sha = "sha", commit = Commit(author = Author(date = date)))

                commit.isToday(date = date.plusDays(1)) shouldBe false
            }
        }
    }
})
