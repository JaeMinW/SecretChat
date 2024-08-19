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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;

import vo.ReviewVO;


class MovieAdapter extends BaseAdapter {

    int resource;
    ReviewVO vo;
    Context context;
    ArrayList<ReviewVO> list;

    public MovieAdapter(Context context, ArrayList<ReviewVO> list,
                        GridView myListView){

        this.context = context;
        this.list = list;

        myListView.setOnItemClickListener(click);
    }//생성자

    AdapterView.OnItemClickListener click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            //String m_title, m_director, m_date, img, review_title, et_review;

            String img = list.get(i).getImg();
            int user_idx = list.get(i).getUser_idx();
            int review_idx = list.get(i).getReview_idx();
            int rating_bar = list.get(i).getRating_bar();
            String m_title = list.get(i).getM_title();
            String m_director = list.get(i).getM_director();
            String m_date = list.get(i).getM_date();
            String review_title = list.get(i).getReview_title();
            String et_review = list.get(i).getEt_review();

            Intent intent = new Intent(context, Review_detailActivity.class);
            intent.putExtra("img", img);
            intent.putExtra("user_idx", user_idx);
            intent.putExtra("review_idx", review_idx);
            intent.putExtra("rating_bar", rating_bar);
            intent.putExtra("title", m_title);
            intent.putExtra("director", m_director);
            intent.putExtra("date", m_date);
            intent.putExtra("r_title", review_title);
            intent.putExtra("review", et_review);

            Log.i("T", "r:"+list.get(i).getReview_title() + ", rev:"+ list.get(i).getEt_review());

            context.startActivity(intent);


        }
    };



    // ArrayList<ReviewVO> list = new ArrayList<ReviewVO>();

    @Override
    public int getCount() {
        return list.size();
    }

    public void addItem(ReviewVO reviewVO) {
        list.add(reviewVO);
    }

    @Override
    public ReviewVO getItem(int i) {return list.get(i);}

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater linf =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        resource = R.layout.singeritem;

        //resource가 movie_item 하나의 layout
        convertView = linf.inflate(resource, null);
        ImageView img = convertView.findViewById(R.id.img_cover);
        vo = list.get(position);
        Log.i("M", "listSize:" + list.size() + ",data:"+list.get(0).getImg());
        new ImageAsync(context, img, vo).execute();
//            SingleViewer singerViewer = new SingleViewer(getApplicationContext());
//            singerViewer.setItem(list.get(i));
        return convertView;
    }

    class ImageAsync extends AsyncTask<String, Void, Bitmap> {

        Bitmap bm;
        ImageView img;
        ReviewVO vo;
        Context context;

        public ImageAsync(Context context, ImageView img, ReviewVO vo){
            this.img = img;
            this.vo = vo;
            this.context = context;

        }

        @Override
        protected Bitmap doInBackground(String... strings) {

            try{

                URL img_url = new URL( vo.getImg());
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
                    context.getResources(), R.drawable.no_image);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            img.setImageBitmap(bitmap);
        }
    }

}