package fii.licenta.ioana.dicerolling;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private MainRenderer mainRenderer;
    private MainSurfaceView mainSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = activityManager.getDeviceConfigurationInfo();
        boolean supportsES2 = (info.reqGlEsVersion >= 0x20000);
        if (supportsES2) {
            mainSurfaceView = new MainSurfaceView(this);
            mainRenderer = new MainRenderer(this);

            //mainSurfaceView.setEGLConfigChooser(false);
            //mainSurfaceView.setEGLContextClientVersion(2);
            mainSurfaceView.setRenderer(mainRenderer);
            mainSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

            this.setContentView(mainSurfaceView);
        } else {
            Log.e("OpenGLES 2", "Your device doesn't support ES2. (" + info.reqGlEsVersion + ")");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mainSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainSurfaceView.onResume();
    }
}
