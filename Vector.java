
import static java.lang.Math.*;

public class Vector {
    double X,Y;

    public Vector(double X, double Y){
        this.X = X;
        this.Y = Y;
    }
    public Vector(){}
    public Vector(double x1, double y1, double x2, double y2){
        X = x2 - x1;
        Y = y2 - y1;
    }

    public void add(Vector vect){
        X += vect.X;
        Y += vect.Y;
    }

    public void sub(Vector vect){
        X -= vect.X;
        Y -= vect.Y;
    }

    public double magnitude(){
        return sqrt(X*X + Y*Y);
    }

    public void normalize(){
        double mag = magnitude();

        X /= mag;
        Y /= mag;
    }

    public void mult(double s){
        X *= s;
        Y *= s;
    }

    public double getX(double x){
        return x + X;
    }
    public double getY(double y){
        return y + Y;
    }


    public static Vector add(Vector vec1, Vector vec2){
        return new Vector(vec1.X + vec2.X,vec1.Y+vec2.Y);
    }
    public static Vector sub(Vector vec2, Vector vec1) {//distance from vec1 to vec2
        return new Vector(vec2.X - vec1.X, vec2.Y - vec1.Y);
    }


}
