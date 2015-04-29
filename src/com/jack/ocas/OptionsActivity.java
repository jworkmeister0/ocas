package com.jack.ocas;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class OptionsActivity extends Activity {
	
	String rootNote, scale, instrument;
	int fretCount, octave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		final Spinner noteSpinner = (Spinner) findViewById(R.id.root_note_array);
		ArrayAdapter<CharSequence> noteAdapter = ArrayAdapter.createFromResource(this,
		        R.array.root_note_array, android.R.layout.simple_spinner_item);
		noteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		noteSpinner.setAdapter(noteAdapter);

		/*final Spinner instrumentSpinner = (Spinner) findViewById(R.id.instrument_array);
		ArrayAdapter<CharSequence> instrumentAdapter = ArrayAdapter.createFromResource(this,
		        R.array.scale_array, android.R.layout.simple_spinner_item);
		instrumentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		instrumentSpinner.setAdapter(instrumentAdapter);*/

		final Spinner scaleSpinner = (Spinner) findViewById(R.id.scale_array);
		ArrayAdapter<CharSequence> scaleAdapter = ArrayAdapter.createFromResource(this,
		        R.array.scale_array, android.R.layout.simple_spinner_item);
		scaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		scaleSpinner.setAdapter(scaleAdapter);

		final Spinner fretSpinner = (Spinner) findViewById(R.id.fret_array);
		ArrayAdapter<CharSequence> fretAdapter = ArrayAdapter.createFromResource(this,
		        R.array.fret_array, android.R.layout.simple_spinner_item);
		fretAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fretSpinner.setAdapter(fretAdapter);

		final Spinner octaveSpinner = (Spinner) findViewById(R.id.octave_array);
		ArrayAdapter<CharSequence> octaveAdapter = ArrayAdapter.createFromResource(this,
		        R.array.octave_array, android.R.layout.simple_spinner_item);
		octaveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		octaveSpinner.setAdapter(octaveAdapter);

		Button button = (Button) findViewById(R.id.dummy_button);
        button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
						OptionsActivity.this);
				SharedPreferences.Editor editor = prefs.edit();

				//instrument=String.valueOf(instrumentSpinner.getSelectedItem()); 
				rootNote=String.valueOf(noteSpinner.getSelectedItem()); 
				scale=String.valueOf(scaleSpinner.getSelectedItem()); 
				fretCount=Integer.parseInt(String.valueOf(fretSpinner.getSelectedItem()));
				octave=Integer.parseInt(String.valueOf(octaveSpinner.getSelectedItem()));

				editor.putString("instrument", instrument);
				editor.putString("root", rootNote);
				editor.putString("scale", scale);
				editor.putInt("fretCount", fretCount);
				editor.putInt("octave", octave);
				editor.apply();

				//eye candy confirmation
				Toast.makeText(OptionsActivity.this,
					"Saved Settings: " + 
					"\nRoot: "+ String.valueOf(noteSpinner.getSelectedItem()) + 
					"\nScale: "+ String.valueOf(scaleSpinner.getSelectedItem()) + 
					"\nOctave: "+ Integer.parseInt(String.valueOf(octaveSpinner.getSelectedItem()))+ 
					"\nFrets: "+ Integer.parseInt(String.valueOf(fretSpinner.getSelectedItem())),
					Toast.LENGTH_LONG).show(); //was LENGTH_SHORT
			}
		});	
	}

}
