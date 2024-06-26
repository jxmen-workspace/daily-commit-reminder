package org.example.messenger

import org.example.github.dto.TodayGitHubContributes
import org.example.support.logger.ConsoleLogger
import org.example.support.logger.Logger
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TelegramMessenger(
    botToken: String,
    private val botUsername: String,
    private val chatId: String, // 메시지를 보낼 채팅방 ID
    private val logger: Logger = ConsoleLogger(),
) : TelegramLongPollingBot(botToken),
    Messenger {
    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    override fun getBotUsername(): String = botUsername

    override fun onUpdateReceived(update: Update) {
        val message = update.message
        val chatId = message.chatId

        this.logger.log("Received message from $chatId: ${message.text}")
    }

    override fun sendMessage(
        text: String,
        logger: Logger,
    ) {
        sendMessageTo(chatId, text, logger)
    }

    override fun sendGitHubContributesMessage(
        contributes: TodayGitHubContributes,
        logger: Logger,
        now: LocalDateTime,
    ) {
        val message =
            contributes.let {
                if (it.total == 0) {
                    """
                    |=======================
                    |❌ Today's GitHub Contributes
                    |Date: ${now.format(dateFormatter)}
                    |Name: ${it.username}
                    |Link: https://github.com/${it.username}
                    |=======================
                    |No Contributes Today.
                    """.trimMargin()
                } else {
                    """
                    |=======================
                    |✅ Today's GitHub Contributes
                    |Date: ${now.format(dateFormatter)}
                    |Name: ${it.username}
                    |Link: https://github.com/${it.username}
                    |=======================
                    |Commits: ${it.commit}
                    |Open Issues: ${it.openIssues}
                    |Open Pull Requests: ${it.openPullRequests}
                    |Create Repository: ${it.createRepository}
                    |Fork Repository: ${it.fork}
                    |
                    |Total: ${it.total}
                    """.trimMargin()
                }
            }

        sendMessageTo(chatId = chatId, message = message, logger = logger)
    }

    override fun sendErrorMessage(
        error: Exception,
        logger: Logger,
    ) {
        sendMessageTo(
            chatId = chatId,
            message = "Failed to get today's GitHub Contributes.\nError Message: ${error.message}",
            logger = logger,
        )
        logger.log("Failed to get today's GitHub Contributes. Stack Trace: ${error.stackTraceToString()}")
    }

    private fun sendMessageTo(
        chatId: String,
        message: String,
        logger: Logger,
    ) {
        logger.log("send telegram message to: '$chatId'")
        execute(SendMessage(chatId, message))
        logger.log("telegram message send completed to: '$chatId'")
    }
}
