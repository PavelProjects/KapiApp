package com.pavel.kapicard;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pavel.kapicard.Api.App;
import com.pavel.kapicard.model.Client;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeOwnerData extends AppCompatActivity {
    private Gson gson = new Gson();
    private Client client_old;
    private EditText name, mail, bike_model, phone_number;
    private Editable new_name, new_mail, new_bike, new_phone_number;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_owner_data);
        Intent intent = getIntent();
        client_old=gson.fromJson(intent.getStringExtra("client"),Client.class);
        if (client_old==null) {
            Toast.makeText(this,"client null",Toast.LENGTH_SHORT).show();
            this.finish();
        }

        name = (EditText) findViewById(R.id.new_name);
        mail = (EditText) findViewById(R.id.new_email);
        bike_model = (EditText) findViewById(R.id.new_bike_model);
        phone_number = (EditText) findViewById(R.id.new_phone);
        phone_number.setText(String.valueOf(client_old.getPhone_number()));
        name.setText(client_old.getName());
        mail.setText(client_old.getMail());
        bike_model.setText(client_old.getBike_model());

        new_name = name.getText();
        new_mail = mail.getText();
        new_bike= bike_model.getText();
        new_phone_number = phone_number.getText();

    }
    public void saveChanges (View view){
        Client client = new Client();
        if (new_name.length()>0 && new_mail.length()>0 && new_bike.length()>0) {
            client.setPhone_number(Integer.valueOf(new_phone_number.toString()));
            client.setName(new_name.toString());
            client.setMail(new_mail.toString());
            client.setBike_model(new_bike.toString());
            client.setPhone_number(client_old.getPhone_number());
            Log.d("fLog",gson.toJson(client));
            App.getApi().changeOwnerData(MainActivity.getAuthToken(), client).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()){
                        setResult(RESULT_OK);
                        Toast.makeText(ChangeOwnerData.this,"изменено",Toast.LENGTH_SHORT).show();
                        ChangeOwnerData.this.finish();
                    }else{
                        Toast.makeText(ChangeOwnerData.this,"Что-то пошло не так",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ChangeOwnerData.this,t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(this,"Поле не может быть пустым!", Toast.LENGTH_LONG).show();
        }
    }
}
