package com.mmlab.m1.widget;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mmlab.m1.R;

public class AboutDialog extends DialogFragment {
    public static final String TAG = "AboutDialog";

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onDetach() {
        super.onDetach();
    }
    public AboutDialog() {
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_about, null);

        return new MaterialDialog.Builder(getActivity())
                .title(R.string.about_deh)
                .customView(view, true)
                .widgetColor(getResources().getColor(R.color.colorPrimary))
                .positiveText(R.string.website)
                //.negativeText(R.string.direction)
                .neutralText(R.string.shutdown)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        String url="http://deh.csie.ncku.edu.tw";
                        Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(ie);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {

                    }
                })
                .build();
    }

}
