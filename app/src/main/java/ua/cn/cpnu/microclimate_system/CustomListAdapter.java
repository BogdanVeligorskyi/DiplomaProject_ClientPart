package ua.cn.cpnu.microclimate_system;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter {

    // to reference the Activity
    private final Activity context;

    // to store the list of device names
    private final String[] devicesArray;

    // to store the list of IPs
    private final String[] ipsArray;

    public CustomListAdapter(Activity context, String[] deviceArrayParam, String[] ipArrayParam){
        super(context, R.layout.listview_row, deviceArrayParam);
        this.context = context;
        this.devicesArray = deviceArrayParam;
        this.ipsArray = ipArrayParam;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"})
        View rowView=inflater.inflate(R.layout.listview_row, null,true);

        TextView deviceTextField = (TextView) rowView.findViewById(R.id.device_name);
        TextView ipTextField = (TextView) rowView.findViewById(R.id.device_ip);

        deviceTextField.setText(devicesArray[position]);
        ipTextField.setText(ipsArray[position]);

        return rowView;

    }
}
