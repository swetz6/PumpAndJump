package com.musicgame.PumpAndJump.game.sound;


import java.util.ArrayList;


public class BeatDetector
{
	//this is the instantaneous VEdata
	public ArrayList<float[]> VEdata = new ArrayList<float[]>();
	//this is the average VEdata (over 43 Energy histories
	public ArrayList<Double> AveragedEnergydata = new ArrayList<Double>();

	public static final int historyLength = 20;//43;
	public static final int LongHistoryLength = historyLength*4;//43;

	private double[] EnergyHistory = new double[historyLength];
	int currentHistoryIndex = 0;


	ArrayList<Beat> detectedBeats = new ArrayList<Beat>();

	double maxEnergy = 0;


	int shiftAvg = historyLength/2;//this is used so that some of the future values are computed in the average of the current value

	//values used for the actual beat detection
	boolean aboveAverage = false;
	long highestIndex = 0;//the index of the highest point when it is above the beat
	double timeIndex = 0;
	long highestShiftIndex = 0;//the index of the highest point when it is above the beat (shifted for the average value
	float highestPoint;
	//the senstitivity of the beat detector:  smaller numbers remove more beats and is more strict
	double senstitivity = 0.8;

	short[] longArray = new short[MusicHandler.LargeFrameSize];

	//location for VEdata
	int counterIndex = 0;//this number always counts up
	int currentIndex = 0;//this number is the counterIndex%historyLength*2
	int shiftIndex = 0;//this number is (counterIndex-avgShift)%historyLength*2


	public BeatDetector()
	{
		for(int k = 0;k<LongHistoryLength;k++)
		{
			VEdata.add(new float[2]);
		}
	}

	public void combineArray(ArrayList<short[]> timeData,int startIndex)
	{
		for(int k = 0 ;k<4;k++)
		{
			short[] tempArray = timeData.get((k+startIndex)%MusicHandler.arraySampleLength);
			int skipIndex = k*MusicHandler.frameSize;
			for(int q = 0;q<MusicHandler.frameSize;q++)
			{
				longArray[q+skipIndex] = tempArray[q];
			}
		}
		calculateVE(longArray);
	}

	public void calculateVE(short[] timeData)
   	{
   		//the size of bits that the array is taken over
   		int averageSize = MusicHandler.LargeFrameSize;
   		//number of values
   		int index = 0;
   		float[] result = VEdata.get(currentIndex);
		float volume = 0;
		float energy = 0;
		for(int q = 0; q<averageSize;q++)
		{
			float data = timeData[index];
			volume+=data;
			energy+=data*data;

			index++;
		}
		maxEnergy = Math.max(maxEnergy, energy);
		result[0] = volume;
		result[1] = energy;
		VEdata.add(result);

		EnergyHistory[currentHistoryIndex] = energy;

		double value = 0;
		for(int q=0;q<historyLength;q++)
		{
			value+=EnergyHistory[q];
		}
		value/=historyLength;
		AveragedEnergydata.add(value);

		//moves the index for the history which is a looping value
		currentHistoryIndex++;
		currentHistoryIndex%=historyLength;

		if(counterIndex>=shiftAvg)
			beatDetectionAlgorithm();
		//moves the index for the index in VEdata
		counterIndex++;
		currentIndex%=LongHistoryLength;

   	}


	/**
	 * Goes through each point once and sees if it is large enough away from the average to be considered a beat
	 * Need to make this static and go through all beats to determine Major beats
	 */
	public void beatDetectionAlgorithm()
	{
		shiftIndex = (counterIndex-shiftAvg)%LongHistoryLength;

		float instantEnergy = VEdata.get((currentIndex))[1];
		double averageEnergy = AveragedEnergydata.get(shiftIndex);
		if(instantEnergy>=averageEnergy)
		{
			if(instantEnergy>highestPoint)
			{
				highestPoint = instantEnergy;
				highestIndex = counterIndex;
				timeIndex = (MusicHandler.inputFrame*MusicHandler.frameSize)/((double)MusicHandler.sampleRate);
				highestShiftIndex = shiftIndex;
			}
			aboveAverage = true;
		}else if(aboveAverage)
		{
			double avgEnergy = AveragedEnergydata.get((int) (highestShiftIndex));
			double division = avgEnergy/highestPoint;

			aboveAverage = false;
			if(division<senstitivity)
			{
				detectedBeats.add(new Beat(highestIndex,highestPoint,detectedBeats.size(),timeIndex));
			}
			highestPoint = 0;
			highestIndex = -1;
		}
		/**
		 * Will look for spikes that are above the average...
		 * Every spike above the average is a minor beat
		 * one the energy level crosses the average energy level we only take one spike until it falls back below
		 * This one spike is the maximum spike
		 *
		 * (maybe take two averages?)
		 */
	}

}