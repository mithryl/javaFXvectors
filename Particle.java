import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {
    Vector position = new Vector(5,0);
    Vector velocity = new Vector(0,0.0);
    Vector acceleration = new Vector(0,0);

    public void drawpath(VectorField field, Color color){
        GraphicsContext gc = field.gc;

        double lx = position.X;
        double ly = position.Y;

        gc.clearRect(0,0,2000,2000);

        for(int i = 0; i < 1000; i++) {

            update(field);
            gc.setStroke(color);
            gc.setLineWidth(4);

            field.strokeLine(gc,lx,ly,position.X,position.Y);

            lx = position.X;
            ly = position.Y;
        }
    }

    public AnimationTimer getanimation(VectorField field, Color color){
        GraphicsContext gc = field.gc;
        gc.clearRect(0,0,field.WIDTH,field.HEIGHT);

        return new AnimationTimer(){
            double lx = position.X;
            double ly = position.Y;

            public void handle(long t){
                update(field);
                gc.setStroke(color);
                gc.setLineWidth(4);

                field.strokeLine(gc,lx,ly,position.X,position.Y);

                lx = position.X;
                ly = position.Y;
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
        acceleration.normalize();
        acceleration.mult(0.01);

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
