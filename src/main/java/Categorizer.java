import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Categorizer {
    /**
     * A map containing all possible categories mapped to their sample colors.
     */
    private static Map<String, List<Color>> categoryColors = new HashMap<>();

    /**
     * Trains the algorithm based on images.
     *
     * @param file     the image to use for training
     * @param category the category of items that are on the image
     * @param colorKey the color key of the background
     */
    public static void train(File file, String category, Color colorKey) {
        try {
            BufferedImage image = ImageIO.read(file);

            int sampleCount = 0;
            List<Color> colors = new ArrayList<>();
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    // Only add the first 1000 colors to keep the sample count small
                    if (sampleCount > 1000) {
                        break;
                    }

                    // If it's not the color key
                    int curColor = image.getRGB(i, j);
                    if (curColor != colorKey.getInt()) {
                        colors.add(new Color(curColor));
                        sampleCount++;
                    }
                }
            }

            categoryColors.put(category, colors);

            System.out.println("Added " + colors.size() + " to <" + category + "> using image: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Manually trains the algorithm
     */
    public static void manualTrain() {
        List<Color> strawColors = new ArrayList<>();
        strawColors.add(new Color(113, 107, 91));
        strawColors.add(new Color(161, 161, 133));
        strawColors.add(new Color(100, 97, 80));
        categoryColors.put("straw", strawColors);

        List<Color> redbeanColors = new ArrayList<>();
        redbeanColors.add(new Color(50, 26, 27));
        redbeanColors.add(new Color(74, 50, 64));
        redbeanColors.add(new Color(70, 30, 38));
        categoryColors.put("red_beans", redbeanColors);

        List<Color> pumpkinColors = new ArrayList<>();
        pumpkinColors.add(new Color(70, 70, 70));
        pumpkinColors.add(new Color(42, 44, 43));
        pumpkinColors.add(new Color(42, 46, 47));
        categoryColors.put("pumpkin", pumpkinColors);

        List<Color> stoneColors = new ArrayList<>();
        stoneColors.add(new Color(75, 96, 123));
        stoneColors.add(new Color(80, 90, 100));
        stoneColors.add(new Color(72, 85, 102));
        categoryColors.put("stone", stoneColors);

        List<Color> cleanColors = new ArrayList<>();
        cleanColors.add(new Color(63, 95, 60));
        cleanColors.add(new Color(82, 76, 76));
        cleanColors.add(new Color(87, 78, 73));
        categoryColors.put("clean", cleanColors);
    }

    /**
     * Finds the sample with the closest color to the given color.
     * Returns the category of the closest sample.
     *
     * @param color the color to check
     * @return the closest category
     */
    static String closestAverageDistance(Color color) {
        String smallestDistCategory = null;
        int smallestDistance = Integer.MAX_VALUE;
        // Calculate the distance to each sample
        for (Map.Entry<String, List<Color>> category : categoryColors.entrySet()) {
            int totalDistance = 0;
            List<Color> sampleColors = category.getValue();
            for (int i = 0; i < sampleColors.size(); i++) {
                totalDistance += Math.abs(color.getR() - sampleColors.get(i).getR())
                        + Math.abs(color.getG() - sampleColors.get(i).getG())
                        + Math.abs(color.getB() - sampleColors.get(i).getB());
            }

            // Find the closest sample and it's category
            int avgDistance = totalDistance / sampleColors.size();
            if (avgDistance < smallestDistance) {
                smallestDistance = avgDistance;
                smallestDistCategory = category.getKey();
            }
        }

        return smallestDistCategory;
    }

    /**
     * Returns the category with the closest average color to the given color.
     * This is faster but less precise.
     *
     * @param color the color to check
     * @return the closest category
     */
    static String closestDistanceToAverage(Color color) {
        // Calculate average colors
        Map<String, Color> avgColors = new HashMap<>();
        for (Map.Entry<String, List<Color>> category : categoryColors.entrySet()) {
            long redSum = 0;
            long greenSum = 0;
            long blueSum = 0;
            for (Color categoryColor : category.getValue()) {
                redSum += categoryColor.getR();
                greenSum += categoryColor.getG();
                blueSum += categoryColor.getB();
            }

            int avgRed = (int) (redSum / category.getValue().size());
            int avgGreen = (int) (greenSum / category.getValue().size());
            int avgBlue = (int) (blueSum / category.getValue().size());

            avgColors.put(category.getKey(), new Color(avgRed, avgGreen, avgBlue));
        }

        String smallestDistCategory = null;
        int smallestDistance = Integer.MAX_VALUE;
        // Calculate the distance to the average color of each category
        for (Map.Entry<String, Color> avgColor : avgColors.entrySet()) {
            int distance = Math.abs(color.getR() - avgColor.getValue().getR())
                    + Math.abs(color.getG() - avgColor.getValue().getG())
                    + Math.abs(color.getB() - avgColor.getValue().getB());

            // Find the smallest distance
            if (distance < smallestDistance) {
                smallestDistance = distance;
                smallestDistCategory = avgColor.getKey();
            }
        }

        return smallestDistCategory;
    }
}
