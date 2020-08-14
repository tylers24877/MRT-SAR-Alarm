/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 14/08/20 17:30
 *
 */

package uk.mrs.saralarm.ui.respond;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RespondViewModel extends ViewModel {

    private final MutableLiveData<Integer> mEta;

    public RespondViewModel() {
        mEta = new MutableLiveData<>();
    }

    public LiveData<Integer> getEta() {
        return mEta;
    }

    public void setEta(int eta) {
        mEta.setValue(eta);
    }
}