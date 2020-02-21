package org.tensorflow.lite.examples.classification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.AnimationListener;


public class IndexActivity extends AppCompatActivity {
    private ImageButton go_page_camera;
    private ImageButton go_page_fifty;
    private ImageView imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ImageButton nextPageBtn1 = (ImageButton)findViewById(R.id.go_page_camera);
        ImageButton nextPageBtn2 = (ImageButton)findViewById(R.id.go_page_fifty);
        ImageButton nextPageBtn3 = (ImageButton)findViewById(R.id.go_page_reward);
        ImageButton nextPageBtn4 = (ImageButton)findViewById(R.id.go_page_learn);
        nextPageBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    GifImageView ImageView = findViewById(R.id.manachan_gif);
                    GifDrawable gif_bird=new GifDrawable(getResources(),R.drawable.mana_bird);
                    gif_bird.setLoopCount(1);
                    gif_bird.addAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationCompleted(int loopNumber) {
                            try{
                                GifDrawable gifDrawable1=new GifDrawable(getResources(),R.drawable.mana_stand);
                                ImageView.setImageDrawable(gifDrawable1);
                                Intent intent = new Intent();
                                intent.setClass(IndexActivity.this  , ClassifierActivity.class);
                                startActivity(intent);
                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    ImageView.setImageDrawable(gif_bird);
                }catch(Exception e) {
                    e.printStackTrace();
                }

            }
        });
        nextPageBtn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{
                    GifImageView ImageView = findViewById(R.id.manachan_gif);
                    GifDrawable gif_bird=new GifDrawable(getResources(),R.drawable.mana_bird);
                    gif_bird.setLoopCount(1);
                    gif_bird.addAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationCompleted(int loopNumber) {
                            try{
                                GifDrawable gifDrawable1=new GifDrawable(getResources(),R.drawable.mana_stand);
                                ImageView.setImageDrawable(gifDrawable1);
                                Intent intent2 = new Intent();
                                intent2.setClass(IndexActivity.this  , FiftyActivity.class);
                                startActivity(intent2);
                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    ImageView.setImageDrawable(gif_bird);
                }catch(Exception e) {
                    e.printStackTrace();
                }

            }

        });
        nextPageBtn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{
                    GifImageView ImageView = findViewById(R.id.manachan_gif);
                    GifDrawable gif_bird=new GifDrawable(getResources(),R.drawable.mana_bird);
                    gif_bird.setLoopCount(1);
                    gif_bird.addAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationCompleted(int loopNumber) {
                            try{
                                GifDrawable gifDrawable1=new GifDrawable(getResources(),R.drawable.mana_stand);
                                ImageView.setImageDrawable(gifDrawable1);
                                Intent intent3 = new Intent();
                                intent3.setClass(IndexActivity.this  , RewardActivity.class);
                                startActivity(intent3);
                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    ImageView.setImageDrawable(gif_bird);
                }catch(Exception e) {
                    e.printStackTrace();
                }


            }
        });
        nextPageBtn4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{
                    GifImageView ImageView = findViewById(R.id.manachan_gif);
                    GifDrawable gif_bird=new GifDrawable(getResources(),R.drawable.mana_bird);
                    gif_bird.setLoopCount(1);
                    gif_bird.addAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationCompleted(int loopNumber) {
                            try{
                                GifDrawable gifDrawable1=new GifDrawable(getResources(),R.drawable.mana_stand);
                                ImageView.setImageDrawable(gifDrawable1);
                                Intent intent4 = new Intent();
                                intent4.setClass(IndexActivity.this  , LearnActivity.class);
                                startActivity(intent4);
                            }catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    ImageView.setImageDrawable(gif_bird);
                }catch(Exception e) {
                    e.printStackTrace();
                }


            }
        });

        GifImageView ImageView = findViewById(R.id.manachan_gif);
            try {
                GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.mana_hi);
                gifDrawable.setLoopCount(1);
                gifDrawable.addAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationCompleted(int loopNumber) {
                        try{
                            GifDrawable gifDrawable1=new GifDrawable(getResources(),R.drawable.mana_stand);
                            ImageView.setImageDrawable(gifDrawable1);
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            ImageView.setImageDrawable(gifDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ImageView mana_img=(ImageView)findViewById(R.id.manachan_gif);
        mana_img.setOnClickListener(new View.OnClickListener() {
            int i=0;
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - firstPressedTime < 2000) {
                    i+=1;
                    if(i>=5 && i<10){
                        try{
                            GifImageView ImageView = findViewById(R.id.manachan_gif);
                            GifDrawable gif_donttouch=new GifDrawable(getResources(),R.drawable.mana_donttouch);
                            gif_donttouch.setLoopCount(1);
                            gif_donttouch.addAnimationListener(new AnimationListener() {
                                @Override
                                public void onAnimationCompleted(int loopNumber) {
                                    try{
                                        GifDrawable gifDrawable1=new GifDrawable(getResources(),R.drawable.mana_stand);
                                        ImageView.setImageDrawable(gifDrawable1);
                                    }catch(Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            ImageView.setImageDrawable(gif_donttouch);
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }else if(i>=10){
                        try{
                            GifImageView ImageView = findViewById(R.id.manachan_gif);
                            GifDrawable gif_angry=new GifDrawable(getResources(),R.drawable.mana_angry);
                            gif_angry.setLoopCount(1);
                            gif_angry.addAnimationListener(new AnimationListener() {
                                @Override
                                public void onAnimationCompleted(int loopNumber) {
                                    try{
                                        GifDrawable gifDrawable1=new GifDrawable(getResources(),R.drawable.mana_stand);
                                        ImageView.setImageDrawable(gifDrawable1);
                                    }catch(Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            ImageView.setImageDrawable(gif_angry);
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                        i=0;
                    }
                } else {
                    firstPressedTime = System.currentTimeMillis();
                }
            }
        });



    }

    // 第一次按下返回键的事件
    private long firstPressedTime;
    // System.currentTimeMillis() 当前系统的时间
    public void onBackPressed(){
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            try{
                GifImageView ImageView = findViewById(R.id.manachan_gif);
                GifDrawable gif_byebye=new GifDrawable(getResources(),R.drawable.mana_byebye);
                gif_byebye.setLoopCount(1);
                gif_byebye.addAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationCompleted(int loopNumber) {
                        try{
                            GifDrawable gifDrawable1=new GifDrawable(getResources(),R.drawable.mana_stand);
                            ImageView.setImageDrawable(gifDrawable1);
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                ImageView.setImageDrawable(gif_byebye);
            }catch(Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(IndexActivity.this, "再按一次跟瑪納醬說byebye~", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }
}

