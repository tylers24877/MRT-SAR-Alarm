/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 28/08/20 19:41
 *
 */

package uk.mrs.saralarm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import uk.mrs.saralarm.support.UpdateWorker;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(allOf = {"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE", "android.permission.WAKE_LOCK"})
    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (!pref.getBoolean("startedBefore", false)) {
            startActivity(new Intent(this, SetupActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        //get the toolbar from the view by id.
        Toolbar toolbar = findViewById(R.id.ResponseToolbar);
        //set the toolbar as actionbar. To support lollipop.
        setSupportActionBar(toolbar);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_respond, R.id.navigation_settings).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //request disable battery saver mode. Otherwise the alarm will not trigger when the device is sleeping
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        if (savedInstanceState == null)
            if (pref.getBoolean("betaChannel", false))
                new AppUpdater(this)
                        .setUpdateFrom(UpdateFrom.XML)
                        .setDisplay(Display.DIALOG)
                        .setUpdateXML("https://raw.githubusercontent.com/tylers24877/MRT-SAR-Alarm/master/update_beta.xml")
                        .setCancelable(false).start();
            else
                new AppUpdater(this)
                        .setUpdateFrom(UpdateFrom.XML)
                        .setDisplay(Display.DIALOG)
                        .setUpdateXML("https://raw.githubusercontent.com/tylers24877/MRT-SAR-Alarm/master/update.xml")
                        .setCancelable(false).start();

        if (savedInstanceState == null) {

            WorkManager.getInstance(this).cancelUniqueWork("SARCALL_CHECK_UPDATE");
            WorkManager.getInstance(this).cancelUniqueWork("SARCALL_CHECK_UPDATE_V1");

            PeriodicWorkRequest updateRequest =
                    new PeriodicWorkRequest.Builder(UpdateWorker.class, 12, TimeUnit.HOURS, 30, TimeUnit.MINUTES)
                            // Constraints
                            .addTag("SARCALL_CHECK_UPDATE_V2_TAG")
                            .build();

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "SARCALL_CHECK_UPDATE_V2",
                    ExistingPeriodicWorkPolicy.KEEP,
                    updateRequest);
        }

        if (pref.getBoolean("prefEnabled", false))
            if (!pref.getBoolean("prefUsePhoneNumber", false)
                    && pref.getString("prefUseCustomTrigger", "").isEmpty()
                    && pref.getStringSet("triggerResponses", Collections.<String>emptySet()).isEmpty()) {
                pref.edit().putBoolean("prefEnabled", false).apply();
            }
    }
    /**
     * Called when menu should be created.
     *
     * @return created or not.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();

        //get the menu items from XML
        inflater.inflate(R.menu.main_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when item is selected from Menu
     *
     * @param item item selected
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}