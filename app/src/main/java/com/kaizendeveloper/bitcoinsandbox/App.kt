package com.kaizendeveloper.bitcoinsandbox

import android.app.Application
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Security.addProvider(BouncyCastleProvider())
    }
}