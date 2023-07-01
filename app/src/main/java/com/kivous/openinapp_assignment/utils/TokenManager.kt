package com.kivous.openinapp_assignment.utils

import android.content.Context
import com.kivous.openinapp_assignment.utils.Constants.PREF_KEY
import com.kivous.openinapp_assignment.utils.Constants.TOKEN_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private var pref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val editor = pref.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun getToken(): String? {
        return pref.getString(TOKEN_KEY, null)
    }
}