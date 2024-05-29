package com.example.radiologist.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBas {
    private val _events = MutableSharedFlow<SharedInterface>()
    val events = _events.asSharedFlow()

    suspend fun postEvent(event: SharedInterface) {
        _events.emit(event)
    }
}