package org.tensorflow.lite.examples.classification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class LearnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_learn);
        ImageButton nextPageBtn = (ImageButton) findViewById(R.id.back);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LearnActivity.this.finish();
            }
        });

        GifImageView ImageView = findViewById(R.id.manachan_gif);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.mana_prepare);
            gifDrawable.setLoopCount(1);
            ImageView.setImageDrawable(gifDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        File f = new File("/data/data/org.tensorflow.lite.examples.classification/shared_prefs/label.xml");
        SharedPreferences shared = this.getSharedPreferences("label", Context.MODE_PRIVATE);

        ImageView imageView3=(ImageView) findViewById(R.id.manachan_gif);
        imageView3.setOnClickListener(new View.OnClickListener() {
            int i=0;
            @Override
            public void onClick(View view) {
                TextView text_bedroom = (TextView) findViewById(R.id.learn_office);
                text_bedroom.setMovementMethod(ScrollingMovementMethod.getInstance());
                String title_final="";
                String title_all = shared.getString("title_all", null);
                String[] title_all_arry=title_all.split(",");
                for (int i=1;i<title_all_arry.length;i++){
                    System.out.println(title_all_arry[i]);
                    title_final+=title_all_arry[i]+"\n";

                }
                text_bedroom.setText(title_final);

            }

        });


    }


}
