package com.az.elib.util

import android.util.Patterns
import android.widget.EditText

class InputChecker{

    fun extractEmail(email: String?): String {
        if (email == null) throw Exception("Please enter a valid email address")
        val pattern = Patterns.EMAIL_ADDRESS
        return if (pattern.matcher(email)
                .matches()
            && email.endsWith("@estin.dz")
        ) email else throw Exception("Please enter a valid email address")
    }

    fun extractPsw(psw: String?): String {
        if(psw == null) throw Exception("Please enter a valid password")
        return if (psw.length >= 6) psw else throw Exception("Password has to be at least 6 characters")
    }

    fun extractText(etText: EditText, textType:String): String {
        val text = etText.text.toString().trim()
        return text.ifBlank { throw Exception("Please enter a valid $textType") }
    }

}

