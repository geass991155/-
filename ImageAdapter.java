package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public ImageAdapter(Context c){
        mContext=c;
    }
    public int getCount(){
        return mThumbIds.length;
    }
    public Object getItem(int position){
        return null;
    }
    public long getItemId(int position){
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;
        if (convertView==null){
            imageView=new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(150,150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        }else{
            imageView=(ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }
    public  Integer[] mThumbIds={
            R.drawable.fifty_in_01,R.drawable.fifty_in_02,
            R.drawable.fifty_in_03,R.drawable.fifty_in_04,
            R.drawable.fifty_in_05,R.drawable.fifty_in_06,
            R.drawable.fifty_in_07,R.drawable.fifty_in_08,
            R.drawable.fifty_in_09,R.drawable.fifty_in_10,
            R.drawable.fifty_in_11,R.drawable.fifty_in_12,
            R.drawable.fifty_in_13,R.drawable.fifty_in_14,
            R.drawable.fifty_in_15,R.drawable.fifty_in_16,
            R.drawable.fifty_in_17,R.drawable.fifty_in_18,
            R.drawable.fifty_in_19,R.drawable.fifty_in_20,
            R.drawable.fifty_in_21,R.drawable.fifty_in_22,
            R.drawable.fifty_in_23,R.drawable.fifty_in_24,
            R.drawable.fifty_in_25,R.drawable.fifty_in_26,
            R.drawable.fifty_in_27,R.drawable.fifty_in_28,
            R.drawable.fifty_in_29,R.drawable.fifty_in_30,
            R.drawable.fifty_in_31,R.drawable.fifty_in_32,
            R.drawable.fifty_in_33,R.drawable.fifty_in_34,
            R.drawable.fifty_in_35,R.drawable.fifty_in_36,
            R.drawable.fifty_none,R.drawable.fifty_in_38,
            R.drawable.fifty_none,R.drawable.fifty_in_40,
            R.drawable.fifty_in_41,R.drawable.fifty_in_42,
            R.drawable.fifty_in_43,R.drawable.fifty_in_44,
            R.drawable.fifty_in_45,R.drawable.fifty_in_46,
            R.drawable.fifty_none,R.drawable.fifty_in_48,
            R.drawable.fifty_none,R.drawable.fifty_in_50,
    };
}
