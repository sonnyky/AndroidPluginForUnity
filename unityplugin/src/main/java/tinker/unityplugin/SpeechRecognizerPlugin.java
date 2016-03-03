package tinker.unityplugin;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerNativeActivity;

import java.util.ArrayList;

import static android.speech.SpeechRecognizer.ERROR_AUDIO;
import static android.speech.SpeechRecognizer.ERROR_CLIENT;
import static android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS;
import static android.speech.SpeechRecognizer.ERROR_NETWORK;
import static android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT;
import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;
import static android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
import static android.speech.SpeechRecognizer.ERROR_SERVER;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
import static android.speech.SpeechRecognizer.createSpeechRecognizer;


/**
 * Created by sonny.kurniawan on 2016/03/02.
 */
public class SpeechRecognizerPlugin extends RecognitionService implements RecognitionListener{
    private SpeechRecognizer m_EngineSR;
    public static UnityPlayerNativeActivity mActivity;
    static String TAG = "VOICE RECOGNITION";

    /**
     * StartListening**************************************************
     * Static function call by the c# to launch the service SpeechService
     */
    public static void StartListening(UnityPlayerNativeActivity activity) {
        Log.i(TAG, "START THE SERVICE! ");

        if( UnityPlayer.currentActivity != null) {
            Log.i(TAG, "STARTING THE SERVICE! ");
            UnityPlayer.UnitySendMessage("CameraMovement", "ReceiveMessageFromAndroid", "TRY 2 RESTART SERVICE");
            mActivity = activity;
            Intent intent = new Intent(mActivity, SpeechRecognizerPlugin.class);
            mActivity.startService(intent);
        }else{
            UnityPlayer.UnitySendMessage("CameraMovement", "ReceiveMessageFromAndroid", "Succesfully Started Speech Recognition");
        }

    }
    //The service is created and voice recognition service has been started
    @Override
    public void onCreate() {

        Log.i(TAG, "onCreate()");
        m_EngineSR = createSpeechRecognizer(this);
        m_EngineSR.setRecognitionListener(this);
        Intent voiceIntent = RecognizerIntent.getVoiceDetailsIntent(getApplicationContext());
        m_EngineSR.startListening(voiceIntent);
        super.onCreate();
    }

    private void checkForCommands(Bundle bundle) {
        ArrayList<String> voiceText = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (voiceText != null) {
            //SendToUnity("");
            if (voiceText.size() > 0) {
                SendToUnity(voiceText.get(0));
            } else {
                SendToUnity("nothing!");
            }

        } else {
            SendToUnity("voiceText empty!");
        }
    }

    /**
     * Send the data to Unity
     * @param text text or errors string
     */
    public void SendToUnity(String text){
        if(m_EngineSR!=null) {

            if(text != null && text.isEmpty() )Log.i("TESTING: ", "final message! =" + text);
            try {

                if( UnityPlayer.currentActivity != null) UnityPlayer.UnitySendMessage("CubeMessage", "GetMessage", text);

            } catch (Exception e) {
                // Log.e(TAG, "UnitySendMessage failed" + e.getMessage());
            }
            // m_EngineSR.stopListening();
        }


        this.onDestroy();
    }

    /**
     * Send an error to Unity
     * @param text errors string
     */
    public void SendErrorToUnity(String text){
        if(m_EngineSR!=null) {
            try {

                if( UnityPlayer.currentActivity != null) UnityPlayer.UnitySendMessage("CubeMessage", "GetError", text);

            } catch (Exception e) {
                // Log.e(TAG, "UnitySendMessage failed" + e.getMessage());
            }
            // m_EngineSR.stopListening();
        }
    }


    @Override
    public void onRmsChanged(float rmsdB){
    }
    @Override
    public void onReadyForSpeech(Bundle params) {
    }
    @Override
    public void onBufferReceived(byte[] buffer) {
    }
    @Override
    public void onBeginningOfSpeech() {
    }
    @Override
    public void onEndOfSpeech() {
    }
    @Override
    protected void onCancel(Callback listener) {
    }
    @Override
    public void onResults(Bundle results) {
        //checkForCommands(results);
    }
    @Override
     public void onPartialResults(Bundle partialResults) {
        //checkForCommands(partialResults);
    }
    @Override
    protected void onStartListening(Intent recognizerIntent, Callback listener) {
    }
    @Override
      protected void onStopListening(Callback listener) {
        //SendStopWritingUnity();
    }
    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    @Override
    public void onError(int error) {
        try {


            String message;
            switch (error)
            {
                case ERROR_AUDIO:
                    message = "Audio recording error";
                    break;
                case ERROR_CLIENT:
                    message = "Client side error";
                    break;
                case ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    break;
                case ERROR_NETWORK:
                    message = "Network error";
                    break;
                case ERROR_NETWORK_TIMEOUT:
                    message = "Network timeout";
                    break;
                case ERROR_NO_MATCH:
                    message = "No match";
                    break;
                case ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy";
                    break;
                case ERROR_SERVER:
                    message = "error from server";
                    break;
                case ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Didn't understand, please try again.";
                    break;
            }

           SendErrorToUnity(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //m_SRListener.onError(error);
    }



}