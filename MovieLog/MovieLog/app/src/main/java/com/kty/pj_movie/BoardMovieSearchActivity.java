package com.kty.pj_movie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import parser.Parser;
import vo.MovieVO;

public class BoardMovieSearchActivity extends AppCompatActivity {

    public static EditText search;
    ListView myListView;
    Button search_btn;
    Parser parser;
    BoardViewModelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardmoviesearch);

        search = findViewById(R.id.search);
        myListView = findViewById(R.id.myListView);
        search_btn = findViewById(R.id.search_btn);
        parser = new Parser();

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = null;

                //서버 연결( Async클래스의 doInBackground() 메서드를 호출)
                new MovieAsync().execute("홍","길");
            }
        });

    }//onCreate()

    class MovieAsync extends AsyncTask<String, Void, ArrayList<MovieVO>>{

        @Override
        protected ArrayList<MovieVO> doInBackground(String... strings) {

            //각종 반복이나 제어 등의 백그라운드에서 필요한 처리코드를 담당하는 메서드
            return parser.connectNaver();
        }

        @Override
        protected void onPostExecute(ArrayList<MovieVO> movieVOS) {
            //최종 작업 결과를 받는 메서드
            //doInBackground에서 return된 최종 작업 결과를 bookVOS가 받게된다.

            //movieVOS를 ListView를 그리기위해 존재하는 adapter클래스에게 넘겨줘야 한다
            adapter = new BoardViewModelAdapter(BoardMovieSearchActivity.this, R.layout.boardmovie_item,movieVOS, myListView);

            //준비된 어댑터를 ListView에 탑재
            myListView.setAdapter(adapter);

            //Log.i("MY",""+movieVOS.size());

            /*for( int i = 0; i < movieVOS.size(); i++){
                Log.i("MY", ""+movieVOS.get(i).getTitle());
            }*/
        }
    }

}