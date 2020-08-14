/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 14/08/20 17:30
 *
 */

package uk.mrs.saralarm.support;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

public class UpdateWorker extends Worker {
    public UpdateWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        AppUpdater appUpdater = new AppUpdater(getApplicationContext())
                .setUpdateFrom(UpdateFrom.XML)
                .setDisplay(Display.NOTIFICATION)
                .setUpdateXML("https://raw.githubusercontent.com/tylers24877/MRT-SAR-Alarm/master/update.xml")
                .setCancelable(false);
        appUpdater.start();
        return Result.success();
    }
}