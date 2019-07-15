package com.pavel.kapicard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pavel.kapicard.Api.App;
import com.pavel.kapicard.model.Card;
import java.io.UnsupportedEncodingException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Gson gson =  new Gson();
    private TextView balance;
    private Editable cardNumber, phoneNumber;
    private Card card = new Card();
    private String login, password;
    public static String userLP;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences=getSharedPreferences("user_keys", MODE_PRIVATE);
        login=sharedPreferences.getString("login","");
        password =sharedPreferences.getString("password","");
        Log.d("fLog",login+":"+ password);
        if (login.length()>0 && password.length()>0){
            checkAuth(login,password);
        }else{
            showAuth();
        }
        balance = (TextView) findViewById(R.id.balance);
        cardNumber = ((EditText)findViewById(R.id.cardNumber)).getText();
        phoneNumber = ((EditText) findViewById(R.id.phoneCard)).getText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_new_card :
                startActivityForResult(new Intent(MainActivity.this,AddNewCard.class),2);
                break;
            case R.id.action_all_clients :
                startActivity(new Intent(MainActivity.this,AllClients.class));
                break;
            case R.id.action_logout :
                editor = sharedPreferences.edit();
                editor.putString("login","");
                editor.putString("password","");
                editor.apply();
                showAuth();
                break;
        }
        return true;
    }

    public void showBalance (View view){
        update();
    }

    public void changeBalance(View view){
        if (card!=null) {
            if (card.getOwner()!=null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                final View customView = inflater.inflate(R.layout.card_balance_change, null);
                EditText number = (EditText) customView.findViewById(R.id.number);

                builder.setView(customView)
                        .setTitle("На счету: " + balance.getText())
                        .setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String number = ((EditText) customView.findViewById(R.id.number)).getText().toString();
                                String balance = ((EditText) customView.findViewById(R.id.new_balance)).getText().toString();
                                card.setId(Integer.valueOf(number));
                                card.setBalance(Integer.valueOf(balance));
                                if (balance.length() > 0 && number.length() > 0) {
                                    change(card);
                                    dialog.cancel();
                                } else {
                                    Toast.makeText(MainActivity.this, "Введите номер карты и сумму!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog ad = builder.create();
                if (cardNumber != null) {
                    if (cardNumber.length() > 0) {
                        number.setText(cardNumber.toString());
                    }
                }
                ad.show();
            }else{
                Toast.makeText(this,"Сначала найдите карту!",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Сначала найдите карту!",Toast.LENGTH_SHORT).show();
        }
    }

    private void showAuth (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View customView = inflater.inflate(R.layout.auth_wind, null);
        final EditText l = (EditText) customView.findViewById(R.id.login);
        final EditText p = (EditText) customView.findViewById(R.id.password);

        builder.setView(customView)
                .setTitle("Авторизация:")
                .setPositiveButton("Войти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (l.getText().length()>0&&p.getText().length()>0){
                            checkAuth(l.getText().toString(),p.getText().toString());
                            dialog.cancel();
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog ad = builder.create();
        ad.show();
    }
    public void showOwner (View view){
        if (card!=null) {
            if (card.getOwner()!=null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                final View customView = inflater.inflate(R.layout.owner_wind, null);
                final TextView phone = (TextView) customView.findViewById(R.id.phone_number);
                final TextView name = (TextView) customView.findViewById(R.id.owner_name);
                final TextView mail = (TextView) customView.findViewById(R.id.owner_mail);
                final TextView bike = (TextView) customView.findViewById(R.id.bike_model);

                builder.setView(customView)
                        .setTitle("Данные держателя")
                        .setNeutralButton("Изменить данные", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivityForResult(new Intent(MainActivity.this, ChangeOwnerData.class).putExtra("client", gson.toJson(card.getOwner())), 1);
                            }
                        });
                AlertDialog ad = builder.create();
                name.setText(card.getOwner().getName().replace(" ", ""));
                phone.setText(String.valueOf(card.getOwner().getPhone_number()));
                mail.setText(card.getOwner().getMail().replace(" ", ""));
                bike.setText(card.getOwner().getBike_model().replace(" ", ""));
                ad.show();
            }else{
                Toast.makeText(this,"Сначала найдите карту!",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Сначала найдите карту!",Toast.LENGTH_SHORT).show();
        }
    }

    public void cardByPhone (View view){
        if (phoneNumber.length()>0) {
            App.getApi().cardByPhone(getAuthToken(), Integer.valueOf(phoneNumber.toString())).enqueue(new Callback<Card>() {
                @Override
                public void onResponse(Call<Card> call, Response<Card> response) {
                    if(response.isSuccessful()){
                        if (response.code()==200){
                            try {
                                Log.d("fLog",gson.toJson(response.body()));
                                card = response.body();
                                balance.setText(String.valueOf(card.getBalance()));
                                cardNumber.clear();
                                cardNumber.append(String.valueOf(card.getId()));
                            }catch (Exception e){
                                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }else if (response.code() == 204){
                            Toast.makeText(MainActivity.this,"не существует карты с таким номером телефона",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Card> call, Throwable t) {

                }
            });
        }else{
            Toast.makeText(MainActivity.this,"введи номер телефона",Toast.LENGTH_SHORT).show();
        }
    }

    private void update(){
        if (cardNumber.length()>0) {
            App.getApi().getCardById(getAuthToken(),cardNumber.toString()).enqueue(new Callback<Card>() {
                @Override
                public void onResponse(Call<Card> call, Response<Card> response) {
                    if (response.isSuccessful()) {
                        if (response.code()==204){
                            Toast.makeText(MainActivity.this,"Нет такой карты",Toast.LENGTH_SHORT).show();
                        }else {
                            try {
                                card = response.body();
                                balance.setText(String.valueOf(card.getBalance()));
                                phoneNumber.clear();
                                phoneNumber.append(String.valueOf(card.getOwner().getPhone_number()));
                            }catch (Exception e){
                                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<Card> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(MainActivity.this,"ВВЕДИТЕ НОМЕР КАРТЫ",Toast.LENGTH_LONG).show();
        }
    }

    private void change(final Card card){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View customView = inflater.inflate(android.R.layout.simple_list_item_1, null);
        final TextView tet = (TextView) customView.findViewById(android.R.id.text1);

        builder.setView(customView)
                .setTitle("Авторизация:")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        App.getApi().changeBalance(getAuthToken(), card).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    update();
                                } else {
                                    Toast.makeText(MainActivity.this, "что то пошло не так", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog ad = builder.create();
        tet.setText("Новый баланс: "+String.valueOf(card.getBalance())+", все верно?");
        ad.show();

    }


    private void checkAuth(final String nlogin, final String npassword){
        userLP=nlogin+":"+npassword;
        App.getApi().authUser(getAuthToken()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Done",Toast.LENGTH_SHORT).show();
                    editor = sharedPreferences.edit();
                    editor.putString("login",nlogin);
                    editor.putString("password",npassword);
                    editor.apply();
                    userLP=nlogin+":"+npassword;
                }else{
                    if (response.code()==401){
                        showAuth();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                update();
                break;
            case 2:
                try {
                    int id = data.getIntExtra("id",1);
                    if (id > 0) {
                        cardNumber.clear();
                        cardNumber.append(String.valueOf(id));
                        balance.setText("0");
                        update();
                    }
                }catch (Exception e){
                    Log.d("fLog", e.getLocalizedMessage());
                }
        }
    }
    @NonNull
    public static String getAuthToken() {
        byte[] data = new byte[0];
        try {
            data = (userLP).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }

}
