package ua.cn.cpnu.microclimate_system;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// client side of application
public class AppClient implements Runnable {

    // actions
    public static final int ACTION_GET_DEVICES = 1;
    public static final int ACTION_GET_MEASUREMENTS = 2;

    public static boolean IS_SUCCESS = false;

    // server data
    private final String SERVER_IP = "192.168.0.115";
    private final int PORT = 50013;

    // variables and objects
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final Context context;
    private final int action;
    private final int sensor_id;
    private final String datetime_1;
    private final String datetime_2;

    // constructor for ACTION 1 (get devices from server)
    public AppClient(Context context, int action) {
        this.context = context;
        this.action = action;
        this.sensor_id = 0;
        this.datetime_1 = null;
        this.datetime_2 = null;
        IS_SUCCESS = false;
    }

    // constructor for ACTION 2 (get measurements from server)
    public AppClient(Context context, int action, int sensor_id,
                      String datetime_1, String datetime_2) {
        this.context = context;
        this.action = action;
        this.sensor_id = sensor_id;
        this.datetime_1 = datetime_1;
        this.datetime_2 = datetime_2;
        IS_SUCCESS = false;
    }

    // try to connect to server
    private void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    // send message to server and retrieve answer
    private String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    // disconnect from server
    private void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    @Override
    public void run() {
        if (action == ACTION_GET_DEVICES) {
            try {
                startConnection(SERVER_IP, PORT);
                String response = sendMessage("GET_DEVICES");
                Log.d("RECEIVED devices", response);
                response = response.substring(response.indexOf(":") + 1);
                FileProcessing.saveDevices(context, response);

                response = sendMessage("GET_SENSORS");
                Log.d("RECEIVED sensors", response);
                response = response.substring(response.indexOf(":") + 1);
                FileProcessing.saveSensors(context, response);

                stopConnection();
                IS_SUCCESS = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (action == ACTION_GET_MEASUREMENTS) {
            try {
                startConnection(SERVER_IP, PORT);
                String response = sendMessage("GET_MEASUREMENTS:" + sensor_id + "," + datetime_1
                        + "," + datetime_2);
                Log.d("RECEIVED measurements", response);
                response = response.substring(response.indexOf(":") + 1);
                FileProcessing.saveMeasurements(context, response);
                stopConnection();
                IS_SUCCESS = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
