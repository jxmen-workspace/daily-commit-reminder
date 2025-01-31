package org.example.support.logger

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger

abstract class AbstractLambdaLoggerContext : Context {
    override fun getAwsRequestId(): String = throw NotImplementedError()

    override fun getLogGroupName(): String = throw NotImplementedError()

    override fun getLogStreamName(): String = throw NotImplementedError()

    override fun getFunctionName(): String = throw NotImplementedError()

    override fun getFunctionVersion(): String = throw NotImplementedError()

    override fun getInvokedFunctionArn(): String = throw NotImplementedError()

    override fun getIdentity(): CognitoIdentity = throw NotImplementedError()

    override fun getClientContext(): ClientContext = throw NotImplementedError()

    override fun getRemainingTimeInMillis(): Int = throw NotImplementedError()

    override fun getMemoryLimitInMB(): Int = throw NotImplementedError()

    abstract override fun getLogger(): LambdaLogger
}

/**
 * Aws Lambda Context의 Logger를 ConsoleLogger로 구현한 클래스
 */
class ConsoleLoggerLambdaContext : AbstractLambdaLoggerContext() {
    companion object {
        val consoleLogger = ConsoleLogger()
    }

    override fun getLogger(): LambdaLogger =
        object : LambdaLogger {
            override fun log(message: String) {
                consoleLogger.log(message)
            }

            override fun log(message: ByteArray) {
                consoleLogger.log(message)
            }
        }
}
