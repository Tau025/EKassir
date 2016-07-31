package com.devtau.ekassir.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import com.devtau.ekassir.R;
import com.devtau.ekassir.util.Logger;
/**
 * Этот диалог показываем пользователю, когда подключения к интернету нет.
 * Пользователь может включить сеть и повторить попытку подключения.
 */
public class NoInternetDF extends DialogFragment {
    private static final String TAG = NoInternetDF.class.getSimpleName();
    private NoInternetDFListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try { mListener = (NoInternetDFListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " + mListener.getClass().getName());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.no_internet_msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.retryConnection();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {/*NOP*/ }
                });
        return builder.create();
    }

    //метод создаст новый диалог, если его еще нет на экране
    public static boolean show(FragmentManager manager) {
        NoInternetDF dialog = (NoInternetDF) manager.findFragmentByTag(TAG);
        if (dialog == null) {
            Logger.d(TAG, "NoInternetDF not found. going to create new one");
            new NoInternetDF().show(manager, TAG);
            return true;
        } else {
            Logger.d(TAG, "NoInternetDF already shown");
            return false;
        }
    }

    public interface NoInternetDFListener{
        void retryConnection();
    }
}