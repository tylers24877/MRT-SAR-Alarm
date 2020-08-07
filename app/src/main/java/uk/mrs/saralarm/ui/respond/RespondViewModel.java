/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 07/08/20 16:09
 *
 */

package uk.mrs.saralarm.ui.respond;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RespondViewModel extends ViewModel {

    private MutableLiveData<Integer> mEta;

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