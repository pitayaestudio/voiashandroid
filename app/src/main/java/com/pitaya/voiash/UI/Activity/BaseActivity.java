package com.pitaya.voiash.UI.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pitaya.voiash.R;

/**
 * Created by rulo on 06/07/17.
 */

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog dialog;

    public DatabaseReference getBaseReference() {
        return FirebaseDatabase.getInstance().getReference().child("devDB");
    }

    public void showAlert(String title, String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(android.R.string.ok), onClickListener)
                .create().show();
    }

    public void showAlert(String title, String message) {
        showAlert(title, message, null);
    }

    void showProgressDialog() {
        showProgressDialog(getString(R.string.lbl_please_wait), getString(R.string.lbl_loading_information));
    }

    void hideProgressDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    void showProgressDialog(String title, String content) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }
        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
