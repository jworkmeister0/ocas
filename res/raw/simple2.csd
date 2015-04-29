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
ksmps=32
sr = 44100

ga1 init 0							;initialize ga1

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
	;printk .5, kx, 10			;print kx at a rate of 2/sec
	;printk .25, ky				;print ky at a rate of 2/sec

	;kenv linsegr 0, .001, 1, .1, 1, .25, 0	;configuring the envelope

	a1 osciliktp ky, 1, kx
	ga1 = ga1 + a1
endin
instr 2

	a1 moogladder ga1, 10000, .5
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

