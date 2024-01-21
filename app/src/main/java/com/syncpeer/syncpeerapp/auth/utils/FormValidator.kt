package com.syncpeer.syncpeerapp.auth.utils

import java.util.regex.Matcher
import java.util.regex.Pattern


object FormValidator {
    private val emailRegexPattern: Pattern =
        Pattern.compile(Constants.EMAIL_REGEX, Pattern.CASE_INSENSITIVE)

    fun checkPassword(password: String, confirmPassword: String): Boolean {
        if (password.isEmpty() || confirmPassword.isEmpty() || password == "null" || confirmPassword == "null" || password != confirmPassword)
            return false
        return true
    }


    fun checkEmail(emailStr: String?): Boolean {
        val matcher: Matcher = emailRegexPattern.matcher(emailStr)
        return matcher.matches()
    }

}