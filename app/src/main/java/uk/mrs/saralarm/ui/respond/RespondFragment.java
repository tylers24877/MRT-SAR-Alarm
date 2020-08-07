/*
 * *
 *  * Created by Tyler Simmonds.
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 07/08/20 20:25
 *
 */

package uk.mrs.saralarm.ui.respond;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

import uk.mrs.saralarm.R;
import uk.mrs.saralarm.support.SARResponseCode;

import static java.text.DateFormat.getDateTimeInstance;

public class RespondFragment extends Fragment {

    RespondViewModel respondViewModel;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        respondViewModel = ViewModelProviders.of(this).get(RespondViewModel.class);
        View root = inflater.inflate(R.layout.fragment_respond, container, false);
        final Context context = root.getContext();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            //if not granted, request permission.
            requestPermissions(new String[]{
                    Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        Button clickButtonSARA = root.findViewById(R.id.respond_sar_a_button);
        clickButtonSARA.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogSARAOpen(context);
            }
        });

        Button clickButtonSARL = root.findViewById(R.id.respond_sar_l_button);
        clickButtonSARL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogSARLOpen(context);
            }
        });

        Button clickButtonSARN = root.findViewById(R.id.respond_sar_n_button);
        clickButtonSARN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogSARNOpen();
            }
        });

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            setPreview(context, root);
        } else {
            TextView textView = root.findViewById(R.id.respond_sms_preview_txtview);
            TextView textViewDate = root.findViewById(R.id.respond_preview_date_txtview);
            textViewDate.setText("");
            textView.setText("Permission not granted!");
        }

        return root;
    }

    public void setPreview(Context context, View root) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> activationSet = pref.getStringSet("triggerResponses", null);

        ContentResolver cr = context.getContentResolver();
        Cursor c;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);

            int totalSMS = 0;
            if (c != null) {
                totalSMS = c.getCount();
                if (c.moveToFirst()) {
                    for (int j = 0; j < totalSMS; j++) {
                        String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                        String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                        Date date = new Date(Long.parseLong(smsDate));

                        if (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE))) == Telephony.Sms.MESSAGE_TYPE_INBOX) {
                            if (activationSet != null) {
                                if (checkStringSet(activationSet, body)) {
                                    TextView textView = root.findViewById(R.id.respond_sms_preview_txtview);
                                    TextView textViewDate = root.findViewById(R.id.respond_preview_date_txtview);
                                    textViewDate.setText(String.format("Received: %s", getDateTimeInstance().format(date)));
                                    textView.setText(body);
                                    c.close();
                                    return;
                                }
                            }
                        }
                        c.moveToNext();
                    }
                }
                c.close();
                TextView textView = root.findViewById(R.id.respond_sms_preview_txtview);
                textView.setText("No messages");
                TextView textViewDate = root.findViewById(R.id.respond_preview_date_txtview);
                textViewDate.setText("");
            }
        }
    }

    public boolean checkStringSet(Set<String> SS, String m) {
        for (String s : SS) {
            if (m.toLowerCase().replaceAll("\\s+", "").startsWith(s.replaceAll("\\s+", "").toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void dialogSARNOpen() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        sendSMSResponse(getContext(), dialog, SARResponseCode.SAR_N, 0, null);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you're unavailable?").setPositiveButton("I'm sure", dialogClickListener)
                .setNegativeButton("Take me back", dialogClickListener).show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void dialogSARAOpen(final Context context) {
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
        final SeekBar seekBar = dialog.findViewById(R.id.respond_dialog_sar_a_seek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                EditText editText = dialog.findViewById(R.id.respond_dialog_sar_a_message_editview);
                if (editText.isFocused()) editText.clearFocus();
                InputMethodManager imm = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), 0);
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

        Button buttonSAR_A = dialog.findViewById(R.id.respond_dialog_sar_a_submit_button);
        buttonSAR_A.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int progressCal = seekBar.getProgress() * 5;
                EditText editText = dialog.findViewById(R.id.respond_dialog_sar_a_message_editview);

                sendSMSResponse(context, dialog, SARResponseCode.SAR_A, progressCal, editText.getText().toString());
            }
        });


        seekBar.setProgress(6);
    }


    @SuppressLint("ClickableViewAccessibility")
    public void dialogSARLOpen(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_respond_sar_l);
        Window window = dialog.getWindow();
        assert window != null;
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialog.show();

        ConstraintLayout constraintLayout = dialog.findViewById(R.id.respond_dialog_sar_l_constraint_layout);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    EditText editText = dialog.findViewById(R.id.respond_dialog_sar_l_message_editview);
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
        final SeekBar seekBar = dialog.findViewById(R.id.respond_dialog_sar_l_seek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                EditText editText = dialog.findViewById(R.id.respond_dialog_sar_l_message_editview);
                if (editText.isFocused()) editText.clearFocus();
                InputMethodManager imm = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), 0);
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= 0 && progress <= seekBar.getMax()) {

                    int progressCal = (progress * 5) + 30;
                    respondViewModel.setEta(progressCal);

                    TextView etaTxtView = dialog.findViewById(R.id.respond_dialog_sar_l_seek_eta_txtview);
                    etaTxtView.setText(String.format(Locale.ENGLISH, "Estimated time to RV: %d minutes", respondViewModel.getEta().getValue())); // the TextView Reference
                    seekBar.setSecondaryProgress(progress);

                }
            }
        });

        Button buttonSAR_A = dialog.findViewById(R.id.respond_dialog_sar_l_submit_button);
        buttonSAR_A.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int progressCal = (seekBar.getProgress() * 5) + 30;
                EditText editText = dialog.findViewById(R.id.respond_dialog_sar_l_message_editview);

                sendSMSResponse(context, dialog, SARResponseCode.SAR_L, progressCal, editText.getText().toString());
            }
        });


        seekBar.setProgress(6);
    }

    @SuppressLint("UnlocalizedSms")
    public void sendSMSResponse(Context context, DialogInterface dialog, SARResponseCode responseCode, int eta, String message) {
        SmsManager smsManager = SmsManager.getDefault();

        // SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        //String phoneNumber = pref.getString("sarResponsePhoneNumber", "07537415551");
        //if (phoneNumber != null && phoneNumber.isEmpty()) {

        String phoneNumber = "07537415551";
        //String phoneNumber = "07479923541";

        //}
        Toast toast;
        switch (responseCode) {
            case SAR_A:
                if (eta != 0)
                    smsManager.sendTextMessage(phoneNumber, null, "SAR A" + eta + " " + message, null, null);
                else
                    smsManager.sendTextMessage(phoneNumber, null, "SAR A  " + message, null, null);

                toast = Toast.makeText(context, "SAR A sent to SARCALL", Toast.LENGTH_LONG);
                toast.show();
                dialog.cancel();
                break;
            case SAR_L:
                smsManager.sendTextMessage(phoneNumber, null, "SAR L" + eta + " " + message, null, null);

                toast = Toast.makeText(context, "SAR L sent to SARCALL", Toast.LENGTH_LONG);
                toast.show();
                dialog.cancel();
                break;
            case SAR_N:
                smsManager.sendTextMessage(phoneNumber, null, "SAR N", null, null);

                toast = Toast.makeText(context, "SAR N sent to SARCALL", Toast.LENGTH_LONG);
                toast.show();
                dialog.cancel();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + responseCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_SMS)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        setPreview(getContext(), getView());
                    }
                }
            }
        }
    }
}