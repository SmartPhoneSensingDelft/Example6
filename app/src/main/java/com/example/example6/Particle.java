package com.example.example6;

import android.provider.Telephony;

public class Particle {

    private double x;
    private double y;
    private double distance;
    private double rotation;

    public Particle(double x, double y, double dist, double rot) {
        this.x = x;
        this.y = y;
        this.distance = dist;
        this.rotation = rot;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getDistance() { return this.distance;}

    public void updateDistance(double distance, double rotation) {
        //TODO: add distance covered and the rotation of the particle, and update X and Y
        this.distance += distance;
        this.rotation = rotation;
        double radians = Math.toRadians(rotation);
//        System.out.println("Calculated Radians: " + radians + ", used rotation " + rotation);
        this.y -= distance * Math.cos(radians);
        this.x += distance * Math.sin(radians);
    }
}