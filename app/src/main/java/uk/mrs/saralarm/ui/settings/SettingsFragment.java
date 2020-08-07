/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 07/08/20 16:09
 *
 */

package uk.mrs.saralarm.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import uk.mrs.saralarm.MainActivity;
import uk.mrs.saralarm.R;

public class SettingsFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        getChildFragmentManager().beginTransaction().replace(R.id.settings_content_frame, new MainActivity.PrefsFragment()).commit();

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


}