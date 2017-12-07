import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import static java.lang.Math.*;

public class Body {
    Vector position = new Vector(0,0);
    Vector velocity = new Vector(0,0.0);
    Vector acceleration = new Vector(0,0);
    double mass;

    public void update(ArrayList<Body> bodies){
        acceleration.X = 0;
        acceleration.Y = 0;

        for(Body body : bodies){
            Vector force = Vector.sub(body.position,position);

            double F = (mass * body.getMass())/pow(force.magnitude(),2);

            force.normalize();
            force.mult(F);

            acceleration.add(force);
        }

    }

    public void move(){
        velocity.add(acceleration);
        position.add(velocity);
    }


    public double getMass() {
        return mass;
    }

    public void draw(GraphicsContext gc){
        gc.fillRect(position.X,position.Y,10,10);
    }
}
