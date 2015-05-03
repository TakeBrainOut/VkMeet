package vershitsky.kirill.myapp;

import android.graphics.Bitmap;
import android.location.Address;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Вершицкий on 15.03.2015.
 */
public class AppUser implements Parcelable {
    private String id;
    private String firstName;
    private String lastName;
    private String sex;
    private String bdate;
    private String photoURL;
    private Bitmap userPhoto;

    //location
    private String countryName;
    private String adminArea;
    private String locality;

    private DateTimeParser[] parcers = {DateTimeFormat.forPattern("dd.MM.yyyy").getParser(), DateTimeFormat.forPattern("dd.MM").getParser()};

    public AppUser(String id, String firstName, String lastName, String sex, String bdate, String photoURL) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.bdate = bdate;

        this.photoURL = photoURL;
    }

    public AppUser() {
    }

    public AppUser(JSONObject jsonUser) {
        setAppUser(jsonUser);
    }

    public void setAppUser(JSONObject jsonUser) {
        try {
            this.id = jsonUser.getString("id");
            this.firstName = jsonUser.getString("first_name");
            this.lastName = jsonUser.getString("last_name");
            this.sex = jsonUser.getString("sex");

        } catch (JSONException e) {
        }
        try {
            this.bdate = jsonUser.getString("bdate");
        } catch (JSONException e) {
            this.bdate = Constants.UNKNOWN;
        }
        try {
            this.photoURL = jsonUser.getString("photo_200_orig");
        } catch (JSONException e) {
        }
//        if(!bdate.equals(Constants.UNKNOWN)) setDateBirthday(bdate);
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setUserPhoto(Bitmap userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getId() {
        return id;
    }

    public Bitmap getUserPhoto() {
        return userPhoto;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public String getLocality() {
        return locality;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSex() {
        return sex;
    }

    public DateTime setDateBirthday(String stringDateBirthday) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, parcers).toFormatter();
        if (!bdate.equals(Constants.UNKNOWN))
            return formatter.parseDateTime(stringDateBirthday);
        return null;
    }

    public String getBdate() {
        return bdate;
    }

    public String getStringDateBirthday() {
        DateTime dateBirthday = setDateBirthday(bdate);
        if (dateBirthday != null)
            return dateBirthday.dayOfMonth().getAsString() + " " + dateBirthday.monthOfYear().getAsText(Locale.ENGLISH) + " " + dateBirthday.year().getAsString();
        return "NULL";
    }

    @Override
    public String toString() {
        return "id: " + id + " first_name: " + firstName + " last_name:" + lastName + " sex:" + sex + " bdate: " + bdate + " photo:" + photoURL + " country:" + this.countryName + " admin:" + adminArea + " locality:" + locality;
    }

    public String getUserInfo() {
        return "Name: " + firstName + " " + lastName + "\n" + "sex: " + sex + " " + "bdate: " + bdate;
    }

    public void setLocation(String countryName, String adminArea, String locality) {
        if (countryName != null) {
            this.countryName = countryName;
        } else this.countryName = Constants.UNKNOWN;
        if (adminArea != null) {
            this.adminArea = adminArea;
        } else this.adminArea = Constants.UNKNOWN;
        if (locality != null) {
            this.locality = locality;
        } else this.locality = Constants.UNKNOWN;

    }

    public String getLocation() {
        return this.countryName + " " + this.adminArea + " " + this.locality;
    }

    public void setFullFromJson(JSONObject jsonUser) {
        setAppUser(jsonUser);
        try {
            this.locality = jsonUser.getString("locality");
            this.countryName = jsonUser.getString("countryName");
            this.adminArea = jsonUser.getString("adminArea");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJSON() {
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put("id", this.id);
            jsonUser.put("first_name", this.firstName);
            jsonUser.put("last_name", this.lastName);
            jsonUser.put("sex", this.sex);
            jsonUser.put("bdate", this.bdate);
            jsonUser.put("photo_200_orig", this.photoURL);
            jsonUser.put("countryName", this.countryName);
            jsonUser.put("adminArea", this.adminArea);
            jsonUser.put("locality", this.locality);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppUser)) return false;
        AppUser appUser = (AppUser) o;
        if (!adminArea.equals(appUser.adminArea)) return false;
        if (!bdate.equals(appUser.bdate)) return false;
        if (!countryName.equals(appUser.countryName)) return false;
        if (!firstName.equals(appUser.firstName)) return false;
        if (!id.equals(appUser.id)) return false;
        if (!lastName.equals(appUser.lastName)) return false;
        if (!locality.equals(appUser.locality)) return false;
        if (!photoURL.equals(appUser.photoURL)) return false;
        if (!sex.equals(appUser.sex)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + sex.hashCode();
        result = 31 * result + bdate.hashCode();
        result = 31 * result + photoURL.hashCode();
        result = 31 * result + (userPhoto != null ? userPhoto.hashCode() : 0);
        result = 31 * result + countryName.hashCode();
        result = 31 * result + adminArea.hashCode();
        result = 31 * result + locality.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(sex);
        dest.writeString(bdate);
        dest.writeString(photoURL);
        dest.writeString(countryName);
        dest.writeString(adminArea);
        dest.writeString(locality);
        dest.writeParcelable(userPhoto, flags);
    }

    public static final Creator<AppUser> CREATOR = new Creator<AppUser>() {
        @Override
        public AppUser createFromParcel(Parcel source) {
            return new AppUser(source);
        }

        @Override
        public AppUser[] newArray(int size) {
            return new AppUser[size];
        }
    };

    private AppUser(Parcel parcel) {
        id = parcel.readString();
        firstName = parcel.readString();
        lastName = parcel.readString();
        sex = parcel.readString();
        bdate = parcel.readString();
        photoURL = parcel.readString();
        countryName = parcel.readString();
        adminArea = parcel.readString();
        locality = parcel.readString();
        userPhoto = (Bitmap) parcel.readParcelable(getClass().getClassLoader());
    }
}
