package ua.cn.cpnu.microclimate_system;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient implements Runnable {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Context context;
    private int action;
    private int sensor_id;
    private String datetime_1;
    private String datetime_2;

    public TestClient(Context context, int action, int sensor_id) {
        this.context = context;
        this.action = action;
        this.sensor_id = sensor_id;
        this.datetime_1 = null;
        this.datetime_2 = null;
    }

    public TestClient(Context context, int action, int sensor_id, String datetime_1, String datetime_2) {
        this.context = context;
        this.action = action;
        this.sensor_id = sensor_id;
        this.datetime_1 = datetime_1;
        this.datetime_2 = datetime_2;
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    @Override
    public void run() {
        if (action == 1) {
            try {
                startConnection("192.168.0.115", 50011);
                String response = sendMessage("GET_DEVICES");
                Log.d("RECEIVED devices", response);
                response = response.substring(response.indexOf(":") + 1);
                MainActivity.saveDevices(context, response);

                response = sendMessage("GET_SENSORS");
                Log.d("RECEIVED sensors", response);
                response = response.substring(response.indexOf(":") + 1);
                MainActivity.saveSensors(context, response);
                Toast.makeText(context, "Data were updated successfully!", Toast.LENGTH_LONG).show();
                stopConnection();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (action == 2) {
            try {
                startConnection("192.168.0.115", 50011);
                String response = sendMessage("GET_MEASUREMENTS:" + sensor_id + "," + datetime_1
                        + "," + datetime_2);
                Log.d("RECEIVED measurements", response);
                response = response.substring(response.indexOf(":") + 1);
                MainActivity.saveMeasurements(context, response);
                stopConnection();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
