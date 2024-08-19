package com.kty.pj_movie;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Board_WriteActivity extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    boolean isFirst = true;
    SharedPreferences pref;

    Button btn_reg, btn_movie_select;
    EditText et_subject, et_pw, et_write;
    String title, director, actor, image, pubDate, user_id;
    boolean search_result = false;
    ImageView movie_image;
    TextView movie_title, movie_director, movie_actor;
    Intent i;
    int user_idx;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        pref = PreferenceManager.getDefaultSharedPreferences(Board_WriteActivity.this);
        load();
        //assets폴더의 DB를 휴대폰 내부저장소에 저장
        copyAssets();
        save();

        //위에서 copyAssets()을 통해 복사된 DB를 읽기
        mDatabase = openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/database/board/movieBoardDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        i = getIntent();

        title = i.getStringExtra("title");
        director = i.getStringExtra("director");
        actor = i.getStringExtra("actor");
        image = i.getStringExtra("img");
        pubDate = i.getStringExtra("pubDate");
        user_id = i.getStringExtra("user_id");
        user_idx = i.getIntExtra("user_idx",0);

        //boolean값은 값을 불러올 때, 값이 없을 경우 어떤 값으로 대체할지 정해야한다.
        search_result = i.getBooleanExtra("search_result", false);

        Log.i("Board_Write_image", "image: "+image);

        movie_image = findViewById(R.id.movie_image);
        movie_title = findViewById(R.id.movie_title);
        movie_director = findViewById(R.id.movie_director);
        movie_actor = findViewById(R.id.movie_actor);

        btn_reg = findViewById(R.id.btn_reg);
        btn_movie_select = findViewById(R.id.btn_movie_select);

        et_subject = findViewById(R.id.et_subject);
        //et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        et_write = findViewById(R.id.et_write);

        btn_reg.setOnClickListener(write_reg);
        btn_movie_select.setOnClickListener(movie_choice);


        if(search_result == true){
            movie_title.setText(title);
            movie_director.setText(director);
            movie_actor.setText(actor);
            new ImgAsync(movie_image, image).execute();
        }

        /*searchQuery(String.format(
                "create table if not exists board( boardnum number(%d) ,id varchar2(%d), pw varchar2(%d), subject varchar2(%d),write CLOB,title varchar2(%d), director varchar2(%d), actor varchar2(%d), image varchar2(%d), pubdate varchar2(%d) )"
                ,4, 16, 16, 16, 16, 16, 16, 16, 32));*/

   }

    View.OnClickListener write_reg = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(et_subject.getText() == null){
                Toast.makeText(Board_WriteActivity.this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
/*            }else if(et_id == null){
                Toast.makeText(Board_WriteActivity.this,"아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                return;*/
            }else if(et_pw.getText() == null) {
                Toast.makeText(Board_WriteActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }else if(title == null || director == null || actor == null || image == null ){
                Toast.makeText(Board_WriteActivity.this, "영화를 선택하세요", Toast.LENGTH_SHORT).show();
                return;
            }


            searchQuery(String.format(
                    "insert into board(id, pw, subject, write, title, director, actor, image) values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                    user_id,et_pw.getText().toString().trim(),et_subject.getText().toString().trim(),et_write.getText().toString().trim(),title,director,actor,image));

            Intent goList = new Intent(Board_WriteActivity.this, BoardMainActivity.class);
            startActivity(goList);
            finish();
        }
    };//write_reg


    View.OnClickListener movie_choice = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i_movie = new Intent(Board_WriteActivity.this, BoardMovieSearchActivity.class);
            startActivity(i_movie);
        }
    };

    public void searchQuery( String query ){
        Cursor c = mDatabase.rawQuery(query, null);

        //c.getColumnCount() : board테이블의 컬럼 수(name, phone, age)
        String[] col = new String[c.getColumnCount()];
        col = c.getColumnNames();
        //col[0] : name
        //col[1] : phone
        //col[2] : age

        String[] str = new String[c.getColumnCount()];
        String result = ""; //최종 결과를 저장해둘 변수

        //Log.i("My", col[0] + "/" + col[1] + "/" + col[2]);

        //행 단위로 한줄씩 커서가 이동
        while(c.moveToNext()){
            for(int i = 0; i < col.length; i++ ){
                str[i] = "";
                str[i] += c.getString(i);//각 컬럼별 실제 데이터

                //result = 컬럼명 : 값 ( name : hong
                //                      phone : 010-1111-1111
                //                      age : 20)
                result += col[i] + " : " + str[i] + "\n";
            }

            result += "\n";
        }//while

        Log.i("result", "result_query: "+result);

        //result_txt.setText(result);

    }//searchQuery

    //assets폴더의 DB를 휴대폰 내부저장소에 저장
    public void copyAssets(){

        AssetManager assetManager = getAssets();
        String[] files = null;
        String mkdir = "";

        try{
            //assets폴더의 모든 파일의 이름을 가져온다
            //files[0] --> "images"
            //files[1] --> "movieBoardDB.db"
            files = assetManager.list("");

            InputStream in = null;
            OutputStream out = null;

            //files[1]의 값인 "movieBoardDB.db"와 같은 이름의 파일을 찾아서
            //inputStream으로 읽어온다다
            in = assetManager.open(files[1]);

            Log.i("files", "files0: "+files[0]);
            Log.i("files", "files1: "+files[1]);
            Log.i("files", "files2: "+files[2]);

            //내부저장소에 폴더 생성
            //휴대폰 내부(기본) 저장소의 root(최상위) 경로로 접근
            String str = "" + Environment.getExternalStorageDirectory();

            Log.i("str", "copyAssets: " +str);

            mkdir = str + "/database/board";//database라는 이름의 폴더를 생성할 예정
            File mpath = new File(mkdir);

            Log.i("path", "copyAssets: "+ mpath);

            if(!mpath.exists()){
                isFirst = true;
            }

            if( isFirst ){
                mpath.mkdirs();//database라는 폴더를 생성

                //root/database/movieBoardDB.db
                out = new FileOutputStream(mkdir + "/" + files[1]);


                byte[] buffer = new byte[2048];
                int read = 0;
                while( (read = in.read(buffer)) != -1){
                    out.write(buffer, 0, read);
                }

                out.close();
                in.close();

                isFirst = false;
            }

        }catch (Exception e){

        }



    }//copyAssets()


    //isFirst의 값을 저장
    public void save(){
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("save", isFirst);
        edit.commit();
    }

    //isFirst의 값을 로드
    public void load(){
        isFirst = pref.getBoolean("save", true);
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
                            getResources(),
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