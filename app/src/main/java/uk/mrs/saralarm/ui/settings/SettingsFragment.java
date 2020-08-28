/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 28/08/20 19:41
 *
 */

package uk.mrs.saralarm.ui.settings;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;

import uk.mrs.saralarm.R;
import uk.mrs.saralarm.Widget;

public class SettingsFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        getParentFragmentManager().beginTransaction().replace(R.id.settings_content_frame, new PrefsFragment()).commit();
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    /**
     * This class is used to update the widget if the settings are changed within the app.
     */
    public static class PrefsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.preference, rootKey);
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            findPreference("prefEnabled").setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) this);

        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sP, String key) {
            if (key.equals("prefEnabled")) {

                if (getContext() != null) {
                    Intent intent = new Intent(getContext(), Widget.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    int[] ids = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(requireContext(), Widget.class));
                    if (ids != null && ids.length > 0) {
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                        requireContext().sendBroadcast(intent);
                    }
                }
            }

            if (sP.getBoolean("prefEnabled", false))
                if (!sP.getBoolean("prefUsePhoneNumber", false)
                        && sP.getString("prefUseCustomTrigger", "").isEmpty()
                        && sP.getStringSet("triggerResponses", Collections.<String>emptySet()).isEmpty()) {
                    ((SwitchPreferenceCompat) findPreference("prefEnabled")).setChecked(false);
                    Snackbar mySnackbar = Snackbar.make(requireView(), "SARCALL Alarm disabled! Please choose an activation method, then re-enable.", BaseTransientBottomBar.LENGTH_LONG);
                    mySnackbar.show();
                }

            switch (key) {
                case "prefUsePhoneNumber":
                    findPreference("triggerResponses").setEnabled(!sP.getBoolean("prefUsePhoneNumber", false));
                    findPreference("prefUseCustomTrigger").setEnabled(!sP.getBoolean("prefUsePhoneNumber", false));
                    break;
                case "prefUseCustomTrigger":
                    findPreference("prefUsePhoneNumber").setEnabled(sP.getString("prefUseCustomTrigger", "").isEmpty());
                    findPreference("triggerResponses").setEnabled(sP.getString("prefUseCustomTrigger", "").isEmpty());
                    break;
                case "triggerResponses":
                    findPreference("prefUsePhoneNumber").setEnabled(sP.getStringSet("triggerResponses", Collections.<String>emptySet()).isEmpty());
                    findPreference("prefUseCustomTrigger").setEnabled(sP.getStringSet("triggerResponses", Collections.<String>emptySet()).isEmpty());
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            SharedPreferences sP = PreferenceManager.getDefaultSharedPreferences(requireContext());

            ((SwitchPreferenceCompat) findPreference("prefEnabled")).setChecked(sP.getBoolean("prefEnabled", false));

            findPreference("prefUsePhoneNumber").setEnabled((
                    sP.getStringSet("triggerResponses", Collections.<String>emptySet()).isEmpty()
                            && sP.getString("prefUseCustomTrigger", "").isEmpty()));

            findPreference("triggerResponses").setEnabled((
                    sP.getString("prefUseCustomTrigger", "").isEmpty()
                            && !sP.getBoolean("prefUsePhoneNumber", false)));

            findPreference("prefUseCustomTrigger").setEnabled((
                    !sP.getBoolean("prefUsePhoneNumber", false)
                            && sP.getStringSet("triggerResponses", Collections.<String>emptySet()).isEmpty()));


        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {


            if (preference.getKey().equals("prefEnabled") && (boolean) newValue) {
                SharedPreferences sP = PreferenceManager.getDefaultSharedPreferences(requireContext());
                if (!sP.getBoolean("prefUsePhoneNumber", false)
                        && sP.getString("prefUseCustomTrigger", "").isEmpty()
                        && sP.getStringSet("triggerResponses", Collections.<String>emptySet()).isEmpty()) {
                    Snackbar mySnackbar = Snackbar.make(requireView(), "Error. Please choose an activation method first.", BaseTransientBottomBar.LENGTH_LONG);
                    mySnackbar.show();
                    return false;
                }
            }
            return true;
        }
    }
}