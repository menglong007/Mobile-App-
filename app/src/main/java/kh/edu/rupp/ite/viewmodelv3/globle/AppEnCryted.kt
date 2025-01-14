package kh.edu.rupp.ite.viewmodelv3.globle

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class AppEncrypted private constructor() {

    fun storeToken(context: Context, token: String) {
        getPref(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        return getPref(context).getString(KEY_TOKEN, null)
    }

    private fun getPref(context: Context): SharedPreferences {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val PREF_NAME = "PREF_NAME"
        private const val KEY_TOKEN = "TOKEN"
        private var instance: AppEncrypted? = null

        fun get(): AppEncrypted {
            if (instance == null) {
                instance = AppEncrypted()
            }
            return instance!!
        }
    }
}
