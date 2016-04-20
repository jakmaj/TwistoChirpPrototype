package cz.jakmaj.chirpprototype;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import io.chirp.sdk.CallbackRead;
import io.chirp.sdk.ChirpSDK;
import io.chirp.sdk.ChirpSDKListener;
import io.chirp.sdk.model.Chirp;
import io.chirp.sdk.model.ChirpError;
import io.chirp.sdk.model.ShortCode;

public class ReceiveActivity extends AppCompatActivity {
    private static final int RESULT_REQUEST_RECORD_AUDIO = 0;

    private ChirpSDK chirpSDK;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        mTextView = (TextView) findViewById(R.id.text_code);

        chirpSDK = new ChirpSDK(this, "", "");
        chirpSDK.setListener(chirpSDKListener);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RESULT_REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        chirpSDK.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        chirpSDK.stopListening();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "Granted");
                } else {
                    Log.d("permission", "Denied");
                }
            }
        }
    }

    private ChirpSDKListener chirpSDKListener = new ChirpSDKListener() {
        @Override
        public void onChirpHeard(final ShortCode shortCode) {
            Log.d("listener", "ShortCode received: " + shortCode.getShortCode());
            runOnUiThread(new Runnable() { // Chirp is listening on background thread, so need this to manipulate with UI
                @Override
                public void run() {
                    mTextView.setText(shortCode.getShortCode());
                }
            });
        }

        @Override
        public void onChirpError(ChirpError error) {
            Log.d("listener", "ShortCode received error: " + error.getMessage());
        }
    };
}
