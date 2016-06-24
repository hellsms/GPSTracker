package fii.licenta.ioana.securebitexchange.application;

import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

import fii.licenta.ioana.securebitexchange.R;
import fii.licenta.ioana.securebitexchange.nsd.NsdHelper;
import fii.licenta.ioana.securebitexchange.protocol.ProtocolClient;
import fii.licenta.ioana.securebitexchange.protocol.ProtocolServer;

/**
 * Created by Ioana on 20/04/2016.
 */
public class MainActivity extends AppCompatActivity {

    private NsdHelper mNsdHelper;

    public static final String TAG = "MainActivity";

    private TextView terminalTextView;
    private TextView connectionTextView;
    private TextView exchangeTextView;
    private Button startButton;
    private Button okButton;

    private int port;
    private InetAddress host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);

        terminalTextView = (TextView) findViewById(R.id.textView1);
        connectionTextView = (TextView) findViewById(R.id.textView2);
        connectionTextView.setVisibility(View.INVISIBLE);
        exchangeTextView = (TextView) findViewById(R.id.textView3);
        exchangeTextView.setVisibility(View.INVISIBLE);

        startButton = (Button) findViewById(R.id.button1);
        startButton.setVisibility(View.INVISIBLE);
        okButton = (Button) findViewById(R.id.button2);
        okButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mNsdHelper = new NsdHelper(this);
        mNsdHelper.initializeNsd();
        Handler handler = new Handler(Looper.getMainLooper());

        mNsdHelper.discoverServices();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
                if (service != null && mNsdHelper.resolved) {
                    port = service.getPort();
                    host = service.getHost();
                    System.out.println("Conectat la " + String.valueOf(host) + " " + port);
                    mNsdHelper.stopDiscovery();
                    terminalTextView.setText("Client terminal.");

                    initClientButton();
                    connectionTextView.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.VISIBLE);
                }
            }
        }, 3000);

        mNsdHelper.stopDiscovery();

        mNsdHelper.initializeServerSocket();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println(TAG + " S-a inregistrat? " + mNsdHelper.registered);
                System.out.println(TAG + " Terminat.");
                terminalTextView.setText("Server terminal.");

                initServerButton();
                connectionTextView.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.VISIBLE);
            }
        }, 3000);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initClientButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProtocolClient protocolClient = new ProtocolClient(host, port, connectionTextView, exchangeTextView, okButton);
                Thread client = new Thread(protocolClient);
                client.start();
            }
        });
    }

    public void initServerButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProtocolServer protocolServer = new ProtocolServer(mNsdHelper.mServerSocket, connectionTextView, exchangeTextView, okButton);
                Thread server = new Thread(protocolServer);
                server.start();
            }
        });
    }

}