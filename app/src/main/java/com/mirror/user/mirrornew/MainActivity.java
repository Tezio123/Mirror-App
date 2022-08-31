package com.mirror.user.mirrornew;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import java.util.List;

public class MainActivity extends Activity {

    private CameraPreview mPreview;
    private android.hardware.Camera mCam;
    private int mCameraId = 0;
    private FrameLayout mPreviewLayout;
    float mDist = 0;
    private int minExposure = 0;
    private int maxExposure = 0;
    private SeekBar exposureSB = null;
    private android.hardware.Camera.Parameters myCamParam = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT );

        mCameraId = findFirstFrontFacingCamera();

        mPreviewLayout = (FrameLayout) findViewById( R.id.camPreview1 );
        mPreviewLayout.removeAllViews();

        startCameraInLayout( mPreviewLayout, mCameraId );

    }

    private int findFirstFrontFacingCamera() {
        int foundId = -1;
        int numCams = android.hardware.Camera.getNumberOfCameras();
        for (int camId = 0; camId < numCams; camId++) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo( camId, info );
            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                foundId = camId;
                break;
            }
        }
        return foundId;
    }

    private void startCameraInLayout(FrameLayout mPreviewLayout, int mCameraId) {

        mCam = android.hardware.Camera.open( mCameraId );
        if (mCam != null) {
            mPreview = new CameraPreview( this, mCam );
            mPreviewLayout.addView( mPreview );

            if (this.mCam != null) {
                this.myCamParam = this.mCam.getParameters();
                this.minExposure = this.myCamParam.getMinExposureCompensation();
                this.maxExposure = this.myCamParam.getMaxExposureCompensation();
                this.myCamParam.setExposureCompensation(0);
                this.mCam.setParameters(this.myCamParam);

            }
            this.initExposureSeekBar();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCam == null && mPreviewLayout != null) {
            mPreviewLayout.removeAllViews();
            startCameraInLayout( mPreviewLayout, mCameraId );
        }
    }

    @Override
    protected void onPause() {
        if (mCam != null) {
            mCam.release();
            mCam = null;
        }
        super.onPause();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        android.hardware.Camera.Parameters params = mCam.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing( event );
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                mCam.cancelAutoFocus();
                handleZoom( event, params );
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus( event, params );
            }
        }
        return true;
    }

    private void handleZoom(MotionEvent event, android.hardware.Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing( event );
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom( zoom );
        mCam.setParameters( params );
    }

    public void handleFocus(MotionEvent event, android.hardware.Camera.Parameters params) {
        int pointerId = event.getPointerId( 0 );
        int pointerIndex = event.findPointerIndex( pointerId );
        // Get the pointer's current position
        float x = event.getX( pointerIndex );
        float y = event.getY( pointerIndex );

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains( android.hardware.Camera.Parameters.FOCUS_MODE_AUTO )) {
            mCam.autoFocus( new android.hardware.Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, android.hardware.Camera camera) {
                    // currently set to auto-focus on single touch
                }
            } );
        }
    }

    /**
     * Determine the space between the first two fingers
     */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX( 0 ) - event.getX( 1 );
        float y = event.getY( 0 ) - event.getY( 1 );
        return (float) Math.sqrt( x * x + y * y );
    }

    private void initExposureSeekBar() {
        this.exposureSB = (SeekBar) this.findViewById( R.id.seekBar1 );
        this.exposureSB.setVisibility( View.VISIBLE );
        this.exposureSB.setMax( Math.abs( (int) this.minExposure ) + this.maxExposure );
        this.exposureSB.setProgress( Math.abs( (int) this.minExposure ) );
        this.exposureSB.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {

            /*
             * Enabled aggressive block sorting
             */
            public void onProgressChanged(SeekBar seekBar, int n, boolean bl) {
                int n2 = n + MainActivity.this.minExposure;
                if (n2 < MainActivity.this.minExposure) {
                    n2 = MainActivity.this.minExposure;
                } else if (n2 > MainActivity.this.maxExposure) {
                    n2 = MainActivity.this.maxExposure;
                }
                MainActivity.access$3( MainActivity.this, MainActivity.this.mCam.getParameters() );
                MainActivity.this.myCamParam.setExposureCompensation( n2 );
                MainActivity.this.mCam.setParameters( MainActivity.this.myCamParam );
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        } );


    }

    private static void access$3(MainActivity mainActivity, android.hardware.Camera.Parameters parameters) {

    }
}




