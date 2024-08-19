package com.kty.pj_movie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.net.URL;

public class BoardRevDetActivity extends AppCompatActivity {

    Intent rew_i;
    TextView rev_subject, rev_title, rev_director, rev_actor, rev_write;
    String subject, title, director, actor, write, image;
    ImageView rev_image;
    boolean state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rev_det);

        rev_subject = findViewById(R.id.rev_subject);
        rev_title = findViewById(R.id.rev_title);
        rev_director = findViewById(R.id.rev_director);
        rev_actor = findViewById(R.id.rev_actor);
        rev_write = findViewById(R.id.rev_write);
        rev_image = findViewById(R.id.rev_image);

        rew_i = getIntent();
        subject = rew_i.getStringExtra("subject");
        title = rew_i.getStringExtra("title");
        director = rew_i.getStringExtra("director");
        actor = rew_i.getStringExtra("actor");
        write = rew_i.getStringExtra("write");
        state = rew_i.getBooleanExtra("state",false);
        image = rew_i.getStringExtra("image");

        if( state ){
            rev_subject.setText(subject);
            rev_title.setText(title);
            rev_director.setText(director);
            rev_actor.setText(actor);
            rev_write.setText(write);
            new ImgAsync(rev_image, image).execute();
        }

    }//onCreate()

    class ImgAsync extends AsyncTask<Void, Void, Bitmap> {

        Bitmap bm;
        ImageView img;
        String image;

        public ImgAsync(ImageView img, String image) {
            this.img = img;
            this.image = image;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try{
                //vo가 가지고 있는 vo.getImg()를 통해
                //이미지 경로를 따라 들어가자

                URL img_url = new URL(image);
                Log.i("img_url",""+img_url);

                //BufferedInputStream을 통해 이미지를 로드
                BufferedInputStream bis =
                        new BufferedInputStream(img_url.openStream());

                //bis가 읽어온 데이터를 이미지로 변호나하기 위해
                //bitmap형태로 변경경
                bm = BitmapFactory.decodeStream(bis);

                bis.close();

            }catch (Exception e){

            }

            if( bm != null ){
                return bm;
            }
            //불러올 이미지가 없을 때 기본이미지로 bitmap설정
            Bitmap bitmap =
                    BitmapFactory.decodeResource(
                            getResources(),
                            R.drawable.no_image);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //비트맵 객체를 이미지뷰로 변환
            img.setImageBitmap(bitmap);
        }
    }
}