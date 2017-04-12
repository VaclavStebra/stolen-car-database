package cz.muni.fi.a2p06.stolencardatabase.ocr;

import android.Manifest;
import android.content.Context;
import android.support.annotation.RequiresPermission;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

/**
 * Created by robert on 12.4.2017.
 */

public class OCRCameraView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "OCRCameraView";

    private SurfaceHolder mHolder;
    private CameraSource mCameraSource;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;

    public OCRCameraView(Context context) {
        super(context);
        prepareView();
    }

    public OCRCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        prepareView();
    }

    public OCRCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        prepareView();
    }

    private void prepareView() {
        mHolder = getHolder();
        mHolder.addCallback(this);

        mStartRequested = false;
        mSurfaceAvailable = false;
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSource cameraSource) throws IOException, SecurityException {
        if (cameraSource == null) {
            stop();
        }

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            mStartRequested = true;
            startIfReady();
        }
    }

    public void stop() {
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private void startIfReady() throws IOException, SecurityException {
        Log.d(TAG, "startIfReady: prepare" + (mStartRequested == true) + " " + (mSurfaceAvailable == true));
        if (mSurfaceAvailable && mStartRequested) {
            Log.d(TAG, "startIfReady: started");
            mCameraSource.start(mHolder);
            mStartRequested = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceAvailable = true;
        try {
            startIfReady();
        } catch (SecurityException se) {
            Log.e(TAG, "Do not have permission to start the camera", se);
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceAvailable = false;
    }
}
