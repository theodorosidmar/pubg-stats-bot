package com.github.theodorosidmar.pubgstats.bot.commons

fun String.titlecase(): String =
    this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase()
        else it.toString()
    }
