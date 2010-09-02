package com.android.kino.logic.interceptor;

import java.util.Date;

import com.android.kino.logic.event.DoubleTapEvent;

import android.util.Log;

public class DoubleTapInterceptor extends AudioReader.Listener implements Runnable, InputEventInterceptor {
    private static final String TAG = "DoubleTapInterceptor";
    
    private static final float MIN_SILENCE = 5f;

    // TODO Move to advanced settings
    private static final int SAMPLE_EVERY = 30;
    
    private static final float MIN_TAP_NOISE = 30f;
    private static final float MAX_TAP_NOISE = 50f;
    private static final int MIN_NEEDED_SILENCE = 20;
    // TODO Move to advanced settings
    private static final int TAP_MIN_SPACE = 4;
    // TODO Move to advanced settings
    private static final int TAP_MAX_SPACE = 10;
    // TODO Move to advanced settings
    private static final short TAP_MIN_MAX = 10000;

    // Maximum signal amplitude for 16-bit data.
    private static final float MAX_16_BIT = 32768;
    
    // This fudge factor is added to the output to make a realistically
    // fully-saturated signal come to 0dB.  Without it, the signal would
    // have to be solid samples of -32768 to read zero, which is not
    // realistic.  This really is a fudge, because the best value depends
    // on the input frequency and sampling rate.  We optimize here for
    // a 1kHz signal at 16,000 samples/sec.
    private static final float FUDGE = 55.6f;
    
    private float mLastPower;
    
    public boolean mStop = false;
    
    private AudioReader mAudio;

    private Thread mSamplingThread;
    
    private int mSilenceCount = 0;
    private boolean mHadFirstTap = false;
    
    private InputEventListener mListener = null;

	private short mLastMax;
    
    public DoubleTapInterceptor() {
        mLastPower = MIN_SILENCE;
        mLastMax = 0;
        
        mAudio = new AudioReader();
    }
    
    @Override
    public void run() {
        long runtime = 0;
        while (!mStop) {
            if (SAMPLE_EVERY > runtime) {
                try {
                    Thread.sleep(SAMPLE_EVERY - runtime);
                }
                catch (InterruptedException e) {
                    break;
                }
            }
            long before = new Date().getTime();
            takeSample();
            runtime = new Date().getTime() - before;
        }
    }

    @Override
    public void setListener(InputEventListener listener) {
        mListener = listener;
    }

    @Override
    public void startIntercepting() {
        mAudio.startReader(8000, 256, this);
        
        mSamplingThread = new Thread(this);
        mSamplingThread.start();
    }

    @Override
    public void stopIntercepting() {
        mStop = true;
        mAudio.stopReader();
    }

    @Override
    public void onReadComplete(short[] buffer) {
    	long sum = 0;
    	short max = 0;
    	short s;
    	for (int i = 0; i < buffer.length; ++i) {
    		s = (short) Math.abs(buffer[i]);
    		if (s > max) {
    			max = s;
    		}
    		sum += s;
    	}
    	mLastMax = max;
        mLastPower = (float) calculatePowerDb(buffer);
    }

    @Override
    public void onReadError(int error) {
        // Ignore
    }
    
    private void takeSample() {
        float sample = Math.abs(mLastPower);
        short max = mLastMax;
        
        if (max > TAP_MIN_MAX && sample >= MIN_TAP_NOISE && sample <= MAX_TAP_NOISE) {
            Log.v(TAG, String.format("Silence interrupted after %d. s: %f\tm: %d", mSilenceCount, sample, max));
            if (!mHadFirstTap) {
                // First tap is registered only after long enough silence
                mHadFirstTap = mSilenceCount >= MIN_NEEDED_SILENCE;
            }
            else if (mSilenceCount >= TAP_MIN_SPACE && mSilenceCount <= TAP_MAX_SPACE) {
                mHadFirstTap = false;
                Log.w(TAG, "TAP!");
                if (mListener != null) {
                    mListener.onEventTriggered(DoubleTapEvent.ID);
                }
            }
            else {
                mHadFirstTap = false;
            }
            mSilenceCount = 0;
        }
        else if (sample >= MIN_TAP_NOISE) {
            Log.v(TAG, String.format("Noise... s: %f\tm: %d", sample, max));
            mHadFirstTap = false;
            mSilenceCount = 0;
        }
        else if (mSilenceCount < MIN_NEEDED_SILENCE) {
            ++mSilenceCount;
        }
    }

    /**
     * Calculate the power of the given input signal.
     * 
     * @param   sdata       Buffer containing the input samples to process.
     */
    private final static double calculatePowerDb(short[] sdata) {
        // Calculate the sum of the values, and the sum of the squared values.
        // We need longs to avoid running out of bits.
        double sum = 0;
        double sqsum = 0;
        for (int i = 0; i < sdata.length; i++) {
            final long v = sdata[i];
            sum += v;
            sqsum += v * v;
        }
        
        // sqsum is the sum of all (signal+bias)², so
        //     sqsum = sum(signal²) + samples * bias²
        // hence
        //     sum(signal²) = sqsum - samples * bias²
        // Bias is simply the average value, i.e.
        //     bias = sum / samples
        // Since power = sum(signal²) / samples, we have
        //     power = (sqsum - samples * sum² / samples²) / samples
        // so
        //     power = (sqsum - sum² / samples) / samples
        double power = (sqsum - sum * sum / sdata.length) / sdata.length;

        // Scale to the range 0 - 1.
        power /= MAX_16_BIT * MAX_16_BIT;

        // Convert to dB, with 0 being max power.  Add a fudge factor to make
        // a "real" fully saturated input come to 0 dB.
        return Math.log10(power) * 10f + FUDGE;
    }
}
