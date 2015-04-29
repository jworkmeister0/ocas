package com.jack.ocas.instruments;

import com.jack.ocas.R;
import com.jack.ocas.R.raw;

import android.os.Bundle;


public class Simple2 extends VariableFret{	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public String getSound(){
		return getResourceFileAsString(R.raw.simple2);
	}
}