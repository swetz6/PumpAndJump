package edu.emory.mathcs.jtransforms.dct;

import edu.emory.mathcs.utils.ConcurrencyUtils;
import edu.emory.mathcs.utils.IOUtils;
import java.io.PrintStream;

public class BenchmarkFloatDCT
{
  private static int nthread = 2;
  private static int nsize = 6;
  private static int niter = 200;
  private static boolean doWarmup = true;
  private static int initialExponent1D = 17;
  private static int initialExponent2D = 7;
  private static int initialExponent3D = 2;
  private static boolean doScaling = false;

  public static void parseArguments(String[] paramArrayOfString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++)
      System.out.println("args[" + i + "]:" + paramArrayOfString[i]);
    if ((paramArrayOfString == null) || (paramArrayOfString.length != 10))
    {
      System.out.println("Parameters: <number of threads> <THREADS_BEGIN_N_2D> <THREADS_BEGIN_N_3D> <number of iterations> <perform warm-up> <perform scaling> <number of sizes> <initial exponent for 1D transforms> <initial exponent for 2D transforms> <initial exponent for 3D transforms>");
      System.exit(-1);
    }
    nthread = Integer.parseInt(paramArrayOfString[0]);
    ConcurrencyUtils.setThreadsBeginN_2D(Integer.parseInt(paramArrayOfString[1]));
    ConcurrencyUtils.setThreadsBeginN_3D(Integer.parseInt(paramArrayOfString[2]));
    niter = Integer.parseInt(paramArrayOfString[3]);
    doWarmup = Boolean.parseBoolean(paramArrayOfString[4]);
    doScaling = Boolean.parseBoolean(paramArrayOfString[5]);
    nsize = Integer.parseInt(paramArrayOfString[6]);
    initialExponent1D = Integer.parseInt(paramArrayOfString[7]);
    initialExponent2D = Integer.parseInt(paramArrayOfString[8]);
    initialExponent3D = Integer.parseInt(paramArrayOfString[9]);
    ConcurrencyUtils.setNumberOfProcessors(nthread);
  }

  public static void benchmarkForward_1D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Forward DCT 1D of size 2^" + j);
      FloatDCT_1D localFloatDCT_1D = new FloatDCT_1D(k);
      float[] arrayOfFloat = new float[k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_1D(k, arrayOfFloat);
        localFloatDCT_1D.forward(arrayOfFloat, doScaling);
        IOUtils.fillMatrix_1D(k, arrayOfFloat);
        localFloatDCT_1D.forward(arrayOfFloat, doScaling);
      }
      float f = 0.0F;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_1D(k, arrayOfFloat);
        l = System.nanoTime();
        localFloatDCT_1D.forward(arrayOfFloat, doScaling);
        l = System.nanoTime() - l;
        f += (float)l;
      }
      arrayOfDouble[i] = (f / 1000000.0D / niter);
      System.out.println("Average execution time: " + String.format("%.2f", new Object[] { Double.valueOf(f / 1000000.0D / niter) }) + " msec");
      arrayOfFloat = null;
      localFloatDCT_1D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkFloatForwardDCT_1D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble);
  }

  public static void benchmarkForward_2D_input_1D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Forward DCT 2D (input 1D) of size 2^" + j + " x 2^" + j);
      FloatDCT_2D localFloatDCT_2D = new FloatDCT_2D(k, k);
      float[] arrayOfFloat = new float[k * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_2D(k, k, arrayOfFloat);
        localFloatDCT_2D.forward(arrayOfFloat, doScaling);
        IOUtils.fillMatrix_2D(k, k, arrayOfFloat);
        localFloatDCT_2D.forward(arrayOfFloat, doScaling);
      }
      float f = 0.0F;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_2D(k, k, arrayOfFloat);
        l = System.nanoTime();
        localFloatDCT_2D.forward(arrayOfFloat, doScaling);
        l = System.nanoTime() - l;
        f += (float)l;
      }
      arrayOfDouble[i] = (f / 1000000.0D / niter);
      System.out.println("Average execution time: " + String.format("%.2f", new Object[] { Double.valueOf(f / 1000000.0D / niter) }) + " msec");
      arrayOfFloat = null;
      localFloatDCT_2D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkFloatForwardDCT_2D_input_1D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble);
  }

  public static void benchmarkForward_2D_input_2D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Forward DCT 2D (input 2D) of size 2^" + j + " x 2^" + j);
      FloatDCT_2D localFloatDCT_2D = new FloatDCT_2D(k, k);
      float[][] arrayOfFloat = new float[k][k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_2D(k, k, arrayOfFloat);
        localFloatDCT_2D.forward(arrayOfFloat, doScaling);
        IOUtils.fillMatrix_2D(k, k, arrayOfFloat);
        localFloatDCT_2D.forward(arrayOfFloat, doScaling);
      }
      float f = 0.0F;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_2D(k, k, arrayOfFloat);
        l = System.nanoTime();
        localFloatDCT_2D.forward(arrayOfFloat, doScaling);
        l = System.nanoTime() - l;
        f += (float)l;
      }
      arrayOfDouble[i] = (f / 1000000.0D / niter);
      System.out.println("Average execution time: " + String.format("%.2f", new Object[] { Double.valueOf(f / 1000000.0D / niter) }) + " msec");
      arrayOfFloat = (float[][])null;
      localFloatDCT_2D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkFloatForwardDCT_2D_input_2D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble);
  }

  public static void benchmarkForward_3D_input_1D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Forward DCT 3D (input 1D) of size 2^" + j + " x 2^" + j + " x 2^" + j);
      FloatDCT_3D localFloatDCT_3D = new FloatDCT_3D(k, k, k);
      float[] arrayOfFloat = new float[k * k * k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_3D(k, k, k, arrayOfFloat);
        localFloatDCT_3D.forward(arrayOfFloat, doScaling);
        IOUtils.fillMatrix_3D(k, k, k, arrayOfFloat);
        localFloatDCT_3D.forward(arrayOfFloat, doScaling);
      }
      float f = 0.0F;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_3D(k, k, k, arrayOfFloat);
        l = System.nanoTime();
        localFloatDCT_3D.forward(arrayOfFloat, doScaling);
        l = System.nanoTime() - l;
        f += (float)l;
      }
      arrayOfDouble[i] = (f / 1000000.0D / niter);
      System.out.println("Average execution time: " + String.format("%.2f", new Object[] { Double.valueOf(f / 1000000.0D / niter) }) + " msec");
      arrayOfFloat = null;
      localFloatDCT_3D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkFloatForwardDCT_3D_input_1D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble);
  }

  public static void benchmarkForward_3D_input_3D(int paramInt)
  {
    int[] arrayOfInt = new int[nsize];
    double[] arrayOfDouble = new double[nsize];
    for (int i = 0; i < nsize; i++)
    {
      int j = paramInt + i;
      int k = (int)Math.pow(2.0D, j);
      arrayOfInt[i] = k;
      System.out.println("Forward DCT 3D (input 3D) of size 2^" + j + " x 2^" + j + " x 2^" + j);
      FloatDCT_3D localFloatDCT_3D = new FloatDCT_3D(k, k, k);
      float[][][] arrayOfFloat = new float[k][k][k];
      if (doWarmup)
      {
        IOUtils.fillMatrix_3D(k, k, k, arrayOfFloat);
        localFloatDCT_3D.forward(arrayOfFloat, doScaling);
        IOUtils.fillMatrix_3D(k, k, k, arrayOfFloat);
        localFloatDCT_3D.forward(arrayOfFloat, doScaling);
      }
      float f = 0.0F;
      long l = 0L;
      for (int m = 0; m < niter; m++)
      {
        IOUtils.fillMatrix_3D(k, k, k, arrayOfFloat);
        l = System.nanoTime();
        localFloatDCT_3D.forward(arrayOfFloat, doScaling);
        l = System.nanoTime() - l;
        f += (float)l;
      }
      arrayOfDouble[i] = (f / 1000000.0D / niter);
      System.out.println("Average execution time: " + String.format("%.2f", new Object[] { Double.valueOf(f / 1000000.0D / niter) }) + " msec");
      arrayOfFloat = (float[][][])null;
      localFloatDCT_3D = null;
      System.gc();
    }
    IOUtils.writeFFTBenchmarkResultsToFile("benchmarkFloatForwardDCT_3D_input_3D.txt", nthread, niter, doWarmup, doScaling, arrayOfInt, arrayOfDouble);
  }

  public static void main(String[] paramArrayOfString)
  {
    parseArguments(paramArrayOfString);
    benchmarkForward_1D(initialExponent1D);
    benchmarkForward_2D_input_1D(initialExponent2D);
    benchmarkForward_2D_input_2D(initialExponent2D);
    benchmarkForward_3D_input_1D(initialExponent3D);
    benchmarkForward_3D_input_3D(initialExponent3D);
    System.exit(0);
  }
}

/* Location:           /Users/gigemjt/workspace/PumpAndJump/PumpAndJump/resources/spectro-edit_0.4 /
 * Qualified Name:     edu.emory.mathcs.jtransforms.dct.BenchmarkFloatDCT
 * JD-Core Version:    0.6.1
 */