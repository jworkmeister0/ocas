package com.jack.ocas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;

import com.csounds.CsoundObj;

@SuppressLint("NewApi") public class CsoundBase extends Activity {

	protected CsoundObj csoundObj = new CsoundObj(false);
	protected Handler handler = new Handler();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		csoundObj.setMessageLoggingEnabled(true);
		super.onCreate(savedInstanceState);
		 Log.d("CsoundObj", "FRAMES:" + ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		csoundObj.stop();

	}

	public void setSeekBarValue(SeekBar seekBar, double min, double max, double value) {
		double range = max - min;
		double percent = (value - min) / range;

		seekBar.setProgress((int)(percent * seekBar.getMax()));
	}


	protected String getResourceFileAsString(int resId) {
		StringBuilder str = new StringBuilder();

		InputStream is = getResources().openRawResource(resId);
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String line;

		try {
			while ((line = r.readLine()) != null) {
				str.append(line).append("\n");
			}
		} catch (IOException ios) {

		}

		return str.toString();
	}

	protected File createTempFile(String csd) {
		File f = null;

		Log.d("createTempFile", "trying...");
		try {
			f = File.createTempFile("temp", ".csd", this.getCacheDir());
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(csd.getBytes());
			fos.close();
			Log.d("createTempFile", "successful!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}


		return f;
	}
}
