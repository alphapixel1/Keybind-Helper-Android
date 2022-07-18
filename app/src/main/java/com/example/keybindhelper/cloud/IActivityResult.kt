package com.example.keybindhelper.cloud

import android.content.Intent

/**
 * Interface for passing Activity Result from mainactivity to CatalogFragment/SettingsFragment
 */
interface IActivityResult {
    fun onResult(requestCode: Int, resultCode: Int, data: Intent?)
}