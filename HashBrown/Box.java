/**
 * Created by bakafish on 2/13/17.
 */
public class Box {

    public int x, y;

    public Box(int xVal, int yVal) {
        x = xVal;
        y = yVal;
    }

    public String toString() {
        return String.format("[%1$d | %2$d]", x, y);
    }

}
