package com.pitaya.voiash.UI.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pitaya.voiash.Core.VoiashUser;
import com.pitaya.voiash.R;
import com.pitaya.voiash.Util.Log;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ProfileFragment extends BaseFragment {
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;
    private ValueEventListener userListener;
    private TextView txt_profile_name;
    private ImageView img_profile_picture;
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
        txt_profile_name = (TextView) v.findViewById(R.id.txt_profile_name);
        btn_delete_account = (Button) v.findViewById(R.id.btn_delete_account);
        btn_sign_out = (Button) v.findViewById(R.id.btn_sign_out);
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
                    VoiashUser user = dataSnapshot.getValue(VoiashUser.class);
                    if (user != null) {
                        txt_profile_name.setText(user.getFullName());
                        Glide.with(ProfileFragment.this).load(user.getProfilePicture()).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(getContext())).into(img_profile_picture);
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
                Log.wtf(TAG, "Sign Out");
                FirebaseAuth.getInstance().signOut();
            }
        });
        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf(TAG, "Delete Account");
                getBaseReference().child("users").child(firebaseUser.getUid()).removeValue();
                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), getString(R.string.lbl_account_deleted), Toast.LENGTH_SHORT).show();
                    }
                });

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
