package fii.licenta.ioana.securebitexchange.nsd;

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
import fii.licenta.ioana.securebitexchange.protocol.ProtocolClient;
import fii.licenta.ioana.securebitexchange.protocol.ProtocolServer;

/**
 * Created by Ioana on 20/04/2016.
 */
public class MainActivity extends AppCompatActivity {

    private NsdHelper mNsdHelper;

    public static final String TAG = "MainActivity";

    private TextView textView;
    private Button button;

    private int port;
    private InetAddress host;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);
        textView = (TextView) findViewById(R.id.textView1);
        button = (Button) findViewById(R.id.button1);
        button.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mNsdHelper = new NsdHelper(this);
        mNsdHelper.initializeNsd();
        Handler handler = new Handler(Looper.getMainLooper());

        mNsdHelper.initializeServerSocket();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println(TAG + " S-a inregistrat? " + mNsdHelper.registered);
                System.out.println(TAG + " Terminat.");
                textView.setText("Server terminal.");

                initServerButton();
                button.setVisibility(View.VISIBLE);
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProtocolClient protocolClient = new ProtocolClient(host, port);
                Thread client = new Thread(protocolClient);
                client.start();
                Handler handler = new Handler(getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(protocolClient.ended){
                            byte xor = protocolClient.xor;
                            exposeWinner(xor);
                        }
                    }
                }, 3000);
            }
        });

    }

    public void initServerButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.INVISIBLE);
                final ProtocolServer protocolServer = new ProtocolServer(mNsdHelper.mServerSocket);
                Thread server = new Thread(protocolServer);
                server.start();
                Handler handler = new Handler(getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(protocolServer.ended){
                            byte xor = protocolServer.xor;
                            exposeWinner(xor);
                        }
                    }
                }, 3000);
            }
        });
    }

    public void exposeWinner(byte xor){
        if(xor == 0){
            System.out.println("xor egal cu 0");
            Toast.makeText(getApplicationContext(), "Server terminal starts.", Toast.LENGTH_LONG).show();

        }else{
            System.out.println("xor egal cu 0");
            Toast.makeText(getApplicationContext(), "Client terminal starts.", Toast.LENGTH_LONG).show();
        }
    }

}