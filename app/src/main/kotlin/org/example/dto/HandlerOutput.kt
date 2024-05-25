package org.example.dto

data class HandlerOutput(
    val message: String,
    val todayCommitCount: Int?,
    val errorMessage: String?,
) {
    constructor(message: String, todayCommitCount: Int) : this(
        message = message,
        todayCommitCount = todayCommitCount,
        errorMessage = null,
    )

    constructor(message: String, errorMessage: String?) : this(
        message = message,
        todayCommitCount = null,
        errorMessage = errorMessage,
    )
}
