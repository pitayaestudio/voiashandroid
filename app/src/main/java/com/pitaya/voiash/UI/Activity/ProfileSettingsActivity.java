package com.pitaya.voiash.UI.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pitaya.voiash.Core.VoiashUser;
import com.pitaya.voiash.R;
import com.pitaya.voiash.Util.ImageSelectorHelper;
import com.pitaya.voiash.Util.Log;
import com.pitaya.voiash.Util.PermissionsHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.pitaya.voiash.Util.PermissionsHelper.PERMISSIONS_RESULT;
import static com.pitaya.voiash.Util.PermissionsHelper.validateGrantedPermissions;
import static com.pitaya.voiash.Util.UI.setProfilePicture;

public class ProfileSettingsActivity extends BaseMainActivity implements ValueEventListener {
    private static final int MEDIA_REQUEST = 158;
    private static final int GALLERY_REQUEST = 714;
    private static final int CROP_REQUEST = 116;
    private DatabaseReference userReference;
    private VoiashUser thisUser;
    private EditText[] editTexts;
    private TextInputLayout[] textInputLayouts;
    private ImageView img_edit_profile_picture;
    private SimpleDateFormat birthdayDateFormat = new SimpleDateFormat("dd MMMM yyyy");
    private Calendar userDate = Calendar.getInstance();
    private String TAG = "ProfileSettingsActivity";
    private FloatingActionButton fab_edit_picture;
    private ArrayList<String> permissionList = new ArrayList<String>() {{
        add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        add(Manifest.permission.READ_EXTERNAL_STORAGE);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile_settings);
        img_edit_profile_picture = (ImageView) findViewById(R.id.img_edit_profile_picture);
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
                dpd.getDatePicker().setMaxDate(new Date().getTime());
                dpd.show();
            }
        });
        fab_edit_picture = (FloatingActionButton) findViewById(R.id.fab_edit_picture);
        fab_edit_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionsHelper.checkAndRequestPermissions(ProfileSettingsActivity.this, MEDIA_REQUEST, permissionList)) {
                    ImageSelectorHelper.requestFromGallery(ProfileSettingsActivity.this, GALLERY_REQUEST);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_settings, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        userReference.addValueEventListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_profile_settings) {
            if (validateFieds()) {
                attempSaveUser();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void attempSaveUser() {


        thisUser.setName(editTexts[0].getText().toString());
        thisUser.setLastName(editTexts[1].getText().toString());
        if (!TextUtils.isEmpty(editTexts[2].getText().toString()))
            thisUser.setBirthday(firebaseDateFormat.format(userDate.getTime()));
        userReference.setValue(thisUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

    private boolean validateFieds() {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (validateGrantedPermissions(permissions, grantResults, permissionList) == PERMISSIONS_RESULT.PERMISSIONS_OK) {
            ImageSelectorHelper.requestFromGallery(ProfileSettingsActivity.this, GALLERY_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST:
                   /*   Uri photoUri = onSelectFromGalleryResult(this, data);
                        Glide.with(this).load(photoUri).into(img_edit_profile_picture);
                        if (photoUri != null)
                        cropImage(ProfileSettingsActivity.this, photoUri, CROP_REQUEST);*/
                    break;
            }
        }
    }
}
