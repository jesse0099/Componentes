/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

/**
 *
 * @author Nvidi
 */
public class Car {
    
    private int id;
    private int year;
    private String color;
    private String brand;
    private float price;
    
    public Car(){}

    public Car(int id, int year, String color, float price,String brand) {
        this.id = id;
        this.year = year;
        this.color = color;
        this.price = price;
        this.brand = brand;
    }
    
    
    public String getBrand(){
        return this.brand;
    }
    
    public float getPrice(){
        return this.price;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }
    
    public void setPrice(float price){
        this.price = price;
    }

    public void setColor(String color) {
        this.color = color;
    }
    
    public void setBrand(String brand){
        this.brand = brand;
    }
    
}
