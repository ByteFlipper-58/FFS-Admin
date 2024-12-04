package com.byteflipper.ffsadmin.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferencesManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyGenParameterSpec(
            KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .build()
        )
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "github_config",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveGitHubConfig(token: String, owner: String, repo: String) {
        sharedPreferences.edit().apply {
            putString(KEY_GITHUB_TOKEN, token)
            putString(KEY_GITHUB_OWNER, owner)
            putString(KEY_GITHUB_REPO, repo)
        }.apply()
    }

    fun getGitHubToken(): String? = sharedPreferences.getString(KEY_GITHUB_TOKEN, null)
    fun getGitHubOwner(): String? = sharedPreferences.getString(KEY_GITHUB_OWNER, null)
    fun getGitHubRepo(): String? = sharedPreferences.getString(KEY_GITHUB_REPO, null)

    fun clearGitHubConfig() {
        sharedPreferences.edit().apply {
            remove(KEY_GITHUB_TOKEN)
            remove(KEY_GITHUB_OWNER)
            remove(KEY_GITHUB_REPO)
        }.apply()
    }

    companion object {
        private const val KEY_GITHUB_TOKEN = "github_token"
        private const val KEY_GITHUB_OWNER = "github_owner"
        private const val KEY_GITHUB_REPO = "github_repo"
    }
}