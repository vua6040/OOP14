package com.example.ghichu.components;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ghichu.MainActivity;
import com.example.ghichu.R;
import com.example.ghichu.api.ApiService;
import com.example.ghichu.models.UserModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Identify extends AppCompatActivity {

    private List<UserModel> users;
    private EditText edtUsername, edtPassWord;
    private Button btnLogin;
    private Boolean isGetApiSuccess = true;
    private String userId="-2";
    private String Idmax="0";
    private UserModel user = new UserModel();
    private  Boolean isExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        users = new ArrayList<>();
        edtUsername= findViewById(R.id.inputUsername);
        edtPassWord= findViewById(R.id.inputPassword);
        btnLogin= findViewById(R.id.btnLogin);

        ApiService.apiService.getUsers().enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                users.addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                Toast.makeText(Identify.this,"Server Error",Toast.LENGTH_LONG).show();
                isGetApiSuccess = false;
            }
        });

        btnLogin.setOnClickListener(view -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassWord.getText().toString().trim();
            user.setUsername(username);
            user.setPassword(password);
            for(UserModel u:users){
                if(u.getPassword().trim().equals(password) && u.getUsername().trim().equals(username)){
                    userId = String.valueOf(u.getId());
                    isExist = true;
                    break;
                }
            }
            if(!users.isEmpty()&&users.size()>0){
                Idmax = String.valueOf(users.get(users.size()-1).getId());
            }else{
                Idmax="0";
            }

            System.out.println("listId "+DataLocalManager.getListUserAccountId());
            System.out.println("IDLOCAL:"+DataLocalManager.getFirstUser());
            System.out.println("userid:"+userId);
            System.out.println("isExist:"+isExist);

            List<String> userIds = new ArrayList<>();
            userIds.addAll(DataLocalManager.getListUserAccountId());
            if(userIds.contains(userId)){
                DataLocalManager.setListUserAccountId(userId);
                if(!userId.equals(DataLocalManager.getFirstUser())){
                    DataLocalManager.setFirstUser(userId);
                }
                Intent i = new Intent(Identify.this,MainActivity.class);
                startActivity(i);
            }else{
                DataLocalManager.setListUserAccountId(userId);
            }

            if(!userId.equals(DataLocalManager.getFirstUser()) || Integer.parseInt(userId)<=0){
                if(isExist){
                    Toast.makeText(getApplicationContext(),"User Exist!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(isGetApiSuccess && (username.length() != 0) && (password.length() != 0)){
                    ApiService.apiService.addUser(user).enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            DataLocalManager.setFirstUser(String.valueOf(Integer.parseInt(Idmax)+1));
                            Intent i = new Intent(Identify.this,MainActivity.class);
                            startActivity(i);
                        }
                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"Server Error!",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"Username or Password Fail!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}