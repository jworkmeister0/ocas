
package com.jack.ocas.instruments;

import java.io.File;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.csounds.CsoundObj;
import com.csounds.CsoundObjListener;
import com.csounds.bindings.CsoundBinding;
import com.jack.ocas.BackgroundView;
import com.jack.ocas.CsoundBase;
import com.jack.ocas.R;

import csnd6.CsoundMYFLTArray;
import csnd6.controlChannelType;

public class VariableFret extends CsoundBase implements
	OnTouchListener, CsoundObjListener, CsoundBinding{

	public BackgroundView fretView;

	int fretCount;
	int octave;
	int fret;

	int touchIds[] = new int[10];
	float touchX[] = new float[10];
	float touchY[] = new float[10];
	float fretboard[];

	float note;
	float fretTouch;
	float lowestNote;
	float ratio;

	CsoundMYFLTArray touchXPtr[] = new CsoundMYFLTArray[10];
	CsoundMYFLTArray touchYPtr[] = new CsoundMYFLTArray[10];

	String scale;
	String root;
	String instrument;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("onCreate","super.onCreate");

		//initialize arrays
		for(int i = 0; i < touchIds.length; i++) {
			touchIds[i]	= -1;
			touchX[i]= -1;
			touchY[i]= -1;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		getSettings();

		fretView = new BackgroundView(this, fretCount);
		fretView.setOnTouchListener(this);
		setContentView(fretView);

		String sound = getSound();
		File foo = createTempFile(sound);

		csoundObj.addBinding(this);
		csoundObj.startCsound(foo);
	}

	public String getSound(){
		return getResourceFileAsString(R.raw.tuned);
	}

	public void getSettings(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		octave=prefs.getInt("octave", 4);
		fretCount=prefs.getInt("fretCount", 10);
		root=prefs.getString("root", "C");
		scale=prefs.getString("scale", "major");


		ratio=((float)fretCount)/10;
		fretboard=new float[fretCount];

		switch (root){
			case "C": lowestNote=16.35f;
				break;
			case "C#": lowestNote=17.32f;
				break;
			case "D": lowestNote=18.35f;
				break;
			case "D#": lowestNote=19.45f;
				break;
			case "E": lowestNote=20.60f;
				break;
			case "F": lowestNote=21.83f;
				break;
			case "F#": lowestNote=23.12f;
				break;
			case "G": lowestNote=24.50f;
				break;
			case "G#": lowestNote=25.96f;
				break;
			case "A": lowestNote=27.50f;
				break;
			case "A#": lowestNote=29.14f;
				break;
			case "B": lowestNote=30.87f;
				break;
			default: lowestNote=16.35f;
				break;
		}

		if(octave!=0){
			for(int i=0; i<octave; i++){
				lowestNote=lowestNote*2;
			}
		}
		Toast.makeText(this, "lowestNoteFreq:" +lowestNote,
				Toast.LENGTH_LONG).show();

		switch(scale){
			case "Chromatic": getChromatic(fretCount, lowestNote);
				break;
			case "Major": getMajor(fretCount, lowestNote);
				break;
			case "Harmonic Minor": getHarmonicMinor(fretCount, lowestNote);
				break;
			case "Natural Minor": getNaturalMinor(fretCount, lowestNote);
				break;
			case "Lydian Mode": getLydian(fretCount, lowestNote);
				break;
			case "Aeolian Mode": getAeolian(fretCount, lowestNote);
				break;
			case "Dorian Mode": getDorian(fretCount, lowestNote);
				break;
			default: getMajor(fretCount, lowestNote);
				break;
		}

	}


	/* The basic reason to override the onTouch method is to tell
	 * pass the CsoundOject different things when different touch events occur.
	 * Locations pressed on the screen are represented by decimal representations
	 * of the percentage from the bottom left of the screen
	 *
	 * @implements android.view.View.OnTouchListener.onTouch
	 */
	public boolean onTouch(View v, MotionEvent event) {
		final int action = event.getAction() & MotionEvent.ACTION_MASK;
		//final int action = MotionEventCompat.getActionMasked(event);
		Log.d("onTouch","The action is " + action);
		switch(action) {

			//ACTION_DOWN indicates a touch (no release)
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				for(int i = 0; i < event.getPointerCount(); i++) {
					int pointerId = event.getPointerId(i);
					int id = getTouchId(pointerId);

					if(id == -1) {

						id = getTouchIdAssignment();
						//Log.d("afterTouchAssign","id is now " + id);

						if(id != -1) {
							touchIds[id] = pointerId;

							//percent of x width
							touchX[id] = event.getX(i) / fretView.getWidth();
						//	Log.d("touchX[id]","is now " + touchX[id]);
							touchY[id] =getNote(getFret(event, i));


							//int thisFret=getFret(event, i);
							//Log.d("ACTION_DOWN", "thisFret="+thisFret);

							//note=getNote(thisFret);
							//Log.d("ACTION_DOWN", "note="+thisFret);

							//touchY[id] = note;
						//	Log.d("touchY[id]","is now " + touchY[id]);

							if(touchXPtr[id] != null) {
								touchXPtr[id].SetValue(0, touchX[id]);
								touchYPtr[id].SetValue(0, touchY[id]);								//Log.d("SendScoreDOWN","touchY[id]=" + touchY[id]);
								//Log.d("SendScoreDOWN","touchX[id]=" + touchX[id]);
								//Log.d("SendScoreDOWN", String.format("i1.%d 0 -2 %d", id, id));
								//Log.d("SendScoreDOWN","fret=" + fret);

								//tell Csound to start playing
								csoundObj.sendScore(String.format("i1.%d 0 -2 %d", id, id));
							}
						}
					}

				}
				break;

			//ACTION_MOVE indicates a movement of a touch
			case MotionEvent.ACTION_MOVE:
				for(int i = 0; i < event.getPointerCount(); i++) {
					int pointerId = event.getPointerId(i);
					int id = getTouchId(pointerId);

					//Log.d("MOVE","pointerID=" + pointerId);
					//Log.d("MOVE","id=" + id);

					if(id != -1) {

						//Log.d("ACTION_MOVE", "thisFret="+thisFret);

						//Log.d("ACTION_MOVE:", "note="+thisFret);

						touchX[id] = event.getX(i) / fretView.getWidth();
						touchY[id] =getNote(getFret(event, i));
						//updateValuesToCsound();
						//Log.d("ACTION_MOVE","touchX[id] is " + touchX[id]);
						//Log.d("ACTION_MOVE","touchY[id] is " + touchY[id]);
						//Log.d("ACTION_MOVE","fret=" + fret);
					}
				}
				break;

			//ACTION_MOVE indicates a release of a touch
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_UP:
			{
				int activePointerIndex = event.getActionIndex();
				int pointerId = event.getPointerId(activePointerIndex);

				//Log.d("ACTION_UP","actionPointerIndex=" + activePointerIndex);
				//Log.d("ACTION_UP","pointerID=" + pointerId);
				int id = getTouchId(pointerId);
				if(id != -1) {
					touchIds[id] = -1;
					//Log.d("ACTION_UP","touchY[id] is " + touchY[id]);
					//System.out.println(id);
					//Log.d("SendScoreUP", String.format("i-1.%d 0 0 %d", id, id));
					csoundObj.sendScore(String.format("i-1.%d 0 0 %d", id, id));
				}
			}
				break;
		}//switch

		return true;
	}

	/*
	 * Feed this "event.getY(i) / fretView.getHeight()"
	 * @returns fret from touch locations
	 */
	public int getFret(MotionEvent event, int i){
		Log.d("getFret","Ratio is"+ratio);

		float foo=0;
		foo=(event.getY(i) / fretView.getHeight());
		foo=(1-foo);
		int bar=Math.round((ratio*foo)*10);
		if(bar>=fretCount) return fretCount-1;
		return bar;
	}

	/*
	 * Take the fret number and returns its
	 * corresponding frequency
	 */
	public float getNote(int fret){
		Log.d("getNote", "Got Fret "+fret);
		return fretboard[fret];
	}

	public void getChromatic(int fretCount, float lowestNote){
		Log.d("getScale", "BUILDING SCALE: "+fretCount +"@"+lowestNote);
		double halfStep=0.0;	//to avoid integer division below
		for(int i=0; i<fretCount; i++){
			double stepChanger=Math.pow(2, (halfStep/12));
			fretboard[i]=(float) (lowestNote*stepChanger);
			Log.d("getScale", "chromaticSCALE:"+fretboard[i] +"@"+i);

			halfStep++;
		}
	}

	public void getMajor(int fretCount, float lowestNote){
		double halfStep=0.0;
		for(int i=0; i<fretCount; i++){
			double stepChanger=Math.pow(2, (halfStep/12));
			fretboard[i]=(float) (lowestNote*stepChanger);
			Log.d("getScale", "majorSCALE:"+fretboard[i] +"@"+i);

			halfStep++;

			int wholeSteps=i%7;
			switch (wholeSteps){
				case 1:
				case 2:
				case 4:
				case 5:
				case 6:
					halfStep++;
			}
		}
	}

	public void getHarmonicMinor(int fretCount, float lowestNote){
		double halfStep=0.0;
		for(int i=0; i<fretCount; i++){
			double stepChanger=Math.pow(2, (halfStep/12));
			fretboard[i]=(float) (lowestNote*stepChanger);
			Log.d("getScale", "harmonicMinorSCALE:"+fretboard[i] +"@"+i);
			halfStep++;

			int wholeSteps=i%7;
			switch (wholeSteps){
				case 6:
					halfStep++;
					halfStep++;
					break;
				case 1:
				case 3:
				case 4:
				case 7:
					halfStep++;
			}
		}
	}

	public void getNaturalMinor(int fretCount, float lowestNote){
		double step=0.0;
		for(int i=0; i<fretCount; i++){
			double stepChanger=Math.pow(2, (step/12));
			fretboard[i]=(float) (lowestNote*stepChanger);
			Log.d("getScale", "naturlaMinorSCALE:"+fretboard[i] +"@"+i);
			step++;

			int wholeSteps=i%7;
			switch (wholeSteps){
				case 1:
				case 3:
				case 4:
				case 6:
				case 7:
					step++;
			}
		}

	}

	public void getLydian(int fretCount, float lowestNote){
		double halfStep=0.0;
		for(int i=0; i<fretCount; i++){
			double stepChanger=Math.pow(2, (halfStep/12));
			fretboard[i]=(float) (lowestNote*stepChanger);
			Log.d("getScale", "Lydian Mode"+fretboard[i] +"@"+i);

			halfStep++;

			int wholeSteps=i%7;
			switch (wholeSteps){
				case 1:
				case 2:
				case 3:
				case 5:
				case 6:
					halfStep++;
			}
		}
	}

	public void getAeolian(int fretCount, float lowestNote){
		double halfStep=0.0;
		for(int i=0; i<fretCount; i++){
			double stepChanger=Math.pow(2, (halfStep/12));
			fretboard[i]=(float) (lowestNote*stepChanger);
			Log.d("getScale", "AeolianMode:"+fretboard[i] +"@"+i);

			halfStep++;

			int wholeSteps=i%7;
			switch (wholeSteps){
				case 1:
				case 3:
				case 4:
				case 6:
				case 7:
					halfStep++;
			}
		}
	}

	public void getDorian(int fretCount, float lowestNote){
		double halfStep=0.0;
		for(int i=0; i<fretCount; i++){
			double stepChanger=Math.pow(2, (halfStep/12));
			fretboard[i]=(float) (lowestNote*stepChanger);
			Log.d("getScale", "AeolianMode:"+fretboard[i] +"@"+i);

			halfStep++;

			int wholeSteps=i%7;
			switch (wholeSteps){
				case 1:
				case 3:
				case 4:
				case 5:
				case 7:
					halfStep++;
			}
		}
	}
	public void setup(CsoundObj csoundObj) {
		for (int i = 0; i < touchIds.length; i++) {
			touchXPtr[i] = csoundObj.getInputChannelPtr(
					String.format("touch.%d.x", i),
					controlChannelType.CSOUND_CONTROL_CHANNEL);
			touchYPtr[i] = csoundObj.getInputChannelPtr(
					String.format("touch.%d.y", i),
					controlChannelType.CSOUND_CONTROL_CHANNEL);
		}
	}

	public void updateValuesToCsound() {
		for(int i = 0; i < touchX.length; i++) {
			touchXPtr[i].SetValue(0, touchX[i]);
			touchYPtr[i].SetValue(0, touchY[i]);
		}

	}

	/*	Return the touchID int if it is in
	 *	the touchIDs array. If not, return -1
	 */
	protected int getTouchId(int touchId) {
		int i=0;
		while(i < touchIds.length){
			if(touchIds[i] == touchId) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/* Make touchXPtr and touchYPtr empty
	 */
	public void cleanup() {
		for(int i = 0; i < touchIds.length; i++) {
			touchXPtr[i].Clear();
			touchXPtr[i] = null;

			touchYPtr[i].Clear();
			touchYPtr[i] = null;
		}
	}

	/* Get the location in array when touchID==-1
	 */
	protected int getTouchIdAssignment() {
		for(int i = 0; i < touchIds.length; i++) {
			if(touchIds[i] == -1) {
				return i;
			}
		}
		return -1;
	}

	public void updateValuesFromCsound() {}
	public void csoundObjComplete(CsoundObj csoundObj) {}

	@Override
	public void csoundObjStarted(CsoundObj csoundObj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void csoundObjCompleted(CsoundObj csoundObj) {
		// TODO Auto-generated method stub

	}
}
