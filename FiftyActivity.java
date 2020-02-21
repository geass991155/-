package org.tensorflow.lite.examples.classification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import android.widget.Toast;


public class FiftyActivity extends AppCompatActivity {
    private MediaPlayer mp=new MediaPlayer();
    private ImageButton back;
    private GridView fifty_01;
    private GridView fifty_top;

    private int[] imageId = {
            R.drawable.fifty_top_01, R.drawable.fifty_top_02,
            R.drawable.fifty_top_03, R.drawable.fifty_top_04,
            R.drawable.fifty_top_05, R.drawable.fifty_top_06,
            R.drawable.fifty_top_07, R.drawable.fifty_top_08,
            R.drawable.fifty_top_09, R.drawable.fifty_top_10,
            R.drawable.fifty_top_11, R.drawable.fifty_top_12,
            R.drawable.fifty_top_13, R.drawable.fifty_top_14,
            R.drawable.fifty_top_15, R.drawable.fifty_top_16,
            R.drawable.fifty_top_17, R.drawable.fifty_top_18,
            R.drawable.fifty_top_19, R.drawable.fifty_top_20,
            R.drawable.fifty_top_21, R.drawable.fifty_top_22,
            R.drawable.fifty_top_23, R.drawable.fifty_top_24,
            R.drawable.fifty_top_25, R.drawable.fifty_top_26,
            R.drawable.fifty_top_27, R.drawable.fifty_top_28,
            R.drawable.fifty_top_29, R.drawable.fifty_top_30,
            R.drawable.fifty_top_31, R.drawable.fifty_top_32,
            R.drawable.fifty_top_33, R.drawable.fifty_top_34,
            R.drawable.fifty_top_35, R.drawable.fifty_top_36,
            R.drawable.fifty_none, R.drawable.fifty_top_38,
            R.drawable.fifty_none, R.drawable.fifty_top_40,
            R.drawable.fifty_top_41, R.drawable.fifty_top_42,
            R.drawable.fifty_top_43, R.drawable.fifty_top_44,
            R.drawable.fifty_top_45, R.drawable.fifty_top_46,
            R.drawable.fifty_none, R.drawable.fifty_top_48,
            R.drawable.fifty_none, R.drawable.fifty_top_50,
    };

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
    Context context = FiftyActivity.this;
    ImageAdapter adapter = new ImageAdapter(this);
    AlertDialog dialogDemo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifty);

        CustomGrid adapter1 = new CustomGrid(FiftyActivity.this, imageId);
        fifty_top = (GridView) findViewById(R.id.fifty_top);
        fifty_top.setAdapter(adapter1);


        ImageButton nextPageBtn = (ImageButton) findViewById(R.id.back);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiftyActivity.this.finish();
            }
        });

        fifty_top.bringToFront();

        GridView gridview = (GridView) findViewById(R.id.fifty_01);
        gridview.setNumColumns(5);
        gridview.setAdapter(adapter);

        fifty_top.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, final int position, long id) {
                ImageView iv = new ImageView(context);
                iv.setImageResource(adapter.mThumbIds[position]);
                int f = adapter.mThumbIds[position];
                int f_id = f-2131230827;
                String bcc = String.format("%02d",f_id);
                String label ="fifty_"+bcc;
                MediaPlayer fifty = MediaPlayer.create(FiftyActivity.this, getResId(label, R.raw.class));


                    new AlertDialog.Builder(context)
                            .setView(iv)
                            .setNeutralButton("點擊發音", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.out.println(imageId);
                                    System.out.println(f);
                                    fifty.start();
                                    try {
                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, false);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

//                                    keepDialogOpen(dialogDemo);

                                }

                            })
                            .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    try {
                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                        field.setAccessible(true);
                                        field.set(dialog, true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .show();


            }
            private void keepDialogOpen(AlertDialog dialog) {
                try {
                    java.lang.reflect.Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}





