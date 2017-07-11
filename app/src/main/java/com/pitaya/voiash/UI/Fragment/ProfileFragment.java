package com.pitaya.voiash.UI.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pitaya.voiash.Core.VoiashUser;
import com.pitaya.voiash.R;
import com.pitaya.voiash.UI.Activity.BaseMainActivity;
import com.pitaya.voiash.UI.Activity.ZoomImageActivity;
import com.pitaya.voiash.Util.UI;

import java.util.Calendar;

import static com.pitaya.voiash.Util.UserHelper.getFullName;

public class ProfileFragment extends BaseFragment {
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;
    private ValueEventListener userListener;
    private TextView txt_profile_name, txt_profile_age;
    private ImageView img_profile_picture, img_profile_back;
    private Button btn_delete_account, btn_sign_out;
    private final String TAG = "ProfileFragment";

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        img_profile_picture = (ImageView) v.findViewById(R.id.img_profile_picture);
        img_profile_back = (ImageView) v.findViewById(R.id.img_profile_back);
        txt_profile_name = (TextView) v.findViewById(R.id.txt_profile_name);
        btn_delete_account = (Button) v.findViewById(R.id.btn_delete_account);
        btn_sign_out = (Button) v.findViewById(R.id.btn_sign_out);
        txt_profile_age = (TextView) v.findViewById(R.id.txt_profile_age);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userReference = getBaseReference().child("users").child(firebaseUser.getUid());
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        final VoiashUser user = dataSnapshot.getValue(VoiashUser.class);
                        if (user != null) {
                            txt_profile_name.setText(getFullName(user));
                            UI.setProfilePicture(getContext(), user.getProfilePicture(), img_profile_picture);
                            if (!TextUtils.isEmpty(user.getProfilePicture()))
                                UI.setProfilePictureSquare(getContext(), user.getProfilePicture(), img_profile_back);
                                img_profile_picture.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity(), ZoomImageActivity.class);
                                        intent.putExtra(ZoomImageActivity.ZOOMABLE_USER, user);
                                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), img_profile_picture, "expanded_image");
                                        startActivity(intent, options.toBundle());
                                    }
                                });

                            try {
                                Calendar userDate = Calendar.getInstance();
                                userDate.setTime(firebaseDateFormat.parse(user.getBirthday()));
                                Calendar currentDate = Calendar.getInstance();
                                currentDate.add(Calendar.DAY_OF_YEAR, -userDate.get(Calendar.DAY_OF_YEAR));
                                int diff = currentDate.get(Calendar.YEAR) - userDate.get(Calendar.YEAR);
                                txt_profile_age.setText(String.format(getString(R.string.format_years), diff));
                                txt_profile_age.setVisibility(View.VISIBLE);
                            } catch (Exception x) {

                            }
                        }
                    } catch (Exception x) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseMainActivity) getContext()).signOut();
            }
        });
        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseMainActivity) getContext()).deleteAccount();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        userReference.addValueEventListener(userListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        userReference.removeEventListener(userListener);
    }
}
