package com.example.example6;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
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

    private List<Particle> particles = new ArrayList<>();

    private List<Rectangle> building = new ArrayList<>();

    private final int NUM_PART = 100;

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
        System.out.println("Size of screen known as: " + width + ", and height " + height);

        // create a drawable object
        drawBuilding(width,height);
//
//
//
//        // create a canvas
        ImageView canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        canvasView.setImageBitmap(blankBitmap);

        generateParticles();

       for (Particle p : particles) {
            ShapeDrawable shape = new ShapeDrawable(new OvalShape());
            shape.setBounds((int)p.getX()-10, (int) p.getY()-10, (int) p.getX()+10, (int) p.getY()+10);
            shape.getPaint().setColor(Color.RED);
            shape.draw(canvas);
        }
        // draw the objects
        drawable.draw(canvas);
        for(ShapeDrawable wall : walls) {
            System.out.println("Drawing a wall");
            wall.draw(canvas);
        }
//
//


    }

    //method to hardcode all rooms within frame
    // pick 38 pixels per meter, based on 26,5 meters for the building, plus 2 meters for avoiding the screen border
    // 1080/(26,5+2)= 38 pixels/m
    // top border capped on 300 distance
    private void defineBuilding() {
        Rectangle room1 = new Rectangle(38,300,  138,120);
        building.add(room1);
        Rectangle room2 = new Rectangle(38,420,  138,120);
        building.add(room2);
        Rectangle room3 = new Rectangle(129, 540, 47,150);
        building.add(room3);
        Rectangle room4 = new Rectangle(178, 540-162, 182,84);
        building.add(room4);
        Rectangle room5 = new Rectangle(360, 540-162, 182,84);
        building.add(room5);
        Rectangle room6 = new Rectangle(542, 540-162, 182,84);
        building.add(room6);
        Rectangle room7 = new Rectangle(724, 540-162, 182,84);
        building.add(room7);
        Rectangle room8 = new Rectangle(906,300,  138,120);
        building.add(room8);
        Rectangle room9 = new Rectangle(906,420,  138,120);
        building.add(room9);
        Rectangle room10 = new Rectangle(906, 540, 47,150);
        building.add(room10);
        Rectangle room11 = new Rectangle(906-174, 690, 221,47);
        building.add(room11);
        Rectangle room12 = new Rectangle(906-164, 540-78, 164,78);
        building.add(room12);
        Rectangle room13 = new Rectangle(580, 540-162+84, 87,164); //TODO: find exact X value
        building.add(room13);

        System.out.println("Defined building " + building.size());
    }

    private void drawBuilding(int width, int height) {
        walls = new ArrayList<>();
        drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(Color.BLUE);
        drawable.setBounds(width/2-20, height/2-20, width/2+20, height/2+20);

        walls = new ArrayList<>();

        defineBuilding();

        for (int i = 0; i < building.size(); i++) {
            if (building.get(i) != null) {
                ShapeDrawable[] todraw = building.get(i).drawRectangle();
                for (int j = 0; j < todraw.length; j++) {
                    walls.add(todraw[j]);
                }
            }

        }

        System.out.println("Number of rooms is now:" + building.size());
        System.out.println("Size of walls is now: " + walls.size());
    }

    //do some validity testing for a particle
    private boolean validParticle(Particle p) {
        if (building != null) {
            for (int i = 0; i < building.size(); i++) {
                Rectangle room = building.get(i);
                if (p.getX() > room.getTopleftX() && p.getX() < room.getTopleftX() + room.getWidth()) {
                    if (p.getY() > room.getTopleftY() && p.getY() < room.getTopleftY() + room.getLength()) {
                        System.out.println("particle added: X " + p.getX() + " and Y " + p.getY());
                        System.out.println("found in room " + i + ", coordinates " + room.getTopleftX() + ", " + room.getTopleftY());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void generateParticles() {
        System.out.println("Generating particles");
        while(particles.size() < NUM_PART) {
            Particle part = new Particle(Math.random()*1080, Math.random()*2000, 1, Math.random()* 360);
            if (validParticle(part)) {
                particles.add(part);
            }
        }
    }

    private void updateParticles(double distance, double direction) {
        for (Particle p: particles) {
            System.out.println("Current X and Y: " + p.getX() + ", " + p.getY());
            p.updateDistance(distance, direction);
            System.out.println("New X and Y: " + p.getX() + ", " + p.getY());
        }
        List<Particle> toremove = new ArrayList<>();
        for (int i = 0; i < particles.size(); i++) {
            if (!validParticle(particles.get(i))) {
                toremove.add(particles.get(i));
            }
        }
        for (Particle p:
             toremove) {
            particles.remove(p);
        }

        //TODO: add replacement for samples
        double[] cdf = cdfFromWeights();
        while (particles.size() < NUM_PART) {
            double rand = Math.random();
            int kernel = 0;
            for (int i = 0; i < cdf.length; i++) {
                if (cdf[i] > rand) {
                    kernel = i;
                    break;
                }
            }

            //apply Gaussian to set point nearby, using h as std dev and kernel X and Y as mean
            //idea used from: https://stats.stackexchange.com/questions/43674/simple-sampling-method-for-a-kernel-density-estimator
            

        }


        System.out.println("new particle size is now: " + particles.size());
    }

    public double[] cdfFromWeights(double ) {
        float totalDist = 0;
        for (Particle p : particles) {
            totalDist += p.getDistance();
        }
        double[] x_coord = new double[particles.size()];
        double[] y_coord = new double[particles.size()];
        double[] weights = new double[particles.size()];
        for (int i = 0; i < particles.size(); i++) {
            x_coord[i] = particles.get(i).getX();
            y_coord[i] = particles.get(i).getY();
            weights[i] = particles.get(i).getDistance()/totalDist;
        }
        double[] cdf = new double[particles.size()];
        cdf[0] = weights[0];
        for (int i = 1; i < weights.length; i++) {
            cdf[i] = cdf[i-1] + weights[i];
        }
        return cdf;
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
        double direction = 0;
        double distance = 0;
        switch (v.getId()) {
            // UP BUTTON
            case R.id.button1: {
                Toast.makeText(getApplication(), "UP", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left,r.top-20,r.right,r.bottom-20);
                textView.setText("\n\tMove Up" + "\n\tTop Margin = "
                        + drawable.getBounds().top);
                direction = 0;
                distance = 38;
                break;
            }
            // DOWN BUTTON
            case R.id.button4: {
                Toast.makeText(getApplication(), "DOWN", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left,r.top+20,r.right,r.bottom+20);
                textView.setText("\n\tMove Down" + "\n\tTop Margin = "
                        + drawable.getBounds().top);
                direction = 180;
                distance = 38;
                break;
            }
            // LEFT BUTTON
            case R.id.button2: {
                Toast.makeText(getApplication(), "LEFT", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left-20,r.top,r.right-20,r.bottom);
                textView.setText("\n\tMove Left" + "\n\tLeft Margin = "
                        + drawable.getBounds().left);
                direction = 270;
                distance = 38;
                break;
            }
            // RIGHT BUTTON
            case R.id.button3: {
                Toast.makeText(getApplication(), "RIGHT", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left+20,r.top,r.right+20,r.bottom);
                textView.setText("\n\tMove Right" + "\n\tLeft Margin = "
                        + drawable.getBounds().left);
                direction = 90;
                distance = 38;
                break;
            }
        }

        updateParticles(distance, direction);


        // if there is a collision between the dot and any of the walls
        if(isCollision()) {
            // reset dot to center of canvas
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            drawable.getPaint().setColor(Color.BLUE);
            drawable.setBounds(width/2-20, height/2-20, width/2+20, height/2+20);


        }

        // redrawing of the object
        canvas.drawColor(Color.WHITE);
        drawable.draw(canvas);
        for(ShapeDrawable wall : walls)
            wall.draw(canvas);


        drawable.draw(canvas);
        for (Particle p : particles) {
            ShapeDrawable shape = new ShapeDrawable(new OvalShape());
            shape.setBounds((int)p.getX()-10, (int) p.getY()-10, (int) p.getX()+10, (int) p.getY()+10);
            shape.getPaint().setColor(Color.RED);
            if (p.getY() < 300) {
                shape.getPaint().setColor(Color.GREEN);
            }
            shape.draw(canvas);
        }

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