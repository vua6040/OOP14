package com.example.ghichu.components;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ghichu.R;

public class Identify extends AppCompatActivity {
    EditText edtUsername, edtPassWord;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        edtUsername=(EditText)findViewById(R.id.inputEmail);
        edtPassWord=(EditText)findViewById(R.id.inputPassword);
        btnLogin=(Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username ="admin";
                String password ="admin";
                if(edtUsername.getText().toString().equals(username) &&    edtPassWord.getText().toString().equals(password)){
                    Toast.makeText(getApplicationContext(),"Đăng nhập thành công",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Tài khoản hoặc Mật khẩu sai",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}