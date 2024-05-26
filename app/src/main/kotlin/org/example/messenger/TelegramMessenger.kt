package org.example.messenger

import org.example.github.dto.TodayGitHubContributes
import org.example.support.logger.ConsoleLogger
import org.example.support.logger.Logger
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDateTime

class TelegramMessenger(
    botToken: String,
    private val botUsername: String,
    private val chatId: String, // 메시지를 보낼 채팅방 ID
    private val logger: Logger = ConsoleLogger(),
) : TelegramLongPollingBot(botToken), Messenger {
    override fun getBotUsername(): String {
        return botUsername
    }

    override fun onUpdateReceived(update: Update) {
        val message = update.message
        val chatId = message.chatId

        this.logger.log("Received message from $chatId: ${message.text}")
    }

    override fun sendMessage(
        text: String,
        logger: Logger,
    ) {
        logger.log("send telegram message to: '$chatId', message: '$text'")
        sendMessageTo(chatId, text)
        logger.log("telegram message send completed.")
    }

    override fun sendGitHubContributesMessage(
        contributes: TodayGitHubContributes,
        logger: Logger,
    ) {
        val now = LocalDateTime.now()

        val message =
            contributes.let {
                if (it.total == 0) {
                    """
                    |=======================
                    |❌ Today's GitHub Contributes
                    |Date: ${now.year}-${String.format("%02d", now.monthValue)}-${now.dayOfMonth}
                    |Name: ${it.username}
                    |Link: https://github.com/${it.username}
                    |=======================
                    |No Contributes Today.
                    """.trimMargin()
                } else {
                    """
                    |=======================
                    |✅ Today's GitHub Contributes
                    |Date: ${now.year}-${String.format("%02d", now.monthValue)}-${now.dayOfMonth}
                    |Name: ${it.username}
                    |Link: https://github.com/${it.username}
                    |=======================
                    |Commits: ${it.commit}
                    |Open Issues: ${it.openIssues}
                    |Open Pull Requests: ${it.openPullRequests}
                    |
                    |Total: ${it.total}
                    """.trimMargin()
                }
            }

        logger.log("send telegram github contributes message to: '$chatId'")
        sendMessageTo(chatId, message)
        logger.log("telegram github contributes message send completed to: '$chatId'")
    }

    private fun sendMessageTo(
        chatId: String,
        text: String,
    ) {
        execute(SendMessage(chatId, text))
    }
}
