package com.kty.pj_movie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;

import vo.MovieVO;

public class BoardViewModelAdapter extends ArrayAdapter<MovieVO> {

    Context context;
    int resource;
    MovieVO vo;
    ArrayList<MovieVO> list;

    public BoardViewModelAdapter(Context context, int resource, ArrayList<MovieVO> list, ListView myListview) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        //movie_item.xml을 불러오는 것
        this.list = list;

        myListview.setOnItemClickListener( click );

    }

    AdapterView.OnItemClickListener click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String title = list.get(i).getTitle();
            String director = list.get(i).getDirector();
            String actor = list.get(i).getActor();
            String img = list.get(i).getImg();
            String pubDate = list.get(i).getPubDate();
            boolean search_result = true;

            //화면전환을 위한 Intent를 준비
            Intent intent = new Intent(context, Board_WriteActivity.class);
            intent.putExtra("title",title);
            intent.putExtra("director",director);
            intent.putExtra("actor",actor);
            intent.putExtra("img",img);
            intent.putExtra("pubDate",pubDate);
            intent.putExtra("search_result",search_result);

            context.startActivity(intent);
        }
    };//click

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //myListView.setAdapter(adapter) 하는 순간 호출되는 메서드(getView())
        //생성자의 파라미터 받은 list의 사이즈만큼 getView()메서드가 반복 호출

        LayoutInflater linf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = linf.inflate(resource, null);

        vo = list.get(position);

        TextView title = convertView.findViewById(R.id.movie_title);
        TextView director = convertView.findViewById(R.id.movie_director);
        TextView actor = convertView.findViewById(R.id.movie_actor);
        ImageView img = convertView.findViewById(R.id.movie_img);
        Log.i("img",""+img);

        title.setText(vo.getTitle());
        director.setText(vo.getDirector());
        actor.setText(vo.getActor());

        //백그라운드에서 이미지 로드
        new ImgAsync(img, vo).execute();//doInBackground() 호출

        return convertView;
    }//getView()

    //이미지를 가져올 Async클래스
    class ImgAsync extends AsyncTask<Void, Void, Bitmap>{

        Bitmap bm;
        ImageView img;
        MovieVO vo;

        public ImgAsync(ImageView img, MovieVO vo) {
            this.img = img;
            this.vo = vo;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try{
                //vo가 가지고 있는 vo.getImg()를 통해
                //이미지 경로를 따라 들어가자

                URL img_url = new URL(vo.getImg());
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
                            context.getResources(),
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
