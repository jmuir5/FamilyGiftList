package com.noxapps.familygiftlist.login

fun String.isValidPassword(): Boolean {
    if (length < 8) return false
    if (filter { it.isDigit() }.firstOrNull() == null) return false
    if (filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
    if (filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
    if (filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

    return true
}