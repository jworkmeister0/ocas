package com.jack.ocas;

import com.jack.ocas.instruments.Bowed;
import com.jack.ocas.instruments.Drone;
import com.jack.ocas.instruments.Moog;
import com.jack.ocas.instruments.Simple2;
import com.jack.ocas.instruments.VariableFret;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends CsoundBase {

	String[] testNames = new String[] {
			"Settings",  "Drone", "Simple Sine", 
			"Simple Sine 2", "Moog", "Bowed",
			""};

	@SuppressWarnings("rawtypes")
	Class[] activities = new Class[] {
			OptionsActivity.class, Drone.class, VariableFret.class, 
			Simple2.class, Moog.class, Bowed.class};
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView listView = (ListView) findViewById(R.id.list_view);

		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				testNames));

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0,
					View arg1, int position, long arg3) {
				
				if(activities[position]==null){
					System.out.println("what the hell?");
					return;
				}

				Intent intent=new Intent(MainActivity.this, activities[position]);

				startActivity(intent);
				
			}
		});

	}
}
