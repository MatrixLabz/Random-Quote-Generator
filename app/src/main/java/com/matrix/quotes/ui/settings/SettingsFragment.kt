package com.matrix.quotes.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.matrix.quotes.R
import com.matrix.quotes.data.AppConstants
import com.matrix.quotes.data.room.FavoriteQuoteDB
import com.matrix.quotes.data.room.FavoriteQuoteDao
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {


    private lateinit var favoriteQuotesDao: FavoriteQuoteDao

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setupRoom()
        setupData()
        setupListeners()
    }

    private fun setupRoom() {
        favoriteQuotesDao = FavoriteQuoteDB.getDatabase(requireContext()).favoriteQuoteDao()
    }

    private fun setupData() {
        val aboutInfo = findPreference<Preference>("aboutInfo")
        val shareApp = findPreference<Preference>("shareApp")
        aboutInfo?.summary = "1.0"
        aboutInfo?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("About")
                .setMessage("This is a demo app for Android Study Jams IIIT Ranchi")
                .setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton("Github") { _, _ ->
                    openGithubPage()
                }
                .show()
            true
        }
        shareApp?.setOnPreferenceClickListener {
            openShareChooser()
            true
        }
    }


    private fun setupListeners() {
        findPreference<SwitchPreferenceCompat>("darkTheme")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            true
        }
        findPreference<Preference>("deleteFavorites")?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remove all favorites?")
                .setMessage("This is an irreversible action")
                .setPositiveButton("Confirm") { dialog, _ ->
                    deleteAllQuotes()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            true
        }
    }

    private fun deleteAllQuotes() {
        lifecycleScope.launch {
            favoriteQuotesDao.deleteAllQuotes()
        }
    }

    private fun openGithubPage() {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(AppConstants.GITHUB_LINK)
        startActivity(intent)
    }

    private fun openShareChooser() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey Check out this Great app: ${AppConstants.GITHUB_LINK}"
        )
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Share To:"))
    }
}