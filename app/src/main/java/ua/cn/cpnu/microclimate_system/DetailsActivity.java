package ua.cn.cpnu.microclimate_system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;

// Details Activity where you can make a request for measurements
public class DetailsActivity extends AppCompatActivity {

    // objects and variables
    private Room[] roomsArr;
    private Sensor[] sensorsArr;
    private Measurement[] measurementsArr;
    private final int[] options = new int[3];
    private int position = 0;
    private int current_sensor_id;
    private String datetime_1;
    private String datetime_2;
    private String[] sensorsStr;
    private TextView tvIP;
    private TextView tvResults;
    private EditText edText_1;
    private EditText edText_2;
    private ArrayAdapter ad;
    private Button butStat;

    // strings
    public static final String SENSOR_NAME = "SENSOR_NAME";
    public static final String DATETIME_1 = "DATETIME_1";
    public static final String DATETIME_2 = "DATETIME_2";
    public static final String MEASUREMENTS_ARR = "MEASUREMENTS_ARR";
    public static final String MEASURE_NAME = "MEASURE_NAME";
    public static final String MEASUREMENT_UNIT = "MEASUREMENT_UNIT";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState != null) {
            roomsArr = (Room[]) savedInstanceState.getParcelableArray(MainActivity.ROOMS_ARRAY);
            sensorsArr = (Sensor[]) savedInstanceState.getParcelableArray(MainActivity.SENSORS_ARRAY);
            position = savedInstanceState.getInt(MainActivity.POSITION);
            options[0] = savedInstanceState.getInt(MainActivity.IS_NOTIFICATIONS_ON);
            options[1] = savedInstanceState.getInt(MainActivity.IS_UKRAINIAN_ON);
            options[2] = savedInstanceState.getInt(MainActivity.IS_DARK_MODE_ON);
        } else {
            Parcelable[] roomsArrParc = getIntent().getParcelableArrayExtra(MainActivity.ROOMS_ARRAY);
            Parcelable[] sensorsArrParc = getIntent().getParcelableArrayExtra(MainActivity.SENSORS_ARRAY);
            position = getIntent().getIntExtra(MainActivity.POSITION, 0);
            options[0] = getIntent().getIntExtra(MainActivity.IS_NOTIFICATIONS_ON, 0);
            options[1] = getIntent().getIntExtra(MainActivity.IS_UKRAINIAN_ON, 0);
            options[2] = getIntent().getIntExtra(MainActivity.IS_DARK_MODE_ON, 0);
            roomsArr = new Room[roomsArrParc.length];
            sensorsArr = new Sensor[sensorsArrParc.length];
            for (int i = 0; i < roomsArr.length; i++) {
                roomsArr[i] = (Room) roomsArrParc[i];
            }
            for (int j = 0; j < sensorsArr.length; j++) {
                sensorsArr[j] = (Sensor) sensorsArrParc[j];
            }
        }

        TextView tvDevice = findViewById(R.id.details_device_name);
        tvDevice.setText(roomsArr[position].getDevice() + " | " + roomsArr[position].getName());

        tvIP = findViewById(R.id.details_device_ip);
        tvIP.setText(roomsArr[position].getDeviceIP());

        tvResults = findViewById(R.id.textview_measurements_results);
        if (options[2] == 1) {
            tvResults.setBackgroundColor(Color.DKGRAY);
        }

        edText_1 = findViewById(R.id.edittext_first_bound);
        edText_2 = findViewById(R.id.edittext_second_bound);

        setSpinnerList();

        Spinner spinSensors = findViewById(R.id.spinner_select_sensor);
        spinSensors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                if (position >= 0) {
                    String[] strRow = sensorsStr[position].split(",");
                    String[] strRowId = strRow[0].split("=");
                    current_sensor_id = Integer.parseInt(strRowId[1]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub
            }
        });
        spinSensors.setAdapter(ad);

        butStat = findViewById(R.id.statistics_button);
        butStat.setEnabled(false);

        findViewById(R.id.find_measurements_button).setOnClickListener(button -> {
            Context context = getApplicationContext();
            datetime_1 = edText_1.getText().toString();
            datetime_2 = edText_2.getText().toString();
            Log.d("current_sensor_id=", ""+current_sensor_id);
            new Thread(new AppClient(context, AppClient.ACTION_GET_MEASUREMENTS,
                    current_sensor_id, datetime_1, datetime_2)).start();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*try {
                FileProcessing.saveMeasurements(context, "");
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            showToast(context);
            findMeasurements(context);

        });

        findViewById(R.id.actual_measurements_button).setOnClickListener(button -> {
            butStat.setEnabled(false);
            Context context = getApplicationContext();
            String ip = tvIP.getText().toString();
            Log.d("IP", ip);
            int id = 0;

            // determine id by known IP-address
            for (Room room : roomsArr) {
                if (room.getDeviceIP() == ip) {
                    id = room.getId();
                    break;
                }
            }

            // count number of sensors in specific room
            int sensorsNum = 0;
            for (Sensor sensor : sensorsArr) {
                if (sensor.getRoomId() == id) {
                    sensorsNum++;
                }
            }

            Log.d("sensors", ""+sensorsNum);
            new Thread(new AppClient(context, AppClient.ACTION_GET_ACTUAL, id, sensorsNum)).start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            showToast(context);
            findActualMeasurements(context);

        });

        findViewById(R.id.statistics_button).setOnClickListener(button -> {
            Intent intent = new Intent
                    (this, StatisticsActivity.class);
            intent.putExtra(SENSOR_NAME, getSensorNameBySensorId(measurementsArr[0].getSensorId()));
            intent.putExtra(DATETIME_1, datetime_1);
            intent.putExtra(DATETIME_2, datetime_2);
            intent.putExtra(MEASUREMENTS_ARR, measurementsArr);
            intent.putExtra(MEASURE_NAME, getMeasureBySensorId(measurementsArr[0].getSensorId()));
            intent.putExtra(MEASUREMENT_UNIT, getMeasurementUnitBySensorId(measurementsArr[0].getSensorId()));
            intent.putExtra(MainActivity.IS_NOTIFICATIONS_ON, options[0]);
            intent.putExtra(MainActivity.IS_UKRAINIAN_ON, options[1]);
            intent.putExtra(MainActivity.IS_DARK_MODE_ON, options[2]);
            startActivity(intent);

        });

    }

    // set spinner (add all sensors of room)
    private void setSpinnerList() {
        ArrayList <String> arrayList = new ArrayList<>();
        for (Sensor sensor : sensorsArr) {
            if (sensor.getRoomId() == roomsArr[position].getId()) {
                arrayList.add("id=" + sensor.getId() + ", " + sensor.getName() + ", "
                        + sensor.getMeasure());
            }
        }
        sensorsStr = new String[arrayList.size()];
        for (int j = 0; j < sensorsStr.length; j++) {
            sensorsStr[j] = arrayList.get(j);
        }

        String[] strRow = sensorsStr[0].split(",");
        String[] strRowId = strRow[0].split("=");
        current_sensor_id = Integer.parseInt(strRowId[1]);

        ad = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                sensorsStr);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    // find measurements for specific time interval
    private void findMeasurements(Context context) {
        try {
            measurementsArr = FileProcessing.loadMeasurements(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (measurementsArr == null) {
            Toast.makeText(context,"Measurements haven`t been found!", Toast.LENGTH_LONG).show();
        } else {
            String text = "";
            String unit_measurement = getMeasurementUnitBySensorId(measurementsArr[0].getSensorId());
            for (Measurement measurement : measurementsArr) {
                text += measurement.getDateime() + ", \t";
                text += measurement.getValue() + unit_measurement + "\n";
            }
            tvResults.setText(text);
            butStat.setEnabled(true);
        }
    }

    // find actual measurements
    private void findActualMeasurements(Context context) {
        try {
            measurementsArr = FileProcessing.loadActualMeasurements(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (measurementsArr == null) {
            Toast.makeText(context,"Measurements haven`t been found!", Toast.LENGTH_LONG).show();
        } else {
            String text = "";
            boolean is_norm = true;
            for (Measurement measurement : measurementsArr) {
                String measurement_unit = getMeasurementUnitBySensorId(measurement.getSensorId());
                String measure = getMeasureBySensorId(measurement.getSensorId());
                if (measure.contains("CO") && measurement.getValue() > 55.0) {
                    is_norm = false;
                }
                text += "Sensor id: " + measurement.getSensorId() + ", \t";
                text += measurement.getDateime() + ", \t";
                text += measurement.getValue() + measurement_unit + "\n";
            }
            tvResults.setText(text);
            Log.d("OPTIONS", ""+options[0]);

            // play sound for some seconds
            if (!is_norm && options[0] == 1) {
                for (int i = 0; i < 40; i++) {
                    MediaPlayer music = MediaPlayer.create(DetailsActivity.this, R.raw.notification);
                    music.start();
                }
                AlertDialog.Builder dialog =
                        new AlertDialog.Builder(this);
                dialog.setCancelable(false);
                dialog.setIcon(R.mipmap.ic_launcher_round);
                dialog.setTitle("Warning message");
                dialog.setMessage
                            ("CO concentration is very high!");
                dialog.setPositiveButton("Ok", ((dialogInterface, i) -> finish()));
                dialog.create();
                dialog.show();
            }
        }
    }

    // show operation result
    private void showToast(Context context) {
        if (AppClient.IS_SUCCESS) {
            Toast.makeText(context,"Data were successfully downloaded!",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context,"Server doesn`t respond (or incorrect input), so earlier saved data were downloaded!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(MainActivity.ROOMS_ARRAY, roomsArr);
        outState.putParcelableArray(MainActivity.SENSORS_ARRAY, sensorsArr);
        outState.putInt(MainActivity.IS_NOTIFICATIONS_ON, options[0]);
        outState.putInt(MainActivity.IS_UKRAINIAN_ON, options[1]);
        outState.putInt(MainActivity.IS_DARK_MODE_ON, options[2]);
        outState.putInt(MainActivity.POSITION, position);
    }

    private String getMeasurementUnitBySensorId(int sensor_id) {
        for (Sensor sensor : sensorsArr) {
            if (sensor.getId() == sensor_id) {
                return sensor.getMeasureUnit();
            }
        }
        return "";
    }

    private String getMeasureBySensorId(int sensor_id) {
        for (Sensor sensor : sensorsArr) {
            if (sensor.getId() == sensor_id) {
                return sensor.getMeasure();
            }
        }
        return "";
    }

    private String getSensorNameBySensorId(int sensor_id) {
        for (Sensor sensor : sensorsArr) {
            if (sensor.getId() == sensor_id) {
                return sensor.getName();
            }
        }
        return "";
    }
}
