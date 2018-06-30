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

inline fun <reified T> LiveData<T>.observeOnceBlocked(block: (T?) -> Unit) {
    val data = arrayOfNulls<T>(1)
    val latch = CountDownLatch(1)

    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            data[0] = t
            latch.countDown()
            removeObserver(this)
        }
    })

    latch.await(1, TimeUnit.SECONDS)
    block(data[0])
}

fun <T> LiveData<List<T>>.findItem(predicate: (T) -> Boolean): T {
    val currentValue = value
    return if (currentValue != null) {
        currentValue.first { predicate(it) }
    } else {
        var result: T? = null
        observeOnceBlocked { items ->
            result = items?.first { predicate(it) }
        }
        return result!!
    }
}
