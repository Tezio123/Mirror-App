package com.mirror.user.mirrornew;


import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super( context );
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback( this );
        mHolder.setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay( holder );
            mCamera.startPreview();
        } catch (IOException e) {
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.release();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        mCamera.setDisplayOrientation( 90 );
        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch(IOException e){
        }

    }
}