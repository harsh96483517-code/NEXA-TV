package com.nexatv.launcher.app

import com.nexatv.launcher.domain.model.UiMessage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Fire-and-forget channel for transient messages; the UI shows them in a glass toast. */
class MessageBus {
    private val mutableMessages = MutableSharedFlow<UiMessage>(
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val messages: SharedFlow<UiMessage> = mutableMessages.asSharedFlow()

    fun post(text: String, isError: Boolean = false) {
        mutableMessages.tryEmit(UiMessage(text, isError))
    }
}
