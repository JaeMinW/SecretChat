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

import vo.BoardVO;

public class BoardListModelAdapter extends ArrayAdapter<BoardVO> {

    Context context;
    int resource;
    BoardVO vo;
    ArrayList<BoardVO> list;

    public BoardListModelAdapter(Context context, int resource, ArrayList<BoardVO> list, ListView myListview) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        //movie_item.xml을 불러오는 것
        this.list = list;

        myListview.setOnItemClickListener(insert);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater linf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = linf.inflate(resource, null);

        vo = list.get(position);

        ImageView movie_img = convertView.findViewById(R.id.movie_img);
        TextView c_subject = convertView.findViewById(R.id.c_subject);
        TextView c_write = convertView.findViewById(R.id.c_write);
        TextView c_title = convertView.findViewById(R.id.c_title);
        TextView c_director = convertView.findViewById(R.id.c_director);
        TextView c_actor = convertView.findViewById(R.id.c_actor);

        c_subject.setText(vo.getSubject());
        c_write.setText(vo.getWrite());
        c_title.setText(vo.getTitle());
        c_director.setText(vo.getDirector());
        c_actor.setText(vo.getActor());

        //Log.i("list_adapter", "getView: "+c_subject);


        new ImgAsync(movie_img, vo.getImage()).execute();

        return convertView;
    }

    class ImgAsync extends AsyncTask<Void, Void, Bitmap> {

        Bitmap bm;
        ImageView img;
        String image;

        public ImgAsync(ImageView img, String image) {
            this.img = img;
            this.image = image;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try{
                //vo가 가지고 있는 vo.getImg()를 통해
                //이미지 경로를 따라 들어가자

                URL img_url = new URL(image);
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

    AdapterView.OnItemClickListener insert = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String rev_image = list.get(i).getImage();//영화 포스터
            String rev_title = list.get(i).getTitle();//영화 제목
            String rev_director = list.get(i).getDirector();//영화 감독
            String rev_subject = list.get(i).getSubject();//리뷰 제목
            String rev_write = list.get(i).getWrite();//리뷰 내용
            String rev_id = list.get(i).getId();//리뷰 작성ID
            String rev_pw = list.get(i).getPw();//리뷰 작성 시 설정한 PW
            int rev_Bnum = list.get(i).getBoardNUM();//게시글 등록번호
            String rev_date = list.get(i).getPubDate();//게시글 등록날짜
            boolean state = true;

            Intent rew_i = new Intent(context, BoardRevDetActivity.class);
            rew_i.putExtra("image",rev_image);
            rew_i.putExtra("title",rev_title);
            rew_i.putExtra("director",rev_director);
            rew_i.putExtra("subject",rev_subject);
            rew_i.putExtra("write",rev_write);
            rew_i.putExtra("id",rev_id);
            rew_i.putExtra("pw",rev_pw);
            rew_i.putExtra("Bnum",rev_Bnum);
            rew_i.putExtra("date",rev_date);
            rew_i.putExtra("state", state);

            context.startActivity(rew_i);

        }
    };
}
