package net.bluecow.spectro.clipAndFrame;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEditSupport;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;

import ddf.minim.effects.BandPass;
import ddf.minim.effects.IIRFilter;

import net.bluecow.spectro.inputReader.InputDecoder;
import net.bluecow.spectro.inputReader.MP3Decoder;
import net.bluecow.spectro.math.AudioFileUtils;
import net.bluecow.spectro.math.OverlapBuffer;
import net.bluecow.spectro.painting.ClipDataChangeEvent;
import net.bluecow.spectro.painting.ClipDataChangeListener;
import net.bluecow.spectro.painting.ClipDataEdit;
import net.bluecow.spectro.windowFunctions.NullWindowFunction;
import net.bluecow.spectro.windowFunctions.VorbisWindowFunction;
import net.bluecow.spectro.windowFunctions.WindowFunction;

public class Clip
{
	private static final Logger logger = Logger.getLogger(Clip.class.getName());

	private static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100.0F, 16, 1, true, true);

	private final List<Frame> frames = new ArrayList();

	private int frameSize = 2644;

	private int overlap = 2;

	private double spectralScale = 10000.0D;
	private ClipDataEdit currentEdit;
	private final UndoableEditSupport undoEventSupport = new UndoableEditSupport();

	private final List<ClipDataChangeListener> clipDataChangeListeners = new ArrayList<ClipDataChangeListener> ();

	private WindowFunction preWindowFunction = new VorbisWindowFunction(this.frameSize);;
	private WindowFunction postWindowFunction = new NullWindowFunction();
	private IIRFilter f12800t20000;// = new BandPass(16400.0F, 3600.0f, 44100.0f );
	private IIRFilter f6400t12800;// = new BandPass(9600.0F, 3200.0f, 44100.0f );
	private IIRFilter f3200t6400;// = new BandPass(4800.0F, 1600.0f, 44100.0f );
	private IIRFilter f1600t3200;// = new BandPass(2400.0F, 800.0f, 44100.0f );
	private IIRFilter f800t1600;// = new BandPass(1200.0F, 400.0f, 44100.0f );
	private IIRFilter f400t800;// = new BandPass(600.0F, 200.0f, 44100.0f );
	private IIRFilter f200t400;// = new BandPass(300.0f, 100.0f, 44100.0f );
	private IIRFilter f100t200;// = new BandPass(150.0f, 50.0f, 44100.0f );
	private IIRFilter f50t100;// = new BandPass(75.0f, 25.0f, 44100.0f );
	private IIRFilter f25t50;// = new BandPass(37.5f, 12.5f, 44100.0f );
	private IIRFilter f12t25;// = new BandPass(18.75f, 6.25f, 44100.0f );
	private InputDecoder input;

	public Clip(File file)
		    throws UnsupportedAudioFileException, IOException
	{
		input = new MP3Decoder(spectralScale,file);
		
	}

	public void readAndFilter() throws IOException
	{
		float[] wholeArray = input.readEntireArray();
		prefilter(wholeArray);
		int index = 0;
		ArrayList<double[]> backToBuffers = new ArrayList<double[]>();
		for(int k = 0; k<wholeArray.length/this.frameSize;k++)
		{
			double[] samples = new double[this.frameSize];
			for(int i = 0; i<this.frameSize; i++)
			{
				samples[i] =(double) wholeArray[index];
				index++;
			}
			backToBuffers.add(samples);
		}
		while(backToBuffers.size()>=1)
		{
			this.frames.add(new Frame(backToBuffers.remove(0), preWindowFunction,postWindowFunction));
		}
//		logger.info(String.format("Read %d frames from %s (%d bytes). frameSize=%d overlap=%d\n", new Object[] { Integer.valueOf(this.frames.size()), file.getAbsolutePath(), Integer.valueOf(this.frames.size() * this.frameSize*2), Integer.valueOf(this.frameSize), Integer.valueOf(this.overlap) }));

	}


	/**
	 * runs the entire array through a filter to filter out certain sounds
	 * @param in
	 * @return
	 */
	private void prefilter( float[] input )
	{
		//filter.process(input);
	}

	  public int getFrameTimeSamples()
	  {
	    return this.frameSize;
	  }

	  public int getFrameFreqSamples()
	  {
	    return this.frameSize;
	  }

	  public int getFrameCount()
	  {
	    return this.frames.size();
	  }

	  public Frame getFrame(int i)
	  {
	    return (Frame)this.frames.get(i);
	  }

	  public AudioInputStream getAudio()
	  {
	    return getAudio(0);
	  }

	  public AudioInputStream getAudio(int sample)
	  {
	    final int initialFrame = sample / getFrameTimeSamples();

	    InputStream audioData = new InputStream()
	    {
	      int nextFrame = initialFrame;

	      OverlapBuffer overlapBuffer = new OverlapBuffer(Clip.this.frameSize, Clip.this.overlap);
	      int currentSample;
	      boolean currentByteHigh = true;

	      int emptyFrameCount = 0;

	      public int available() throws IOException
	      {
	        return 2147483647;
	      }

	      public int read() throws IOException
	      {
	        if (this.overlapBuffer.needsNewFrame()) {
	          if (this.nextFrame < Clip.this.frames.size()) {
	            Frame f = (Frame)Clip.this.frames.get(this.nextFrame++);
	            this.overlapBuffer.addFrame(f.asTimeData());
	          } else {
	            this.overlapBuffer.addEmptyFrame();
	            this.emptyFrameCount += 1;
	          }
	        }

	        if (this.emptyFrameCount >= Clip.this.overlap)
	          return -1;
	        if (this.currentByteHigh) {
	          this.currentSample = (int)(this.overlapBuffer.next() * Clip.this.spectralScale);
	          this.currentByteHigh = false;
	          return this.currentSample >> 8 & 0xFF;
	        }
	        this.currentByteHigh = true;
	        return this.currentSample & 0xFF;
	      }
	    };
	    int length = getFrameCount() * getFrameTimeSamples() * (AUDIO_FORMAT.getSampleSizeInBits() / 8) / this.overlap;
	    return new AudioInputStream(audioData, AUDIO_FORMAT, length);
	  }

	  public void beginEdit(Rectangle region, String description)
	  {
	    if (this.currentEdit != null) {
	      throw new IllegalStateException("Already in an edit: " + this.currentEdit);
	    }
	    this.currentEdit = new ClipDataEdit(this, region.x, region.y, region.width, region.height);
	  }

	  public void endEdit()
	  {
	    if (this.currentEdit == null) {
	      throw new IllegalStateException("No edit is in progress");
	    }
	    this.currentEdit.captureNewData();
	    this.undoEventSupport.postEdit(this.currentEdit);
	    regionChanged(this.currentEdit.getRegion());
	    this.currentEdit = null;
	  }

	  public void beginCompoundEdit(String presentationName)
	  {
	    this.undoEventSupport.beginUpdate();
	  }

	  public void endCompoundEdit()
	  {
	    this.undoEventSupport.endUpdate();
	  }

	  public void regionChanged(Rectangle region)
	  {
	    fireClipDataChangeEvent(region);
	  }

	  public void addClipDataChangeListener(ClipDataChangeListener l)
	  {
	    this.clipDataChangeListeners.add(l);
	  }

	  public void removeClipDataChangeListener(ClipDataChangeListener l) {
	    this.clipDataChangeListeners.remove(l);
	  }

	  private void fireClipDataChangeEvent(Rectangle region) {
	    ClipDataChangeEvent e = new ClipDataChangeEvent(this, region);
	    for (int i = this.clipDataChangeListeners.size() - 1; i >= 0; i--)
	      ((ClipDataChangeListener)this.clipDataChangeListeners.get(i)).clipDataChanged(e);
	  }

	  public void addUndoableEditListener(UndoableEditListener l)
	  {
	    this.undoEventSupport.addUndoableEditListener(l);
	  }

	  public UndoableEditListener[] getUndoableEditListeners() {
	    return this.undoEventSupport.getUndoableEditListeners();
	  }

	  public void removeUndoableEditListener(UndoableEditListener l) {
	    this.undoEventSupport.removeUndoableEditListener(l);
	  }

	  public double getSamplingRate() {
	    return AUDIO_FORMAT.getSampleRate();
	  }
	}