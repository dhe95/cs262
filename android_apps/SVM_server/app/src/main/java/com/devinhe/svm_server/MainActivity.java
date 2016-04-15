package com.devinhe.svm_server;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Button start = (Button) findViewById(R.id.start_button);
        if (start != null) {
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
                    File output = new File(Environment.getExternalStorageDirectory() + "/log.log");
                    try {
                        FileWriter writer = new FileWriter(output);
                        writer.write("Hello World");
                        writer.flush();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    File input = new File(Environment.getExternalStorageDirectory() + "/digits/test-images.csv");
                    int n_lines = 0;
                    try(BufferedReader br = new BufferedReader(new FileReader(input))) {
                        for(String line; (line = br.readLine()) != null;) {
                            // whatever lol
                            n_lines++;
                        }
                        TextView main = (TextView) findViewById(R.id.output);
                        main.setText(n_lines + " lines read");
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
