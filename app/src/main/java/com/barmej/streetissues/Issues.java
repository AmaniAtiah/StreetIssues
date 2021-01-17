package com.barmej.streetissues;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.Date;

public class Issues implements Parcelable {
    private String title;
    private String description;
    private String photo;
    private GeoPoint location;

    public Issues() {

    }

    public Issues(String title,String description,String photo,GeoPoint location) {
        this.title = title;
        this.description = description;
        this.photo = photo;
        this.location = location;
    }

    protected Issues(Parcel in) {
        title = in.readString();
        description = in.readString();
        photo = in.readString();
        double lat = in.readDouble();
        double lng = in.readDouble();
        location = new GeoPoint(lat, lng);

    }

    public static final Creator<Issues> CREATOR = new Creator<Issues>() {
        @Override
        public Issues createFromParcel(Parcel in) {
            return new Issues(in);
        }

        @Override
        public Issues[] newArray(int size) {
            return new Issues[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest,int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(photo);
        dest.writeDouble(location.getLatitude());
        dest.writeDouble(location.getLongitude());

    }
}
