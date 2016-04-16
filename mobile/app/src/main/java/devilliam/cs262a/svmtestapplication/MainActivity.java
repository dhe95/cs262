package devilliam.cs262a.svmtestapplication;

import src.*;
import src.libsvm.*;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOError;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    svm_model model;
    LabelData testData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream data_is = getResources().openRawResource(R.raw.data_test);
        BufferedReader data_br = new BufferedReader(new InputStreamReader(data_is)) ;

        InputStream model_is = getResources().openRawResource(R.raw.javalibsvm_digits);
        BufferedReader model_br = new BufferedReader(new InputStreamReader(model_is)) ;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try{
            model = svm.svm_load_model(model_br);
            testData = LabelData.loadLabelAndData(data_br);
        }
        catch(Exception e){
            System.out.println(e.toString());
            System.err.println("Unable to load model from file");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rand_index = (int)(Math.random() * testData.x.size());
                svm_node[] dataValue = testData.x.elementAt(rand_index);
                Double predict_val = svm.svm_predict(model, dataValue);

                TextView tv_prediction = (TextView) findViewById(R.id.prediction);
                tv_prediction.setText(String.format("Prediction of line %d: %s ", rand_index, predict_val.toString()));

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
