package com.standny.gzly.utils;

import com.standny.gzly.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class DialogUtils {

    public static void showDialog(Context context, String message, String title, 
            OnClickListener listener) {
        AlertDialog.Builder builder = new Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        
        builder.setPositiveButton(R.string.retry, listener);
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                    int which) {
                dialog.dismiss();
            }
        });
        try {
            builder.create().show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
