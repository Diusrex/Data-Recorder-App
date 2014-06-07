package com.diusrex.sleepingdata;

import java.io.IOException;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {
    static String LOG_TAG = "MainActivity";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DataContainer container = null;
		
		try {
		    container = FileLoader.LoadData();
        } catch (IOException e) {
            e.printStackTrace();
            container = new DataContainer();
        }
		
		try {
            FileSaver.WriteData(container, false);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to write to file");
            e.printStackTrace();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
