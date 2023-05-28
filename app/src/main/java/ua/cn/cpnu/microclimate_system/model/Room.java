package ua.cn.cpnu.microclimate_system.model;

import android.os.Parcel;
import android.os.Parcelable;

// Room class
public class Room implements Parcelable {

    private final int id;
    private final String name;
    private final String device_ip;
    private final String device;

    public Room(int id, String name, String device_ip, String device) {
        this.id = id;
        this.name = name;
        this.device_ip = device_ip;
        this.device = device;
    }

    protected Room(Parcel in) {
        id = in.readInt();
        name = in.readString();
        device_ip = in.readString();
        device = in.readString();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDeviceIP() {
        return device_ip;
    }

    public String getDevice() {
        return device;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(device_ip);
        parcel.writeString(device);
    }
}
