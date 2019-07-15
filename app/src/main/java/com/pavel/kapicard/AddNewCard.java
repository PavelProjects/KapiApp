package com.pavel.kapicard;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pavel.kapicard.Api.App;
import com.pavel.kapicard.model.Card;
import com.pavel.kapicard.model.Client;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewCard extends AppCompatActivity {
    private EditText number, name, phone, mail, bike;
    private Editable ed_number, ed_name, ed_phone, ed_mail, ed_bike;
    private Card card = new Card();
    private Client client = new Client();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_card);

        number = (EditText) findViewById(R.id.new_card_number);
        name = (EditText) findViewById(R.id.new_card_name);
        phone = (EditText) findViewById(R.id.new_card_phone);
        mail = (EditText) findViewById(R.id.new_card_mail);
        bike = (EditText) findViewById(R.id.new_card_bike);

        ed_number = number.getText();
        ed_name = name.getText();
        ed_phone = phone.getText();
        ed_mail = mail.getText();
        ed_bike = bike.getText();

    }

    public void addCard(View view) {
        if (ed_number.length() > 0 && ed_phone.length() > 0 && ed_name.length() > 0) {
            try {
                card.setId(Integer.valueOf(ed_number.toString()));
                client.setPhone_number(Integer.valueOf(ed_phone.toString()));
                client.setName(ed_name.toString());
                client.setMail(ed_mail.toString());
                client.setBike_model(ed_bike.toString());
                card.setOwner(client);
                card.setBalance(0);
                Log.d("fLog", gson.toJson(card));
                App.getApi().addCard(MainActivity.getAuthToken(), card).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 201) {
                            setResult(RESULT_OK, new Intent().putExtra("id", card.getId()));
                            AddNewCard.this.finish();
                        } else if (response.code()==409){
                            Toast.makeText(AddNewCard.this, "карта с таким номером или номером телеофна уже существует", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AddNewCard.this, "что то пошло не так", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.d("fLog", e.getLocalizedMessage());
            }
        } else {
            Toast.makeText(this, "НОМЕР КАРТЫ, ИМЯ И ТЕЛЕФОН ОБЯЗАТЕЛЬНЫ К ЗАПОЛНЕНИЮ!!!", Toast.LENGTH_LONG).show();
        }
    }
}
