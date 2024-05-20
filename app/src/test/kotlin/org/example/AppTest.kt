/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import org.example.dto.HandlerInput
import org.example.dto.HandlerOutput
import org.example.messenger.Messenger
import org.example.support.logger.Logger
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class AppTest {
    private lateinit var app: App

    @BeforeEach
    fun setUp() {
        val dummyMessenger =
            object : Messenger {
                override fun sendMessage(
                    text: String,
                    logger: Logger,
                ) {
                    logger.log(text)
                }
            }

        app =
            App(
                messenger = dummyMessenger,
            )
    }

    @Test
    fun `handleRequest 메서드는 어떠한 값을 넣어도 hello 메시지가 리턴된다`() {
        val consoleLoggerContext = createConsoleLoggerContext()

        val actual =
            app.handleRequest(
                input = HandlerInput("hi"),
                context = consoleLoggerContext,
            )

        assertEquals(HandlerOutput("hello"), actual)
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
                return object : LambdaLogger {
                    override fun log(message: String?) {
                        println(message)
                    }

                    override fun log(message: ByteArray?) {
                        println(message)
                    }
                }
            }
        }
}
