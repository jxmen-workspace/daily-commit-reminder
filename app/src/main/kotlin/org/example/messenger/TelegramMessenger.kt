package org.example.messenger

import org.example.support.logger.ConsoleLogger
import org.example.support.logger.Logger
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramMessenger(
    botToken: String,
    private val botUsername: String,
    private val chatId: String,
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
        execute(SendMessage(chatId, text))
        logger.log("telegram message send completed.")
    }
}
