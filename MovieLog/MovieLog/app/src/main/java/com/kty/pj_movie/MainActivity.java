package com.kty.pj_movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;

import vo.ReviewVO;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    MovieAdapter movieAdapter;
    ImageView img_search, img_board;

    SQLiteDatabase mDatabase;
    boolean isFirst = true;
    SharedPreferences pref;
    Test tt;
    String user_id;
    int user_idx;

    Intent intent;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tt = new Test(MainActivity.this);

        gridView = findViewById(R.id.gridView);
        img_search = findViewById(R.id.img_search);
        img_board = findViewById(R.id.img_board);

        pref = PreferenceManager.getDefaultSharedPreferences(
                MainActivity.this);


        load();
        //assets 폴더의 DB를 휴대폰 내부저장소에 저장
        copyAssets();
        save();

        //위에서 copyAssets()을 통해 복사된 DB 읽기
        mDatabase = openOrCreateDatabase(
                Environment.getExternalStorageDirectory()+"/database/ReviewDB/ReviewDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        user_idx = intent.getIntExtra("user_idx",0);

        Log.i("main_user_idx", "onCreate: "+ user_idx);
        Log.i("main_user_id", "onCreate: "+ user_id);

        img_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent board_i = new Intent(MainActivity.this, BoardMainActivity.class);
                board_i.putExtra("user_idx", user_idx);
                board_i.putExtra("user_id", user_id);

                startActivity(board_i);
            }
        });

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //화면전환
                Intent i = null;
                i = new Intent(MainActivity.this,NaverActivity.class);
                i.putExtra("user_idx",user_idx);

                Intent test = new Intent(MainActivity.this, ViewModelAdapter.class);
                test.putExtra("user_idx",user_idx);
                startActivity(i);

            }
        });


        //gridView.setAdapter(movieAdapter);

//        gridView.setOnClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast
//
//            }
//        });

        tt.searchQuery(String.format("create table if not exists ReviewDB("+
                "user_idx number(%d), review_idx Integer primary key autoincrement,"+
                "m_title varchar2(%d), m_director varchar2(%d),"+
                "m_date varchar2(%d), img varchar2(%d),"+
                "review_title varchar2(%d), et_review varchar2(%d),"+
                "rating_bar number(%d) )", 2, 16, 16, 16, 16, 16, 16, 5));

    }

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

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("M", "나는 onResume()");

        Log.i("main_idx", "onResume: "+user_idx);
        movieAdapter = new MovieAdapter(MainActivity.this,
                tt.searchQuery(String.format("select * from ReviewDB where user_idx=%d",user_idx)), gridView);
        movieAdapter.addItem(new ReviewVO(R.drawable.no_image));

        int list_size = 3;

        for( int i = list_size; i < 12 ; i++){
            movieAdapter.addItem(new ReviewVO(R.drawable.no_image));
        }

        gridView.setAdapter(movieAdapter);
    }


}