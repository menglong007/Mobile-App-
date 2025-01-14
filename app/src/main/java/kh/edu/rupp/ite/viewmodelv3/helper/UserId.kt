package kh.edu.rupp.ite.viewmodelv3.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import kh.edu.rupp.ite.viewmodelv2.model.ProfileModel

object SharedPreferencesHelper {
    private const val PREF_NAME = "app_preferences"
    private const val PROFILE_KEY = "profile_data"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveProfile(context: Context, profile: ProfileModel) {
        val prefs = getSharedPreferences(context)
        val json = Gson().toJson(profile)  // Convert ProfileModel object to JSON string
        prefs.edit().putString(PROFILE_KEY, json).apply()
    }

    // Retrieve ProfileModel object from JSON
    fun getProfile(context: Context): ProfileModel? {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(PROFILE_KEY, null)
        return if (json != null) {
            Gson().fromJson(json, ProfileModel::class.java)  // Convert JSON string back to ProfileModel object
        } else {
            null
        }
    }

    // Clear stored profile data
    fun clearProfile(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().remove(PROFILE_KEY).apply()
    }
}
