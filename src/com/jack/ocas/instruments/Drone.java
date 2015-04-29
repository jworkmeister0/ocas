package com.jack.ocas.instruments;

import java.io.File;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.csounds.CsoundObj;
import com.csounds.CsoundObjListener;
import com.csounds.bindings.CsoundBinding;
import com.jack.ocas.CsoundBase;
import com.jack.ocas.R;
import com.jack.ocas.R.raw;

import csnd6.CsoundMYFLTArray;
import csnd6.controlChannelType;


public class Drone extends CsoundBase implements
	OnTouchListener, CsoundObjListener, CsoundBinding{

	public View multiTouchView;

	int touchIds[] = new int[10];
	float touchX[] = new float[10];
	float touchY[] = new float[10];
	CsoundMYFLTArray touchXPtr[] = new CsoundMYFLTArray[10];
	CsoundMYFLTArray touchYPtr[] = new CsoundMYFLTArray[10];


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		for(int i = 0; i < touchIds.length; i++) {
			touchIds[i]	= -1;
			touchX[i]= -1;
			touchY[i]= -1;
		}

		multiTouchView = new View(this);

		multiTouchView.setOnTouchListener(this);
		setContentView(multiTouchView);

		//we are pulling the raw text of the csnd file
		String sound = getResourceFileAsString(R.raw.drone);
		File foo = createTempFile(sound);

		csoundObj.addBinding(this);

		//feed csound the raw file.
		csoundObj.startCsound(foo);
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
		//Log.d(DEBUG_TAG,"The action is " + action);
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
							touchX[id] = event.getX(i) / multiTouchView.getWidth();
						//	Log.d("touchX[id]","is now " + touchX[id]);

							//percent of y width
							touchY[id] = 1 - (event.getY(i) / multiTouchView.getHeight());
						//	Log.d("touchY[id]","is now " + touchY[id]);

							if(touchXPtr[id] != null) {
								touchXPtr[id].SetValue(0, touchX[id]);
								touchYPtr[id].SetValue(0, touchY[id]);
								//Log.d("SendScoreDOWN","touchYPtr[id]=" + touchY[id]);
								//Log.d("SendScoreDOWN","touchXPtr[id]=" + touchX[id]);
								//Log.d("SendScoreDOWN", String.format("i1.%d 0 -2 %d", id, id));
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
						touchX[id] = event.getX(i) / multiTouchView.getWidth();
						touchY[id] = 1 - (event.getY(i) / multiTouchView.getHeight());
						updateValuesToCsound();
						//Log.d("ACTION_MOVE","touchX[id] is " + touchX[id]);
						//Log.d("ACTION_MOVE","touchY[id] is " + touchY[id]);
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
