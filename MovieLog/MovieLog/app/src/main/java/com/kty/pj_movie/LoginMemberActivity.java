package com.kty.pj_movie;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginMemberActivity extends AppCompatActivity {

    TextView txt_check_id, txt_check_email,txt_check_pwd;
    EditText mem_name, mem_id, mem_pwd, mem_pwd_check, mem_email, mem_phone;
    Button btn_match, btn_resume, btn_mem_cancel, btn_email_match,btn_complete,btn_email_complete;
    int cnt = 0;

    //유효성
    boolean validcheck = false;  // 유효성, 3가 되면 가능하게
    boolean pwdCheck = false;
    boolean idCheck = false;
    boolean emailCheck = false;

    //db
    SQLiteDatabase mDatabase;
    boolean isFirst = true;
    SharedPreferences pref;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_member);

        mem_name = findViewById(R.id.mem_name);
        mem_id = findViewById(R.id.mem_id);
        mem_pwd = findViewById(R.id.mem_pwd);
        mem_pwd_check = findViewById(R.id.mem_pwd_check);
        mem_email = findViewById(R.id.mem_email);
        mem_phone = findViewById(R.id.mem_phone);

        txt_check_id = findViewById(R.id.txt_check_id);
        txt_check_email = findViewById(R.id.txt_check_email);
        txt_check_pwd = findViewById(R.id.txt_check_pwd);
        btn_email_match = findViewById(R.id.btn_email_match);
        btn_match = findViewById(R.id.btn_match);
        btn_resume = findViewById(R.id.btn_resume);
        btn_mem_cancel = findViewById(R.id.btn_mem_cancel);
        btn_complete = findViewById(R.id.btn_complete);
        btn_email_complete = findViewById(R.id.btn_email_complete);


        btn_match.setOnClickListener(click);
        btn_resume.setOnClickListener(click);
        btn_email_match.setOnClickListener(click);


        //DB
        pref = PreferenceManager.getDefaultSharedPreferences(
                LoginMemberActivity.this
        );
        mem_id.addTextChangedListener(new TextWatcher() {

            String memberName = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //btn_match.setEnabled(true);
                btn_match.setVisibility(View.VISIBLE);
                btn_complete.setVisibility(View.GONE);
                idCheck = false;
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mem_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btn_email_match.setVisibility(View.VISIBLE);
                btn_email_complete.setVisibility(View.GONE);
                emailCheck = false;
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mem_pwd_check.addTextChangedListener(new TextWatcher() { //비번 매칭

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //pwd2 = mem_pwd_check.getText().toString().trim();
                Log.i("A", "p1:"+mem_pwd.getText().toString().trim() + ", p2:" + charSequence.toString());
                if(!mem_pwd.getText().toString().trim().equals(charSequence.toString())){
                    txt_check_pwd.setVisibility(View.VISIBLE);
                    pwdCheck = false;
                }
                else if(mem_pwd.getText().toString().trim().equals(charSequence.toString())){
                    txt_check_pwd.setVisibility(View.GONE);
                    pwdCheck = true;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDatabase = openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/database/userDB/userDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

    }//onCreate()

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_match:
                    cnt = 2; // id 체크는 cnt 2번
                    String id = mem_id.getText().toString().trim();

                    Log.i("check-id", "onClick: "+id);

                    if (id.length() == 0) {//유효성 검사
                        Toast.makeText(
                                LoginMemberActivity.this, "아이디를 입력하세요",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        try{
                            Log.i("try-check1", "onClick: try문 들어왔음");
                            searchQuery(String.format("select * from user_tb where id = '%s'",id));
                            Log.i("try-check2", "onClick: try문 들어왔음");
                            btn_match.setVisibility(View.GONE);
                            btn_complete.setVisibility(View.VISIBLE);
                            idCheck = true;
                            Log.i("try-check3", "onClick: try문 들어왔음");
                        }catch (Exception e ){
                            Toast.makeText(
                                    LoginMemberActivity.this, "아이디가 중복됩니다.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            btn_match.setVisibility(View.VISIBLE);
                            btn_complete.setVisibility(View.GONE);
                            idCheck = false;
                            break;
                        }
                    }

                    break;
                case R.id.btn_email_match:
                    cnt = 4;
                    ArrayList<String> email1 = new ArrayList<String>();

                    String regx = "^(.+)@(.+)$" ; //@

                    //이메일 형식 만들기
                    String email = mem_email.getText().toString().trim();
                    email1.add(email);

                    Pattern pattern = Pattern.compile(regx);
                    for(String email2 : email1){
                        Matcher matcher = pattern.matcher(email2);
                        emailCheck = matcher.matches();

                        if(emailCheck == false){
                            Toast.makeText(
                                    LoginMemberActivity.this, "이메일형식이 올바르지 않습니다. @사용",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }else{
                            Toast.makeText(
                                    LoginMemberActivity.this, "이메일형식을 제대로 사용했습니다. @사용",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                    if (email.length() == 0) {//유효성 검사
                        Toast.makeText(
                                LoginMemberActivity.this, "이메일을 입력하세요",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        try{
                            searchQuery(String.format("select * from user_tb where email = '%s'",email));
                            btn_email_match.setVisibility(View.GONE);
                            btn_email_complete.setVisibility(View.VISIBLE);
                            emailCheck = true;
                        }catch (Exception e ){
                            Toast.makeText(
                                    LoginMemberActivity.this, "이메일이 중복됩니다.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            btn_email_complete.setVisibility(View.GONE);
                            btn_email_match.setVisibility(View.VISIBLE);
                            emailCheck = false;
                            break;
                        }
                    }


                    break;
                case R.id.btn_resume:
                    String name = mem_name.getText().toString().trim();
                    String pwd = mem_pwd.getText().toString().trim();
                    id = mem_id.getText().toString().trim();

                    //Split으로 핸드폰 번호 저장 할 때 -때기
                    String phone = mem_phone.getText().toString().trim();
                    String[] trimPhone = phone.split("-");


                    email = mem_email.getText().toString().trim();
                    Log.i("ABCDEFG", ""+emailCheck+pwdCheck+idCheck);
                    if(emailCheck == true && pwdCheck==true && idCheck == true){
                        validcheck = true;
                    }
                    if(validcheck == true ){
                        searchQuery(String.format(
                                "insert into user_tb(name,id,pwd,phone,email) values ('%s','%s','%s','%s','%s')", name, id, pwd,phone,email) );
                        Toast.makeText(
                                LoginMemberActivity.this, "회원가입 성공했습니다.",
                                Toast.LENGTH_SHORT
                        ).show();
                        //화면 전환
                        Intent i = new Intent(LoginMemberActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(LoginMemberActivity.this, "중복체크를 다시 하세요",Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.btn_mem_cancel:  //fin
                    //메세지 띄우기 앱을 종료할 것인지 로그인 페이지로 이동할 것인지.??
                    //
                    //  AlertDialog.Builder builder = new AlertDialog.Builder(MemberActivity.this);
                    // builder.setTitle("종료");
                    // builder.setMessage("종료하시겠습니까?");
                    //
                    //
                    //

                    Toast.makeText(LoginMemberActivity.this, "로그인 페이지로 이동합니다. ",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginMemberActivity.this, LoginActivity.class);
                    startActivity(i);
                    break;
            }
        }
    };

    public void searchQuery(String query) {//쿼리문 수행
        Log.i("query-check", "searchQuery: 쿼리문 들어옴");
        Cursor c = mDatabase.rawQuery(query, null); //파라미터 묶어서 보낼 것.
        String[] col = new String[c.getColumnCount()]; //array
        col = c.getColumnNames();

        String [] str = new String[c.getColumnCount()];
        String result = "";

        Log.i("query-check", "searchQuery: "+c.moveToNext());

        while(c.moveToNext()){
            for(int i = 0; i<col.length; i++){
                str[i] ="";
                str[i] += c.getString(i);
                result += col[i]+ ":"+str[i]+"\n";
                Log.i("cnt", "searchQuery: "+cnt);
                if(cnt == 2){ //id 중복 체크
                    if( str[2].equalsIgnoreCase(mem_id.getText().toString()) ){
                        Log.i("AAA", "::"+i+str[i] );
                        Toast.makeText(LoginMemberActivity.this, "사용불가 아이디", Toast.LENGTH_SHORT).show();
                        idCheck = false;
                        break;
                    }
                    break;
                }

                    /* if(cnt == 4){//phone 중복 체크
                        if( str[4].equalsIgnoreCase(mem_phone.getText().toString())){
                            Toast.makeText(MemberActivity.this, "중복되는 폰번호", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } */
                if(cnt == 4){//email 중복 체크
                    if( str[4].equalsIgnoreCase(mem_email.getText().toString())){
                        Toast.makeText(LoginMemberActivity.this, "중복되는 이메일", Toast.LENGTH_SHORT).show();
                        emailCheck = false;
                        break;
                    }
                    break;
                }
            }
            result += "\n" ;
        }//while
    }//searchQuery
    public void copyAssets(){ //assets폴더의 DB를 휴대폰 내부저장소에 저장
        AssetManager assetManager = getAssets();
        String[] files = null;
        String mkdir = "";

        try {
            files = assetManager.list("");
            InputStream in = null;
            OutputStream out =null;
            in = assetManager.open(files[3]);

            String str = "" +Environment.getExternalStorageDirectory();
            mkdir = str+"/database"; //database라는 이름의 폴더를 생성할 예정

            File mpath = new File(mkdir);

            if(!mpath.exists()){
                isFirst = true;
            }
            if(isFirst){ //database폴더가 없군

                out = new FileOutputStream(mkdir+"/"+files[3]);

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
            Log.i("t","err" + e.getMessage());
        }

        //    Log.i("ABCD", ""+);
    }//copyAssets

    public void save(){
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("save", isFirst);//save라는 이름을 저장
        edit.commit();
    }
    public  void load(){
        isFirst = pref.getBoolean("save", true);
    }

}