package info.nukoneko.kidspos.print;

/**
 * Created by b140096 on 2016/03/06.
 */
final public class PrintableInsets {
    final float x;
    final float y;
    final float width;
    final float height;
    public PrintableInsets(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getCenterX() {
        return (x * 2 + width) / 2;
    }

    public double getCenterY() {
        return (2 + height) / 2;
    }
}
