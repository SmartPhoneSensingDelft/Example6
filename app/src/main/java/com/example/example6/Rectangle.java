package com.example.example6;

import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Rectangle {
    private double topleftX;
    private double topleftY;
    private double width;
    private double length;

    public Rectangle(double X, double Y, double width, double length) {
        this.topleftX = X;
        this.topleftY = Y;
        this.width = width;
        this.length = length;
    }

    //allow easy drawing of a Rectangle on a screen
    public ShapeDrawable[] drawRectangle() {
        ShapeDrawable[] result = new ShapeDrawable[4];
        ShapeDrawable d0 = new ShapeDrawable(new RectShape()); //left
        d0.setBounds(0,0,0,0);
        d0.setBounds( (int)topleftX, (int)topleftY,(int)(topleftX + 10),(int) (topleftY + length));

        ShapeDrawable d1 = new ShapeDrawable(new RectShape()); //top
        d1.setBounds( (int)topleftX,(int)topleftY,  (int) (topleftX+width),(int) (topleftY + 10));

        ShapeDrawable d2 = new ShapeDrawable(new RectShape()); //right
        d2.setBounds( (int) (topleftX +width), (int)(topleftY ), (int)(topleftX + width+10), (int) (topleftY + length));

        ShapeDrawable d3 = new ShapeDrawable(new RectShape()); //bottom
        d3.setBounds( (int)(topleftX),(int)(topleftY+length),  (int) (topleftX+width+10),(int) (topleftY + length+10));

        result[0] = d0;
        result[1] = d1;
        result[2] = d2;
        result[3] = d3;

        return result;
    }

    public double getTopleftX() {
        return topleftX;
    }

    public double getTopleftY() {
        return topleftY;
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }
}
