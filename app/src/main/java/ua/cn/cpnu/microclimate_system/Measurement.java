package ua.cn.cpnu.microclimate_system;

import android.os.Parcel;
import android.os.Parcelable;

public class Measurement implements Parcelable {
    private int id;
    private int sensor_id;
    private float value;
    private String date_time;

    public Measurement(int id, int sensor_id, float value, String date_time) {
        this.id = id;
        this.sensor_id = sensor_id;
        this.value = value;
        this.date_time = date_time;
    }

    protected Measurement(Parcel in) {
        id = in.readInt();
        sensor_id = in.readInt();
        value = in.readFloat();
        date_time = in.readString();
    }

    public static final Creator<Measurement> CREATOR = new Creator<Measurement>() {
        @Override
        public Measurement createFromParcel(Parcel in) {
            return new Measurement(in);
        }

        @Override
        public Measurement[] newArray(int size) {
            return new Measurement[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getSensorId() {
        return sensor_id;
    }

    public float getValue() {
        return value;
    }

    public String getDateime() {
        return date_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(sensor_id);
        parcel.writeFloat(value);
        parcel.writeString(date_time);
    }
}
