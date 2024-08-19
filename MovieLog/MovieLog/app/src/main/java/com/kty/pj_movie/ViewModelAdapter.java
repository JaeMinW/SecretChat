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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;

import vo.MovieVO;

public class ViewModelAdapter extends ArrayAdapter<MovieVO> {

    Context context;
    int resource;
    MovieVO vo;
    ArrayList<MovieVO> list;
    int user_idx;

    public ViewModelAdapter(Context context, int resource,
                            ArrayList<MovieVO> list, ListView myListView, int user_idx) {
        super(context, resource, list);

        this.context = context;
        this.resource = resource;
        this.list = list;
        this.user_idx = user_idx;

        myListView.setOnItemClickListener(click);
    }//생성자
    AdapterView.OnItemClickListener click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String title = list.get(i).getM_title();
            String subtitle = list.get(i).getM_subtitle();
            String director = list.get(i).getM_director();
            String pubDate = list.get(i).getM_pubDate();
            String img = list.get(i).getM_img();

            Log.i("user_idx(ViewModel)", "onItemClick: "+user_idx);

            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("subtitle", subtitle);
            intent.putExtra("director", director);
            intent.putExtra("pubDate", pubDate);
            intent.putExtra("img", img);
            intent.putExtra("user_idx", user_idx);

            context.startActivity(intent);
        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //myListView.setAdapter(adapter) 하는 순간 호출(getView)
        //생성자의 파라미터 받은 list의 사이즈만큼 getView()메서드 반복호출

        LayoutInflater linf =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //resource가 movie_item 하나의 layout
        convertView = linf.inflate(resource, null);

        vo = list.get(position);

        TextView title = convertView.findViewById(R.id.movie_title);
        TextView subtitle = convertView.findViewById(R.id.movie_subtitle);
        TextView director = convertView.findViewById(R.id.movie_director);
        TextView pubDate = convertView.findViewById(R.id.movie_pubDate);
        ImageView img = convertView.findViewById(R.id.movie_img);

        title.setText(vo.getM_title());
        subtitle.setText(vo.getM_subtitle());
        director.setText("감독 : " + vo.getM_director());
        pubDate.setText(vo.getM_pubDate()+"년");

        //백그라운드에서 이미지 로드
        new ImgAsync(img, vo).execute();//doInBackground()호출

        return convertView;
    }//getView()

    //이미지를 가져올 Async 클래스
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
                //vo가 가지고 있는 vo.getM_img()를 통해
                //이미지 경로로 들어감

                URL img_url = new URL(vo.getM_img());
                BufferedInputStream bis =
                        new BufferedInputStream(img_url.openStream());

                bm = BitmapFactory.decodeStream(bis);

                bis.close();

            }catch (Exception e){

            }

            if(bm != null){
                return bm;
            }

            //이미지 없는 경우
            Bitmap bitmap =
                    BitmapFactory.decodeResource(
                            context.getResources(), R.drawable.no_image);

            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //비트맵 객체를 이미지 뷰로 변환
            img.setImageBitmap(bitmap);
        }
    }
}

