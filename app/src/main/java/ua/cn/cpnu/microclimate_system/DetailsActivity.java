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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private Room[] roomsArr;
    private Sensor[] sensorsArr;
    private Measurement[] measurementsArr;
    private int position = 0;
    private int current_sensor_id;
    private String datetime_1;
    private String datetime_2;

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

        TextView tvDevice = findViewById(R.id.details_device_name);
        tvDevice.setText(roomsArr[position].getDevice() + " | " + roomsArr[position].getName());

        TextView tvIP = findViewById(R.id.details_device_ip);
        tvIP.setText(roomsArr[position].getDeviceIP());

        TextView tvResults = findViewById(R.id.textview_measurements_results);

        EditText edText_1 = findViewById(R.id.edittext_first_bound);
        EditText edText_2 = findViewById(R.id.edittext_second_bound);


        ArrayList <String> arrayList = new ArrayList<String>();
        for (int i = 0; i < sensorsArr.length; i++) {
            if (sensorsArr[i].getRoomId() == roomsArr[position].getId()) {
                arrayList.add("id="+sensorsArr[i].getId() + ", " + sensorsArr[i].getName() + ", "
                        + sensorsArr[i].getMeasure());
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

        findViewById(R.id.find_measurements_button)
                .setOnClickListener(button -> {
                    Context context = getApplicationContext();
                    datetime_1 = edText_1.getText().toString();
                    datetime_2 = edText_2.getText().toString();
                    Log.d("current_sensor_id=", ""+current_sensor_id);
                    new Thread(new TestClient(context, 2, current_sensor_id, datetime_1, datetime_2)).start();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        measurementsArr = loadMeasurements();
                        butStat.setEnabled(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String text = "";
                    Log.d("MEAS", ""+measurementsArr[0].getSensorId());
                    String unit_measurement = sensorsArr[measurementsArr[0].getSensorId()-1].getMeasureUnit();
                    for (Measurement measurement : measurementsArr) {
                        text += "datetime=" + measurement.getDateime() + ", ";
                        text += "value=" + measurement.getValue() + unit_measurement + "\n";
                    }
                    tvResults.setText(text);

                });


    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(MainActivity.ROOMS_ARRAY, roomsArr);
        outState.putParcelableArray(MainActivity.SENSORS_ARRAY, sensorsArr);

    }

    // load measurements from measurements.txt file
    private Measurement[] loadMeasurements() throws IOException {
        InputStream is = getApplicationContext().openFileInput(MainActivity.MEASUREMENTS_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        int measurementsNum = 0;
        // count rooms number
        while ((s = reader.readLine()) != null) {
            measurementsNum++;
        }
        s = "";
        InputStream newIs = getApplicationContext().openFileInput(MainActivity.MEASUREMENTS_FILENAME);
        BufferedReader newReader = new BufferedReader(new InputStreamReader(newIs));
        measurementsArr = new Measurement[measurementsNum];
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
}
