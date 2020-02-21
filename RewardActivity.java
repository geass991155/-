package org.tensorflow.lite.examples.classification;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class RewardActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_reward);

        File f = new File("/data/data/org.tensorflow.lite.examples.classification/shared_prefs/label.xml");
        SharedPreferences shared = this.getSharedPreferences("label", Context.MODE_PRIVATE);
        int getoffice = shared.getInt("office", 0);
        int getroom = shared.getInt("room", 0);
        int getkitchen = shared.getInt("kitchen", 0);
        int getmusic = shared.getInt("music", 0);

        ImageButton nextPageBtn_office = (ImageButton)findViewById(R.id.button_office);
        ImageButton nextPageBtn_bedroom = (ImageButton)findViewById(R.id.button_bedroom);
        ImageButton nextPageBtn_kitchen = (ImageButton)findViewById(R.id.button_kitchen);
        ImageButton nextPageBtn_music = (ImageButton)findViewById(R.id.button_music);
        ImageButton nextPageBtn = (ImageButton) findViewById(R.id.back);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RewardActivity.this.finish();
            }
        });


        //辦公室
        nextPageBtn_office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getoffice<2){
                    Toast toast = Toast.makeText(RewardActivity.this, "尚未解鎖情境獎勵喔", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RewardActivity.this);
                    View dialogView = View.inflate(RewardActivity.this, R.layout.reward_office, null);
                    ImageView iv_gif = dialogView.findViewById(R.id.manachan_gif);
                    Glide.with(RewardActivity.this).load(R.drawable.mana_clap).into(iv_gif);
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

            }
        });

        //房間
        nextPageBtn_bedroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getroom<2){
                    Toast toast = Toast.makeText(RewardActivity.this, "尚未解鎖情境獎勵喔", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RewardActivity.this);
                    View dialogView = View.inflate(RewardActivity.this, R.layout.reward_bedroom, null);
                    ImageView iv_gif = dialogView.findViewById(R.id.manachan_gif);
                    Glide.with(RewardActivity.this).load(R.drawable.mana_clap).into(iv_gif);
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

            }
        });

        //廚房
        nextPageBtn_kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getkitchen<2){
                    Toast toast = Toast.makeText(RewardActivity.this, "尚未解鎖情境獎勵喔", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RewardActivity.this);
                    View dialogView = View.inflate(RewardActivity.this, R.layout.reward_kitchen, null);
                    ImageView iv_gif = dialogView.findViewById(R.id.manachan_gif);
                    Glide.with(RewardActivity.this).load(R.drawable.mana_clap).into(iv_gif);
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

            }
        });

        //音樂工作室
        nextPageBtn_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getmusic<2){
                    Toast toast = Toast.makeText(RewardActivity.this, "尚未解鎖情境獎勵喔", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RewardActivity.this);
                    View dialogView = View.inflate(RewardActivity.this, R.layout.reward_music, null);
                    ImageView iv_gif = dialogView.findViewById(R.id.manachan_gif);
                    Glide.with(RewardActivity.this).load(R.drawable.mana_clap).into(iv_gif);
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

            }
        });


    }


}
