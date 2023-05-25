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

// Statistics activity where you can see chart of measurements
public class StatisticsActivity extends AppCompatActivity {

    // objects and variables
    private Measurement[] measurementsArr;
    private final int[] options = new int[3];
    private String datetime_1;
    private String datetime_2;
    private String measurement_unit;
    private String sensor_name;
    private String measure_name;

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
            options[0] = savedInstanceState.getInt(MainActivity.IS_NOTIFICATIONS_ON);
            options[1] = savedInstanceState.getInt(MainActivity.IS_UKRAINIAN_ON);
            options[2] = savedInstanceState.getInt(MainActivity.IS_DARK_MODE_ON);
        } else {
            datetime_1 = getIntent().getStringExtra(DetailsActivity.DATETIME_1);
            datetime_2 = getIntent().getStringExtra(DetailsActivity.DATETIME_2);
            measurement_unit = getIntent().getStringExtra(DetailsActivity.MEASUREMENT_UNIT);
            sensor_name = getIntent().getStringExtra(DetailsActivity.SENSOR_NAME);
            measure_name = getIntent().getStringExtra(DetailsActivity.MEASURE_NAME);
            options[0] = getIntent().getIntExtra(MainActivity.IS_NOTIFICATIONS_ON, 0);
            options[1] = getIntent().getIntExtra(MainActivity.IS_UKRAINIAN_ON, 0);
            options[2] = getIntent().getIntExtra(MainActivity.IS_DARK_MODE_ON, 0);
            Parcelable[] measurementsParc = getIntent().getParcelableArrayExtra(DetailsActivity.MEASUREMENTS_ARR);
            measurementsArr = new Measurement[measurementsParc.length];
            for (int i = 0; i < measurementsParc.length; i++) {
                measurementsArr[i] = (Measurement) measurementsParc[i];
            }
        }

        TextView tvAverage = findViewById(R.id.arithmetic_mean_value);
        TextView tvDispersion = findViewById(R.id.dispersion_value);
        TextView tvDeviation = findViewById(R.id.standard_deviation_value);
        TextView tvMax = findViewById(R.id.maximum_value);
        TextView tvMin = findViewById(R.id.minimum_value);

        // find statistical parameters if measurements are available
        if (measurementsArr != null) {
            double avg = findArithmeticMean();
            double disp = findDispersion(avg);
            double deviation = findDeviation(disp);
            int max = findMaxValue();
            int min = findMinValue();
            tvAverage.setText(String.valueOf(avg) + measurement_unit);
            tvDispersion.setText(String.valueOf(disp) + measurement_unit);
            tvDeviation.setText(String.valueOf(deviation) + measurement_unit);
            tvMax.setText(String.valueOf(max) + measurement_unit);
            tvMin.setText(String.valueOf(min) + measurement_unit);
        }

        TextView tvLabel = findViewById(R.id.statistics_label);
        tvLabel.setText("Statistics for " + sensor_name);

        TextView tvDate = findViewById(R.id.statistics_date);
        tvDate.setText("Since " + datetime_1 + " to " + datetime_2);

        LineChart mpLineChart = findViewById(R.id.statistics_line_chart);

        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(), measure_name + ", " + measurement_unit);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();
        mpLineChart.setDrawGridBackground(false);

        // change text color if dark theme was enabled
        if (options[2] == 1) {
            mpLineChart.getAxisLeft().setTextColor(getResources().getColor(R.color.white));
            mpLineChart.getXAxis().setTextColor(getResources().getColor(R.color.white));
            mpLineChart.getLegend().setTextColor(getResources().getColor(R.color.white));
            mpLineChart.getDescription().setTextColor(getResources().getColor(R.color.white));
        }

    }

    // initialize chart with values
    private ArrayList<Entry> dataValues1() {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        for (int j = 0; j < measurementsArr.length; j++) {
            dataVals.add(new Entry(j, (int)measurementsArr[j].getValue()));
        }
        return dataVals;
    }

    // find ARITHMETIC AVERAGE (MEAN) value
    private double findArithmeticMean() {
        double sum = 0.0;
        for (Measurement measurement : measurementsArr) {
            sum += measurement.getValue();
        }
        return sum / measurementsArr.length;
    }

    // find DISPERSION value
    private double findDispersion(double avg) {
        double sum_disp = 0.0;
        for (Measurement measurement : measurementsArr) {
            sum_disp += ( (measurement.getValue() - avg) * (measurement.getValue() - avg) );
        }
        return sum_disp / measurementsArr.length;
    }

    // find STANDARD DEVIATION value
    private double findDeviation(double disp) {
        return Math.pow(disp, 0.5);
    }

    // find MAXIMUM value
    private int findMaxValue() {
        int max = (int) measurementsArr[0].getValue();
        for (Measurement measurement : measurementsArr) {
            if ((int) measurement.getValue() > max) {
                max = (int) measurement.getValue();
            }
        }
        return max;
    }

    // find MINIMUM value
    private int findMinValue() {
        int min = (int) measurementsArr[0].getValue();
        for (Measurement measurement : measurementsArr) {
            if ((int) measurement.getValue() < min) {
                min = (int) measurement.getValue();
            }
        }
        return min;
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
        outState.putInt(MainActivity.IS_NOTIFICATIONS_ON, options[0]);
        outState.putInt(MainActivity.IS_UKRAINIAN_ON, options[1]);
        outState.putInt(MainActivity.IS_DARK_MODE_ON, options[2]);
    }

}
