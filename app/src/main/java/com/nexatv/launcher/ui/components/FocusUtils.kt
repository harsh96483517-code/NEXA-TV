package com.nexatv.launcher.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.focus.FocusRequester

private const val MAX_ATTEMPTS = 8

/**
 * Gives [requester] focus when this composable enters the composition.
 *
 * A single requestFocus() call is not enough on TV screens: the target is often the first item of
 * a lazy row or grid, which is only composed during the first layout pass, after effects have
 * started. Requesting focus before the target exists throws, so we retry once per frame for a short
 * while. Without this the D-pad would have nothing focused when a screen opens.
 *
 * Change [key] to request focus again (for example when a state-dependent button appears).
 */
@Composable
fun RequestFocusOnEnter(requester: FocusRequester, key: Any? = Unit) {
    LaunchedEffect(requester, key) {
        repeat(MAX_ATTEMPTS) {
            withFrameNanos { }
            val gotFocus = try {
                requester.requestFocus()
                true
            } catch (e: IllegalStateException) {
                false
            }
            if (gotFocus) return@LaunchedEffect
        }
    }
}
