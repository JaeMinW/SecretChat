package com.kty.pj_movie;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;

import vo.ReviewVO;

public class Test {

    SQLiteDatabase mDatabase;
    //Context context;
    @SuppressLint("WrongConstant")
    public Test(Context context){

        mDatabase = context.openOrCreateDatabase(
                Environment.getExternalStorageDirectory() + "/database/ReviewDB/ReviewDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        //searchQuery("select * from ReviewDB where user_idx=1");
    }

    //@SuppressLint("WrongConstant")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        mDatabase = openOrCreateDatabase(
//                Environment.getExternalStorageDirectory() + "/database/ReviewDB.db",
//                SQLiteDatabase.CREATE_IF_NECESSARY, null);
//
//        searchQuery("select * from ReviewDB where user_idx=1");
//    }//onCreate

    public ArrayList<ReviewVO> searchQuery(String query) {



        Cursor c = mDatabase.rawQuery(query, null);
        String[] col = new String[c.getColumnCount()];
        col = c.getColumnNames();

        String[] str = new String[c.getColumnCount()];
        ArrayList<ReviewVO> list = new ArrayList<>();


        while (c.moveToNext()) {
            //vo생성
            ReviewVO vo = new ReviewVO(R.layout.singeritem);
            for (int i = 0; i < col.length; i++) {
                switch (i) {

                    case 0:
                        vo.setUser_idx(c.getInt(0));
                        break;

                    case 1:
                        vo.setReview_idx(c.getInt(1));
                        break;

                    case 2:
                        vo.setM_title(c.getString(2));
                        break;

                    case 3:
                        vo.setM_director(c.getString(3));
                        break;

                    case 4:
                        vo.setM_date(c.getString(4));
                        break;

                    case 5:
                        vo.setImg(c.getString(5));
                        break;

                    case 6:
                        vo.setReview_title(c.getString(6));
                        break;

                    case 7:
                        vo.setEt_review(c.getString(7));
                        break;

                    case 8:
                        vo.setRating_bar(c.getInt(8));
                        break;

                }//switch
            }//for
            list.add(vo);
        }//searchQuery

        //Log.i("M", "서치쿼리:"+list.size());

        return list;
    }
}
