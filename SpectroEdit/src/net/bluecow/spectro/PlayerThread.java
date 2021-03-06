/*     */ package net.bluecow.spectro;
/*     */
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Logger;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ import javax.sound.sampled.LineUnavailableException;
/*     */ import javax.sound.sampled.SourceDataLine;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;

import net.bluecow.spectro.clipAndFrame.Clip;
import net.bluecow.spectro.painting.PlaybackPositionEvent;
import net.bluecow.spectro.painting.PlaybackPositionListener;
/*     */
/*     */ public class PlayerThread extends Thread
/*     */ {
/*     */
/*  43 */   private boolean playing = false;
/*     */
/*  51 */   private boolean terminated = false;
/*     */   private SourceDataLine outputLine;
/*     */   private final Clip clip;
/*     */   private AudioInputStream in;
/*     */   private long outputLinePositionOffset;
/*     */   private int startSample;
/* 258 */   private final List<ChangeListener> changeListeners = new ArrayList();
/*     */
/* 287 */   private final List<PlaybackPositionListener> playbackPositionListeners = new ArrayList();
/*     */
/*     */   public PlayerThread(Clip clip)
/*     */     throws LineUnavailableException
/*     */   {
/*  90 */     this.clip = clip;
/*     */   }
/*     */
/*     */   public void run()
/*     */   {
/*  95 */     if (this.in == null)
/*  96 */       setPlaybackPosition(0);
/*     */     try
/*     */     {
/*  99 */       AudioFormat outputFormat = this.in.getFormat();
/* 100 */       this.outputLine = AudioSystem.getSourceDataLine(outputFormat);
/* 102 */       this.outputLine.open();
/*     */
/* 104 */       byte[] buf = new byte[this.outputLine.getBufferSize()];
/*     */
/* 106 */       while (!this.terminated)
/*     */       {
/* 108 */         boolean reachedEOF = false;
/* 110 */         fireStateChanged();
/* 111 */         this.outputLine.start();
/*     */
/* 113 */         while ((this.playing) && (!reachedEOF)) {
/* 114 */           synchronized (this) {
/* 115 */             int readSize = Math.min(this.outputLine.available(), 4096);
/* 116 */             int len = this.in.read(buf, 0, readSize);
/* 117 */             if (len != readSize) {
							System.out.println("Didn't read full "+readSize+ "bytes (got "+len+")");
/*     */             }
/* 120 */             if (len == -1)
/* 121 */               reachedEOF = true;
/*     */             else {
/* 123 */               this.outputLine.write(buf, 0, len);
/*     */             }
/*     */           }
/* 126 */           firePlaybackPositionUpdate(getPlaybackPosition());
/*     */         }
/*     */
/* 129 */         if (this.playing)
/*     */         {
					System.out.println("Draining output line...");
/*     */
/* 134 */           long lastPlaybackPos = 0L;
/*     */
/* 136 */           while (this.outputLine.isRunning()) {
/*     */             try {
/* 138 */               Thread.sleep(30L);
/*     */             } catch (InterruptedException ex) {
						System.out.println("Interrupted while draining output line");
/*     */             }
/*     */
/* 143 */             firePlaybackPositionUpdate(getPlaybackPosition());
/*     */
/* 148 */             if (lastPlaybackPos == getPlaybackPosition())
/*     */               break;
/* 150 */             lastPlaybackPos = getPlaybackPosition();
/*     */           }
					System.out.println("Finished draining output line");
/*     */         }
/*     */         else
/*     */         {
/* 157 */           this.outputLine.stop();
/*     */         }
/*     */
/* 160 */         if (reachedEOF) {
/* 161 */           this.playing = false;
/* 162 */           setPlaybackPosition(0);
/*     */         }
/* 166 */         fireStateChanged();
/*     */         while (true)
/*     */         {
/* 169 */           synchronized (this) {
/* 170 */             if ((this.playing) || (this.terminated)) break;
/*     */           }
/*     */           try
/*     */           {
/* 175 */             sleep(10000L);
/*     */           } catch (InterruptedException ex) {
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 182 */       e.printStackTrace();
/*     */     } finally {
/* 184 */       if (this.outputLine != null) {
/* 185 */         this.outputLine.close();
/* 186 */         this.outputLine = null;
/*     */       }
/*     */     }
/*     */   }
/*     */
/*     */   public synchronized void stopPlaying() {
/* 193 */     this.playing = false;
/*     */   }
/*     */
/*     */   public synchronized void startPlaying()
/*     */   {
/* 198 */     this.playing = true;
/* 199 */     interrupt();
/*     */   }
/*     */
/*     */   public synchronized boolean isPlaying() {
/* 203 */     return this.playing;
/*     */   }
/*     */
/*     */   public synchronized void terminate()
/*     */   {
/* 210 */     stopPlaying();
/* 211 */     this.terminated = true;
/* 212 */     interrupt();
/*     */   }
/*     */
/*     */   public synchronized void setPlaybackPosition(int sample)
/*     */   {
/* 223 */     if (this.in != null) {
/*     */       try {
/* 225 */         this.in.close();
/*     */       } catch (IOException ex) {
/* 227 */         throw new RuntimeException(ex);
/*     */       }
/*     */     }
/* 230 */     if (this.outputLine != null) {
/* 231 */       this.outputLine.stop();
/* 232 */       this.outputLine.flush();
/* 233 */       this.outputLine.start();
/*     */     }
/* 235 */     if (this.outputLine != null)
/* 236 */       this.outputLinePositionOffset = this.outputLine.getLongFramePosition();
/*     */     else {
/* 238 */       this.outputLinePositionOffset = 0L;
/*     */     }
/* 240 */     this.startSample = sample;
/* 241 */     this.in = this.clip.getAudio(sample);
/* 242 */     firePlaybackPositionUpdate(getPlaybackPosition());
/*     */   }
/*     */
/*     */   public long getPlaybackPosition()
/*     */   {
/* 249 */     if (this.outputLine == null) {
/* 250 */       return 0L;
/*     */     }
/* 252 */     AudioFormat format = this.outputLine.getFormat();
/* 253 */     long elapsedSamples = (this.outputLine.getLongFramePosition() - this.outputLinePositionOffset) * format.getFrameSize();
/* 254 */     return elapsedSamples + this.startSample;
/*     */   }
/*     */
/*     */   public void addChangeListener(ChangeListener l)
/*     */   {
/* 272 */     this.changeListeners.add(l);
/*     */   }
/*     */
/*     */   public void removeChangeListener(ChangeListener l) {
/* 276 */     this.changeListeners.remove(l);
/*     */   }
/*     */
/*     */   private void fireStateChanged() {
/* 281 */     ChangeEvent e = new ChangeEvent(this);
/* 282 */     for (int i = this.changeListeners.size() - 1; i >= 0; i--)
/* 283 */       ((ChangeListener)this.changeListeners.get(i)).stateChanged(e);
/*     */   }
/*     */
/*     */   public void addPlaybackPositionListener(PlaybackPositionListener l)
/*     */   {
/* 290 */     this.playbackPositionListeners.add(l);
/*     */   }
/*     */
/*     */   public void removePlaybackPositionListener(PlaybackPositionListener l) {
/* 294 */     this.playbackPositionListeners.remove(l);
/*     */   }
/*     */
/*     */   public void firePlaybackPositionUpdate(long samplePos) {
/* 299 */     PlaybackPositionEvent e = new PlaybackPositionEvent(this, samplePos);
/* 300 */     for (int i = this.playbackPositionListeners.size() - 1; i >= 0; i--)
/* 301 */       ((PlaybackPositionListener)this.playbackPositionListeners.get(i)).playbackPositionUpdate(e);
/*     */   }
/*     */ }

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     net.bluecow.spectro.PlayerThread
 * JD-Core Version:    0.6.1
 */