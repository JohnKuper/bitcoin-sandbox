package com.kaizendeveloper.bitcoinsandbox.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val defaultFormat = DecimalFormat("0.00", DecimalFormatSymbols(Locale.UK))

fun formatAmount(amount: Double): String = "${defaultFormat.format(amount)} BTC"