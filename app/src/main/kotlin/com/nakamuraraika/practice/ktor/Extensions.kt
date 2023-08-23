package com.nakamuraraika.practice.ktor

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.title
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

fun <I, O> Route.get(
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    get {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.get(
    path: String,
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    get(path) {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.post(
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    post() {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.post(
    path: String,
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    post(path) {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.put(
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    put {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.put(
    path: String,
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    put(path) {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.delete(
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    get {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.delete(
    path: String,
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    get(path) {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.head(
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    head {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.head(
    path: String,
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    head(path) {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.patch(
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    patch {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}

fun <I, O> Route.patch(
    path: String,
    presenterBuilder: (ApplicationCall, CoroutineContext) -> O,
    interactorBuilder: (output: O, CoroutineContext) -> I,
    controllerBuilder: suspend ApplicationCall.(I) -> Unit
) {
    patch(path) {
        call.presenter(presenterBuilder)
            .interactor(interactorBuilder)
            .controller(controllerBuilder)
    }
}



suspend fun <O> ApplicationCall.presenter(builder: (ApplicationCall, CoroutineContext) -> O): InteractorBuilder<O> {
    val deferred = CompletableDeferred<Unit>()
    return InteractorBuilder(this, deferred, builder(this, coroutineContext), coroutineContext)
}

class InteractorBuilder<O>(private val call: ApplicationCall, val deferred: CompletableDeferred<Unit>, private val outputPort: O, private val coroutineContext: CoroutineContext) {
    fun <I> interactor(builder: (O, CoroutineContext) -> I): ControllerBuilder<I> {
        val interactor = builder(outputPort, coroutineContext)
        return ControllerBuilder(call, interactor, deferred)
    }
}

class ControllerBuilder<I>(private val call: ApplicationCall, val inputPort: I, private val deferred: CompletableDeferred<Unit>) {
    suspend fun controller(handler: suspend ApplicationCall.(inputPort: I) -> Unit): Unit {
        call.handler(inputPort)
        return deferred.await()
    }
}
