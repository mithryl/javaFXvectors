import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;

import static java.lang.Math.*;

public class VectorField extends Application {

    final int WIDTH = 2000, HEIGHT = 2000;  //width and height of javaFX stage
    int offset = WIDTH/2;   //Set origin to center of screen

    double startx = -10,endx = 10,starty = -10,endy = 10;   //x range and y range
    double scale = WIDTH / (endx - startx); //fit width to FX width

    //These may need to be changed to adjust for low resolution screens
    double vectorstep = 0.5;    //size and distance of vectors
    double resolution = 0.03;   //resolution of function
    double pointres = 3;        //size of each pixel drawn by fx, spaced by resultion

    Color pathColor = Color.WHITE;

    Group root = new Group();
    Scene scene = new Scene(root,WIDTH,HEIGHT);

    //two canvas objs are used to remove redunant redraw operations
    Canvas canvas = new Canvas(WIDTH,HEIGHT);
    Canvas funcanvas = new Canvas(WIDTH,HEIGHT);
    GraphicsContext gc = canvas.getGraphicsContext2D();     //gc used for drawing descent and stuff - is cleared
    GraphicsContext fc = funcanvas.getGraphicsContext2D();  //fc used for drawing function and vector field

    AnimationTimer currentAnimtion;

    @Override
    public void start(Stage stage) throws Exception {
        root.getChildren().addAll(funcanvas,canvas);

        gc.setLineWidth(2);

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()){
                case ESCAPE:
                    stage.close();
                    break;
                case SPACE:
                    if(currentAnimtion != null){//kill animation timer
                        currentAnimtion.stop();
                    }
                    break;
            }
        });

        scene.setOnMouseClicked(e ->{
            double x = (e.getX() - offset) / scale;
            double y = -(e.getY() - offset) / scale;

            Particle ball = new Particle();
            ball.position = new Vector(x,y);

            //start animation timer
            if(currentAnimtion == null){
                currentAnimtion = ball.getanimation(this,pathColor);
            }else{
                currentAnimtion.stop();
                currentAnimtion = ball.getanimation(this,pathColor);
            }

            currentAnimtion.start();
        });

        stage.setTitle("Vector Thing...");
        stage.setScene(scene);
        stage.show();

        drawfunction(this::function);
        drawvectorfield(this::function);
    }

    public double function(double x, double y){
        return tanh((cos(x) + cos(y)));
    }

    public void drawvectorfield(BiFunction<Double,Double,Double> func){

        for(double x = startx; x < endx; x+= vectorstep){
            for(double y = starty; y < endy; y+=vectorstep){

                double NX = partialx(func,x,y);
                double NY = partialy(func,x,y);

                Vector vect = new Vector(x,y,x + NX,y + NY);    //create gradient
                vect.normalize();
                vect.mult(-vectorstep * 0.5); //reverse direction and scale 0.9 so vectors don't overlap

                strokeArrow(fc,x,y,vect);
            }
        }
    }

    public void drawfunction(BiFunction<Double,Double,Double> func){
        double maxval = 0;

        ArrayList<double[]> values = new ArrayList<>();

        for(double x = startx; x < endx; x+=resolution){
            for(double y = starty; y < endy; y+=resolution){

                double z = func.apply(x,y);

                values.add(new double[]{x,y,z});//store values in arraylist

                if(z > maxval) maxval = z;//determine maximum value
            }
        }

        for(double[] vals : values){
            vals[2] /= maxval;  //normalize values
            fc.setFill(Color.hsb(vals[2]*100,1.0,1.0));//apply color scheme
            drawPoint(vals[0],vals[1]);
        }
    }


    //These methods apply the scale and offset. Adjusted manually to prevent stretching/shrinking issues
    //All y values are multiplied by -1 due to javaFX coordinate system

    public void strokeLine(GraphicsContext tc,double x1, double y1, double x2, double y2){
        tc.strokeLine((x1 * scale) + offset,-(y1 * scale) + offset,(x2 * scale) + offset,-(y2 * scale) + offset);
    }

    public void strokeArrow(GraphicsContext fc,double x1, double y1, Vector vect){
        double tx = vect.getX(x1);
        double ty = vect.getY(y1);

        strokeLine(fc,x1,y1,tx,ty);

        Vector arrow = new Vector(-vect.Y,vect.X);
        arrow.mult(0.1);

        vect.mult(0.75);

        double nx = vect.getX(x1);
        double ny = vect.getY(y1);

        strokeLine(fc,tx,ty,arrow.getX(nx),arrow.getY(ny));

        arrow.mult(-1);
        strokeLine(fc,tx,ty,arrow.getX(nx),arrow.getY(ny));
    }

    public void drawPoint(double X, double Y){
        fc.fillRect(X*scale + offset,-Y*scale+offset,pointres,pointres);
    }
    

    //Math stuff
    double h = 0.0000000001;//magic derivative value \o/

    public double partialx(BiFunction<Double,Double,Double> function, double X, double Y){
        return derivative(f -> function.apply(f,Y),X);//Y is fixed
    }

    public double partialy(BiFunction<Double,Double,Double> function,double X, double Y){
        return derivative(f -> function.apply(X,f),Y);//X is fixed
    }

    public double derivative(DoubleFunction<Double> function, double X){
        return (function.apply(X + h) - function.apply(X)) / h;
    }


    public static void main(String args[]){
        launch(args);
    }
}
