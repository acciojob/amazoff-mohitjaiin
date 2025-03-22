package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
        this.id=id;
        this.deliveryTime=convertTimeToMinutes(deliveryTime);
    }
    int convertTimeToMinutes(String time) {
        String[] parts = time.split(":"); // Splitti
        // ng HH:MM
        int hours = Integer.parseInt(parts[0]); // Extract HH
        int minutes = Integer.parseInt(parts[1]); // Extract MM
        return hours * 60 + minutes; // Convert to total minutes
    }
    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}
}