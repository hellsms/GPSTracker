package fii.licenta.ioana.rollndice;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

/**
 * Created by Ioana on 09/03/2016.
 */
public class MainSurfaceView extends GLSurfaceView {

    private Renderer mRenderer;

    public Renderer getmRenderer() {
        return mRenderer;
    }

    public void setmRenderer(Renderer mRenderer) {
        this.mRenderer = mRenderer;
    }

    public MainSurfaceView(Context context) {
        super(context);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
    }
}
