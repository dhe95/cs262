package com.devinhe.svm_server;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devinhe.svm_server.benchmark.BenchmarkUtils;
import com.devinhe.svm_server.benchmark.models.CollabFilterLocalModel;
import com.devinhe.svm_server.benchmark.models.DigitsServerModel;
import com.devinhe.svm_server.benchmark.models.ModelType;
import com.devinhe.svm_server.benchmark.models.RandomForestsLocalModel;
import com.devinhe.svm_server.benchmark.models.RecommendationServerModel;
import com.devinhe.svm_server.benchmark.models.SVMLocalModel;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkUtils.class);
    private static final String TAG = MainActivity.class.getCanonicalName();
    private String logLocation;
    private float initBattery;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
                String outloc = Environment.getExternalStorageDirectory() + "/out/svm_server";
                float initBattery = getBatteryLevel();

                DigitsServerModel svmBenchmark = new DigitsServerModel(BenchmarkUtils.NUM_DATA_POINTS, ModelType.SVM);
                String out = svmBenchmark.start();
                endLog();
                try {
                    PrintWriter writer = new PrintWriter(outloc);
                    writer.println(out);
                    writer.print("Init Battery: ");
                    writer.println(initBattery);
                    writer.print("Final Battery: ");
                    writer.println(getBatteryLevel());
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                float initBattery = getBatteryLevel();
                String outloc = Environment.getExternalStorageDirectory() + "/out/forest_server";

                DigitsServerModel benchmark = new DigitsServerModel(BenchmarkUtils.NUM_DATA_POINTS, ModelType.FOREST);
                String out = benchmark.start();
                endLog();
                try {
                    PrintWriter writer = new PrintWriter(outloc);
                    writer.println(out);
                    writer.print("Init Battery: ");
                    writer.println(initBattery);
                    writer.print("Final Battery: ");
                    writer.println(getBatteryLevel());
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                float initBattery = getBatteryLevel();
                String outloc = Environment.getExternalStorageDirectory() + "/out/collab_server";

                RecommendationServerModel benchmark = new RecommendationServerModel(BenchmarkUtils.NUM_DATA_POINTS, ModelType.COLLAB_FILTER);
                String out = benchmark.start();

                endLog();
                try {
                    PrintWriter writer = new PrintWriter(outloc);
                    writer.println(out);
                    writer.print("Init Battery: ");
                    writer.println(initBattery);
                    writer.print("Final Battery: ");
                    writer.println(getBatteryLevel());
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                TextView main = (TextView) findViewById(R.id.out);
                assert main != null;
                main.setText("Collab filtering done");
            }
        });

        Button localSVM = (Button) findViewById(R.id.svm_local);
        assert localSVM != null;
        localSVM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateNumData()) return;
                Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
                String out = "";
                startLog("/SVM_local.log");
                float initBattery = getBatteryLevel();
                String outloc = Environment.getExternalStorageDirectory() + "/out/svm_local";
                try {
                    File input = new File(Environment.getExternalStorageDirectory() + "/resources/data_test.csv");
                    BufferedReader data_br = new BufferedReader(new FileReader(input));
                    File input2 = new File(Environment.getExternalStorageDirectory() + "/resources/javalibsvm_digits.bin");
                    BufferedReader model_br = new BufferedReader(new FileReader(input2));

                    SVMLocalModel benchmark = new SVMLocalModel(BenchmarkUtils.NUM_DATA_POINTS, ModelType.SVM);
                    out = benchmark.start(data_br, model_br);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                endLog();
                try {
                    PrintWriter writer = new PrintWriter(outloc);
                    writer.println(out);
                    writer.print("Init Battery: ");
                    writer.println(initBattery);
                    writer.print("Final Battery: ");
                    writer.println(getBatteryLevel());
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TextView main = (TextView) findViewById(R.id.out);
                assert main != null;
                main.setText("SVM local done");
            }
        });


        Button localForest = (Button) findViewById(R.id.forest_local);
        assert localForest != null;
        localForest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText sizeText = (EditText) findViewById(R.id.model_size);
                String size = sizeText.getText().toString();
                if (!validateNumData()) return;
                Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
                startLog("/forest_local_" + size + ".log");
                float initBattery = getBatteryLevel();
                String out = "";
                String outloc = Environment.getExternalStorageDirectory() + "/out/forest_local_" + size;
                try {
                    File input = new File(Environment.getExternalStorageDirectory() + "/resources/data_test.csv");
                    BufferedReader data_br = new BufferedReader(new FileReader(input));
                    File input2 = new File(Environment.getExternalStorageDirectory() + "/resources/java_digits_forest_"+  size + "trees");
                    BufferedReader model_br = new BufferedReader(new FileReader(input2));

                    RandomForestsLocalModel benchmark = new RandomForestsLocalModel(BenchmarkUtils.NUM_DATA_POINTS, ModelType.FOREST, outloc);
                    out = benchmark.start(data_br, model_br);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                endLog();
                try {
                    PrintWriter writer = new PrintWriter(outloc);
                    writer.println(out);
                    writer.print("Init Battery: ");
                    writer.println(initBattery);
                    writer.print("Final Battery: ");
                    writer.println(getBatteryLevel());
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TextView main = (TextView) findViewById(R.id.out);
                assert main != null;
                main.setText("Forest local done");
            }
        });

        Button localCollabFilter = (Button) findViewById(R.id.collab_filter_local);
        assert localCollabFilter != null;
        localCollabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateNumData()) return;
                Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
                EditText sizeText = (EditText) findViewById(R.id.model_size);
                String size = sizeText.getText().toString();
                startLog("/collab_filter_local_" + size + ".log");
                float initBattery = getBatteryLevel();
                String outloc = Environment.getExternalStorageDirectory() + "/out/collab_local_" + size;
                String out = "";
                try {
                    File input = new File(Environment.getExternalStorageDirectory() + "/resources/user_" + size);
                    BufferedReader user = new BufferedReader(new FileReader(input));
                    File input2 = new File(Environment.getExternalStorageDirectory() + "/resources/product_" + size);
                    BufferedReader product = new BufferedReader(new FileReader(input2));

                    CollabFilterLocalModel benchmark = new CollabFilterLocalModel(BenchmarkUtils.NUM_DATA_POINTS, ModelType.COLLAB_FILTER, Integer.parseInt(size));
                    out = benchmark.start(user, product);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                endLog();
                try {
                    PrintWriter writer = new PrintWriter(outloc);
                    writer.println(out);
                    writer.print("Init Battery: ");
                    writer.println(initBattery);
                    writer.print("Final Battery: ");
                    writer.println(getBatteryLevel());
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TextView main = (TextView) findViewById(R.id.out);
                assert main != null;
                main.setText("collab local done");

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void startLog(String loc) {
        BenchmarkUtils.clearLogCat();
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
        return ((float) level) / (float) scale;
    }

    private boolean validateTextBoxes() {
        EditText ipAddr = (EditText) findViewById(R.id.ipAddr);
        try {
            validateNumData();
            BenchmarkUtils.serverIP = ipAddr.getText().toString();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Problem in text reading", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateNumData() {
        EditText numData = (EditText) findViewById(R.id.numData);
        try {
            BenchmarkUtils.NUM_DATA_POINTS = Integer.parseInt(numData.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Problem in text reading", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.devinhe.svm_server/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.devinhe.svm_server/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
