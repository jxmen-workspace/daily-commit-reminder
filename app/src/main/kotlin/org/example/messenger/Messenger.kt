package org.example.messenger

import org.example.support.logger.Logger

interface Messenger {
    fun sendMessage(
        text: String,
        logger: Logger,
    )
}
