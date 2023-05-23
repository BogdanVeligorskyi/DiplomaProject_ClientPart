package ua.cn.cpnu.microclimate_system;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private Room[] roomsArr;
    private Sensor[] sensorsArr;
    private Measurement[] measurementsArr;
    private int position = 0;
    private int current_sensor_id;
    private String datetime_1;
    private String datetime_2;
    private TextView tvDevice;
    private TextView tvIP;
    private TextView tvResults;
    private EditText edText_1;
    private EditText edText_2;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState != null) {
            roomsArr = (Room[]) savedInstanceState.getParcelableArray(MainActivity.ROOMS_ARRAY);
            sensorsArr = (Sensor[]) savedInstanceState.getParcelableArray(MainActivity.SENSORS_ARRAY);
        } else {
            Parcelable[] roomsArrParc = getIntent().getParcelableArrayExtra(MainActivity.ROOMS_ARRAY);
            Parcelable[] sensorsArrParc = getIntent().getParcelableArrayExtra(MainActivity.SENSORS_ARRAY);
            position = getIntent().getIntExtra(MainActivity.POSITION, 0);
            roomsArr = new Room[roomsArrParc.length];
            sensorsArr = new Sensor[sensorsArrParc.length];
            for (int i = 0; i < roomsArr.length; i++) {
                roomsArr[i] = (Room) roomsArrParc[i];
            }
            for (int j = 0; j < sensorsArr.length; j++) {
                sensorsArr[j] = (Sensor) sensorsArrParc[j];
            }
        }

        tvDevice = findViewById(R.id.details_device_name);
        tvDevice.setText(roomsArr[position].getDevice() + " | " + roomsArr[position].getName());

        tvIP = findViewById(R.id.details_device_ip);
        tvIP.setText(roomsArr[position].getDeviceIP());

        tvResults = findViewById(R.id.textview_measurements_results);

        edText_1 = findViewById(R.id.edittext_first_bound);
        edText_2 = findViewById(R.id.edittext_second_bound);


        ArrayList <String> arrayList = new ArrayList<>();
        for (Sensor sensor : sensorsArr) {
            if (sensor.getRoomId() == roomsArr[position].getId()) {
                arrayList.add("id=" + sensor.getId() + ", " + sensor.getName() + ", "
                        + sensor.getMeasure());
            }
        }
        String[] sensorsStr = new String[arrayList.size()];
        for (int j = 0; j < sensorsStr.length; j++) {
            sensorsStr[j] = arrayList.get(j);
        }
        String[] strRow = sensorsStr[0].split(",");
        String[] strRowId = strRow[0].split("=");
        current_sensor_id = Integer.parseInt(strRowId[1]);
        ArrayAdapter ad = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                sensorsStr);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        Button butStat = findViewById(R.id.statistics_button);
        butStat.setEnabled(false);

        findViewById(R.id.find_measurements_button).setOnClickListener(button -> {
                    Context context = getApplicationContext();
                    datetime_1 = edText_1.getText().toString();
                    datetime_2 = edText_2.getText().toString();
                    Log.d("current_sensor_id=", ""+current_sensor_id);
                    new Thread(new AppClient(context, AppClient.ACTION_GET_MEASUREMENTS,
                            current_sensor_id, datetime_1, datetime_2)).start();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (AppClient.IS_SUCCESS) {
                        Toast.makeText(context,"Data were successfully downloaded!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context,"Server doesn`t respond (or incorrect input), so earlier saved data were downloaded!", Toast.LENGTH_LONG).show();
                    }
                    /*try {
                        FileProcessing.saveMeasurements(context, "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    try {
                        measurementsArr = FileProcessing.loadMeasurements(getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (measurementsArr == null) {
                        Toast.makeText(context,"Measurements haven`t been found!", Toast.LENGTH_LONG).show();
                    } else {
                        String text = "";
                        Log.d("MEAS", ""+measurementsArr[0].getSensorId());
                        String unit_measurement = sensorsArr[measurementsArr[0].getSensorId()-1].getMeasureUnit();
                        for (Measurement measurement : measurementsArr) {
                            text += "Datetime: " + measurement.getDateime() + ", ";
                            text += "Value: " + measurement.getValue() + unit_measurement + "\n";
                        }
                        tvResults.setText(text);
                        butStat.setEnabled(true);
                    }

                });

        findViewById(R.id.actual_measurements_button).setOnClickListener(button -> {
            Context context = getApplicationContext();
            String ip = tvIP.getText().toString();
            Log.d("IP", ip);
            int id = 0;
            for (int i = 0; i < roomsArr.length; i++) {
                if (roomsArr[i].getDeviceIP() == ip) {
                    id = roomsArr[i].getId();
                    break;
                }
            }
            int sensorsNum = 0;
            for (int j = 0; j < sensorsArr.length; j++) {
                if (sensorsArr[j].getRoomId() == id) {
                    sensorsNum++;
                }
            }
            Log.d("sensors", ""+sensorsNum);
            new Thread(new AppClient(context, AppClient.ACTION_GET_ACTUAL, id, sensorsNum)).start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                measurementsArr = FileProcessing.loadMeasurements(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (AppClient.IS_SUCCESS) {
                Toast.makeText(context,"Data were successfully downloaded!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context,"Server doesn`t respond (or incorrect input), so earlier saved data were downloaded!", Toast.LENGTH_LONG).show();
            }
            if (measurementsArr == null) {
                Toast.makeText(context,"Measurements haven`t been found!", Toast.LENGTH_LONG).show();
            } else {

                String text = "";
                Log.d("MEAS", ""+measurementsArr[0].getSensorId());
                String unit_measurement = sensorsArr[measurementsArr[0].getSensorId()-1].getMeasureUnit();
                for (Measurement measurement : measurementsArr) {
                    text += "sId : " + measurement.getSensorId() + ", ";
                    text += "Datetime: " + measurement.getDateime() + ", ";
                    text += "Value: " + measurement.getValue() + unit_measurement + "\n";
                }
                tvResults.setText(text);
                butStat.setEnabled(true);
            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(MainActivity.ROOMS_ARRAY, roomsArr);
        outState.putParcelableArray(MainActivity.SENSORS_ARRAY, sensorsArr);
    }
}
