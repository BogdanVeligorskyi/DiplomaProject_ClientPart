package ua.cn.cpnu.microclimate_system;


import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    LineChart mpLineChart;
    Measurement[] measurementsArr;
    String datetime_1;
    String datetime_2;
    String measurement_unit;
    String sensor_name;
    String measure_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        if (savedInstanceState != null) {
            measurementsArr = (Measurement[]) savedInstanceState.getParcelableArray(DetailsActivity.MEASUREMENTS_ARR);
            datetime_1 = savedInstanceState.getString(DetailsActivity.DATETIME_1);
            datetime_2 = savedInstanceState.getString(DetailsActivity.DATETIME_2);
            measurement_unit = savedInstanceState.getString(DetailsActivity.MEASUREMENT_UNIT);
            sensor_name = savedInstanceState.getString(DetailsActivity.SENSOR_NAME);
            measure_name = savedInstanceState.getString(DetailsActivity.MEASURE_NAME);
        } else {
            datetime_1 = getIntent().getStringExtra(DetailsActivity.DATETIME_1);
            datetime_2 = getIntent().getStringExtra(DetailsActivity.DATETIME_2);
            measurement_unit = getIntent().getStringExtra(DetailsActivity.MEASUREMENT_UNIT);
            sensor_name = getIntent().getStringExtra(DetailsActivity.SENSOR_NAME);
            measure_name = getIntent().getStringExtra(DetailsActivity.MEASURE_NAME);
            Parcelable[] measurementsParc = getIntent().getParcelableArrayExtra(DetailsActivity.MEASUREMENTS_ARR);
            measurementsArr = new Measurement[measurementsParc.length];
            for (int i = 0; i < measurementsParc.length; i++) {
                measurementsArr[i] = (Measurement) measurementsParc[i];
            }
        }

        TextView tvLabel = findViewById(R.id.statistics_label);
        tvLabel.setText("Statistics for " + sensor_name);

        TextView tvDate = findViewById(R.id.statistics_date);
        tvDate.setText("Since " + datetime_1 + " to " + datetime_2);

        mpLineChart = (LineChart) findViewById(R.id.statistics_line_chart);

        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(), measure_name + ", " + measurement_unit);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();
        mpLineChart.setDrawGridBackground(false);

    }

    private ArrayList<Entry> dataValues1() {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        for (int j = 0; j < measurementsArr.length; j++) {
            dataVals.add(new Entry(j, (int)measurementsArr[j].getValue()));
        }
        return dataVals;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(DetailsActivity.MEASUREMENTS_ARR, measurementsArr);
        outState.putString(DetailsActivity.DATETIME_1, datetime_1);
        outState.putString(DetailsActivity.DATETIME_2, datetime_2);
        outState.putString(DetailsActivity.MEASURE_NAME, measure_name);
        outState.putString(DetailsActivity.SENSOR_NAME, sensor_name);
        outState.putString(DetailsActivity.MEASUREMENT_UNIT, measurement_unit);
    }

}
