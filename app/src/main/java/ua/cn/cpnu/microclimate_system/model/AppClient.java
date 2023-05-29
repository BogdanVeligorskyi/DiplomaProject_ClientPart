package ua.cn.cpnu.microclimate_system.model;

import android.content.Context;
import android.util.Log;

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
    public static final int ACTION_GET_ACTUAL = 3;

    // static variable which is TRUE when server responded exactly
    public static boolean IS_SUCCESS = false;

    // string constants
    private static final String GET_DEVICES = "GET_DEVICES";
    private static final String GET_SENSORS = "GET_SENSORS";
    private static final String GET_MEASUREMENTS = "GET_MEASUREMENTS";
    private static final String GET_ACTUAL = "GET_ACTUAL";
    private static final String NO_DEVICES = "No devices";
    private static final String NO_SENSORS = "No sensors";
    private static final String NO_MEASUREMENTS = "No measurements";

    // variables and objects
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final Context context;
    private final int action;
    private final int sensor_id;
    private final String datetime_1;
    private final String datetime_2;
    private final int room_id;
    private final int sensorsNum;

    // constructor for ACTION 1 (get devices and sensors from server)
    public AppClient(Context context, int action) {
        this.context = context;
        this.action = action;
        this.sensor_id = 0;
        this.datetime_1 = null;
        this.datetime_2 = null;
        this.room_id = 0;
        this.sensorsNum = 0;
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
        this.room_id = 0;
        this.sensorsNum = 0;
        IS_SUCCESS = false;
    }

    // constructor for ACTION 3 (get actual measurements)
    public AppClient(Context context, int action, int room_id, int sensors_num) {
        this.context = context;
        this.action = action;
        this.sensor_id = 0;
        this.datetime_1 = null;
        this.datetime_2 = null;
        this.room_id = room_id;
        this.sensorsNum = sensors_num;
        IS_SUCCESS = false;
    }

    // try to connect to server
    private void startConnection() throws IOException {
        String[] networkData = FileProcessing.loadNetwork(context);
        String ip = "192.168.0.115";
        int port = 50028;
        if (networkData != null) {
            ip = networkData[0];
            port = Integer.parseInt(networkData[1]);
        }
        Log.d("SERVER_IP", ip);
        Log.d("PORT", ""+port);
        while (clientSocket == null) {
            clientSocket = new Socket(ip, port);
        }
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader
                (clientSocket.getInputStream()));
    }

    // send message to server and retrieve answer
    private String sendMessage(String msg) throws IOException {
        out.println(msg);
        return in.readLine();
    }

    // disconnect from server
    private void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    @Override
    public void run() {
        // get devices and sensors
        if (action == ACTION_GET_DEVICES) {
            try {
                startConnection();
                String response = sendMessage(GET_DEVICES);
                Log.d("RECEIVED devices", response);
                if (!response.startsWith(NO_DEVICES)) {
                    response = response.substring(response.indexOf(":") + 1);
                    FileProcessing.saveDevices(context, response);
                }
                response = sendMessage(GET_SENSORS);
                Log.d("RECEIVED sensors", response);
                if (!response.startsWith(NO_SENSORS)) {
                    response = response.substring(response.indexOf(":") + 1);
                    FileProcessing.saveSensors(context, response);
                    IS_SUCCESS = true;
                }
                stopConnection();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // get measurements in time interval
        if (action == ACTION_GET_MEASUREMENTS) {
            try {
                startConnection();
                String response = sendMessage(
                        GET_MEASUREMENTS + ":" + sensor_id + "," + datetime_1
                        + "," + datetime_2);
                Log.d("RECEIVED measurements", response);
                if (!response.startsWith(NO_MEASUREMENTS)) {
                    response = response.substring(response.indexOf(":") + 1);
                    FileProcessing.saveMeasurements(context, response);
                    IS_SUCCESS = true;
                }
                stopConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // get actual measurements from sensors in specific room
        if (action == ACTION_GET_ACTUAL) {
            try {
                startConnection();
                String response = sendMessage(
                        GET_ACTUAL + ":" + room_id + "," + sensorsNum);
                Log.d("RECEIVED measurements", response);
                if (!response.startsWith(NO_MEASUREMENTS)) {
                    response = response.substring(response.indexOf(":") + 1);
                    FileProcessing.saveActualMeasurements(context, response);
                    IS_SUCCESS = true;
                }
                stopConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
