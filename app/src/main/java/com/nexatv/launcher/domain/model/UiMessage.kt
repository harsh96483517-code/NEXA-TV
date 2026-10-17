package com.nexatv.launcher.domain.model

/** A short, transient message shown in the toast host (launch failures, confirmations...). */
data class UiMessage(
    val text: String,
    val isError: Boolean = false,
)
