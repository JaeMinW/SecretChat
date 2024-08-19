package com.kty.pj_movie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.net.URL;

import vo.ReviewVO;


public class SingleViewer extends LinearLayout {

    ImageView img_cover;
    String s_img;
    ReviewVO vo;
    public SingleViewer(Context context) {
        super(context);

        init(context);
    }

    public SingleViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.singeritem,this,true);

        img_cover = findViewById(R.id.img_cover);
        s_img = vo.getImg();


    }


    public void setItem(ReviewVO reviewVO) {
        new ImageAsync().execute(s_img);
    }


    class ImageAsync extends AsyncTask<String, Void, Bitmap> {

        Bitmap bm;

        @Override
        protected Bitmap doInBackground(String... strings) {

            try{

                URL img_url = new URL( strings[0]);
                BufferedInputStream bis =
                        new BufferedInputStream(img_url.openStream());

                bm = BitmapFactory.decodeStream(bis);
                bis.close();
            }catch(Exception e){

            }

            if(bm != null){
                return bm;
            }

            Bitmap bitmap = BitmapFactory.decodeResource(
                    getResources(), R.drawable.no_image);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            img_cover.setImageBitmap(bitmap);
        }
    }
}
