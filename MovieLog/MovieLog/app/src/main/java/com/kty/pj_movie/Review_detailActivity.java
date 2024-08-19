package com.kty.pj_movie;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.net.URL;

public class Review_detailActivity extends AppCompatActivity {

    TextView txt_title, txt_director, txt_date, review_title, review;
    ImageView movie_img;
    Intent i;
    String s_img, title, director, date, r_title, s_review;
    int user_idx, review_idx, rating_bar;
    Button btn_del, btn_cancel, btn_update;

    SQLiteDatabase mDatabase;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        i = getIntent();
        s_img = i.getStringExtra("img");
        user_idx = i.getIntExtra("user_idx",0);
        review_idx = i.getIntExtra("review_idx",0);
        rating_bar = i.getIntExtra("rating_bar",0);
        title = i.getStringExtra("title");
        director = i.getStringExtra("director");
        date = i.getStringExtra("date");
        r_title = i.getStringExtra("r_title");
        s_review = i.getStringExtra("review");

        btn_del = findViewById(R.id.btn_del);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_update = findViewById(R.id.btn_update);

        txt_title = findViewById(R.id.txt_title);
        txt_director = findViewById(R.id.txt_director);
        txt_date = findViewById(R.id.txt_date);
        review_title = findViewById(R.id.review_title);
        review = findViewById(R.id.review);

        movie_img = findViewById(R.id.movie_img);

        txt_title.setText(title);
        txt_director.setText(director);
        txt_date.setText("Date : " + date);
        review_title.setText(r_title);
        review.setText(s_review);

        //rating_bar = 입력된 별점(10, 8.5...)
        //RatingBar rating;

        /*float rate = rating_bar/10;
        rating.setRating(rate);*/
        mDatabase = openOrCreateDatabase(
                Environment.getExternalStorageDirectory()+"/database/ReviewDB/ReviewDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        new ImageAsync().execute(s_img);

        btn_cancel.setOnClickListener(click);
        btn_del.setOnClickListener(click);
        btn_update.setOnClickListener(click);
    }//onCreate

    //버튼
    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_cancel:
                    finish();
                    break;

                case R.id.btn_del:

                    AlertDialog.Builder dialog =
                            new AlertDialog.Builder(Review_detailActivity.this);

                    dialog.setTitle(title + " 리뷰를 삭제하시겠습니까?");
                    dialog.setNegativeButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            searchQuery(String.format("delete from ReviewDB where review_idx=%d", review_idx));

                            Intent intent = new Intent(Review_detailActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });

                    dialog.setPositiveButton("아니오", null);
                    dialog.setCancelable(false);
                    dialog.show();
                    break;

                case R.id.btn_update:
                    Intent intent = new Intent(Review_detailActivity.this, Review_updateActivity.class);
                    intent.putExtra("img", s_img);
                    intent.putExtra("user_idx", user_idx);
                    intent.putExtra("review_idx", review_idx);
                    intent.putExtra("rating_bar", rating_bar);
                    intent.putExtra("title", title);
                    intent.putExtra("director", director);
                    intent.putExtra("date", date);
                    intent.putExtra("r_title", r_title);
                    intent.putExtra("review", s_review);
                    startActivity(intent);

            }
        }
    };

    public void searchQuery(String query){
        Log.i("A", "q:"+query);

        Cursor c = mDatabase.rawQuery(query, null);

        String[] col = c.getColumnNames();
        String[] str = new String[c.getColumnCount()];

        while(c.moveToNext()) {
            for (int i = 0; i < col.length; i++) {
                // if (i == review_idx) {
                str[i] = "";
                str[i] += c.getString(i);
                //   break;
            }
        }
    }//searchQuery


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
            movie_img.setImageBitmap(bitmap);
        }
    }

}