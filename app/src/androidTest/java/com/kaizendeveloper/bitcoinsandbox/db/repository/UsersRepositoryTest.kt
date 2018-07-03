package com.kaizendeveloper.bitcoinsandbox.db.repository

import android.support.test.runner.AndroidJUnit4
import com.kaizendeveloper.bitcoinsandbox.db.DbTest
import com.kaizendeveloper.bitcoinsandbox.db.dao.UserDao
import com.kaizendeveloper.bitcoinsandbox.db.entity.User
import com.kaizendeveloper.bitcoinsandbox.util.requireValue
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class UsersRepositoryTest : DbTest() {

    @Inject
    lateinit var usersRepo: UsersRepository
    @Inject
    lateinit var userDao: UserDao

    @Before
    fun setup() {
        appComponent.inject(this)
    }

    @Test
    fun testCreateUserIfAbsent() {
        val satoshi = createUserIfAbsent(SATOSHI_NAME, true)
        val dbSatoshi = getByName(SATOSHI_NAME)

        assertNotNull(satoshi)
        assertEquals(satoshi, dbSatoshi)

        createUserIfAbsent(SATOSHI_NAME, true)
        var users = userDao.getAll().requireValue()

        assertEquals(1, users.size)

        createUserIfAbsent(ALICE_NAME, false)
        users = userDao.getAll().requireValue()

        assertEquals(2, users.size)
    }

    @Test
    fun testUpdateCurrent() {
        val satoshi = createUserIfAbsent(SATOSHI_NAME, true)
        val alice = createUserIfAbsent(ALICE_NAME, false)

        usersRepo.updateCurrent(satoshi, alice).blockingAwait()

        satoshi.isCurrent = false
        alice.isCurrent = true

        assertEquals(satoshi, getByName(SATOSHI_NAME))
        assertEquals(alice, getByName(ALICE_NAME))
    }


    private fun createUserIfAbsent(name: String, isCurrent: Boolean): User {
        return usersRepo.createUserIfAbsent(name, isCurrent).blockingGet()
    }

    private fun getByName(name: String): User {
        return userDao.getByName(name).blockingGet()
    }

    companion object {
        const val SATOSHI_NAME = "Satoshi"
        const val ALICE_NAME = "Alice"
    }
}