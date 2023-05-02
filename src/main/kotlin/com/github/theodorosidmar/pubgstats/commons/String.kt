package com.github.theodorosidmar.pubgstats.commons

fun String.titlecase(): String =
    this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
