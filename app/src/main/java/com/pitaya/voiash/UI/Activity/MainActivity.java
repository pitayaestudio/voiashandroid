package com.pitaya.voiash.UI.Activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.firebase.database.DatabaseReference;
import com.pitaya.voiash.R;
import com.pitaya.voiash.UI.Fragment.BaseFragment;
import com.pitaya.voiash.UI.Fragment.BlankFragment;
import com.pitaya.voiash.UI.Fragment.DialogFragment.ConfirmEmailDialog;
import com.pitaya.voiash.UI.Fragment.ProfileFragment;
import com.pitaya.voiash.Util.Log;

import java.util.HashMap;

public class MainActivity extends BaseMainActivity implements BaseFragment.OnFragmentInteractionListener, AHBottomNavigation.OnTabSelectedListener {
    private AHBottomNavigation bottomNavigation;
    private SelectedNavigationElement selectedNavigationElement;
    private HashMap<Integer, Fragment> fragmentHashMap = new HashMap<>();
    private int mSelectedItem;
    private static final String TAG = "punchewey";
    private DatabaseReference userReference;
    Boolean isShownDialog = false;
    ConfirmEmailDialog confirmEmailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userReference = getUserReference();
        userReference.keepSynced(true);
        setContentView(R.layout.activity_main);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        // bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        fragmentHashMap.put(0, BlankFragment.newInstance());
        fragmentHashMap.put(1, BlankFragment.newInstance());
        fragmentHashMap.put(2, BlankFragment.newInstance());
        fragmentHashMap.put(3, BlankFragment.newInstance());
        fragmentHashMap.put(4, ProfileFragment.newInstance());
        // Create items
        AHBottomNavigationItem itemDiscover = new AHBottomNavigationItem(R.string.lbl_menu_discover, R.drawable.ic_discover, R.color.colorPrimaryDark);
        AHBottomNavigationItem itemMyTravel = new AHBottomNavigationItem(R.string.lbl_menu_my_travel, R.drawable.ic_travel, R.color.colorPrimaryDark);
        AHBottomNavigationItem itemSocial = new AHBottomNavigationItem(R.string.lbl_menu_social, R.drawable.ic_place, R.color.colorPrimaryDark);
        AHBottomNavigationItem itemChat = new AHBottomNavigationItem(R.string.lbl_menu_chat, R.drawable.ic_chat, R.color.colorPrimaryDark);
        AHBottomNavigationItem itemProfile = new AHBottomNavigationItem(R.string.lbl_menu_profile, R.drawable.ic_profile, R.color.colorPrimaryDark);
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        // Add items
        bottomNavigation.addItem(itemDiscover);
        bottomNavigation.addItem(itemMyTravel);
        bottomNavigation.addItem(itemSocial);
        bottomNavigation.addItem(itemChat);
        bottomNavigation.addItem(itemProfile);
        bottomNavigation.setOnTabSelectedListener(this);
        selectedNavigationElement = new SelectedNavigationElement(0, fragmentHashMap.get(0));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.content_main, fragmentHashMap.get(0));
        ft.commit();
        if (preferencesHelper.getFirstOpening()) {
            bottomNavigation.setCurrentItem(4);
            preferencesHelper.putFirstOpeningDisabled();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mAuth.getCurrentUser().isAnonymous()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile_settings:
                startActivity(new Intent(this, ProfileSettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        if (selectedNavigationElement.getPos() != position) {
            selectFragment(position);
        }
        return true;
    }

    private void selectFragment(int position) {
        try {
            Log.wtf("selectFragment", bottomNavigation.getItem(position).getTitle(this));
            Fragment frag = fragmentHashMap.get(position);
            mSelectedItem = position;
            if (frag != null) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                if (frag.isAdded()) {
                    ft.show(frag);
                } else {
                    ft.add(R.id.content_main, frag, bottomNavigation.getItem(position).getTitle(this));
                }
                ft.hide(selectedNavigationElement.getFrag());
                ft.commit();
            }
            selectedNavigationElement = new SelectedNavigationElement(position, frag);
            invalidateOptionsMenu();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (preferencesHelper.getIsEmailProvider() && !preferencesHelper.getHasConfirmedEmail()) {
            mAuth.getCurrentUser().reload();
            if (!mAuth.getCurrentUser().isEmailVerified()) {
                if (!isShownDialog) {
                    confirmEmailDialog = ConfirmEmailDialog.newInstance();
                    confirmEmailDialog.show(getSupportFragmentManager(), "email");
                    isShownDialog = true;
                }
            } else {
                preferencesHelper.putHasConfirmedEmail(true);
                Log.wtf(TAG, "dialog not needed");
                if (isShownDialog)
                    confirmEmailDialog.dismiss();
            }
        }
    }

    class SelectedNavigationElement {
        private Integer pos;
        private Fragment frag;

        public SelectedNavigationElement(Integer pos, Fragment frag) {
            this.pos = pos;
            this.frag = frag;
        }

        public Integer getPos() {
            return pos;
        }

        public Fragment getFrag() {
            return frag;
        }
    }
}
