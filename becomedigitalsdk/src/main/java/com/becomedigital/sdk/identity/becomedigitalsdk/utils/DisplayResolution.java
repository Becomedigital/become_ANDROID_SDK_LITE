package com.becomedigital.sdk.identity.becomedigitalsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DisplayResolution {
    private final static String TAG = DisplayResolution.class.getSimpleName ( );
    public static int[] getResolution(Activity activity){

        WindowManager wm = (WindowManager) activity.getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size1 = new Point ();

        int width, height;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size1);
            width = size1.x;
            height = size1.y;

        } else {
            width = display.getWidth();
            height = display.getHeight();
        }

        //Log.d (TAG,"width: " + width + ", height: " + height);
        return new int[]{width,height};

    }

    public static int calculateRotationDegrees(byte[] data, Activity activity){
        int rotationDegrees = 0;
        ExifInterface exifInterface = null;
        int orientation = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exifInterface = new ExifInterface (new ByteArrayInputStream (data));
                orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                rotationDegrees = 0;
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotationDegrees = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotationDegrees = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotationDegrees = 270;
                        break;
                }
            }else{
                Display display = activity.getWindowManager().getDefaultDisplay();
                rotationDegrees = 0;
                switch (display.getRotation()) {
                    case Surface.ROTATION_0: // This is display orientation
                        rotationDegrees = 90;
                        break;
                    case Surface.ROTATION_90:
                        rotationDegrees = 0;
                        break;
                    case Surface.ROTATION_180:
                        rotationDegrees = 270;
                        break;
                    case Surface.ROTATION_270:
                        rotationDegrees = 180;
                        break;
                }
            }
        } catch (IOException e) {
            // e.printStackTrace(); ( );
        }

        return rotationDegrees;
    }

}


