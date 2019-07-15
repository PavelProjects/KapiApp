package com.pavel.kapicard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pavel.kapicard.model.Card;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CardClientAdapter extends ArrayAdapter<Card>{
    List<Card> cards;
    Context context;


    public CardClientAdapter(@NonNull Context context, int resource, @NonNull List<Card> cards) {
        super(context, resource);
        this.cards = cards;
        this.context = context;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Nullable
    @Override
    public Card getItem(int position) {
        return cards.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Card card = getItem(position);
        ClientInfoHolder holder = new ClientInfoHolder();

        LayoutInflater clientsInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = clientsInflater.inflate(R.layout.client_list_item,null);
        holder.name = (TextView) convertView.findViewById(R.id.client_name);
        holder.phone = (TextView) convertView.findViewById(R.id.client_phone);
        holder.card = (TextView) convertView.findViewById(R.id.client_card);
        convertView.setTag(holder);
        holder.name.setText("Имя: "+card.getOwner().getName());
        holder.phone.setText("Моб.: "+String.valueOf(card.getOwner().getPhone_number()));
        holder.card.setText("Карта: "+String.valueOf(card.getId()));
        return convertView;
    }

    class ClientInfoHolder{
        public TextView name;
        public TextView phone;
        public TextView card;
    }
}
