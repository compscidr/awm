package com.github.compscidr.awm.id

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.util.UUID

/**
 * Singleton used solely for obtaining the UUID for this device on Android (it stores it in the
 * SharedPreferences for persistence).
 *
 * If we want this to become platform independent, we can abstract this to an interface and then
 * implement getUUID functions for each platform, whereby the storage method is platform specific..
 */
object ID {
    private const val UUID_KEY = "UUID"

    /**
     * Get the UUID for this device. If it doesn't exist, create it and save it to shared preferences.
     *
     * Sets the system property UUID_KEY to the uuid (important to do this before the logger has
     * been initialized in order to get the uuid in the log output).
     *
     * isTest should only be used for testing, it will not use the shared preferences for things
     * like compose previews
     */
    fun getUUID(context: Context, isTest: Boolean = false): UUID {
        if (isTest) {
            return UUID.randomUUID()
        }
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val uuidString = sharedPreferences.getString(UUID_KEY, null)
        val uuid = if (uuidString == null) {
            val newUUID = UUID.randomUUID()
            sharedPreferences.edit().putString(UUID_KEY, newUUID.toString()).apply()
            newUUID
        } else {
            UUID.fromString(uuidString)
        }
        System.setProperty(UUID_KEY, uuid.toString())
        return uuid
    }
}