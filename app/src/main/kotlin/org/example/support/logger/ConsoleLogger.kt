package org.example.support.logger

class ConsoleLogger : Logger {
    override fun log(message: String) {
        println(message)
    }

    fun log(message: ByteArray) {
        println(message)
    }
}
