package com.devinhe.svm_server;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devinhe.svm_server.models.DigitsModel;
import com.devinhe.svm_server.models.ModelType;
import com.devinhe.svm_server.models.RecommendationModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
    private static final String TAG = MainActivity.class.getCanonicalName();
    private String logLocation;
    private float initBattery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button svm = (Button) findViewById(R.id.svm);
        assert svm != null;
        svm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateTextBoxes()) return;
                Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
                startLog("/svm_server.log");
                endLog();

                DigitsModel svmBenchmark = new DigitsModel(BenchmarkUtils.NUM_DATA_POINTS, ModelType.SVM);
                svmBenchmark.start();

                TextView main = (TextView) findViewById(R.id.out);
                assert main != null;
                main.setText("SVM Done");
            }
        });

        Button forest = (Button) findViewById(R.id.random_forest);
        assert forest != null;
        forest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateTextBoxes()) return;
                Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
                startLog("/forest_server.log");

                DigitsModel benchmark = new DigitsModel(BenchmarkUtils.NUM_DATA_POINTS, ModelType.FOREST);
                benchmark.start();
                endLog();

                TextView main = (TextView) findViewById(R.id.out);
                assert main != null;
                main.setText("Forest Done");
            }
        });

        Button collab = (Button) findViewById(R.id.collab_filter);
        assert collab != null;
        collab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateTextBoxes()) return;
                Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
                startLog("/collab_server.log");

                RecommendationModel benchmark = new RecommendationModel(BenchmarkUtils.NUM_DATA_POINTS, ModelType.COLLAB_FILTER);
                benchmark.start();

                endLog();

                TextView main = (TextView) findViewById(R.id.out);
                assert main != null;
                main.setText("Collab filtering done");
            }
        });
    }

    private void startLog(String loc) {
        logLocation = Environment.getExternalStorageDirectory() + loc;
        initBattery = getBatteryLevel();
        logger.info("Writing log to: {}", logLocation);
        logger.info("Initial battery level: {}", Float.toString(initBattery));

    }

    private void endLog() {
        float finalBattery = getBatteryLevel();
        logger.info("Final battery level: {}", Float.toString(finalBattery));
        logger.info("Battery used: {}", Float.toString(initBattery - finalBattery));
        BenchmarkUtils.dumpLogCatToFile(logLocation);

    }

    private float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return ((float)level)/(float)scale;
    }

    private boolean validateTextBoxes() {
        EditText numData = (EditText) findViewById(R.id.numData);
        EditText ipAddr = (EditText) findViewById(R.id.ipAddr);
        try {
            BenchmarkUtils.NUM_DATA_POINTS = Integer.parseInt(numData.getText().toString());
            BenchmarkUtils.serverIP = ipAddr.getText().toString();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Problem in text reading", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
