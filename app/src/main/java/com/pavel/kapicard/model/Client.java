package com.pavel.kapicard.model;

public class Client {
    private String id;
    private int phone_number;
    private String mail;
    private String name;
    private String bike_model;

    public Client(){}

    public Client(String id, int phone_numver, String mail, String name, String bike_model){
        this.id = id;
        this.phone_number = phone_numver;
        this.mail=mail;
        this.name=name;
        this.bike_model=bike_model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name.replace(" ","");
    }

    public int getPhone_number() {
        return phone_number;
    }

    public String getMail() {
        return mail.replace(" ","");
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPhone_number(int phone_number) {
        this.phone_number = phone_number;
    }

    public String getBike_model() {
        return bike_model.replace(" ","");
    }

    public void setBike_model(String bike_model) {
        this.bike_model = bike_model;
    }
}
