/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.classification;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.List;
import org.tensorflow.lite.examples.classification.env.ImageUtils;
import org.tensorflow.lite.examples.classification.env.Logger;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Device;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Model;
import org.tensorflow.lite.examples.classification.tflite.Classifier.Recognition;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.AnimationListener;

public abstract class CameraActivity extends AppCompatActivity
    implements OnImageAvailableListener,
        Camera.PreviewCallback,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {
  private static final Logger LOGGER = new Logger();

  private static final int PERMISSIONS_REQUEST = 1;

  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;
  private Runnable postInferenceCallback;
  private Runnable imageConverter;
  private LinearLayout bottomSheetLayout;
  private LinearLayout gestureLayout;
  private BottomSheetBehavior sheetBehavior;
  protected TextView recognitionTextView,
      recognition1TextView,
      recognition2TextView,
      recognitionValueTextView,
      recognition1ValueTextView,
      recognition2ValueTextView;
  protected TextView frameValueTextView,
      cropValueTextView,
      cameraResolutionTextView,
      rotationTextView,
      inferenceTimeTextView;
  protected ImageView bottomSheetArrowImageView;
  private ImageView plusImageView, minusImageView;
  private Spinner modelSpinner;
  private Spinner deviceSpinner;
  private TextView threadsTextView;

  private Model model = Model.QUANTIZED;
  private Device device = Device.CPU;
  private int numThreads = -1;

  private MediaPlayer mp=new MediaPlayer();
  private ImageView imageView3;
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    LOGGER.d("onCreate " + this);
    super.onCreate(null);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.activity_camera);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);



    if (hasPermission()) {
      setFragment();
    } else {
      requestPermission();
    }

    threadsTextView = findViewById(R.id.threads);
    plusImageView = findViewById(R.id.plus);
    minusImageView = findViewById(R.id.minus);
    modelSpinner = findViewById(R.id.model_spinner);
    deviceSpinner = findViewById(R.id.device_spinner);
    bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
    gestureLayout = findViewById(R.id.gesture_layout);
    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
    bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow);

    ViewTreeObserver vto = gestureLayout.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
              gestureLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
              gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            //                int width = bottomSheetLayout.getMeasuredWidth();
            int height = gestureLayout.getMeasuredHeight();

            sheetBehavior.setPeekHeight(height);
          }
        });
    sheetBehavior.setHideable(false);

    sheetBehavior.setBottomSheetCallback(
        new BottomSheetBehavior.BottomSheetCallback() {
          @Override
          public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
              case BottomSheetBehavior.STATE_HIDDEN:
                break;
              case BottomSheetBehavior.STATE_EXPANDED:
                {
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_down);
                }
                break;
              case BottomSheetBehavior.STATE_COLLAPSED:
                {
                  bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                }
                break;
              case BottomSheetBehavior.STATE_DRAGGING:
                break;
              case BottomSheetBehavior.STATE_SETTLING:
                bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
                break;
            }
          }

          @Override
          public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });

    recognitionTextView = findViewById(R.id.detected_item);
    recognitionValueTextView = findViewById(R.id.detected_item_value);
    recognition1TextView = findViewById(R.id.detected_item1);
    recognition1ValueTextView = findViewById(R.id.detected_item1_value);
    recognition2TextView = findViewById(R.id.detected_item2);
    recognition2ValueTextView = findViewById(R.id.detected_item2_value);

    frameValueTextView = findViewById(R.id.frame_info);
    cropValueTextView = findViewById(R.id.crop_info);
    cameraResolutionTextView = findViewById(R.id.view_info);
    rotationTextView = findViewById(R.id.rotation_info);
    inferenceTimeTextView = findViewById(R.id.inference_info);

    modelSpinner.setOnItemSelectedListener(this);
    deviceSpinner.setOnItemSelectedListener(this);

    plusImageView.setOnClickListener(this);
    minusImageView.setOnClickListener(this);

    model = Model.valueOf(modelSpinner.getSelectedItem().toString().toUpperCase());
    device = Device.valueOf(deviceSpinner.getSelectedItem().toString());
    numThreads = Integer.parseInt(threadsTextView.getText().toString().trim());


    GifImageView gifimageview = findViewById(R.id.manachan_gif);
    try{
      GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.manachan_test);
      gifimageview.setImageDrawable(gifDrawable);
    }catch (Exception e){
      e.printStackTrace();
    }

      GifImageView ImageView1 = findViewById(R.id.manachan_gif);
      try{
          GifDrawable gifDrawable1 = new GifDrawable(getResources(), R.drawable.mana_stand);
          ImageView1.setImageDrawable(gifDrawable1);
      }catch (Exception e){
          e.printStackTrace();
      }
  }


  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

  /** Callback for android.hardware.Camera API */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      LOGGER.w("Dropping frame!");
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      return;
    }

    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
        new Runnable() {
          @Override
          public void run() {
            ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
          }
        };

    postInferenceCallback =
        new Runnable() {
          @Override
          public void run() {
            camera.addCallbackBuffer(bytes);
            isProcessingFrame = false;
          }
        };
    processImage();
  }

  /** Callback for Camera2 API */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    // We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
          new Runnable() {
            @Override
            public void run() {
              ImageUtils.convertYUV420ToARGB8888(
                  yuvBytes[0],
                  yuvBytes[1],
                  yuvBytes[2],
                  previewWidth,
                  previewHeight,
                  yRowStride,
                  uvRowStride,
                  uvPixelStride,
                  rgbBytes);
            }
          };

      postInferenceCallback =
          new Runnable() {
            @Override
            public void run() {
              image.close();
              isProcessingFrame = false;
            }
          };

      processImage();
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public synchronized void onStart() {
    LOGGER.d("onStart " + this);
    super.onStart();
  }

  @Override
  public synchronized void onResume() {
    LOGGER.d("onResume " + this);
    super.onResume();

    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @Override
  public synchronized void onPause() {
    LOGGER.d("onPause " + this);

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() {
    LOGGER.d("onStop " + this);
    super.onStop();
  }

  @Override
  public synchronized void onDestroy() {
    LOGGER.d("onDestroy " + this);
    super.onDestroy();
  }

  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
      final int requestCode, final String[] permissions, final int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST) {
      if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED
          && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivity.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
            .show();
      }
      requestPermissions(new String[] {PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
      CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API =
            (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                || isHardwareLevelSupported(
                    characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        LOGGER.i("Camera API lv2?: %s", useCamera2API);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }

    return null;
  }

  protected void setFragment() {
    String cameraId = chooseCamera();

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
          CameraConnectionFragment.newInstance(
              new CameraConnectionFragment.ConnectionCallback() {
                @Override
                public void onPreviewSizeChosen(final Size size, final int rotation) {
                  previewHeight = size.getHeight();
                  previewWidth = size.getWidth();
                  CameraActivity.this.onPreviewSizeChosen(size, rotation);
                }
              },
              this,
              getLayoutId(),
              getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
          new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }

  public static int getResId(String variableName, Class<?> c) {
    Field field = null;
    int resId = 0;
    try {
      field = c.getField(variableName);
      try {
        resId = field.getInt(null);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resId;
  }


    @UiThread
  public void showResultsInBottomSheet(List<Recognition> results) {
    if (results != null && results.size() >= 3) {
      Recognition recognition = results.get(0);
      if (recognition != null && recognition.getConfidence() >=0.60) {
        if (recognition.getTitle() != null)
          recognitionTextView.setText(recognition.getTitle());
        System.out.println("行數：" + recognition.getId());
        String title_id = String.format("%s",recognition.getTitle());
        String b = String.format("%s",recognition.getId());
        //System.out.println("標籤："+title_id);
        int row_id = Integer.parseInt(b);
        String bcc = String.format("%04d",row_id);
        //System.out.println("行數轉四位："+bcc);
        String label ="a"+bcc;
        MediaPlayer mp = MediaPlayer.create(this, getResId(label, R.raw.class));


        imageView3=(ImageView) findViewById(R.id.manachan_gif);
        imageView3.setOnClickListener(new View.OnClickListener() {
          int i=0;
          @Override
          public void onClick(View view) {
            mp.start();
            try{
              GifImageView ImageView = findViewById(R.id.manachan_gif);
              GifDrawable gif_cute=new GifDrawable(getResources(),R.drawable.mana_cute);
              gif_cute.setLoopCount(1);
              gif_cute.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                  try{
                    GifDrawable gif_clap=new GifDrawable(getResources(),R.drawable.mana_clap);
                    gif_clap.setLoopCount(1);
                    ImageView.setImageDrawable(gif_clap);
                    gif_clap.addAnimationListener(new AnimationListener() {
                      @Override
                      public void onAnimationCompleted(int loopNumber) {
                        try{
                          GifDrawable gif_stand=new GifDrawable(getResources(),R.drawable.mana_stand);
                          gif_stand.setLoopCount(1);
                          ImageView.setImageDrawable(gif_stand);
                        }catch(Exception e) {
                          e.printStackTrace();
                        }
                      }
                    });
                  }catch(Exception e) {
                    e.printStackTrace();
                  }
                }
              });
              ImageView.setImageDrawable(gif_cute);
            }catch(Exception e) {
              e.printStackTrace();
            }
          }

        });

        Data(title_id,row_id);

        if (recognition.getConfidence() != null)
          recognitionValueTextView.setText(
              String.format("%.2f", (100 * recognition.getConfidence())) + "%");
      }

      Recognition recognition1 = results.get(1);
      if (recognition1 != null) {
        if (recognition1.getTitle() != null) recognition1TextView.setText(recognition1.getTitle());
        if (recognition1.getConfidence() != null)
          recognition1ValueTextView.setText(
              String.format("%.2f", (100 * recognition1.getConfidence())) + "%");
      }

      Recognition recognition2 = results.get(2);
      if (recognition2 != null) {
        if (recognition2.getTitle() != null) recognition2TextView.setText(recognition2.getTitle());
        if (recognition2.getConfidence() != null)
          recognition2ValueTextView.setText(
              String.format("%.2f", (100 * recognition2.getConfidence())) + "%");
      }
    }
  }

public void Data(String title_id ,int row_id){

    SharedPreferences sharedPreferences = getApplication().
            getSharedPreferences("label", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    int idtotal=0;
    int roomid=0;
    int officeid=0;
    int kitchenid=0;
    int musicid=0;
    //讀取資料
    File f = new File("/data/data/org.tensorflow.lite.examples.classification/shared_prefs/label.xml");
    if (!f.exists()){
        editor.putString("title_all","0,").apply();
        editor.putString("title_office","0,").apply();
        editor.putString("title_room","0,").apply();
        editor.putString("title_kitchen","0,").apply();
        editor.putString("title_music","0,").apply();
        editor.putInt("office", 0 ).apply();
        editor.putInt("room", 0 ).apply();
        editor.putInt("kitchen", 0 ).apply();
        editor.putInt("music", 0 ).apply();
        editor.putInt("jump_num_office",0  ).apply();
        editor.putInt("jump_num_room",0  ).apply();
        editor.putInt("jump_num_kitchen",0  ).apply();
        editor.putInt("jump_num_music",0  ).apply();
    }
    else{
        SharedPreferences shared = this.getSharedPreferences("label", Context.MODE_PRIVATE);
        String title_id_spit = title_id+",";//第一次出來的title加上逗號，要將往後的title加上來做區分

        String title_all = shared.getString("title_all", null);
        if (title_all.equals("0,")){
            String title_id_spit_2 =title_all+title_id_spit;//讀出的title加上偵測的title
            System.out.println(title_id_spit_2);
            editor.putString("title_all", title_id_spit_2).apply();
            String gettitle_in = shared.getString("title_all", null);
            System.out.println("印到煩了喔！"+gettitle_in.toString());
            Boolean bb = editor.commit();
            if(bb){
                Log.i("通知：", "儲存成功！");
            }else{
                Log.i("通知", "儲存失敗！");
            }

        }else if (!title_all.equals(0)){
            String gettitle_in_2 = shared.getString("title_all", null);
            String[] gettitle_all = gettitle_in_2.split(",");
            for(int i=0;i<gettitle_all.length;i++){
                String put_title_all=gettitle_all[i];
                if(put_title_all.equals(title_id)){
                    System.out.println(put_title_all+"///"+title_id);
                    break;
                }else if(!put_title_all.equals(title_id)){
                    System.out.println("我跑進來啦"+i);
                    System.out.println("我跑進來啦"+put_title_all+"///"+title_id);
                    if(i==gettitle_all.length-1){
                        String title_id_spit_2 =gettitle_in_2+title_id_spit;//讀出的title加上偵測的title
                        editor.putString("title_all", title_id_spit_2).apply();

                        editor.commit();
                        Boolean bb = editor.commit();
                        if(bb){
                            Log.i("通知：", "儲存成功！");
                        }else{
                            Log.i("通知", "儲存失敗！");
                        }
                    }

                }

            }

        }

        if (row_id == 621 || row_id == 447 ||row_id == 454 ||row_id == 509 ||row_id == 527 ||row_id == 528 ||row_id == 554 ||row_id == 591 ||row_id == 665 ||row_id == 674 ||row_id == 714 ||row_id == 743 ||row_id == 746 ||row_id == 783 ||row_id == 922 ||row_id == 847){
            int getoffice = shared.getInt("office", 0);
            String gettitle = shared.getString("title_office", null);
            if (getoffice==0 & gettitle.equals("0,")){
                officeid=getoffice+1;
                editor.putInt("office", officeid).apply();
                String title_id_spit_2 =gettitle+title_id_spit;//讀出的title加上偵測的title
                System.out.println(title_id_spit_2);
                editor.putString("title_office", title_id_spit_2).apply();
                String gettitle_in = shared.getString("title_office", null);
                System.out.println("印到煩了喔！"+gettitle_in.toString());
                Boolean bb = editor.commit();
                if(bb){
                    Log.i("通知：", "儲存成功！");
                }else{
                    Log.i("通知", "儲存失敗！");
                }

            }else if (getoffice!=0 & !gettitle.equals(0)){
                String gettitle_in_2 = shared.getString("title_office", null);
                String[] gettitle_all = gettitle_in_2.split(",");
                for(int i=0;i<gettitle_all.length;i++){
                    String put_title_all=gettitle_all[i];
                    if(put_title_all.equals(title_id)){
                        System.out.println(put_title_all+"///"+title_id);
                        break;
                    }else if(!put_title_all.equals(title_id)){
                      System.out.println("我跑進來啦"+i);
                      System.out.println("我跑進來啦"+put_title_all+"///"+title_id);
                      if(i==gettitle_all.length-1){
                        String title_id_spit_2 =gettitle_in_2+title_id_spit;//讀出的title加上偵測的title
                        editor.putString("title_office", title_id_spit_2).apply();

                        editor.commit();
                        officeid=getoffice+1;
                        editor.putInt("office", officeid).apply();
                        Boolean bb = editor.commit();
                        if(bb){
                          Log.i("通知：", "儲存成功！");
                        }else{
                          Log.i("通知", "儲存失敗！");
                        }
                      }

                    }

                }

            }

        }else if (row_id == 698 ||row_id == 722 ||row_id == 912 ||row_id == 905 ||row_id == 906 ||row_id == 897 ||row_id == 893 ||row_id == 851 ||row_id == 849 ||row_id == 823){
          int getroom = shared.getInt("room", 0);
          String gettitle = shared.getString("title_room", null);
          if (getroom==0 & gettitle.equals("0,")){
            roomid=getroom+1;
            editor.putInt("room", roomid).apply();
            String title_id_spit_2 =gettitle+title_id_spit;//讀出的title加上偵測的title
            System.out.println(title_id_spit_2);
            editor.putString("title_room", title_id_spit_2).apply();
            String gettitle_in = shared.getString("title_room", null);
            System.out.println("印到煩了喔！"+gettitle_in.toString());
            Boolean bb = editor.commit();
            if(bb){
              Log.i("通知：", "儲存成功！");
            }else{
              Log.i("通知", "儲存失敗！");
            }

          }else if (getroom!=0 & !gettitle.equals(0)){
            String gettitle_in_2 = shared.getString("title_room", null);
            String[] gettitle_all = gettitle_in_2.split(",");
            for(int i=0;i<gettitle_all.length;i++){
              String put_title_all=gettitle_all[i];
              if(put_title_all.equals(title_id)){
                System.out.println(put_title_all+"///"+title_id);
                break;
              }else if(!put_title_all.equals(title_id)){
                System.out.println("我跑進來啦"+i);
                System.out.println("我跑進來啦"+put_title_all+"///"+title_id);
                if(i==gettitle_all.length-1){
                  String title_id_spit_2 =gettitle_in_2+title_id_spit;//讀出的title加上偵測的title
                  editor.putString("title_room", title_id_spit_2).apply();

                  editor.commit();
                  roomid=getroom+1;
                  editor.putInt("room", roomid).apply();
                  Boolean bb = editor.commit();
                  if(bb){
                    Log.i("通知：", "儲存成功！");
                  }else{
                    Log.i("通知", "儲存失敗！");
                  }
                }

              }

            }

          }
        }else if (row_id == 412 ||row_id == 568 ||row_id == 739 ||row_id == 761 ||row_id == 899 ||row_id == 900 ||row_id == 892 ||row_id == 869 ||row_id == 860 ||row_id == 850 ||row_id == 827){
            int getkitchen = shared.getInt("kitchen", 0);
            String gettitle = shared.getString("title_kitchen", null);
            if (getkitchen==0 & gettitle.equals("0,")){
                kitchenid=getkitchen+1;
                editor.putInt("kitchen", kitchenid).apply();
                String title_id_spit_2 =gettitle+title_id_spit;//讀出的title加上偵測的title
                System.out.println(title_id_spit_2);
                editor.putString("title_kitchen", title_id_spit_2).apply();
                String gettitle_in = shared.getString("title_kitchen", null);
                System.out.println("印到煩了喔！"+gettitle_in.toString());
                Boolean bb = editor.commit();
                if(bb){
                    Log.i("通知：", "儲存成功！");
                }else{
                    Log.i("通知", "儲存失敗！");
                }

            }else if (getkitchen!=0 & !gettitle.equals(0)){
                String gettitle_in_2 = shared.getString("title_kitchen", null);
                String[] gettitle_all = gettitle_in_2.split(",");
                for(int i=0;i<gettitle_all.length;i++){
                    String put_title_all=gettitle_all[i];
                    if(put_title_all.equals(title_id)){
                        System.out.println(put_title_all+"///"+title_id);
                        break;
                    }else if(!put_title_all.equals(title_id)){
                        System.out.println("我跑進來啦"+i);
                        System.out.println("我跑進來啦"+put_title_all+"///"+title_id);
                        if(i==gettitle_all.length-1){
                            String title_id_spit_2 =gettitle_in_2+title_id_spit;//讀出的title加上偵測的title
                            editor.putString("title_kitchen", title_id_spit_2).apply();

                            editor.commit();
                            kitchenid=getkitchen+1;
                            editor.putInt("kitchen", kitchenid).apply();
                            Boolean bb = editor.commit();
                            if(bb){
                                Log.i("通知：", "儲存成功！");
                            }else{
                                Log.i("通知", "儲存失敗！");
                            }
                        }

                    }

                }

            }
        }else if (row_id == 402 ||row_id == 403 ||row_id == 421 ||row_id == 433 ||row_id == 487 ||row_id == 542 ||row_id == 543 ||row_id == 547 ||row_id == 559 ||row_id == 597 ||row_id == 578 ||row_id == 580 ||row_id == 594 ||row_id == 595 ||row_id == 651 ||row_id == 684 ||row_id == 700 ||row_id == 777 ||row_id == 685 ||row_id == 688 ||row_id == 903 ||row_id == 890 ||row_id == 882 ||row_id == 824){
            int getmusic = shared.getInt("music", 0);
            String gettitle = shared.getString("title_music", null);
            if (getmusic==0 & gettitle.equals("0,")){
                musicid=getmusic+1;
                editor.putInt("music", musicid).apply();
                String title_id_spit_2 =gettitle+title_id_spit;//讀出的title加上偵測的title
                System.out.println(title_id_spit_2);
                editor.putString("title_music", title_id_spit_2).apply();
                String gettitle_in = shared.getString("title_music", null);
                System.out.println("印到煩了喔！"+gettitle_in.toString());
                Boolean bb = editor.commit();
                if(bb){
                    Log.i("通知：", "儲存成功！");
                }else{
                    Log.i("通知", "儲存失敗！");
                }

            }else if (getmusic!=0 & !gettitle.equals(0)){
                String gettitle_in_2 = shared.getString("title_music", null);
                String[] gettitle_all = gettitle_in_2.split(",");
                for(int i=0;i<gettitle_all.length;i++){
                    String put_title_all=gettitle_all[i];
                    if(put_title_all.equals(title_id)){
                        System.out.println(put_title_all+"///"+title_id);
                        break;
                    }else if(!put_title_all.equals(title_id)){
                        System.out.println("我跑進來啦"+i);
                        System.out.println("我跑進來啦"+put_title_all+"///"+title_id);
                        if(i==gettitle_all.length-1){
                            String title_id_spit_2 =gettitle_in_2+title_id_spit;//讀出的title加上偵測的title
                            editor.putString("title_music", title_id_spit_2).apply();

                            editor.commit();
                            musicid=getmusic+1;
                            editor.putInt("music", musicid).apply();
                            Boolean bb = editor.commit();
                            if(bb){
                                Log.i("通知：", "儲存成功！");
                            }else{
                                Log.i("通知", "儲存失敗！");
                            }
                        }

                    }

                }

            }
        }
        //在讀一次，這裡是讀到三次跳另外視窗
        int getroom_jump = shared.getInt("room", 0);
        int getoffice_jump = shared.getInt("office", 0);
        int getkitchen_jump = shared.getInt("kitchen", 0);
        int getmusic_jump = shared.getInt("music", 0);
        int jump_num=0;
//        String gettitle_jump=shared.getString("title_office",null);
//        System.out.println("gettitle_jump："+gettitle_jump+"getoffice_jump:"+getoffice_jump);
//        System.out.println(getoffice_jump);

        if (getoffice_jump == 3){

          int jump_num_office = shared.getInt("jump_num_office", 0);
          System.out.println("jump_num_office："+jump_num_office);
          if (jump_num_office==0){
            AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
            View dialogView = View.inflate(CameraActivity.this, R.layout.reward_office, null);
            ImageView iv_gif = dialogView.findViewById(R.id.manachan_gif);
            Glide.with(CameraActivity.this).load(R.drawable.mana_clap).into(iv_gif);
            builder.setView(dialogView)
                    .setPositiveButton("知道囉", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                      }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

          }
          jump_num+=1;
          editor.putInt("jump_num_office",jump_num).apply();
        }else if (getroom_jump == 3){
            int jump_num_room = shared.getInt("jump_num_room", 0);
            if (jump_num_room==1){
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                View dialogView = View.inflate(CameraActivity.this, R.layout.reward_bedroom, null);
                ImageView iv_gif = dialogView.findViewById(R.id.manachan_gif);
                Glide.with(CameraActivity.this).load(R.drawable.mana_clap).into(iv_gif);
                builder.setView(dialogView)
                        .setPositiveButton("知道囉", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
          jump_num+=1;
          editor.putInt("jump_num_room",jump_num  ).apply();

        }else if (getkitchen_jump == 3){
            jump_num+=1;
            editor.putInt("jump_num_kitchen",jump_num  ).apply();
            int jump_num_kitchen = shared.getInt("jump_num_kitchen", 0);
            if (jump_num_kitchen==1){
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                View dialogView = View.inflate(CameraActivity.this, R.layout.reward_kitchen, null);
                ImageView iv_gif = dialogView.findViewById(R.id.manachan_gif);
                Glide.with(CameraActivity.this).load(R.drawable.mana_clap).into(iv_gif);
                builder.setView(dialogView)
                        .setPositiveButton("知道囉", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
          jump_num+=1;
          editor.putInt("jump_num_kitchen",jump_num  ).apply();

        }else if (getmusic_jump == 3){
            jump_num+=1;
            editor.putInt("jump_num_music",jump_num  ).apply();
            int jump_num_music = shared.getInt("jump_num_music", 0);
            if (jump_num_music==1){
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                View dialogView = View.inflate(CameraActivity.this, R.layout.reward_music, null);
                ImageView iv_gif = dialogView.findViewById(R.id.manachan_gif);
                Glide.with(CameraActivity.this).load(R.drawable.mana_clap).into(iv_gif);
                builder.setView(dialogView)
                        .setPositiveButton("知道囉", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
          jump_num+=1;
          editor.putInt("jump_num_music",jump_num  ).apply();

        }

    }



}

  protected void showFrameInfo(String frameInfo) {
    frameValueTextView.setText(frameInfo);
  }

  protected void showCropInfo(String cropInfo) {
    cropValueTextView.setText(cropInfo);
  }

  protected void showCameraResolution(String cameraInfo) {
    cameraResolutionTextView.setText(previewWidth + "x" + previewHeight);
  }

  protected void showRotationInfo(String rotation) {
    rotationTextView.setText(rotation);
  }

  protected void showInference(String inferenceTime) {
    inferenceTimeTextView.setText(inferenceTime);
  }

  protected Model getModel() {
    return model;
  }

  private void setModel(Model model) {
    if (this.model != model) {
      LOGGER.d("Updating  model: " + model);
      this.model = model;
      onInferenceConfigurationChanged();
    }
  }

  protected Device getDevice() {
    return device;
  }

  private void setDevice(Device device) {
    if (this.device != device) {
      LOGGER.d("Updating  device: " + device);
      this.device = device;
      final boolean threadsEnabled = device == Device.CPU;
      plusImageView.setEnabled(threadsEnabled);
      minusImageView.setEnabled(threadsEnabled);
      threadsTextView.setText(threadsEnabled ? String.valueOf(numThreads) : "N/A");
      onInferenceConfigurationChanged();
    }
  }

  protected int getNumThreads() {
    return numThreads;
  }

  private void setNumThreads(int numThreads) {
    if (this.numThreads != numThreads) {
      LOGGER.d("Updating  numThreads: " + numThreads);
      this.numThreads = numThreads;
      onInferenceConfigurationChanged();
    }
  }

  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

  protected abstract int getLayoutId();

  protected abstract Size getDesiredPreviewFrameSize();

  protected abstract void onInferenceConfigurationChanged();

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.plus) {
      String threads = threadsTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads >= 9) return;
      setNumThreads(++numThreads);
      threadsTextView.setText(String.valueOf(numThreads));
    } else if (v.getId() == R.id.minus) {
      String threads = threadsTextView.getText().toString().trim();
      int numThreads = Integer.parseInt(threads);
      if (numThreads == 1) {
        return;
      }
      setNumThreads(--numThreads);
      threadsTextView.setText(String.valueOf(numThreads));
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    if (parent == modelSpinner) {
      setModel(Model.valueOf(parent.getItemAtPosition(pos).toString().toUpperCase()));
    } else if (parent == deviceSpinner) {
      setDevice(Device.valueOf(parent.getItemAtPosition(pos).toString()));
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }
}
