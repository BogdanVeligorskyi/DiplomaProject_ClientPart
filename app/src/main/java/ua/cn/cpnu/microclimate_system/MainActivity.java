package ua.cn.cpnu.microclimate_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Measure;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    // options
    public static final String IS_NOTIFICATIONS_ON = "IS_NOTIFICATIONS_ON";
    public static final String IS_UKRAINIAN_ON = "IS_UKRAINIAN_ON";
    public static final String IS_DARK_MODE_ON = "IS_DARK_MODE_ON";
    public static final int OPTIONS_REQUEST_CODE = 1;

    // objects
    public static final String ROOMS_ARRAY = "ROOMS_ARRAY";
    public static final String SENSORS_ARRAY = "SENSORS_ARRAY";

    public static final String POSITION = "POSITION";

    // filenames
    public static final String SETTINGS_FILENAME = "settings.txt";
    public static final String DEVICES_FILENAME = "devices.txt";
    public static final String SENSORS_FILENAME = "sensors.txt";
    public static final String MEASUREMENTS_FILENAME = "measurements.txt";

    private ListView listView;
    private CustomListAdapter cla;
    private String[] devicesArray = {};
    private String[] ipsArray = {};
    private int[] options;
    private Room[] roomsArr = new Room[2];
    private Sensor[] sensorsArr = new Sensor[2];
    private Measurement[] measurementsArr = new Measurement[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        options = new int[3];
        if (savedInstanceState != null) {
            options[0] = savedInstanceState.getInt(MainActivity.IS_NOTIFICATIONS_ON);
            options[1] = savedInstanceState.getInt(MainActivity.IS_UKRAINIAN_ON);
            options[2] = savedInstanceState.getInt(MainActivity.IS_DARK_MODE_ON);
        } else {
            try {
                options = loadOptions();
                roomsArr = loadDevices();
                sensorsArr = loadSensors();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            roomsArr = loadDevices();
            sensorsArr = loadSensors();
        } catch (IOException e) {
            e.printStackTrace();
        }

        findViewById(R.id.scan_devices_button)
                .setOnClickListener(button -> {
                    Context context = getApplicationContext();
                    new Thread(new TestClient(context, 1, -1)).start();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        roomsArr = loadDevices();
                        sensorsArr = loadSensors();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("DEBUG", "Hello from MainThread");
                    cla = new CustomListAdapter(this, devicesArray, ipsArray);
                    listView.setAdapter(cla);

                });

        int[] finalOptions = options;
        findViewById(R.id.options_button)
                .setOnClickListener(button -> {
                    Intent intent = new Intent
                            (this, OptionsActivity.class);
                    intent.putExtra
                            (MainActivity.IS_NOTIFICATIONS_ON, finalOptions[0]);
                    intent.putExtra(MainActivity.IS_UKRAINIAN_ON, finalOptions[1]);
                    intent.putExtra(MainActivity.IS_DARK_MODE_ON, finalOptions[2]);
                    startActivityForResult(intent, OPTIONS_REQUEST_CODE);
                });

        listView = findViewById(R.id.devices_listview);
        listView.setOnItemClickListener(
                (adapterView, view, position, l) -> {
                    Intent intent = new Intent(MainActivity.this,
                            DetailsActivity.class);
                    Log.d("POSITION", ""+position);
                    intent.putExtra(MainActivity.ROOMS_ARRAY, roomsArr);
                    intent.putExtra(MainActivity.SENSORS_ARRAY, sensorsArr);
                    intent.putExtra(MainActivity.POSITION, position);
                    startActivity(intent);
                });
        if (options[2] == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        devicesArray = new String[roomsArr.length];
        ipsArray = new String[roomsArr.length];
        for (int i = 0; i < roomsArr.length; i++) {

            devicesArray[i] = roomsArr[i].getDevice() + " | " + roomsArr[i].getName();
            ipsArray[i] = roomsArr[i].getDeviceIP();
        }

        cla = new CustomListAdapter(this, devicesArray, ipsArray);
        listView.setAdapter(cla);
    }

    // load options from settings.txt file
    private int[] loadOptions() throws IOException {
        InputStream is = getApplicationContext().openFileInput(MainActivity.SETTINGS_FILENAME);
        int[] optionsValues = new int[3];
        BufferedReader reader = new BufferedReader
                (new InputStreamReader(is));
        String s;
        for (int i = 0; i < 3; i++) {
            s = reader.readLine();
            String[] rowParts = s.split("=");
            optionsValues[i] = Integer.parseInt(rowParts[1]);
        }
        return optionsValues;
    }

    // load devices list from devices.txt file
    private Room[] loadDevices() throws IOException {
        Log.d("Here", "hhh");
        InputStream is = getApplicationContext().openFileInput(MainActivity.DEVICES_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        int roomsNum = 0;
        // count rooms number
        while ((s = reader.readLine()) != null) {
            roomsNum++;
        }
        s = "";
        InputStream newIs = getApplicationContext().openFileInput(MainActivity.DEVICES_FILENAME);
        BufferedReader newReader = new BufferedReader(new InputStreamReader(newIs));
        roomsArr = new Room[roomsNum];
        int counter = 0;
        while ((s = newReader.readLine()) != null) {
            String[] roomPair = s.split(",");
            String[] valuesForRoomObject = new String[4];
            for (int k = 0; k < 4; k++) {
                String[] roomDetailData = roomPair[k].split("=");
                valuesForRoomObject[k] = roomDetailData[1];
            }
            roomsArr[counter] = new Room(
                    Integer.parseInt(valuesForRoomObject[0]),
                    valuesForRoomObject[1],
                    valuesForRoomObject[2],
                    valuesForRoomObject[3]);
            counter++;
        }
        Log.d("Rooms detail", ""+roomsArr[0].getName());

        return roomsArr;
    }


    // load devices list from devices.txt file
    private Sensor[] loadSensors() throws IOException {
        InputStream is = getApplicationContext().openFileInput(MainActivity.SENSORS_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        int sensorsNum = 0;
        // count rooms number
        while ((s = reader.readLine()) != null) {
            sensorsNum++;
        }
        s = "";
        InputStream newIs = getApplicationContext().openFileInput(MainActivity.SENSORS_FILENAME);
        BufferedReader newReader = new BufferedReader(new InputStreamReader(newIs));
        sensorsArr = new Sensor[sensorsNum];
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
                            (context.openFileOutput(MainActivity.DEVICES_FILENAME,
                                    Context.MODE_PRIVATE));
            String[] devicesStr = text.split(";");
            for (int i = 0; i < devicesStr.length; i++) {
                outputStreamWriter.write(devicesStr[i] + "\n");
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    // save sensors to sensors.txt file
    public static void saveSensors(Context context, String text) throws IOException {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter
                            (context.openFileOutput(MainActivity.SENSORS_FILENAME,
                                    Context.MODE_PRIVATE));
            String[] sensorsStr = text.split(";");
            for (int i = 0; i < sensorsStr.length; i++) {
                outputStreamWriter.write(sensorsStr[i] + "\n");
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    // returning modified options from other activities
    public void onActivityResult
    (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPTIONS_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            options[0] = data.getIntExtra(IS_NOTIFICATIONS_ON, 0);
            options[1] = data.getIntExtra(IS_UKRAINIAN_ON, 0);
            options[2] = data.getIntExtra(IS_DARK_MODE_ON, 0);
        }
    }

    // saving current state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IS_NOTIFICATIONS_ON, options[0]);
        outState.putInt(IS_UKRAINIAN_ON, options[1]);
        outState.putInt(IS_DARK_MODE_ON, options[2]);
    }

    @Override
    public void onResume(){
        super.onResume();

        // switch theme after closing 'Options' screen if it was changed
        if (options[2] == 1) {
            AppCompatDelegate.setDefaultNightMode
                    (AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode
                    (AppCompatDelegate.MODE_NIGHT_NO);
        }
    }



    // save measurements to measurements.txt file
    public static void saveMeasurements(Context context, String text) throws IOException {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter
                            (context.openFileOutput(MainActivity.MEASUREMENTS_FILENAME,
                                    Context.MODE_PRIVATE));
            String[] measurementsStr = text.split(";");
            for (int i = 0; i < measurementsStr.length; i++) {
                outputStreamWriter.write(measurementsStr[i] + "\n");
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


}