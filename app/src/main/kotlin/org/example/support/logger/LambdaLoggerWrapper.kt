package org.example.support.logger

import com.amazonaws.services.lambda.runtime.LambdaLogger

/**
 * LambdaLogger를 Logger 인터페이스로 감싸는 클래스
 */
class LambdaLoggerWrapper(private val logger: LambdaLogger) : Logger {
    override fun log(message: String) {
        logger.log(message)
    }
}
