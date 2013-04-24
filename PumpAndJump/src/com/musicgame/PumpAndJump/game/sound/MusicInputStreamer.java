package com.musicgame.PumpAndJump.game.sound;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.io.Decoder;
import com.badlogic.gdx.audio.io.WavDecoder;
import com.badlogic.gdx.files.FileHandle;
import com.musicgame.PumpAndJump.Util.FileFormatException;
import com.musicgame.PumpAndJump.game.PumpAndJump;

/**
 * This is the thread that runs and decompiles the music
 * @author gigemjt
 *
 */
public class MusicInputStreamer extends Thread
{

	public String fileName= "the_hand_that_feeds.wav";
//	public String fileName= "Skrillex_Cinema.wav";
//	public String fileName = "Windows_XP_Startup.wav";
	Decoder decoder;
	public ArrayList<short[]> frames = new ArrayList<short[]>();
	public int currentFrame;
	public static int frameSize = 1024/4;
	public static long sampleRate = 44100;
	public boolean buffering = true;
	public boolean doneReading = false;
	//do frame stuff here

	public void loadSound() throws FileNotFoundException, FileFormatException
	{
		FileHandle file = null;
		try
		{
			file = Gdx.files.internal(fileName);
		}catch(Exception e)
		{
			e.printStackTrace();
			try
			{
				file = Gdx.files.absolute(fileName);
			}catch(Exception e2)
			{
				e2.printStackTrace();
			}
		}
		if(file == null||!file.exists())
		{
			throw new FileNotFoundException(fileName);

		}
		//it is not null and it exists

		String extension = file.extension();
		if(extension.equalsIgnoreCase("wav"))
		{
			decoder = new WavDecoder(file);
		}else if(extension.equalsIgnoreCase("mp3"))
		{
			decoder = PumpAndJump.MP3decoder.getInstance(file);
		}else
		{
			throw new FileFormatException("File format not supported "+extension);
		}
	}

	/**
	 * The running thread that decompiles the music
	 */
	public void run()
	{
		int readSong = 1;
		while(readSong != 0)
		{
			short[] buf = new short[frameSize*2];
			short[] frame2 = new short[frameSize];
			readSong = decoder.readSamples(buf,0, frameSize*2);
			for(int k=0;k<frame2.length;k++)
			{
				if(decoder.getChannels()==2)
				{
					frame2[k] = (short) ((buf[k*2]+buf[k*2+1])/2.0);//gets half of the song (maybe because it is stereo?)
				}else if(decoder.getChannels()==1)
				{
					frame2[k] = (buf[k]);//its mono so we can put it straight through
				}
			}
		//	System.out.println("Reading the song" +readSong+" "+currentFrame);
			frames.add(frame2);
			currentFrame++;
			if(buffering)
			{
				/*
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				*/
			}else
			{
			//	System.out.println("Music Input");
				/*
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				*/
			}
		}
		System.out.println("FINISHED READING THE MUSIC FILE");
		doneReading = true;
	}
}
