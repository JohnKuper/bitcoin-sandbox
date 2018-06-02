package com.kaizendeveloper.bitcoinsandbox.db

import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.LiveDataTestUtil.getValue
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest : DbTest() {

    @Test
    fun insertAndUpdate() {
        val satoshi = User("Satoshi", "satoshi_address", true)
        val alice = User("Alice", "alice_address", false)

        with(db.userDao()) {
            insert(satoshi)
            insert(alice)
        }

        val current = getValue(db.userDao().getCurrent())
        assertEquals(satoshi, current)

        satoshi.isCurrent = false
        alice.isCurrent = true
        with(db.userDao()) {
            update(satoshi)
            update(alice)
        }

        val updatedCurrent = getValue(db.userDao().getCurrent())
        assertEquals(alice, updatedCurrent)
    }
}
