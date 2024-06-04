package com.example.demo4;

public class DataItem {
    private int id;
    private String description;

    public DataItem(int id, String description){
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }

    public int getId(){
        return id;
    }

}
