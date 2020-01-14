package BoardCompFeatures;

import java.awt.*;

/**
 * Created by Lev on 06.04.14.
 */
public class ClosePoint extends Point {

    public ClosePoint(Point point) {
        super(point);
    }

    public ClosePoint(int x, int y) {
        super(x, y);
    }

    final public static int ERROR = 10;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point point = (Point) o;

        return (Math.abs(x - point.x) < ERROR && Math.abs(y - point.y) < ERROR);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = java.lang.Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = java.lang.Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
