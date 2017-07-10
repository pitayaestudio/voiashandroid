package com.pitaya.voiash.Core;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rulo on 06/07/17.
 */

public class VoiashUser implements Parcelable {
    private String email;
    private String lastName;
    private String name;
    private String phoneNumber;
    private String profilePicture;
    private String provider;
    private String pushToken;
    private String birthday;

    public VoiashUser() {
    }


    protected VoiashUser(Parcel in) {
        email = in.readString();
        lastName = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        profilePicture = in.readString();
        provider = in.readString();
        pushToken = in.readString();
        birthday = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFullName() {
        return String.format("%s %s", getName(), getLastName());
    }

    @Override
    public String toString() {
        return "VoiashUser{" +
                "email='" + email + '\'' +
                ", lastName='" + lastName + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", provider='" + provider + '\'' +
                ", pushToken='" + pushToken + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(lastName);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(profilePicture);
        dest.writeString(provider);
        dest.writeString(pushToken);
        dest.writeString(birthday);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<VoiashUser> CREATOR = new Parcelable.Creator<VoiashUser>() {
        @Override
        public VoiashUser createFromParcel(Parcel in) {
            return new VoiashUser(in);
        }

        @Override
        public VoiashUser[] newArray(int size) {
            return new VoiashUser[size];
        }
    };
}