/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example

import org.example.dto.HandlerOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class AppTest {
    val app = App()

    @Test
    fun appHasHelloFunction() {
        assertEquals("Hello", app.hello())
    }

    @Test
    fun `handleRequest 메서드는 어떠한 값을 넣어도 hello 메시지가 리턴된다`() {
        assertEquals(
            HandlerOutput("hello"), app.handleRequest(
                null, null
            )
        )
    }
}
