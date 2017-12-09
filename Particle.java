import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {
    Vector position = new Vector(5,0);
    Vector velocity = new Vector(0,0.0);
    Vector acceleration = new Vector(0,0);

    public Particle(){} //default constructor
    public Particle(double X, double Y){
        velocity = new Vector(X,Y);
    }


    public void drawpath(VectorField field, Color color, int iterations){
        GraphicsContext gc = field.gc;

        gc.setStroke(color);
        gc.setLineWidth(1);

        double lx = position.X;
        double ly = position.Y;

        for(int i = 0; i < iterations; i++) {
                update(field);
                field.strokeLine(gc, lx, ly, position.X, position.Y);
                lx = position.X;
                ly = position.Y;
        }
    }

    public AnimationTimer getanimation(VectorField field, Color color){
        GraphicsContext gc = field.gc;
        gc.clearRect(0,0,field.WIDTH,field.HEIGHT);
        gc.setStroke(color);

        return new AnimationTimer(){
            public void handle(long t){
                drawpath(field,color,field.animationSpeed);
            }
        };
    }


    public void update(VectorField field){  //small steps, doesn't use descent algorithm
        acceleration.X = 0;
        acceleration.Y = 0;

        Vector funcforce = new Vector();

        funcforce.X = -field.partialx(field::function,position.X,position.Y);
        funcforce.Y = -field.partialy(field::function,position.X,position.Y);

        acceleration.add(funcforce);
        velocity.add(acceleration);
        position.add(velocity);
    }

    public void gradientdescent(VectorField field){
        Vector funcforce = new Vector();
        funcforce.X = -field.partialx(field::function,position.X,position.Y);
        funcforce.Y = -field.partialy(field::function,position.X,position.Y);

        position.add(funcforce);
    }
}
