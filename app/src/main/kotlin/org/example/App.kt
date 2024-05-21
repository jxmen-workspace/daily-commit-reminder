/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.RequestHandler
import org.example.dto.HandlerInput
import org.example.dto.HandlerOutput
import org.example.messenger.Messenger
import org.example.messenger.TelegramMessenger
import org.example.support.logger.ConsoleLogger
import org.example.support.logger.LambdaLoggerAdapter

class App(
    private val messenger: Messenger =
        TelegramMessenger(
            botToken = System.getenv("TELEGRAM_BOT_TOKEN"),
            botUsername = System.getenv("TELEGRAM_BOT_USERNAME"),
            chatId = System.getenv("TELEGRAM_CHAT_ID"),
        ),
) : RequestHandler<HandlerInput, HandlerOutput> {
    override fun handleRequest(
        input: HandlerInput,
        context: Context,
    ): HandlerOutput {
        val logger = context.logger
        input.message.let { logger.log("received input message: '$it'") }

        logger.log("start sending message")
        messenger.sendMessage(text = "hello", logger = LambdaLoggerAdapter(logger))
        logger.log("sending message completed")

        return HandlerOutput(message = "hello")
    }
}

fun main() {
    val app = App()

    app.handleRequest(
        input = HandlerInput("hi"),
        context = createConsoleLoggerContext(),
    )
}

private fun createConsoleLoggerContext() =
    object : Context {
        override fun getAwsRequestId(): String {
            throw NotImplementedError()
        }

        override fun getLogGroupName(): String {
            throw NotImplementedError()
        }

        override fun getLogStreamName(): String {
            throw NotImplementedError()
        }

        override fun getFunctionName(): String {
            throw NotImplementedError()
        }

        override fun getFunctionVersion(): String {
            throw NotImplementedError()
        }

        override fun getInvokedFunctionArn(): String {
            throw NotImplementedError()
        }

        override fun getIdentity(): CognitoIdentity {
            throw NotImplementedError()
        }

        override fun getClientContext(): ClientContext {
            throw NotImplementedError()
        }

        override fun getRemainingTimeInMillis(): Int {
            throw NotImplementedError()
        }

        override fun getMemoryLimitInMB(): Int {
            throw NotImplementedError()
        }

        override fun getLogger(): LambdaLogger {
            val logger = ConsoleLogger()

            return object : LambdaLogger {
                override fun log(message: String) {
                    logger.log(message)
                }

                override fun log(message: ByteArray) {
                    logger.log(message)
                }
            }
        }
    }
