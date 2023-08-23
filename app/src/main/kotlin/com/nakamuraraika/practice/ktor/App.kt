/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.nakamuraraika.practice.ktor

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlin.coroutines.CoroutineContext

fun main() {
    embeddedServer(Netty, port = 8080) {
        main()
    }.start(wait = true)
}

fun Application.main() {
    routing {
        get {
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title { +"Clean Ktor!" }

                }
                body {
                    a {
                        href = "/greet"
                        +"greet"
                    }
                }
            }
        }

        get<HogeInputPort, HogeOutputPort>(
            "/greet",
            presenterBuilder = { call, coroutineContext -> HogePresenter(call, coroutineContext) },
            interactorBuilder = { output, coroutineContext -> HogeInteractor(output, coroutineContext) },
        ) {
            val name = request.queryParameters["name"] ?: "world"
            it.execute(name)
        }
    }
}


fun interface HogeInputPort {
    fun execute(input: String)
}

class HogeInteractor(private val outputPort: HogeOutputPort, coroutineContext: CoroutineContext) : HogeInputPort, CoroutineScope by CoroutineScope(coroutineContext) {
    override fun execute(input: String) {
        launch {
            delay(1000)
            outputPort.handle("Hello, $input!")
        }
    }
}

fun interface HogeOutputPort {
    fun handle(output: String)
}

class HogePresenter(private val call: ApplicationCall, coroutineContext: CoroutineContext) : HogeOutputPort, CoroutineScope by CoroutineScope(coroutineContext) {
    override fun handle(output: String) {
        launch {
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title { +"greet" }
                }
                body {
                    form {
                        method = FormMethod.get
                        action = "/greet"
                        input {
                            type = InputType.text
                            name = "name"
                        }
                        input {
                            type = InputType.submit
                            value = "挨拶"
                        }
                    }
                    p { +output }
                }
            }
        }
    }
}