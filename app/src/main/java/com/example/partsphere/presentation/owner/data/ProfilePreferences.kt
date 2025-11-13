package com.example.partsphere.presentation.owner.data


import android.content.Context
import android.net.Uri
import android.content.SharedPreferences

class ProfilePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

    var name: String?
        get() = prefs.getString("name", null)
        set(value) = prefs.edit().putString("name", value).apply()

    var email: String?
        get() = prefs.getString("email", null)
        set(value) = prefs.edit().putString("email", value).apply()

    var designation: String?
        get() = prefs.getString("designation", null)
        set(value) = prefs.edit().putString("designation", value).apply()

    var company: String?
        get() = prefs.getString("company", null)
        set(value) = prefs.edit().putString("company", value).apply()

    var profileUri: String?
        get() = prefs.getString("profileUri", null)
        set(value) = prefs.edit().putString("profileUri", value).apply()
}

