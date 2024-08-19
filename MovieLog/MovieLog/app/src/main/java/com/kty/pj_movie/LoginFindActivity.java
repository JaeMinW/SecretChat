package com.kty.pj_movie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LoginFindActivity extends AppCompatActivity {


    LinearLayout layout_idPage, layout_pwdPage;
    EditText find_name, find_id, find_IdPhone, find_pwdPhone;
    Button btn_findPWD, btn_findID, btn_find_loginPage ;




    @Override //회원번호 찾기 페이지
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginfind);

        btn_find_loginPage =findViewById(R.id.btn_find_loginPage);
        btn_findID = findViewById(R.id.btn_findID);
        btn_findPWD = findViewById(R.id.btn_findPWD);
        find_id = findViewById(R.id.find_id);
        find_pwdPhone = findViewById(R.id.find_pwdPhone);
        find_name =findViewById(R.id.find_name);
        find_id = findViewById(R.id.find_id);
        find_IdPhone = findViewById(R.id.find_IdPhone);

        layout_idPage = (LinearLayout)findViewById(R.id.layout_idPage);
        layout_pwdPage = (LinearLayout)findViewById(R.id.layout_pwdPage);



        btn_findID.setOnClickListener(click);
        btn_find_loginPage.setOnClickListener(click);
        btn_findPWD.setOnClickListener(click);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_findID:
                    //xml 페이지 화면 전환시키자.
                    layout_idPage.setVisibility(View.VISIBLE);
                    layout_pwdPage.setVisibility(View.GONE);
                    break;
                case R.id.btn_findPWD:
                    //xml 페이지 화면 전환시키자.
                    layout_idPage.setVisibility(View.GONE);
                    layout_pwdPage.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_find_loginPage:
                    Intent i = new Intent(LoginFindActivity.this, LoginActivity.class);
                    startActivity(i);
                    break;
            }
        }
    };

}