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
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
//import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    //DB
    SQLiteDatabase mDatabase;
    SharedPreferences pref;
    boolean isFirst = true;

    boolean validCheck;
    Button btn_login, btn_create, btn_find;
    EditText user_id, user_pwd;

    @SuppressLint("WrongConstant") //DB 폴더 접근시 폴더를 만들지나 읽지 못했을 떄 사용하는 어노테이션
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ActivityCompat.checkSelfPermission( LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            //저장 권한 수락해줘야 한다
            setPermission();
            return;
        }

        if(ActivityCompat.checkSelfPermission( LoginActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){

            //주소록 접근 권한 수락해줘야 한다
            setPermission();
            return;
        }


        pref = PreferenceManager.getDefaultSharedPreferences(
                LoginActivity.this
        );


        load();
        //assets폴더의 DB를 휴대폰 내부저장소에 저장.
        copyAssets();
        save();

        user_id = findViewById(R.id.user_id);
        user_pwd = findViewById(R.id.user_pwd);

        btn_login = findViewById(R.id.btn_login);
        btn_create = findViewById(R.id.btn_create);
        btn_find = findViewById(R.id.btn_find);

        btn_login.setOnClickListener(click);
        btn_create.setOnClickListener(click);
        btn_find.setOnClickListener(click);



        mDatabase = openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/database/userDB/userDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);
        //mDatabase = openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/database/userDB/userDB.db",SQLiteDatabase.CREATE_IF_NECESSARY,null);
        createQuery(String.format("create table if not exists user_tb(user_idx Integer primary key autoincrement, name varchar2(%d), id varchar2(%d), pwd varchar2(%d),phone varchar2(%d),email varchar2(%d))", 16, 16, 16, 16, 16));

    }//onCreate()

    //앱 권한설정에 대한 감지자
    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //모든 권한이 수락이 완료 되었을 경우
            //액티비티를 재실행
            Intent i = new Intent(LoginActivity.this, LoginActivity.class);
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

            Intent i = new Intent(LoginActivity.this, LoginActivity.class);
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

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String id = user_id.getText().toString();
            String pwd = user_pwd.getText().toString();
            switch (view.getId()){
                case R.id.btn_login :
                    if(!(id.length() == 0 || pwd.length() == 0)){
                        Log.i("loginButton", "a");
                        searchQuery(String.format("select * from user_tb where id = '%s' and pwd = '%s'", id, pwd));
                        //searchQuery(String.format("select * from user_tb"));
                        return;
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "로그인 실패!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                case R.id.btn_create :
                    Intent i = new Intent(LoginActivity.this, LoginMemberActivity.class);
                    startActivity(i);
                    break;
                case R.id.btn_find :
                    Intent find = new Intent(LoginActivity.this, LoginFindActivity.class);
                    startActivity(find);
                    break;
            }
        }
    };

    //쿼리문 수행을 위한 메서드

    public void createQuery(String query){
        Cursor cq = mDatabase.rawQuery(query, null);
    }

    public void searchQuery(String query){
        String id = user_id.getText().toString();
        String pwd = user_pwd.getText().toString();
        int user_idx = 0;
        Cursor c = mDatabase.rawQuery(query, null);
        String[] col = new String[c.getColumnCount()];
        //id / pw  -> col
        col = c.getColumnNames();
        String [] str = new String[c.getColumnCount()];
        boolean id_check = false;
        boolean pwd_check = false;



        while(c.moveToNext()){
            for(int i = 0; i<col.length; i++){

                Log.i("length", "searchQuery: "+col.length);
                str[i] ="";
                str[i] += c.getString(i);

                switch ( i ) {
                    case 2:
                        str[2] ="";
                        str[2] += c.getString(2);

                        id_check = id.equals(str[2]);//true
                        Log.i("query_str[2]", "searchQuery: " + str[2]);
                        Log.i("query_id", "searchQuery: " + id);
                        break;

                    case 3:
                        str[3] ="";
                        str[3] += c.getString(3);

                        pwd_check = pwd.equals(str[3]);//true

                        Log.i("query_str[3]", "searchQuery: " + str[3]);
                        Log.i("query_pwd", "searchQuery: " + pwd);
                        break;
                }

                Log.i("check", "searchQuery: " + id_check);
                Log.i("check", "searchQuery: " + pwd_check);

                if(id_check == true && pwd_check == true){
                    /*Log.i("query_str[2]", "searchQuery: " + str[2]);
                    Log.i("query_id", "searchQuery: " + id);
                    Log.i("query_str[3]", "searchQuery: " + str[3]);
                    Log.i("query_pwd", "searchQuery: " + pwd);*/
                    user_idx = c.getInt(0);
                    validCheck = true;
                }else if(id_check == false || pwd_check == false){
                    validCheck = false;
                }


                /*Log.i("STR", "::"+i+ c.getString(i) );
                Log.i("CCC", "::"+col.length );
                Log.i("AAA", "::"+i+str[i] );
                Log.i("BBB", "col::"+col[i] );*/
            }
        }//while

        if(validCheck == true){
            Log.i("validcheck", "searchQuery: "+validCheck);
            Intent intent = new Intent( LoginActivity.this, MainActivity.class);
            intent.putExtra("user_id", id);
            intent.putExtra("user_idx", user_idx);
            Log.i("user_idx", "searchQuery: "+user_idx);
            startActivity(intent);
        }
        else{
            Toast.makeText(LoginActivity.this, "아이디나 비번이 틀렸습니다.", Toast.LENGTH_SHORT).show();
        }




    }//searchQuery

    public void copyAssets(){ //assets폴더의 DB를 휴대폰 내부저장소에 저장
        AssetManager assetManager = getAssets();
        String[] files = null;
        String mkdir = "";

        //Log.i("files", "copyAssets: "+files);

        try {
            files = assetManager.list("");
            InputStream in = null;
            OutputStream out =null;
            in = assetManager.open(files[3]);

            Log.i("try_catch", "copyAssets: " + files[0]);
            Log.i("try_catch", "copyAssets: " + files[1]);
            Log.i("try_catch", "copyAssets: " + files[2]);
            Log.i("try_catch", "copyAssets: " + files[3]);

            String str = ""+Environment.getExternalStorageDirectory();
            mkdir = str+"/database/userDB/"; //database라는 이름의 폴더를 생성할 예정
            //mkdir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/database/userDB/"; //database라는 이름의 폴더를 생성할 예정

            File mPath = new File(mkdir);
            Log.i("isFirst", "copyAssets: "+isFirst);
            if(!mPath.exists()){
                isFirst = true;
            }

            Log.i("isFirst-if", "copyAssets: " + isFirst);
            if(isFirst){ //database폴더가 없군

                mPath.mkdirs();

                //root/database/userDB.db
                Log.i("database-test", "copyAssets: database폴더가 없어");
                Log.i("test-out-before", "copyAssets: "+mkdir +"/"+ files[3]);
                out = new FileOutputStream(mkdir+"/"+files[3]);
                Log.i("test-out", "copyAssets: "+out);
                byte[] buffer = new byte[2048];
                int read = 0;
                while((read = in.read(buffer))!= -1){
                    out.write(buffer,0,read);
                }

                out.close();
                in.close();

                isFirst = false; //껐다 켰을 때, isFirst가 false가 되어야 한다.
            }
        }catch (Exception e ){
            Log.i("try_catch","err" + e.getMessage());
        }

        //    Log.i("ABCD", ""+);
        Log.i("load - asset - save", "load: CopyAssets");
    }//copyAssets

    public void save(){
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("save", isFirst);//save라는 이름을 저장
        edit.commit();
        Log.i("load - asset - save", "load: 세이브");
    }
    public  void load(){
        isFirst = pref.getBoolean("save", true);
        Log.i("load - asset - save", "load: 로드");
    }


}