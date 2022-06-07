package com.example.example6;

import static java.lang.System.exit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.analysis.function.Gaussian;

/**
 * Smart Phone Sensing Example 6. Object movement and interaction on canvas.
 */
public class MainActivity extends Activity implements OnClickListener, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepSensor, directionSensor;

    private ShapeDrawable drawable;
    private Canvas canvas;
    private List<ShapeDrawable> walls;

    private List<Particle> particles = new ArrayList<>();
    private List<Rectangle> building = new ArrayList<>();

    private final int NUM_PART = 100;
    private final double H = 2;

    private float ROTATION_OFFSET = 0;      // the buildings standard rotational offset
    private int TOTALSTEPS = 0;
    private final float STEP_SIZE = 0.8f;
    private final int PPM = 38;         // Pixels per meter
    private boolean INITROUND = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize sensormanager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        // get the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        System.out.println("Size of screen known as: " + width + ", and height " + height);

        // create a drawable object
        drawBuilding(width,height);

        // create a canvas
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
    }

    @Override
    public void onClick(View view) {
        // Do nothing.
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing.
    }

    // onResume() registers the sensors for listening the events
    protected void onResume() {
        super.onResume();

        // Set the sensors
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        directionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, directionSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // onPause() unregisters the sensors for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, stepSensor);
        sensorManager.unregisterListener(this, directionSensor);
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
//                        System.out.println("particle added: X " + p.getX() + " and Y " + p.getY());
//                        System.out.println("found in room " + i + ", coordinates " + room.getTopleftX() + ", " + room.getTopleftY());
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
//            System.out.println("Current X and Y: " + p.getX() + ", " + p.getY());
            p.updateDistance(distance, direction);
//            System.out.println("New X and Y: " + p.getX() + ", " + p.getY());
        }
        List<Particle> toremove = new ArrayList<>();
        for (int i = 0; i < particles.size(); i++) {
            if (!validParticle(particles.get(i))) {
                particles.remove(i);
            }
        }

        System.out.println("Amount of particles still left is: " + particles.size());
        if (particles.size() == 0) {
            System.out.println("Empty particle set");
        }
        //TODO: add replacement for samples
        double[] cdf = cdfFromWeights();
        System.out.println("Cdf function now: " + Arrays.toString(cdf));
        while (particles.size() < NUM_PART) {
            double rand = Math.random();
            int kernel = 0;
            for (int i = 0; i < cdf.length; i++) {
                if (cdf[i] > rand) {
                    kernel = i;
                    break;
                }
            }
//            System.out.println("Found fitting kernel value: " + kernel);

            //apply Gaussian to set point nearby, using h as std dev and kernel X and Y as mean
            //idea used from: https://stats.stackexchange.com/questions/43674/simple-sampling-method-for-a-kernel-density-estimator
            double minX = Math.random();
            double minY = Math.random();

//            System.out.println("sample to check from: " + particles.get(kernel).getX() + ", "+ particles.get(kernel).getY());
            double newX = particles.get(kernel).getX() + Math.random() * H;
            double newY = particles.get(kernel).getY() + Math.random() * H;
            if (minX < 0.5) {
                newX = particles.get(kernel).getX() - Math.random() * H;
            }
            if (minY < 0.5) {
                newY = particles.get(kernel).getY() - Math.random() * H;
            }

//            System.out.println("new particle values X " + newX + ", and Y " + newY);

            Particle newP = new Particle(newX, newY, 1, Math.random()*360);
            if (validParticle(newP)) {
                particles.add(newP);
//                System.out.println("new Particle added");
            }
        }

        System.out.println("new particle size is now: " + particles.size());
    }

    public double[] cdfFromWeights() {
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
    public void onSensorChanged(SensorEvent event) {
        float distance = 0;
        float direction = 0;
        int currSteps = 0;

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ROTATION_VECTOR:
                float RotationM[] = new float[9];
                float OrientationM[] = new float[3];        // Gives x, y, and z rotation
                SensorManager.getRotationMatrixFromVector(RotationM, event.values);
                SensorManager.getOrientation(RotationM, OrientationM);

                System.out.println("angles = " + Math.toDegrees(OrientationM[0]) + "," + Math.toDegrees(OrientationM[1]) + "," + Math.toDegrees(OrientationM[2]));
                direction = (float)Math.toDegrees(OrientationM[0]) - ROTATION_OFFSET;
                break;
            case Sensor.TYPE_STEP_COUNTER:
                currSteps = ((int) event.values[0]) - TOTALSTEPS;
                TOTALSTEPS = (int) event.values[0];
                if (INITROUND) {
                    distance = 0;
                    INITROUND = false;
                } else {
                    distance = currSteps * STEP_SIZE * PPM;

                    System.out.println(currSteps + "steps");
                }
                break;
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
        for(ShapeDrawable wall : walls) {
            wall.draw(canvas);
        }

        drawable.draw(canvas);
        for (Particle p : particles) {
            ShapeDrawable shape = new ShapeDrawable(new OvalShape());
            shape.setBounds((int)p.getX()-5, (int) p.getY()-5, (int) p.getX()+5, (int) p.getY()+5);
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