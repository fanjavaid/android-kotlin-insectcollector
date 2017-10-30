package androidkotlin.fanjavaid.com.insectcollector.settings

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import androidkotlin.fanjavaid.com.insectcollector.R

/**
 * Created by fanjavaid on 10/29/17.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting_preferences)
    }
}