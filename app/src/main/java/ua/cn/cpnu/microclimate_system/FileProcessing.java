package ua.cn.cpnu.microclimate_system;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileProcessing {

    // filenames
    public static final String SETTINGS_FILENAME = "settings.txt";
    public static final String DEVICES_FILENAME = "devices.txt";
    public static final String SENSORS_FILENAME = "sensors.txt";
    public static final String MEASUREMENTS_FILENAME = "measurements.txt";

    // load devices list from devices.txt file
    public static Room[] loadDevices(Context context) throws IOException {
        InputStream is = context.openFileInput(DEVICES_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        int roomsNum = 0;
        // count rooms number
        while ((s = reader.readLine()) != null) {
            Log.d("s.length", ""+s.length());
            if (s.length() == 0) {
                return null;
            }
            roomsNum++;
        }
        s = "";
        InputStream newIs = context.openFileInput(DEVICES_FILENAME);
        BufferedReader newReader = new BufferedReader(new InputStreamReader(newIs));
        Room[] roomsArr = new Room[roomsNum];
        int counter = 0;
        while ((s = newReader.readLine()) != null) {
            String[] roomPair = s.split(",");
            String[] valuesForRoomObject = new String[4];
            for (int k = 0; k < roomPair.length; k++) {
                String[] roomDetailData = roomPair[k].split("=");
                valuesForRoomObject[k] = roomDetailData[1];
            }
            if (roomPair.length > 0) {
                roomsArr[counter] = new Room(
                        Integer.parseInt(valuesForRoomObject[0]),
                        valuesForRoomObject[1],
                        valuesForRoomObject[2],
                        valuesForRoomObject[3]);
                counter++;
            }
        }
        return roomsArr;
    }

    // load sensors list from sensors.txt file
    public static Sensor[] loadSensors(Context context) throws IOException {
        InputStream is = context.openFileInput(SENSORS_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        int sensorsNum = 0;
        // count rooms number
        while ((s = reader.readLine()) != null) {
            if (s.length() == 0) {
                return null;
            }
            sensorsNum++;
        }
        s = "";
        InputStream newIs = context.openFileInput(SENSORS_FILENAME);
        BufferedReader newReader = new BufferedReader(new InputStreamReader(newIs));
        Sensor[] sensorsArr = new Sensor[sensorsNum];
        int counter = 0;
        while ((s = newReader.readLine()) != null) {
            String[] sensorPair = s.split(",");
            String[] valuesForSensorObject = new String[5];
            for (int k = 0; k < 5; k++) {
                String[] sensorDetailData = sensorPair[k].split("=");
                valuesForSensorObject[k] = sensorDetailData[1];
            }
            sensorsArr[counter] = new Sensor(
                    Integer.parseInt(valuesForSensorObject[0]),
                    Integer.parseInt(valuesForSensorObject[1]),
                    valuesForSensorObject[2],
                    valuesForSensorObject[3],
                    valuesForSensorObject[4]);
            counter++;
        }
        Log.d("Sensor detail", ""+sensorsArr[0].getName());

        return sensorsArr;
    }

    // save devices to devices.txt file
    public static void saveDevices(Context context, String text) throws IOException {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter
                            (context.openFileOutput(DEVICES_FILENAME,
                                    Context.MODE_PRIVATE));
            String[] devicesStr = text.split(";");
            for (String s : devicesStr) {
                outputStreamWriter.write(s + "\n");
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }

    // save sensors to sensors.txt file
    public static void saveSensors(Context context, String text) throws IOException {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter
                            (context.openFileOutput(SENSORS_FILENAME,
                                    Context.MODE_PRIVATE));
            String[] sensorsStr = text.split(";");
            for (String s : sensorsStr) {
                outputStreamWriter.write(s + "\n");
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }

    // load measurements from measurements.txt file
    public static Measurement[] loadMeasurements(Context context) throws IOException {
        InputStream is = context.openFileInput(MEASUREMENTS_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        int measurementsNum = 0;
        // count rooms number
        while ((s = reader.readLine()) != null) {
            if (s.length() == 0) {
                return null;
            }
            measurementsNum++;
        }
        s = "";
        InputStream newIs = context.openFileInput(MEASUREMENTS_FILENAME);
        BufferedReader newReader = new BufferedReader(new InputStreamReader(newIs));
        Measurement[] measurementsArr = new Measurement[measurementsNum];
        int counter = 0;
        while ((s = newReader.readLine()) != null) {
            String[] measurementPair = s.split(",");
            String[] valuesForMeasuremnetObject = new String[4];
            for (int k = 0; k < 4; k++) {
                String[] measurementDetailData = measurementPair[k].split("=");
                valuesForMeasuremnetObject[k] = measurementDetailData[1];
            }
            measurementsArr[counter] = new Measurement(
                    Integer.parseInt(valuesForMeasuremnetObject[0]),
                    Integer.parseInt(valuesForMeasuremnetObject[1]),
                    Float.parseFloat(valuesForMeasuremnetObject[2]),
                    valuesForMeasuremnetObject[3]);
            counter++;
        }
        Log.d("Measurement detail", ""+measurementsArr[0].getDateime());

        return measurementsArr;
    }

    // save options to settings.txt file
    public static void saveOptions(Context context, int[] options) throws IOException {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter
                            (context.openFileOutput(SETTINGS_FILENAME,
                                    Context.MODE_PRIVATE));
            outputStreamWriter.write("notifications=" + options[0] + "\n"
                    + "language=" + options[1] + "\n"
                    + "theme=" + options[2]);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }

    // save measurements to measurements.txt file
    public static void saveMeasurements(Context context, String text) throws IOException {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter
                            (context.openFileOutput(MEASUREMENTS_FILENAME,
                                    Context.MODE_PRIVATE));
            String[] measurementsStr = text.split(";");
            for (String s : measurementsStr) {
                outputStreamWriter.write(s + "\n");
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }

    // load options from settings.txt file
    public static int[] loadOptions(Context context) throws IOException {
        InputStream is = context.openFileInput(SETTINGS_FILENAME);
        int[] optionsValues = new int[3];
        BufferedReader reader = new BufferedReader
                (new InputStreamReader(is));
        String s;
        for (int i = 0; i < 3; i++) {
            s = reader.readLine();
            if (s.length() == 0) {
                return null;
            }
            String[] rowParts = s.split("=");
            optionsValues[i] = Integer.parseInt(rowParts[1]);
        }
        return optionsValues;
    }
}
