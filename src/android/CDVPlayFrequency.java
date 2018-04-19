package me.rahul.plugins.playfrequency;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class CDVPlayFrequency extends CordovaPlugin {

    private final int duration = 10; // seconds
    private int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private double sample[] = new double[numSamples];
    private double freqOfTone = 440; // hz

    private byte generatedSnd[] = new byte[2 * numSamples];

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("playfrequency")) {

            freqOfTone = Integer.parseInt(args.getString(0));
            sampleRate = Integer.parseInt(args.getString(1));
            Log.d("CordovaLog","Frequency = "+freqOfTone);
            Log.d("CordovaLog","Sample Rate = "+sampleRate);
            this.playSound();
            return true;
        }
        else if(action.equals("stopfrequency")){
            Log.d("CordovaLog","Stoping Tone");
            return true;
        } else {
            return false;
        }
    }

    void genTone(){
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    void playSound(){
        this.genTone();
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }
}
