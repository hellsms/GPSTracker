package fii.licenta.ioana.nsd;

import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ioana on 20/04/2016.
 */
public class NsdActivity extends AppCompatActivity {

    private NsdHelper mNsdHelper;
    private ChatConnection mConnection;
    private Handler mUpdateHandler;

    public static final String TAG = "NsdCoinFlip";

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);
        textView = (TextView) findViewById(R.id.textView1);
        mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String chatLine = msg.getData().getString("msg");
                Toast.makeText(getApplicationContext(), chatLine, Toast.LENGTH_SHORT).show();
                String existingText = textView.getText().toString();
                textView.setText(existingText + chatLine);
            }
        };
    }

    public void advertise() {
        Toast.makeText(this, "Initiating ServerSocket bounding.", Toast.LENGTH_SHORT).show();
        if (mConnection.getLocalPort() > -1) {
            mNsdHelper.registerService(mConnection.getLocalPort());
            Toast.makeText(this, "ServerSocket bounded. Waiting for connections.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "ServerSocket isn't bound.", Toast.LENGTH_SHORT).show();
        }
    }

    public void startDiscover() {
        Toast.makeText(this, "Initiating service discovery.", Toast.LENGTH_SHORT).show();
        mNsdHelper.discoverServices();
        Toast.makeText(this, "Looking for connections.", Toast.LENGTH_LONG).show();
    }

    public void stopDiscover() {
        if (mNsdHelper != null) {
            mNsdHelper.stopDiscovery();
        }
    }

    public int connect() {
        Toast.makeText(this, "Initiating ClientSocket connection.", Toast.LENGTH_SHORT).show();
        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
        if (service != null) {
            mConnection.connectToServer(service.getHost(),
                    service.getPort());
            Toast.makeText(this, "Connected.", Toast.LENGTH_LONG).show();
            return 0;
        } else {
            Toast.makeText(this, "No service to connect to!", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mConnection = new ChatConnection(mUpdateHandler);

        mNsdHelper = new NsdHelper(this);
        mNsdHelper.initializeNsd();

        startDiscover();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int connectionResult = connect();
        if (connectionResult == 0) {
            textView.setText("Client terminal.");
        }
        if (connectionResult == -1) {
            stopDiscover();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            advertise();
            textView.setText("Server terminal.");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNsdHelper != null) {
            mNsdHelper.stopDiscovery();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
/*        if (mNsdHelper != null) {
            mNsdHelper.discoverServices();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mNsdHelper != null) {
            mNsdHelper.tearDown();
        }
        if (mConnection != null) {
            mConnection.tearDown();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNsdHelper != null)
            mNsdHelper.tearDown();
        if (mConnection != null)
            mConnection.tearDown();

    }
}
