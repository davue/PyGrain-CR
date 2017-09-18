import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        Categorizer.manualTrain();

        // Uncomment this to train the algorithm with the images in the samples folder
        /*Color colorKey = new Color(0xffff00ff);
        Categorizer.train(new File("samples/clean_dark.png"), "clean", colorKey);
        Categorizer.train(new File("samples/pumpkin_dark.png"), "pumpkin", colorKey);
        Categorizer.train(new File("samples/redbean_dark.png"), "redbean", colorKey);*/

        // Production mode if arguments are supplied
        if (args.length > 1) {
            categorizeImage(args[0], Integer.valueOf(args[1]));
        } else { // Otherwise start debugging mode
            System.out.println("Running in debug mode.");
            Scanner sc = new Scanner(System.in);

            while (true) {
                //showCategory(sc.next(), sc.nextInt(), sc.next());
                showAverage(sc.next(), sc.nextInt());
            }
        }
    }

    /**
     * Slices the image into equally small squares and categorizes them using the Categorizer.
     * The results are printed into stdout in json format.
     *
     * @param path       the path to the image to process
     * @param squareSize the slicing size
     */
    static void categorizeImage(String path, int squareSize) {
        try {
            File imageFile = new File(path);
            BufferedImage image = ImageIO.read(imageFile);

            JsonObject response = new JsonObject();

            JsonObject slicing = new JsonObject();
            slicing.addProperty("size", squareSize);
            slicing.addProperty("image_width", (image.getWidth() / squareSize) * squareSize);
            slicing.addProperty("image_height", (image.getHeight() / squareSize) * squareSize);
            response.add("slicing", slicing);

            JsonArray slices = new JsonArray();
            for (int i = 0; i < image.getWidth() - squareSize; i += squareSize) {
                for (int j = 0; j < image.getHeight() - squareSize; j += squareSize) {
                    long redSum = 0;
                    long greenSum = 0;
                    long blueSum = 0;

                    for (int k = i; k < i + squareSize; k++) {
                        for (int l = j; l < j + squareSize; l++) {
                            redSum += (image.getRGB(k, l) & 0x00ff0000) >> 16;
                            greenSum += (image.getRGB(k, l) & 0x0000ff00) >> 8;
                            blueSum += image.getRGB(k, l) & 0x000000ff;
                        }
                    }

                    int avgRed = (int) (redSum / (squareSize * squareSize));
                    int avgGreen = (int) (greenSum / (squareSize * squareSize));
                    int avgBlue = (int) (blueSum / (squareSize * squareSize));

                    Color avgColor = new Color(avgRed, avgGreen, avgBlue);

                    JsonObject slice = new JsonObject();
                    slice.addProperty("x", i);
                    slice.addProperty("y", j);
                    slice.addProperty("category", Categorizer.closestAverageDistance(avgColor));
                    slices.add(slice);
                }
            }

            response.add("slices", slices);
            System.out.println(gson.toJson(response));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Slices the image into equally small squares and "rates" them using the Categorizer.
     * All squares which are not recognized as the given category are colored pink.
     *
     * @param path       the path to the image to process
     * @param squareSize the slicing size
     * @param category   the category to show
     */
    static void showCategory(String path, int squareSize, String category) {
        try {
            File imageFile = new File(path);
            BufferedImage image = ImageIO.read(imageFile);

            for (int i = 0; i < image.getWidth() - squareSize; i += squareSize) {
                for (int j = 0; j < image.getHeight() - squareSize; j += squareSize) {
                    long redSum = 0;
                    long greenSum = 0;
                    long blueSum = 0;

                    for (int k = i; k < i + squareSize; k++) {
                        for (int l = j; l < j + squareSize; l++) {
                            redSum += (image.getRGB(k, l) & 0x00ff0000) >> 16;
                            greenSum += (image.getRGB(k, l) & 0x0000ff00) >> 8;
                            blueSum += image.getRGB(k, l) & 0x000000ff;
                        }
                    }

                    int avgRed = (int) (redSum / (squareSize * squareSize));
                    int avgGreen = (int) (greenSum / (squareSize * squareSize));
                    int avgBlue = (int) (blueSum / (squareSize * squareSize));

                    Color avgColor = new Color(avgRed, avgGreen, avgBlue);

                    // Only show squares which where recognized as the given category
                    if (!Categorizer.closestAverageDistance(avgColor).equals(category)) {
                        for (int k = i; k < i + squareSize; k++) {
                            for (int l = j; l < j + squareSize; l++) {
                                image.setRGB(k, l, 0xffff00ff);
                            }
                        }
                    }
                }
            }

            File outFile = new File(imageFile.getName().split("\\.")[0] + "_" + category + squareSize + ".jpg");
            ImageIO.write(image.getSubimage(0, 0, (image.getWidth() / squareSize) * squareSize, (image.getHeight() / squareSize) * squareSize), "jpeg", outFile);
            System.out.println("Written " + outFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Slices the image into equally small squares and fills each square with it's average color.
     *
     * @param path       the path to the image to process
     * @param squareSize the slicing size
     */
    static void showAverage(String path, int squareSize) {
        try {
            File imageFile = new File(path);
            BufferedImage image = ImageIO.read(imageFile);

            for (int i = 0; i < image.getWidth() - squareSize; i += squareSize) {
                for (int j = 0; j < image.getHeight() - squareSize; j += squareSize) {
                    long redSum = 0;
                    long greenSum = 0;
                    long blueSum = 0;

                    for (int k = i; k < i + squareSize; k++) {
                        for (int l = j; l < j + squareSize; l++) {
                            redSum += (image.getRGB(k, l) & 0x00ff0000) >> 16;
                            greenSum += (image.getRGB(k, l) & 0x0000ff00) >> 8;
                            blueSum += image.getRGB(k, l) & 0x000000ff;
                        }
                    }

                    int avgRed = (int) (redSum / (squareSize * squareSize));
                    int avgGreen = (int) (greenSum / (squareSize * squareSize));
                    int avgBlue = (int) (blueSum / (squareSize * squareSize));

                    Color avgColor = new Color(avgRed, avgGreen, avgBlue);

                    // Fill each square with it's average color
                    for (int k = i; k < i + squareSize; k++) {
                        for (int l = j; l < j + squareSize; l++) {
                            image.setRGB(k, l, avgColor.getInt());
                        }
                    }
                }
            }

            File outFile = new File(imageFile.getName().split("\\.")[0] + "_avg" + squareSize + ".jpg");
            ImageIO.write(image.getSubimage(0, 0, (image.getWidth() / squareSize) * squareSize, (image.getHeight() / squareSize) * squareSize), "jpeg", outFile);
            System.out.println("Written " + outFile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
