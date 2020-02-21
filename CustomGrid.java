package org.tensorflow.lite.examples.classification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class CustomGrid extends BaseAdapter {
    private Context context;
    private int[] imageId;
    public CustomGrid(Context context,int[] imageId){
        this.context=context;
        this.imageId=imageId;
    }

    @Override
    public  int getCount(){
        return imageId.length;
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View grid;
        LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            grid=new View(context);
            grid=layoutInflater.inflate(R.layout.grid_single,null);
            ImageView imageView=(ImageView) grid.findViewById(R.id.grid_image);
            imageView.setImageResource(imageId[position]);
        }else{
            grid=(View)convertView;
        }
        return grid;
    }
}
