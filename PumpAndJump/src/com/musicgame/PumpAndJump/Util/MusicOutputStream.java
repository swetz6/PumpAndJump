package com.musicgame.PumpAndJump.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;

public class MusicOutputStream
{
	int latency;
	AudioDevice device;
	public MusicOutputStream()
	{
		device = Gdx.audio.newAudioDevice(44100, true);
		latency = device.getLatency();
	}

	/**
	 * Reads in numSamples at offset into the samples array
	 * it also blocks so send it very short samples
	 * @param input
	 * @param offset
	 * @param numSamples
	 * @see com.badlogic.gdx.audio.AudioDevice#writeSamples(short[], int, int)
	 * @return
	 */
	public void writeData(short[] input, int offset, int numSamples)
	{
		device.writeSamples(input, offset, numSamples);
	}

	public void write(short[] input)
	{
	//	System.out.println(getLatancy());
		writeData(input,0,input.length);
	//	System.out.println(getLatancy());
	}

	public int getLatancy()
	{
		return device.getLatency();
	}
}
