package com.jack.ocas.instruments;

import com.jack.ocas.R;

import android.os.Bundle;

public class Moog extends VariableFret{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public String getSound(){
		return getResourceFileAsString(R.raw.moog);
	}
}