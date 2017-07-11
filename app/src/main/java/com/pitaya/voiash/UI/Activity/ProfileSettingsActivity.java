package com.pitaya.voiash.UI.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.pitaya.voiash.Core.VoiashUser;
import com.pitaya.voiash.R;
import com.pitaya.voiash.Util.FirebaseStorageHelper;
import com.pitaya.voiash.Util.ImageSelector.PickerBuilder;
import com.pitaya.voiash.Util.Log;
import com.pitaya.voiash.Util.PermissionsHelper;
import com.pitaya.voiash.Util.UI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.pitaya.voiash.Util.PermissionsHelper.validateGrantedPermissions;
import static com.pitaya.voiash.Util.UI.setProfilePicture;

public class ProfileSettingsActivity extends BaseMainActivity implements ValueEventListener {
    private static final int CAMERA_REQUEST = 158;
    private static final int GALLERY_REQUEST = 714;
    private DatabaseReference userReference;
    private Button btn_edit_save;
    private VoiashUser thisUser;
    private EditText[] editTexts;
    private TextInputLayout[] textInputLayouts;
    private ImageView img_edit_profile_picture;
    private SimpleDateFormat birthdayDateFormat = new SimpleDateFormat("dd MMMM yyyy");
    private Calendar userDate = Calendar.getInstance();
    private String TAG = "ProfileSettingsActivity";
    private FloatingActionButton fab_edit_picture;
    private ArrayList<String> permissionListGallery = new ArrayList<String>() {{
        add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        add(Manifest.permission.READ_EXTERNAL_STORAGE);
    }};
    private ArrayList<String> permissionListCamera = new ArrayList<String>() {{
        add(Manifest.permission.CAMERA);
        add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        add(Manifest.permission.READ_EXTERNAL_STORAGE);
    }};
    private FirebaseStorageHelper firebaseStorageHelper = new FirebaseStorageHelper();
    private Uri pictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile_settings);
        img_edit_profile_picture = (ImageView) findViewById(R.id.img_edit_profile_picture);
        btn_edit_save = (Button) findViewById(R.id.btn_edit_save);
        userReference = getUserReference();
        textInputLayouts = new TextInputLayout[]{
                (TextInputLayout) findViewById(R.id.til_edit_name),
                (TextInputLayout) findViewById(R.id.til_edit_lastname),
                (TextInputLayout) findViewById(R.id.til_edit_bday),
                (TextInputLayout) findViewById(R.id.til_edit_email)
        };
        editTexts = new EditText[]{
                (EditText) findViewById(R.id.et_edit_name),
                (EditText) findViewById(R.id.et_edit_lastname),
                (EditText) findViewById(R.id.et_edit_bday),
                (EditText) findViewById(R.id.et_edit_email)
        };
        editTexts[3].setEnabled(false);
        editTexts[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(ProfileSettingsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        try {
                            userDate.set(Calendar.YEAR, year);
                            userDate.set(Calendar.MONTH, month);
                            userDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            editTexts[2].setText(birthdayDateFormat.format(userDate.getTime()));
                        } catch (Exception x) {
                            Log.wtf(TAG, x.getMessage());
                            x.printStackTrace();
                        }
                    }
                },
                        userDate.get(Calendar.YEAR),
                        userDate.get(Calendar.MONTH),
                        userDate.get(Calendar.DAY_OF_MONTH)
                );
                Calendar maxCalendar = Calendar.getInstance();
                maxCalendar.add(Calendar.YEAR, -18);
                dpd.getDatePicker().setMaxDate(maxCalendar.getTime().getTime());
                Calendar minCalendar = Calendar.getInstance();
                minCalendar.add(Calendar.YEAR, -100);
                dpd.getDatePicker().setMinDate(minCalendar.getTime().getTime());
                dpd.show();
            }
        });
        fab_edit_picture = (FloatingActionButton) findViewById(R.id.fab_edit_picture);
        fab_edit_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] opsChars = {getString(R.string.lbl_take_photo), getString(R.string.lbl_select_from_gallery)};
                new AlertDialog.Builder(ProfileSettingsActivity.this)
                        .setTitle(getString(R.string.lbl_profile_picture))
                        .setItems(opsChars, new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        if (PermissionsHelper.checkAndRequestPermissions(ProfileSettingsActivity.this, CAMERA_REQUEST, permissionListCamera)) {
                                            requestPictures(false);
                                        }
                                        break;
                                    case 1:
                                        if (PermissionsHelper.checkAndRequestPermissions(ProfileSettingsActivity.this, GALLERY_REQUEST, permissionListGallery)) {
                                            requestPictures(true);
                                        }
                                        break;
                                }
                            }
                        })
                        .create()
                        .show();


              /*  if (PermissionsHelper.checkAndRequestPermissions(ProfileSettingsActivity.this, MEDIA_REQUEST, permissionListGallery)) {
                    requestPictures();
                }*/
            }
        });
        btn_edit_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFieds()) {
                    attempSaveUser();
                }
            }
        });
        userReference.addValueEventListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
        userReference.removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        try {
            thisUser = dataSnapshot.getValue(VoiashUser.class);
            if (thisUser != null) {
                editTexts[0].setText(thisUser.getName());
                editTexts[1].setText(thisUser.getLastName());
                editTexts[3].setText(thisUser.getEmail());
                try {
                    userDate.setTime(firebaseDateFormat.parse(thisUser.getBirthday()));
                    editTexts[2].setText(birthdayDateFormat.format(userDate.getTime()));
                } catch (Exception x) {

                }
            }
            setProfilePicture(this, thisUser.getProfilePicture(), img_edit_profile_picture);
        } catch (Exception x) {
            finish();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @SuppressWarnings("VisibleForTests")
    private void attempSaveUser() {
        showProgressDialog();
        thisUser.setName(editTexts[0].getText().toString());
        thisUser.setLastName(editTexts[1].getText().toString());
        if (!TextUtils.isEmpty(editTexts[2].getText().toString()))
            thisUser.setBirthday(firebaseDateFormat.format(userDate.getTime()));
        userReference.setValue(thisUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (pictureUri == null) {
                    finish();
                } else {
                    firebaseStorageHelper.uploadProfilePhoto(getUserId(), pictureUri, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            hideProgressDialog();
                            if (task.isSuccessful()) {
                                getUserReference().child("profilePicture").setValue(task.getResult().getDownloadUrl().toString());
                                setProfilePicture(ProfileSettingsActivity.this, pictureUri, img_edit_profile_picture);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean validateFieds() {
        for (int i = 0; i < editTexts.length; i++) {
            if (TextUtils.isEmpty(editTexts[i].getText())) {
                textInputLayouts[i].setErrorEnabled(true);
                textInputLayouts[i].setError(getString(R.string.error_field_required));
                editTexts[i].requestFocus();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (validateGrantedPermissions(permissions, grantResults, permissionListGallery) == PermissionsHelper.PERMISSIONS_RESULT.PERMISSIONS_OK) {
                    requestPictures(true);
                }
                break;
            case CAMERA_REQUEST:
                if (validateGrantedPermissions(permissions, grantResults, permissionListCamera) == PermissionsHelper.PERMISSIONS_RESULT.PERMISSIONS_OK) {
                    requestPictures(false);
                }
                break;
        }

    }


    private void requestPictures(boolean fromGallery) {
        new PickerBuilder(ProfileSettingsActivity.this, fromGallery ? PickerBuilder.SELECT_FROM_GALLERY : PickerBuilder.SELECT_FROM_CAMERA)
                .setCropScreenColor(ContextCompat.getColor(ProfileSettingsActivity.this, R.color.colorPrimaryDark))
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(final Uri imageUri) {
                        pictureUri = imageUri;
                        UI.setProfilePicture(ProfileSettingsActivity.this, imageUri, img_edit_profile_picture);
                    }
                })
                .start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

        }
    }


}
