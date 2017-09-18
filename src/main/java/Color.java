/**
 * Represents a 24-bit color in ARGB format
 */
public class Color {
    private final int color;
    private final int r;
    private final int g;
    private final int b;

    /**
     * Creates a new color from RGB
     *
     * @param r the red part
     * @param g the green part
     * @param b the blue part
     */
    public Color(int r, int g, int b) {
        this.color = 0xff000000 | ((r << 16) + (g << 8) + (b));

        this.r = r;
        this.b = b;
        this.g = g;
    }

    /**
     * Creates a new color from a 24-bit integer ignoring alpha channel.
     *
     * @param color the color
     */
    public Color(int color) {
        this.r = (color & 0x00ff0000) >> 16;
        this.g = (color & 0x0000ff00) >> 8;
        this.b = (color & 0x000000ff);

        this.color = color;
    }

    /**
     * @return the color in 24-bit integer format
     */
    public int getInt() {
        return color;
    }

    /**
     * @return the red part of the color as 8-bit integer
     */
    public int getR() {
        return r;
    }

    /**
     * @return the green part of the color as 8-bit integer
     */
    public int getG() {
        return g;
    }

    /**
     * @return the blue part of the color as 8-bit integer
     */
    public int getB() {
        return b;
    }
}
