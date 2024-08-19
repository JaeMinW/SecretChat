package com.kty.pj_movie;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewActivity extends AppCompatActivity{


    TextView m_title, m_director, txt_date;
    EditText et_review_title, et_review;
    Intent i;
    ImageView m_img;
    Button btn_save, btn_cancel;
    String s_img;
    RatingBar rating;
    int user_idx;

    SQLiteDatabase mDatabase;
    boolean isFirst = true;
    SharedPreferences pref;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        pref = PreferenceManager.getDefaultSharedPreferences(
                ReviewActivity.this);

        load();
        //assets 폴더의 DB를 휴대폰 내부저장소에 저장
        copyAssets();
        save();

        //위에서 copyAssets()을 통해 복사된 DB 읽기
        mDatabase = openOrCreateDatabase(
                Environment.getExternalStorageDirectory()+"/database/ReviewDB/ReviewDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        i = getIntent();

        s_img = i.getStringExtra("img");
        String s_title = i.getStringExtra("title");
        String s_director = i.getStringExtra("director");
        user_idx = i.getIntExtra("user_idx",0);

        Log.i("review_user_idx", "onCreate: "+user_idx);

        m_img = findViewById(R.id.m_img);
        m_title = findViewById(R.id.m_title);
        m_director = findViewById(R.id.m_director);
        txt_date = findViewById(R.id.txt_date);

        et_review_title = findViewById(R.id.et_review_title);
        et_review = findViewById(R.id.et_review);

        m_title.setText(s_title);
        m_director.setText(s_director);
        txt_date.setText(getTime());

        rating =findViewById(R.id.rating);

        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);

        btn_save.setOnClickListener(click);
        btn_cancel.setOnClickListener(click);

        new ImageAsync().execute(s_img);

        searchQuery(String.format("create table if not exists ReviewDB("+
                "user_idx number(%d), review_idx Integer primary key autoincrement,"+
                "m_title varchar2(%d), m_director varchar2(%d),"+
                "m_date varchar2(%d), img varchar2(%d),"+
                "review_title varchar2(%d), et_review varchar2(%d),"+
                "rating_bar number(%d) )", 2, 16, 16, 16, 16, 16, 16, 5));


    }//onCreate()

    private String getTime(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        String getTime = dataFormat.format(date);

        return getTime;
    }//getTime

    //assets 폴더의 DB를 휴대폰 내부저장소에 저장
    public void copyAssets(){

        AssetManager assetManager = getAssets();
        String[] files = null;
        String mkdir = "";

        try{
            //assets 폴더의 모든 파일의 이름을 가져온다
            //files[1] --> "ReviewDB.db"
            files = assetManager.list("");

            InputStream in = null;
            OutputStream out = null;

            //files[1]의 값인 "ReviewDB.db"과 같은 이름 파일 찾아서
            //inputStream으로 읽어온다
            in = assetManager.open(files[0]);

            //내부저장소에 폴더 생성
            //휴대폰 내부(기본) 저장소의 root(최상위) 경로로 접근근
            String str = "" + Environment.getExternalStorageDirectory();
            mkdir = str + "/database/ReviewDB/";//database라는 이름의 폴더를 생성할 예정

            File mpath = new File(mkdir);
            if(!mpath.exists()){
                isFirst = true;
            }

            if (isFirst){
                mpath.mkdirs();//database라는 폴더 생성
                out = new FileOutputStream( mkdir+"/"+files[0]);

                byte[] buffer = new byte[2048];
                int read = 0;
                while((read = in.read(buffer)) != -1){
                    out.write(buffer,0,read);
                }

                out.close();
                in.close();

                isFirst = false;
            }


        }catch (Exception e){
            Log.i("T","err : "+e.getMessage());
        }

    }//copyAssets()

    public void save(){
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("save", isFirst);
        edit.commit();
    }

    //isFirst값 로드
    public void load(){
        isFirst = pref.getBoolean("save", true);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_save://리뷰 저장

                    String title = m_title.getText().toString().trim();
                    String director = m_director.getText().toString().trim();
                    String date = txt_date.getText().toString().trim();
                    String r_title = et_review_title.getText().toString().trim();
                    String review = et_review.getText().toString().trim();

                    float f_rating = rating.getRating();
                    int rating_bar = (int)(f_rating*10);

                    //int review_idx = 1;


                    if( date.length()==0 | r_title.length()==0 | review.length() == 0){
                        Toast.makeText(ReviewActivity.this,
                                "형식에 맞게 모두 작성해 주세요",
                                Toast.LENGTH_SHORT).show();
                    }else{

                        //searchQuery
                        searchQuery(String.format(
                                "insert into ReviewDB(user_idx, m_title, m_director, m_date, img, review_title, et_review, rating_bar) values('%d','%s','%s','%s','%s','%s','%s','%d')",
                                user_idx, title, director, date, s_img, r_title, review, rating_bar));



                        // s_img, user_idx, review_idx
                        //
                        //

                        Intent i = new Intent(ReviewActivity.this, MainActivity.class);
                        i.putExtra("user_idx", user_idx);
                        startActivity(i);
//                        searchQuery("select * from ReviewDB");

                    }

                    break;
                case R.id.btn_cancel:
                    finish();
            }//switch()
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }

    //쿼리문 수행 메서드
    public void searchQuery(String query){
        Cursor c = mDatabase.rawQuery(query,null);
        //c.getColumCount() : friend table의 colum 수(name, phone, age)
        String[] col = new String[c.getColumnCount()];
        col = c.getColumnNames();
        //col[0] : name
        //col[1] : phone
        //col[2] : age

        String[] str = new String[c.getColumnCount()];
        String result = "";//최종 결과를 저장해둘 변수

        //Log.i("MY", col[0]+ "/" + col[1] + "/" + col[2]);

        //행 단위로 한줄씩 커서가 이동
        while( c.moveToNext() ) {
            for (int i = 0; i < col.length; i++) {
                str[i] = "";
                str[i] += c.getString(i);//각 컬럼별 실제 데이터

                //result = 컬럼명 : 값( name : hong
                //                     phone : 010-111-1111
                //                     age : 20 )
                result += col[i] + " : " + str[i] + "\n";
            }
        }

        Log.i("res", "res:" + result);
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
            m_img.setImageBitmap(bitmap);
        }
    }

};