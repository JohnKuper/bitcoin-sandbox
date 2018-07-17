package com.kaizendeveloper.bitcoinsandbox.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.observeOnce(block: (T?) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            block(t)
            removeObserver(this)
        }
    })
}

/**
 * Should be used only on a worker thread, since [Observer.onChanged] will be invoked on the main thread.
 */
fun <T> LiveData<T>.requireValue(): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)

    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            data[0] = t
            latch.countDown()
            removeObserver(this)
        }
    })

    latch.await(2, TimeUnit.SECONDS)
    @Suppress("UNCHECKED_CAST")
    return data[0] as T
}