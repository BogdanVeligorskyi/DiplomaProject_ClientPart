package ua.cn.cpnu.microclimate_system;

import android.os.Parcel;
import android.os.Parcelable;

// Sensor class
public class Sensor implements Parcelable {

    private final int id;
    private final int room_id;
    private final String name;
    private final String measure;
    private final String measure_unit;

    public Sensor(int id, int room_id, String name, String measure, String measure_unit) {
        this.id = id;
        this.room_id = room_id;
        this.name = name;
        this.measure = measure;
        this.measure_unit = measure_unit;
    }

    protected Sensor(Parcel in) {
        id = in.readInt();
        room_id = in.readInt();
        name = in.readString();
        measure = in.readString();
        measure_unit = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(room_id);
        dest.writeString(name);
        dest.writeString(measure);
        dest.writeString(measure_unit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sensor> CREATOR = new Creator<Sensor>() {
        @Override
        public Sensor createFromParcel(Parcel in) {
            return new Sensor(in);
        }

        @Override
        public Sensor[] newArray(int size) {
            return new Sensor[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getRoomId() {
        return room_id;
    }

    public String getName() {
        return name;
    }

    public String getMeasure() {
        return measure;
    }

    public String getMeasureUnit() {
        return measure_unit;
    }
}

