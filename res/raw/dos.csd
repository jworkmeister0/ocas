<CsoundSynthesizer>
<CsOptions>
-o dac -d -b512 -B2048
</CsOptions>

<CsInstruments>
/*
* Below defines channels, maxdb, sample size and sample rate
* all standard stuff. might need to tweak the ksmps values if
* it sounds crappy
*/
nchnls=2
0dbfs=1
ksmps=16
sr = 48000

ga1 init 0							;initialize ga1
giSine ftgen 0, 0, 2^10, 10, 1		;create a function table

	/*
	* Instrument 1 is essentially the pitch/frequency component
	* of the sound the app creates. Instrument 2 serves as the
	* effect part of the sound
	*/
	instr 1

	/*
	* when itie isn't negative, the instrument is playing.
	* when itie is negative, the instrument stops
	*/
	itie tival
	i_instanceNum = p4

	;printf "touch.%d.x", 1, i_instanceNum			;debugging stuff
	;printf "touch.%d.y", 1, i_instanceNum			;debugging stuff

	/*
	* S_[x,y]Name becomes the name of the software bus
	* so we can refer to the software bus later
	*/
	S_xName sprintf "touch.%d.x", i_instanceNum
	S_yName sprintf "touch.%d.y", i_instanceNum

	/*
	* k[x,y] reads data from a software bus (designated above)
	* this is the magic that allows for coordinates to be
	* sent here. k[x,y] is updated in real time!
	*/
	kx chnget S_xName			;kx for modulation
	ky chnget S_yName			;ky for frequency

	/*debugging & seeing whats going on*/
	;printk .5, kx				;print kx at a rate of 2/sec
	;printk .5, ky				;print ky at a rate of 2/sec

	kenv linsegr 0, .001, 1, .1, 1, .25, 0	;configuring the envelope

	/*
	* vco2 implements the oscillator
	* syntax:
	* vco2 kamp, kcps [, imode] [, kpw] [, kphs] [, inyx]
	* original below commented out
	* a1 vco2 kx * .5 * kenv, 60 + (log(1 - ky) * 3000), 0
	* a1 vco2 kx * .9 * kenv, 30 + (log(1 - ky) * 500), 0
	* al foscil kx *.9 * kenv, 30 + (log(1 - ky)
	* a1 oscil kx * .5 * kenv, 60 + (log(1 - ky) * 3000), -.8655, 0
	* a1 gbuzz kx * .5 * kenv, 60 + (log(1 - ky) * 3000), 3, 0, 0, 3
	* kosc kx*.1, kx, 3, kx*.2, 3, 3, 3, 3, 1
	*/

	/*
	* kout vibrato kAverageAmp, kAverageFreq, kRandAmountAmp, kRandAmountFreq,
	* kAmpMinRate, kAmpMaxRate, kcpsMinRate, kcpsMaxRate, ifn [, iphs]
	*/
	;foo vibrato avgAmp, avgFrq, randAmtAmp, randAmtFrq, ampMinRate, ampMaxRate, minFreqRate, maxFreqRate, ifn [, iphs]
	kvib vibrato kx*1.2,     kx, 	 kx*1.2, 		  7, 	      5,  	     5, 		   4, 		   	4, 1

	printk .10, kvib, 10			;debug info

	;ares poscil3 kamp, kcps [, ifn, iphs]
	a1 poscil3 .8-kvib, (60) + (log(1 - ky) * 1500), giSine

	ga1 = ga1 + a1

endin

instr 2

	;kcutoff chnget  "cutoff"
	;kresonance chnget s_yName
	;kcutoff = 5000
	;kresonance = .9

	;itie tival
	;i_instanceNum = p3

	;S_xName sprintf "touch.%d.x", i_instanceNum
	;kx chnget S_xName			;kx for modulation

	a1 moogladder ga1, 10000, .9
	aL, aR reverbsc a1, a1, .72, 5000

	outs aL, aR
	ga1 = 0

endin
</CsInstruments>

<CsScore>
	;i1 is the score passed via a csoundObject
	f1 0 16384 10 1
	i2 0 360000
</CsScore>
</CsoundSynthesizer>
