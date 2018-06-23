package com.kaizendeveloper.bitcoinsandbox.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(block: (T?) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            block(t)
            removeObserver(this)
        }
    })
}
