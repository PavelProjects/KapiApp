package com.pavel.kapicard;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pavel.kapicard.Api.App;
import com.pavel.kapicard.model.Card;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllClients extends AppCompatActivity {
    private CardClientAdapter adapter;
    private List<Card> cards =  new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_clients);
        ListView listView = (ListView) findViewById(R.id.clients);
        adapter = new CardClientAdapter(this, R.layout.client_list_item, cards);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AllClients.this);
                LayoutInflater inflater = AllClients.this.getLayoutInflater();
                final View customView = inflater.inflate(R.layout.owner_wind, null);
                final TextView phone = (TextView) customView.findViewById(R.id.phone_number);
                final TextView name = (TextView) customView.findViewById(R.id.owner_name);
                final TextView mail = (TextView) customView.findViewById(R.id.owner_mail);
                final TextView bike = (TextView) customView.findViewById(R.id.bike_model);

                builder.setView(customView)
                        .setTitle("Данные держателя");
                Card card = cards.get(i);
                AlertDialog ad = builder.create();
                name.setText(card.getOwner().getName().replace(" ", ""));
                phone.setText(String.valueOf(card.getOwner().getPhone_number()));
                mail.setText(card.getOwner().getMail().replace(" ", ""));
                bike.setText(card.getOwner().getBike_model().replace(" ", ""));
                ad.show();
            }
        });
        listView.setAdapter(adapter);
        loadCards();
    }
    private void loadCards(){
        App.getApi().allCards(MainActivity.getAuthToken()).enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                if (response.isSuccessful()){
                    try{
                        cards.clear();
                        cards.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        setTitle("Всего: "+cards.size());
                    }catch(Exception e){
                        Log.d("fLog",e.getLocalizedMessage());
                    }
                }else{
                    Toast.makeText(AllClients.this,"что-то пошло не так",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                Toast.makeText(AllClients.this,t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
