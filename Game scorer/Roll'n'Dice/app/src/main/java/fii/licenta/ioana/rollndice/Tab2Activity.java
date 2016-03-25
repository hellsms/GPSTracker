package fii.licenta.ioana.rollndice;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Ioana on 23/03/2016.
 */
public class Tab2Activity extends Fragment {
    private MainRenderer mainRenderer;
    private MainSurfaceView mainSurfaceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.activity_tab2, container, false);
        LinearLayout linearLayout = (LinearLayout)mainView.findViewById(R.id.tab2_linearLayout);
        mainSurfaceView = new MainSurfaceView(getContext());
        mainRenderer = new MainRenderer(getContext());

        mainSurfaceView.setRenderer(mainRenderer);
        mainSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        linearLayout.addView(mainSurfaceView);
        return mainView;
    }
}
