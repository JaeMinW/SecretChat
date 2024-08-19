package com.kty.pj_movie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

import parser.Parser2;
import vo.MovieVO;

public class NaverActivity extends AppCompatActivity {

    public static EditText search;
    ListView myListView;
    Button search_btn;
    Parser2 parser2;
    ViewModelAdapter adapter;
    Intent intent;
    int user_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver);

        search = findViewById(R.id.search);
        myListView = findViewById(R.id.myListView);
        search_btn = findViewById(R.id.search_btn);
        parser2 = new Parser2();
        intent = getIntent();
        user_idx = intent.getIntExtra("user_idx",0);
        Log.i("Naver", "onCreate: "+user_idx);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter = null;

                //서버연결( Async 클래스의 doInBackground() 메서드를 호출 )
                new NaverAsync().execute("홍","길","동");//Async클래스의 doInBackGround() 메서드 호출

            }
        });
    }//onCreate()

/*    public int getUser_idx() {
        return user_idx;
    }

    public void setUser_idx(int user_idx) {
        this.user_idx = user_idx;
    }*/


    //AsyncTask : 백그라운드 서버통신
    //parameter 1) doInBackground param 타입
    //          2) onProgressUpdate overriding시 사용 param 타입
    //          3) doInBackground 반환형, 작업의 최종 결과 처리하는 onPostExecute() param 타입
    class NaverAsync extends AsyncTask<String, Void, ArrayList<MovieVO>>{

        @Override
        protected ArrayList<MovieVO> doInBackground(String... strings) {

            //필수 메서드(반복, 제어 등의 백그라운드 필요 처리코드 담당 메서드)
            Log.i("NaverAsync_user_idx", "doInBackground: "+user_idx);
            return parser2.connectNaver(user_idx);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieVO> movieVOS) {
            //doInBackground에서 return된 최종 작업 결과를 movieVOS가 받게된다

            //movieVOs를 ListView를 그리기위해 존재하는 adpater클래스에게 넘겨줘야한다
            adapter = new ViewModelAdapter(
                    NaverActivity.this, R.layout.movie_item, movieVOS, myListView, user_idx);


            //준비된 어댑터를 ListView에 탑재
            myListView.setAdapter(adapter);

        }
    }

}