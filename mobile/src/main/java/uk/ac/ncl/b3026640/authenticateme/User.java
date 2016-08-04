package uk.ac.ncl.b3026640.authenticateme;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jonathan on 04-Aug-16.
 */
public class User implements Parcelable {
    private String uID;

    // Twitter ID
    private long tID;

    // Twitter Screen name
    private String tScreenName;

    // Facebook ID
    private long fID;
    private Date lastAuth;
    private Map<Date, Boolean> authAttempts;

    public User() {}

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uID", uID);
        result.put("tID", tID);
        result.put("tScreenName", tScreenName);
        result.put("fID", fID);
        result.put("lastAuth", lastAuth);
        result.put("authAttempts", authAttempts);

        return result;
    }

    public static class Builder {
        // Required Parameters
        private String uID;

        // Optional Parameters - initialized to default
        private long tID = 0;
        private String tScreenName = "";
        private long fID = 0;
        private Date lastAuth = null;
        private Map<Date, Boolean> authAttempts = new HashMap<>();

        public Builder(String uID) { this.uID = uID; }
        public Builder tID(long val) {
            tID = val;
            return this;
        }

        public Builder tScreenName(String val) {
            tScreenName = val;
            return this;
        }

        public Builder fID(long val) {
            fID = val;
            return this;
        }

        public Builder lastAuth(Date val) {
            lastAuth = val;
            return this;
        }

        public Builder authAttempts(Map<Date, Boolean> val) {
            authAttempts = val;
            return this;
        }

        public User build() { return new User(this); }
    }

    private User(Builder builder) {
        uID = builder.uID;
        tID = builder.tID;
        tScreenName = builder.tScreenName;
        fID = builder.fID;
        lastAuth = builder.lastAuth;
        authAttempts = builder.authAttempts;
    }


    @Exclude
    @Override
    public int describeContents() {
        return 0;
    }

    @Exclude
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uID);
        dest.writeLong(this.tID);
        dest.writeString(this.tScreenName);
        dest.writeLong(this.fID);
        dest.writeLong(this.lastAuth != null ? this.lastAuth.getTime() : -1);
        dest.writeInt(this.authAttempts.size());
        for (Map.Entry<Date, Boolean> entry : this.authAttempts.entrySet()) {
            dest.writeLong(entry.getKey() != null ? entry.getKey().getTime() : -1);
            dest.writeValue(entry.getValue());
        }
    }


    protected User(Parcel in) {
        this.uID = in.readString();
        this.tID = in.readLong();
        this.tScreenName = in.readString();
        this.fID = in.readLong();
        long tmpLastAuth = in.readLong();
        this.lastAuth = tmpLastAuth == -1 ? null : new Date(tmpLastAuth);
        int authAttemptsSize = in.readInt();
        this.authAttempts = new HashMap<Date, Boolean>(authAttemptsSize);
        for (int i = 0; i < authAttemptsSize; i++) {
            long tmpKey = in.readLong();
            Date key = tmpKey == -1 ? null : new Date(tmpKey);
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.authAttempts.put(key, value);
        }
    }

    @Exclude
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public long gettID() {
        return tID;
    }

    public void settID(long tID) {
        this.tID = tID;
    }

    public String gettScreenName() {
        return tScreenName;
    }

    public void settScreenName(String tScreenName) {
        this.tScreenName = tScreenName;
    }

    public long getfID() {
        return fID;
    }

    public void setfID(long fID) {
        this.fID = fID;
    }

    public Date getLastAuth() {
        return lastAuth;
    }

    public void setLastAuth(Date lastAuth) {
        this.lastAuth = lastAuth;
    }

    public Map<Date, Boolean> getAuthAttempts() {
        return authAttempts;
    }

    public void setAuthAttempts(Map<Date, Boolean> authAttempts) {
        this.authAttempts = authAttempts;
    }

    @Override
    public String toString() {
        return "User{" +
                "uID='" + uID + '\'' +
                ", tID=" + tID +
                ", tScreenName='" + tScreenName + '\'' +
                ", fID=" + fID +
                ", lastAuth=" + lastAuth +
                ", authAttempts=" + authAttempts +
                '}';
    }
}
