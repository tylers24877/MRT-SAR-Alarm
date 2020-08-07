/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 07/08/20 16:09
 *
 */

package uk.mrs.saralarm.ui.respond;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.Locale;

import uk.mrs.saralarm.R;

public class RespondFragment extends Fragment {

    RespondViewModel respondViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        respondViewModel = ViewModelProviders.of(this).get(RespondViewModel.class);
        View root = inflater.inflate(R.layout.fragment_respond, container, false);
        final Context context = root.getContext();

        Button clickButton = root.findViewById(R.id.respond_sar_a_button);
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogOpen(context);
            }
        });

        return root;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void dialogOpen(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_respond_sar_a);
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.show();

        ConstraintLayout constraintLayout = dialog.findViewById(R.id.respond_dialog_sar_a_constraint_layout);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    EditText editText = dialog.findViewById(R.id.respond_dialog_sar_a_message_editview);

                    if (editText.isFocused()) {
                        Rect outRect = new Rect();
                        editText.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            editText.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });

        //seekbar
        SeekBar seekBar = dialog.findViewById(R.id.respond_dialog_sar_a_seek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                EditText editText = dialog.findViewById(R.id.respond_dialog_sar_a_message_editview);
                if (editText.isFocused()) editText.clearFocus();
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= 0 && progress <= seekBar.getMax()) {

                    int progressCal = progress * 5;
                    respondViewModel.setEta(progressCal);

                    TextView etaTxtView = dialog.findViewById(R.id.respond_dialog_sar_a_seek_eta_txtview);
                    etaTxtView.setText(String.format(Locale.ENGLISH, "Estimated time to RV: %d minutes", respondViewModel.getEta().getValue())); // the TextView Reference
                    seekBar.setSecondaryProgress(progress);

                }
            }
        });
        seekBar.setProgress(6);
    }
}