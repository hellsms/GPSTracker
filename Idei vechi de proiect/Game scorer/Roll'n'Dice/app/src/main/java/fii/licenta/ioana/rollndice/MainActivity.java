package fii.licenta.ioana.rollndice;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.lang.reflect.Type;

public class MainActivity extends FragmentActivity {
    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("1").setIndicator("Roll"), Tab1Activity.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("2").setIndicator("Dice"), Tab2Activity.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("3").setIndicator("Coin"), Tab3Activity.class, null);

    }

}
