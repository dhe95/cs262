package com.devinhe.svm_server;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final String processId = Integer.toString(android.os.Process.myPid());
    private static final String serverIP = "52.38.202.70";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = (Button) findViewById(R.id.start);
        assert start != null;
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearLogCat();
                Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
                MODEL modelType = MODEL.SVM;
                String logLocation = Environment.getExternalStorageDirectory() + "/log.log";
                float initBattery = getBatteryLevel();
                int n_data = 10000;
                logger.info("Benchmark's PID is {}", processId);
                logger.info("Writing log to: {}", logLocation);
                logger.info("Running {} connecting to server with {} data points", modelType.name(), n_data);
                logger.info("========================== Starting Benchmark ==========================");
                logger.info("Initial battery level: {}", Float.toString(initBattery));
                int n_lines = 0;
                try {
                    File input = new File(Environment.getExternalStorageDirectory() + "/digits/test-images.csv");
                    BufferedReader br = new BufferedReader(new FileReader(input));
                    while (n_lines < n_data) {
                        for (String line; (line = br.readLine()) != null; ) {
                            // whatever lol
                            logger.info("Making request {}", n_lines);
                            try {
                                String response = new PostSender().execute(new PostSenderParams(modelType, line, serverIP, n_lines)).get();
                                logger.info("Received response {}", response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            n_lines++;
                            break;
                        }
                        br = new BufferedReader(new FileReader(input));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TextView main = (TextView) findViewById(R.id.out);
                assert main != null;
                main.setText(n_lines + " lines read");
                Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                float finalBattery = getBatteryLevel();
                logger.info("Final battery level: {}", Float.toString(finalBattery));
                logger.info("Battery used: {}", Float.toString(initBattery - finalBattery));
                dumpLogCatToFile(logLocation);
            }
        });
    }

    private float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return ((float)level)/(float)scale;
    }

    private static void clearLogCat() {
        try {
            Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void dumpLogCatToFile(String file) {
        try {
            File logFile = new File(file);
            String cmd = "logcat -d -v threadtime -f " + logFile.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public enum MODEL {
        SVM, FOREST, COLLAB_FILTER
    }

    public static String getPath(MODEL modelType) {
        switch (modelType) {
            case SVM:
                return "/svm_predict";
            case FOREST:
                return "/random_forest_predict";
            default:
                return "/";
        }
    }

}
