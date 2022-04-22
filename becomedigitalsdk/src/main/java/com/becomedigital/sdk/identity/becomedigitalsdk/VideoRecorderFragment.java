package com.becomedigital.sdk.identity.becomedigitalsdk;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.becomedigital.sdk.identity.becomedigitalsdk.R;
import com.becomedigital.sdk.identity.becomedigitalsdk.mediaRecorders.AutoFitTextureView;
import com.becomedigital.sdk.identity.becomedigitalsdk.models.BDIVConfig;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static androidx.navigation.Navigation.findNavController;

public class VideoRecorderFragment extends Fragment {
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray ( );
    private static final String TAG = "Camera2VideoFragment";
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;


    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;
    /**
     * A refernce to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;
    /**
     * A reference to the current {@link CameraCaptureSession} for preview.
     */
    private CameraCaptureSession mPreviewSession;
    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener ( ) {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            openCamera (width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
            configureTransform (width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };
    /**
     * The {@link Size} of camera preview.
     */
    private Size mPreviewSize;
    /**
     * The {@link Size} of video recording.
     */
    private Size mVideoSize;
    /**
     * Camera preview.
     */
    private CaptureRequest.Builder mPreviewBuilder;
    /**
     * MediaRecorder
     */
    private MediaRecorder mMediaRecorder;
    /**
     * Whether the app is recording video now
     */
    private boolean mIsRecordingVideo;
    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;
    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;
    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore (1);
    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its status.
     */
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback ( ) {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview ( );
            mCameraOpenCloseLock.release ( );
            if (null != mTextureView) {
                configureTransform (mTextureView.getWidth ( ), mTextureView.getHeight ( ));
            }
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release ( );
            cameraDevice.close ( );
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release ( );
            cameraDevice.close ( );
            mCameraDevice = null;
            Activity activity = getActivity ( );
            if (null != activity) {
                activity.finish ( );
            }
        }
    };

    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes larger
     * than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth ( ) == size.getHeight ( ) * 4 / 3 && size.getWidth ( ) <= 1080) {
                return size;
            }
        }
        //Log.e (TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values, and whose aspect
     * ratio matches with the specified value.
     *
     * @param choices     The list of sizes that the camera supports for the intended output class
     * @param width       The minimum desired width
     * @param height      The minimum desired height
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size> ( );
        int w = aspectRatio.getWidth ( );
        int h = aspectRatio.getHeight ( );
        for (Size option : choices) {
            if (option.getHeight ( ) == option.getWidth ( ) * h / w &&
                    option.getWidth ( ) >= width && option.getHeight ( ) >= height) {
                bigEnough.add (option);
            }
        }
        // Pick the smallest of those, assuming we found any
        if (bigEnough.size ( ) > 0) {
            return Collections.min (bigEnough, new CompareSizesByArea ( ));
        } else {
            //Log.e (TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate (R.layout.fragment_video_recorder, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        mTextureView = getActivity ( ).findViewById (R.id.texture);
       Bundle arguments = getArguments();
        if (arguments != null) {
            config = (BDIVConfig) arguments.getSerializable("config");
        }
    }

    @Override
    public void onResume() {
        super.onResume ( );
        ((MainBDIV) getActivity ( )).changeColorToolbar (true);
        startBackgroundThread ( );
        if (mTextureView.isAvailable ( )) {
            openCamera (mTextureView.getWidth ( ), mTextureView.getHeight ( ));
        } else {
            mTextureView.setSurfaceTextureListener (mSurfaceTextureListener);
        }

        initialSetups ( );
        final Animation animScale = AnimationUtils.loadAnimation (getActivity ( ), R.anim.anim_scale);

        countdownRecording = 4;
        countdownToStart = 4;
        initCoundown ( );
        Glide.with (this)
                .load (R.drawable.recording)
                .into (imgAnimatorIndocator);

    }

    private void initCoundown() {
        countDownTimerRecord = new CountDownTimer (3000, 1000) {
            public void onTick(long millisUntilFinished) {
                //Log.d (TAG, "time to record video:" + millisUntilFinished);
            }

            public void onFinish() {
                //Log.d (TAG, "time: done");
                startRecordingVideo ( );
                recording = true;
            }
        }.start ( );
        countDownTimerGeneral = new CountDownTimer (7000, 1000) {
            public void onTick(long millisUntilFinished) {
                //Log.d (TAG, "time: " + millisUntilFinished);
                countdownToStart--;
                if (countdownToStart <= 0) { // inicia la visualizacion de la cuenta regresiva de grabado
                    if (countdownRecording >= 0) {
                        countdownRecording--;
                        textTimerRecord.setVisibility (View.VISIBLE);
                        String textCoundownRecording = "0" + countdownRecording;
                        textTimerRecord.setText (textCoundownRecording); // asigna el valor al contador inferior
                        imgAnimatorIndocator.setVisibility (View.VISIBLE);
                        textTimerToStart.setVisibility (View.INVISIBLE);
                    }
                } else {
                    String counDownToStratS = String.valueOf (countdownToStart);
                    textTimerToStart.setText (counDownToStratS); // asigna el valor al contador en el medio
                }
            }

            public void onFinish() {
                //Log.d (TAG, "time: done");
                try {
                    stopRecordingVideo ( );
                } catch (Exception e) {
                    // e.printStackTrace(); ( );
                }
                recording = false;
                Bundle bundle = new Bundle ( );
                bundle.putSerializable("config", config);
                bundle.putString("urlVideoFile", urlVideoFile);
                findNavController (getActivity ( ), R.id.nav_host_fragment).navigate (R.id.actionOkRecordSelecDocument, bundle);
            }
        }.start ( );
    }


    @Override
    public void onPause() {
        closeCamera ( );
        stopBackgroundThread ( );
        super.onPause ( );
    }

    private ImageButton btnCapture;
    private static MediaRecorder mRecorder;
    private boolean isCameraActive = false;
    private static TextView textTimerRecord;
    private static TextView textTimerToStart;
    private static ImageView imgAnimatorIndocator;
    private TextView textTittleVideo;
    private static int countdownToStart;
    public static int countdownRecording;
    private boolean recording;
    private CountDownTimer countDownTimerRecord;
    private CountDownTimer countDownTimerGeneral;
    private Integer mSensorOrientation;
    private BDIVConfig config;
    private String urlVideoFile;
    private void initialSetups() {
        textTittleVideo = getActivity ( ).findViewById (R.id.textTittleVideo);
        // controls video
        imgAnimatorIndocator = getActivity ( ).findViewById (R.id.img_animation_indicator);
        textTimerRecord = getActivity ( ).findViewById (R.id.textTimerRecord);
        textTimerToStart = getActivity ( ).findViewById (R.id.textTimerToStart);
        // controls image
        btnCapture = getActivity ( ).findViewById (R.id.btnCapture);

    }

    @Override
    public void onDestroy() {
        super.onDestroy ( );
        countDownTimerGeneral.cancel ( );
        countDownTimerRecord.cancel ( );
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread ("CameraBackground");
        mBackgroundThread.start ( );
        mBackgroundHandler = new Handler (mBackgroundThread.getLooper ( ));
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely ( );
        try {
            mBackgroundThread.join ( );
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            // e.printStackTrace(); ( );
        }
    }

    /**
     * Tries to open a {@link CameraDevice}. The result is listened by `mStateCallback`.
     */
    @SuppressLint("MissingPermission")
    private void openCamera(int width, int height) {
        final Activity activity = getActivity ( );
        if (null == activity || activity.isFinishing ( )) {
            return;
        }
        CameraManager manager = (CameraManager) activity.getSystemService (Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire (2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException ("Time out waiting to lock camera opening.");
            }
            String cameraId = manager.getCameraIdList()[CameraCharacteristics.LENS_FACING_BACK];
            // Choose the sizes for camera preview and video recording
            CameraCharacteristics characteristics = manager.getCameraCharacteristics (cameraId);

            StreamConfigurationMap map = characteristics
                    .get (CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            mVideoSize = chooseVideoSize (map.getOutputSizes (MediaRecorder.class));
            mPreviewSize = chooseOptimalSize (map.getOutputSizes (SurfaceTexture.class),
                    width, height, mVideoSize);
            int orientation = getResources ( ).getConfiguration ( ).orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio (mPreviewSize.getWidth ( ), mPreviewSize.getHeight ( ));
            } else {
                mTextureView.setAspectRatio (mPreviewSize.getHeight ( ), mPreviewSize.getWidth ( ));
            }
            configureTransform (width, height);
            mMediaRecorder = new MediaRecorder ( );

            manager.openCamera (cameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            Toast.makeText (activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show ( );
            activity.finish ( );
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.

        } catch (InterruptedException e) {
            throw new RuntimeException ("Interrupted while trying to lock camera opening.");
        }
    }

    private void closeCamera() {
        try {
            countDownTimerGeneral.cancel ( );
            countDownTimerRecord.cancel ( );
            mCameraOpenCloseLock.acquire ( );
            if (null != mCameraDevice) {
                mCameraDevice.close ( );
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release ( );
                mMediaRecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException ("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release ( );
        }
    }

    /**
     * Start the camera preview.
     */
    private void startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable ( ) || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession ( );
            SurfaceTexture texture = mTextureView.getSurfaceTexture ( );
            assert texture != null;
            texture.setDefaultBufferSize (mPreviewSize.getWidth ( ), mPreviewSize.getHeight ( ));
            mPreviewBuilder = mCameraDevice.createCaptureRequest (CameraDevice.TEMPLATE_PREVIEW);
            Surface previewSurface = new Surface (texture);
            mPreviewBuilder.addTarget (previewSurface);
            mCameraDevice.createCaptureSession (Collections.singletonList (previewSurface),
                    new CameraCaptureSession.StateCallback ( ) {
                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            mPreviewSession = cameraCaptureSession;
                            updatePreview ( );
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            Activity activity = getActivity ( );
                            if (null != activity) {
                                Toast.makeText (activity, "Failed", Toast.LENGTH_SHORT).show ( );
                            }
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            // e.printStackTrace(); ( );
        }
    }

    /**
     * Update the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder (mPreviewBuilder);
            HandlerThread thread = new HandlerThread ("CameraPreview");
            thread.start ( );
            mPreviewSession.setRepeatingRequest (mPreviewBuilder.build ( ), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            // e.printStackTrace(); ( );
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set (CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    /**
     * Configures the necessary {@link Matrix} transformation to `mTextureView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity ( );
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager ( ).getDefaultDisplay ( ).getRotation ( );
        Matrix matrix = new Matrix ( );
        RectF viewRect = new RectF (0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF (0, 0, mPreviewSize.getHeight ( ), mPreviewSize.getWidth ( ));
        float centerX = viewRect.centerX ( );
        float centerY = viewRect.centerY ( );
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset (centerX - bufferRect.centerX ( ), centerY - bufferRect.centerY ( ));
            matrix.setRectToRect (viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max (
                    (float) viewHeight / mPreviewSize.getHeight ( ),
                    (float) viewWidth / mPreviewSize.getWidth ( ));
            matrix.postScale (scale, scale, centerX, centerY);
            matrix.postRotate (90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform (matrix);
    }

    private void setUpMediaRecorder() throws IOException {
        final Activity activity = getActivity ( );
        if (null == activity) {
            return;
        }
        mMediaRecorder.setAudioSource (MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource (MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat (MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile (getVideoFile (activity).getAbsolutePath ( ));
        urlVideoFile = getVideoFile (activity).getAbsolutePath ( );
        CamcorderProfile profile = CamcorderProfile.get (CamcorderProfile.QUALITY_720P);
        mMediaRecorder.setVideoEncodingBitRate (profile.videoBitRate);
        mMediaRecorder.setVideoFrameRate (profile.videoFrameRate);
        mMediaRecorder.setVideoSize (profile.videoFrameWidth, profile.videoFrameHeight);
        mMediaRecorder.setVideoEncoder (MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder (MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioSamplingRate (profile.audioSampleRate);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        mMediaRecorder.prepare ( );
    }




    private void prepareRecordingWithProfile(CamcorderProfile profile) throws Exception {
        final Activity activity = getActivity ( );
        if (null == activity) {
            return;
        }
        // Prepare MediaRecorder.
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setProfile(profile);
        mMediaRecorder.setOutputFile(getVideoFile (activity).getAbsolutePath ( ));
        urlVideoFile = getVideoFile (activity).getAbsolutePath ( );
        mMediaRecorder.setVideoSize (profile.videoFrameWidth, profile.videoFrameHeight);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (mSensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }
        mMediaRecorder.prepare();
    }

    private File getVideoFile(Context context) {
        return new File (context.getExternalFilesDir (null), "video.mp4");
    }


    public void startRecordingVideo() {
        if (null == mCameraDevice || !mTextureView.isAvailable ( ) || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession ( );
            CamcorderProfile profile = CamcorderProfile.get (CamcorderProfile.QUALITY_480P);
            prepareRecordingWithProfile(profile);
//            setUpMediaRecorder ( );
            SurfaceTexture texture = mTextureView.getSurfaceTexture ( );
            assert texture != null;
            texture.setDefaultBufferSize (mPreviewSize.getWidth ( ), mPreviewSize.getHeight ( ));
            mPreviewBuilder = mCameraDevice.createCaptureRequest (CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<> ( );

            /**
             * Surface for the camera preview set up
             */

            Surface previewSurface = new Surface (texture);
            surfaces.add (previewSurface);
            mPreviewBuilder.addTarget (previewSurface);

            //MediaRecorder setup for surface
            Surface recorderSurface = mMediaRecorder.getSurface ( );
            surfaces.add (recorderSurface);
            mPreviewBuilder.addTarget (recorderSurface);

            // Start a capture session
            mCameraDevice.createCaptureSession (surfaces, new CameraCaptureSession.StateCallback ( ) {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview ( );
                    getActivity ( ).runOnUiThread (() -> {
                        mIsRecordingVideo = true;
                        // Start recording
                        mMediaRecorder.start ( );
                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //Log.e (TAG, "onConfigureFailed: Failed");
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException | IOException e) {
            // e.printStackTrace(); ( );
        } catch (Exception e) {
            // e.printStackTrace(); ( );
        }

    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close ( );
            mPreviewSession = null;
        }
    }

    public void stopRecordingVideo() throws Exception {
        // UI
        mIsRecordingVideo = false;
        try {
            mPreviewSession.stopRepeating ( );
            mPreviewSession.abortCaptures ( );
        } catch (CameraAccessException e) {
            // e.printStackTrace(); ( );
        }

        // Stop recording
        mMediaRecorder.stop ( );
        mMediaRecorder.reset ( );
        countDownTimerGeneral.cancel ( );
        countDownTimerRecord.cancel ( );
        urlVideoFile = urlVideoFile;
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum ((long) lhs.getWidth ( ) * lhs.getHeight ( ) -
                    (long) rhs.getWidth ( ) * rhs.getHeight ( ));
        }
    }

}