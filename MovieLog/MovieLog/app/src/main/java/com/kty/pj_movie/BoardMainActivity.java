package com.kty.pj_movie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.ImageView;
import android.widget.ListView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vo.BoardVO;
import vo.MovieVO;

public class BoardMainActivity extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    boolean isFirst = true;
    SharedPreferences pref;

    ListView review_board;
    Button btn_board_write;
    BoardVO vo;
    ArrayList<BoardVO> list;
    BoardListModelAdapter listAdapter;
    Intent i;
    int user_idx;
    String user_id;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_main);

        btn_board_write = findViewById(R.id.btn_board_write);
        review_board = findViewById(R.id.review_board);

        i = getIntent();
        user_idx = i.getIntExtra("user_idx",0);
        user_id = i.getStringExtra("user_id");


        btn_board_write.setOnClickListener(write);

        pref = PreferenceManager.getDefaultSharedPreferences(BoardMainActivity.this);
        load();
        //assets폴더의 DB를 휴대폰 내부저장소에 저장
        copyAssets();
        save();

        if(ActivityCompat.checkSelfPermission( BoardMainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            //저장 권한 수락해줘야 한다
            setPermission();
            return;
        }

        if(ActivityCompat.checkSelfPermission( BoardMainActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){

            //주소록 접근 권한 수락해줘야 한다
            setPermission();
            return;
        }

        //위에서 copyAssets()을 통해 복사된 DB를 읽기
        mDatabase = openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/database/board/movieBoardDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        //listAdapter = null;

        //new BoardAsync().execute("홍","길");

        searchQuery(String.format(
                "create table if not exists board( boardnum Integer primary key autoincrement ,id varchar2(%d), pw varchar2(%d), subject varchar2(%d),write CLOB,title varchar2(%d), director varchar2(%d), actor varchar2(%d), image varchar2(%d), pubdate varchar2(%d) )"
                ,4, 16, 16, 16, 16, 16, 16, 16, 32));

        searchQuery("select * from board order by boardnum DESC ");

    }//onCreate()

    //게시글 작성 Activity 실행
    View.OnClickListener write = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i_write = new Intent(BoardMainActivity.this, Board_WriteActivity.class);
            i_write.putExtra("user_id",user_id);
            i_write.putExtra("user_idx",user_idx);
            startActivity(i_write);
        }
    };

    //앱 권한설정에 대한 감지자
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //모든 권한이 수락이 완료 되었을 경우
            //액티비티를 재실행
            Intent i = new Intent(BoardMainActivity.this, BoardMainActivity.class);
            startActivity(i);
            finish();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            //한가지라도 허용되지 않은 권한이 있다면 호출

            //모든 권한이 수락되지 않았다면 강제종료
            /*Toast.makeText(BoardMainActivity.this,
                    "모든 권한을 수락해야 합니다",
                    Toast.LENGTH_SHORT).show();

            finish();//강제종료*/

            Intent i = new Intent(BoardMainActivity.this, BoardMainActivity.class);
            startActivity(i);
            finish();
        }
    };

    public void setPermission(){

        TedPermission.create().setPermissionListener(permissionListener)
                .setDeniedMessage("이 앱에서 요구하는 권한이 있습니다\n\n[설정] > [권한]에서 해당 권한을 활성화 해주세요")
                .setPermissions( Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS).check();

    }//setPermission()

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
            Log.i("files", "copyAssets: " + files[2]);

            //내부저장소에 폴더 생성
            //휴대폰 내부(기본) 저장소의 root(최상위) 경로로 접근
            String str = "" + Environment.getExternalStorageDirectory();

            Log.i("str", "copyAssets: " +str);

            mkdir = str + "/database/board/";//database라는 이름의 폴더를 생성할 예정
            File mpath = new File(mkdir);

            Log.i("path", "copyAssets: "+ mpath);

            if(!mpath.exists()){
                isFirst = true;
            }

            if( isFirst ){
                mpath.mkdirs();//database라는 폴더를 생성

                //root/database/movieBoardDB.db
                out = new FileOutputStream(mkdir + "/" + files[0]);


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

    public void searchQuery( String query ){
        list = new ArrayList<>();
        Cursor c = mDatabase.rawQuery(query, null);

        //c.getColumnCount() : board테이블의 컬럼 수
        String[] col = new String[c.getColumnCount()];
        col = c.getColumnNames();
        //col[0] : boardnum
        //col[1] : phone
        //col[2] : age

/*        for( int i = 0; i < c.getColumnCount(); i++){
            Log.i("My", ""+col[i]);
        }

        int[] num = new int[c.getColumnCount()];
        String[] str = new String[c.getColumnCount()];
        while(c.moveToNext()) {
            for (int i = 1; i < c.getColumnCount(); i++) {
                str[i] = "";
                str[i] += c.getString(i);
                Log.i("my_str", "searchQuery: " + str[i]);
            }
        }*/

        String[] str = new String[c.getColumnCount()];

        //행 단위로 한줄씩 커서가 이동
        while (c.moveToNext()) {
            BoardVO vo = new BoardVO();

            for (int i = 0; i < col.length; i++) {

                if (i > 0) {
                    str[i] = "";
                    str[i] += c.getString(i);
                }

                switch (i) {
                    case 0:
                        Log.i("list_", "searchQuery: " + vo);
                        vo.setBoardNUM(c.getInt(0));
                        break;

                    case 1:
                        vo.setId(str[1]);
                        break;

                    case 2:
                        vo.setPw(str[2]);
                        break;

                    case 3:
                        vo.setSubject(str[3]);
                        break;

                    case 4:
                        vo.setWrite(str[4]);
                        break;

                    case 5:
                        vo.setTitle(str[5]);
                        break;

                    case 6:
                        vo.setDirector(str[6]);
                        break;

                    case 7:
                        vo.setActor(str[7]);
                        break;

                    case 8:
                        vo.setImage(str[8]);
                        break;

                    case 9:
                        vo.setPubDate(str[9]);
                        break;


                }//switch

            }//for

            Log.i("list_", "searchQuery: " + list.size());
            list.add(vo);

        }//while



        listAdapter = new BoardListModelAdapter(BoardMainActivity.this,R.layout.community_item, list, review_board);
        review_board.setAdapter(listAdapter);

    }//searchQuery

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
    }//imgasync

}