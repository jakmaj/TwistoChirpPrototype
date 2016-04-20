package cz.jakmaj.chirpprototype;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.chirp.sdk.CallbackCreate;
import io.chirp.sdk.ChirpSDK;
import io.chirp.sdk.ChirpSDKHelpers;
import io.chirp.sdk.ChirpSDKListener;
import io.chirp.sdk.model.Chirp;
import io.chirp.sdk.model.ChirpError;
import io.chirp.sdk.model.ShortCode;

public class SendActivity extends AppCompatActivity {
    private static final int RESULT_REQUEST_RECORD_AUDIO = 0;

    private ChirpSDK chirpSDK;

    private TextView mTextView;
    private Button mButtonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        chirpSDK = new ChirpSDK(this, "", ""); // API keys not required
        chirpSDK.setListener(chirpSDKListener); // although we are sending chirp, we need at least empty listener and be listening

        mTextView = (TextView) findViewById(R.id.text_code);
        mButtonSend = (Button) findViewById(R.id.button_send);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShortCode code = ChirpSDKHelpers.generateRandomShortcode(); // generates random valid code (needs to be 10 chars...)

                mTextView.setText(code.getShortCode());

                chirpSDK.play(code);
            }
        });

        // for Android 6 need to request permission, manifest is not enough
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
        public void onChirpHeard(ShortCode shortCode) {
        }

        @Override
        public void onChirpError(ChirpError error) {
        }
    };
}
