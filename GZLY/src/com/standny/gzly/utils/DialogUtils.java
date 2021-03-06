package com.standny.gzly.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.standny.gzly.R;

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
    
    public static void showCreateChannelDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.road_create_channel, null, false);
        final Dialog dialog = new Dialog(context, R.style.dialog);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        
        dialog.show();
    }
}
