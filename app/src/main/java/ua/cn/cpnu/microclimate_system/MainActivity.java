package ua.cn.cpnu.microclimate_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

// Main Activity which looks like main menu
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

    private ListView listView;
    private String[] devicesArray = {};
    private String[] ipsArray = {};
    private int[] options = null;
    private Room[] roomsArr = null;
    private Sensor[] sensorsArr = null;
    ImageButton butOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*try {
            FileProcessing.saveDevices(getApplicationContext(), "");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        if (savedInstanceState != null) {
            options = new int[3];
            options[0] = savedInstanceState.getInt(MainActivity.IS_NOTIFICATIONS_ON);
            options[1] = savedInstanceState.getInt(MainActivity.IS_UKRAINIAN_ON);
            options[2] = savedInstanceState.getInt(MainActivity.IS_DARK_MODE_ON);
        } else {
            try {
                if (options == null) {
                    options = new int[3];
                }
                options = FileProcessing.loadOptions(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        loadData();

        butOptions = findViewById(R.id.options_button);

        // 'Options' button
        findViewById(R.id.options_button)
                .setOnClickListener(button -> {
                    Intent intent = new Intent
                            (this, OptionsActivity.class);
                    intent.putExtra
                            (MainActivity.IS_NOTIFICATIONS_ON, options[0]);
                    intent.putExtra(MainActivity.IS_UKRAINIAN_ON, options[1]);
                    intent.putExtra(MainActivity.IS_DARK_MODE_ON, options[2]);
                    startActivityForResult(intent, OPTIONS_REQUEST_CODE);
                });

        // 'Scan devices' button
        findViewById(R.id.scan_devices_button)
                .setOnClickListener(button -> {
                    Context context = getApplicationContext();
                    new Thread(new AppClient(context, AppClient.ACTION_GET_DEVICES)).start();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (AppClient.IS_SUCCESS) {
                        Toast.makeText(context,"Data were successfully updated!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context,"Server doesn`t respond, so earlier saved data were downloaded!", Toast.LENGTH_LONG).show();
                    }

                    loadData();

                    Log.d("DEBUG", "Hello from MainThread");
                    initListView();

                });

        listView = findViewById(R.id.devices_listview);
        listView.setOnItemClickListener(
                (adapterView, view, position, l) -> {
                    if (!checkForSensors(ipsArray, position)) {
                        Toast.makeText(getApplicationContext(), "This device has no sensors!", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(MainActivity.this,
                                DetailsActivity.class);
                        Log.d("POSITION", "" + position);
                        intent.putExtra(MainActivity.ROOMS_ARRAY, roomsArr);
                        intent.putExtra(MainActivity.SENSORS_ARRAY, sensorsArr);
                        intent.putExtra(MainActivity.POSITION, position);
                        intent.putExtra
                                (MainActivity.IS_NOTIFICATIONS_ON, options[0]);
                        intent.putExtra(MainActivity.IS_UKRAINIAN_ON, options[1]);
                        intent.putExtra(MainActivity.IS_DARK_MODE_ON, options[2]);
                        startActivity(intent);
                    }
                });

        checkTheme();
        initListView();
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
        checkTheme();
    }

    private void checkTheme() {
        // switch theme after closing 'Options' screen if it was changed
        if (options[2] == 1) {
            AppCompatDelegate.setDefaultNightMode
                    (AppCompatDelegate.MODE_NIGHT_YES);
            butOptions.setImageResource(R.drawable.icon_options_dark);

        } else {
            AppCompatDelegate.setDefaultNightMode
                    (AppCompatDelegate.MODE_NIGHT_NO);
            butOptions.setImageResource(R.drawable.icon_options);

        }
    }

    private void initListView() {
        if (roomsArr != null) {
            devicesArray = new String[roomsArr.length];
            ipsArray = new String[roomsArr.length];
            for (int i = 0; i < roomsArr.length; i++) {
                devicesArray[i] = roomsArr[i].getDevice() + " | " + roomsArr[i].getName();
                ipsArray[i] = roomsArr[i].getDeviceIP();
            }
        }
        CustomListAdapter cla = new CustomListAdapter(this, devicesArray, ipsArray);
        listView.setAdapter(cla);
    }

    private void loadData() {
        try {
            roomsArr = FileProcessing.loadDevices(getApplicationContext());
            sensorsArr = FileProcessing.loadSensors(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (roomsArr == null) {
            Toast.makeText(getApplicationContext(), "Devices haven`t been found!", Toast.LENGTH_LONG).show();
        }
        if (sensorsArr == null) {
            Toast.makeText(getApplicationContext(), "Sensors haven`t been found!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkForSensors(String[] ipsArray, int position) {
        String ip = ipsArray[position];
        int id = 0;
        for (Room room : roomsArr) {
            if (ip == room.getDeviceIP()) {
                id = room.getId();
                break;
            }
        }
        for (Sensor sensor : sensorsArr) {
            if (id == sensor.getRoomId()) {
                return true;
            }
        }
        return false;
    }

}