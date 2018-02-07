package com.example.example6;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Smart Phone Sensing Example 6. Object movement and interaction on canvas.
 */
public class MainActivity extends Activity implements OnClickListener {

    /**
     * The buttons.
     */
    private Button up, left, right, down;
    /**
     * The text view.
     */
    private TextView textView;
    /**
     * The shape.
     */
    private ShapeDrawable drawable;
    /**
     * The canvas.
     */
    private Canvas canvas;
    /**
     * The walls.
     */
    private List<ShapeDrawable> walls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the buttons
        up = (Button) findViewById(R.id.button1);
        left = (Button) findViewById(R.id.button2);
        right = (Button) findViewById(R.id.button3);
        down = (Button) findViewById(R.id.button4);

        // set the text view
        textView = (TextView) findViewById(R.id.textView1);

        // set listeners
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);

        // get the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // create a drawable object
        drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(Color.BLUE);
        drawable.setBounds(width/2-20, height/2-20, width/2+20, height/2+20);

        walls = new ArrayList<>();
        ShapeDrawable d = new ShapeDrawable(new RectShape());
        d.setBounds(width/2-200, height/2-90, width/2+200, height/2-80);
        ShapeDrawable d2 = new ShapeDrawable(new RectShape());
        d2.setBounds(width/2-200, height/2+60, width/2+200, height/2+70);
        ShapeDrawable d3 = new ShapeDrawable(new RectShape());
        d3.setBounds(width/2+200, height/2-90, width/2+210, height/2+70);
        walls.add(d);
        walls.add(d2);
        walls.add(d3);

        // create a canvas
        ImageView canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        canvasView.setImageBitmap(blankBitmap);

        // draw the objects
        drawable.draw(canvas);
        for(ShapeDrawable wall : walls)
            wall.draw(canvas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // This happens when you click any of the four buttons.
        // For each of the buttons, when it is clicked we change:
        // - The text in the center of the buttons
        // - The margins
        // - The text that shows the margin
        switch (v.getId()) {
            // UP BUTTON
            case R.id.button1: {
                Toast.makeText(getApplication(), "UP", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left,r.top-20,r.right,r.bottom-20);
                textView.setText("\n\tMove Up" + "\n\tTop Margin = "
                        + drawable.getBounds().top);
                break;
            }
            // DOWN BUTTON
            case R.id.button4: {
                Toast.makeText(getApplication(), "DOWN", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left,r.top+20,r.right,r.bottom+20);
                textView.setText("\n\tMove Down" + "\n\tTop Margin = "
                        + drawable.getBounds().top);
                break;
            }
            // LEFT BUTTON
            case R.id.button2: {
                Toast.makeText(getApplication(), "LEFT", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left-20,r.top,r.right-20,r.bottom);
                textView.setText("\n\tMove Left" + "\n\tLeft Margin = "
                        + drawable.getBounds().left);
                break;
            }
            // RIGHT BUTTON
            case R.id.button3: {
                Toast.makeText(getApplication(), "RIGHT", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left+20,r.top,r.right+20,r.bottom);
                textView.setText("\n\tMove Right" + "\n\tLeft Margin = "
                        + drawable.getBounds().left);
                break;
            }
        }
        // if there is a collision between the dot and any of the walls
        if(isCollision()) {
            // reset dot to center of canvas
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            drawable.setBounds(width/2-20, height/2-20, width/2+20, height/2+20);
        }

        // redrawing of the object
        canvas.drawColor(Color.WHITE);
        drawable.draw(canvas);
        for(ShapeDrawable wall : walls)
            wall.draw(canvas);
    }

    /**
     * Determines if the drawable dot intersects with any of the walls.
     * @return True if that's true, false otherwise.
     */
    private boolean isCollision() {
        for(ShapeDrawable wall : walls) {
            if(isCollision(wall,drawable))
                return true;
        }
        return false;
    }

    /**
     * Determines if two shapes intersect.
     * @param first The first shape.
     * @param second The second shape.
     * @return True if they intersect, false otherwise.
     */
    private boolean isCollision(ShapeDrawable first, ShapeDrawable second) {
        Rect firstRect = new Rect(first.getBounds());
        return firstRect.intersect(second.getBounds());
    }
}