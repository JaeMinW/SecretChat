package com.kty.pj_movie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    ImageView img;
    TextView txt_title, txt_subtitle, txt_director, txt_pubDate;
    Button btn_comment;
    Intent i;
    String s_img;
    int user_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        i = getIntent();
        s_img = i.getStringExtra("img");
        String s_title = i.getStringExtra("title");
        String s_subtitle = i.getStringExtra("subtitle");
        String s_director = i.getStringExtra("director");
        String s_pubDate = i.getStringExtra("pubDate");
        user_idx = i.getIntExtra("user_idx",0);

        Log.i("DetailActivity_user_idx", "onCreate: "+user_idx);

        img = findViewById(R.id.img);
        txt_title = findViewById(R.id.txt_title);
        txt_subtitle = findViewById(R.id.txt_subtitle);
        txt_director = findViewById(R.id.txt_director);
        txt_pubDate = findViewById(R.id.txt_pubDate);

        txt_title.setText(s_title);
        txt_subtitle.setText(s_subtitle);
        txt_director.setText("감독 : " + s_director);
        txt_pubDate.setText("제작 년도 : " + s_pubDate);


        btn_comment = findViewById(R.id.btn_comment);
        btn_comment.setOnClickListener(click);

        new ImageAsync().execute(s_img);
    }//onCreate()

    //리뷰작성
    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(DetailActivity.this, ReviewActivity.class);
            intent.putExtra("title", txt_title.getText().toString());
            intent.putExtra("director", txt_director.getText().toString());
            intent.putExtra("img", s_img);
            intent.putExtra("user_idx",user_idx);

            startActivity(intent);
        }
    };

    class ImageAsync extends AsyncTask<String, Void, Bitmap>{

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

            img.setImageBitmap(bitmap);
        }
    }
}