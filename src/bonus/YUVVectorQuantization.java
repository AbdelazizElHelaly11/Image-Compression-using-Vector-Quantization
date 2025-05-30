package src.bonus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class YUVVectorQuantization {
    // Read images from a directory
    public static List<BufferedImage> readImages(String directory) throws IOException {
        List<BufferedImage> images = new ArrayList<>();
        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Directory not found: " + dir.getAbsolutePath());
            return images;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.err.println("No files found in: " + dir.getAbsolutePath());
            return images;
        }
        for (File file : files) {
            if (file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png"))) {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    images.add(img);
                } else {
                    System.err.println("Failed to read image: " + file.getName());
                }
            }
        }
        System.out.println("Loaded " + images.size() + " images from " + directory);
        return images;
    }

    // Collect 2x2 block vectors for Y (channel=0), U (channel=1), or V (channel=2)
    public static List<double[]> collectTrainingVectors(List<BufferedImage> images, int channel) {
        List<double[]> vectors = new ArrayList<>();
        for (BufferedImage img : images) {
            double[][][] yuv = YUVConverter.rgbToYUV(img);
            double[][] component = channel == 0 ? yuv[0] : (channel == 1 ? yuv[1] : yuv[2]);
            int height = component.length;
            int width = component[0].length;

            // Dimensions should already be even due to padding in YUVConverter
            if (height % 2 != 0 || width % 2 != 0) {
                throw new IllegalStateException("Component dimensions must be even: " + width + "x" + height);
            }

            // Collect 2x2 blocks from component
            for (int y = 0; y < height; y += 2) {
                for (int x = 0; x < width; x += 2) {
                    double[] vector = new double[4];
                    vector[0] = component[y][x];
                    vector[1] = component[y][x + 1];
                    vector[2] = component[y + 1][x];
                    vector[3] = component[y + 1][x + 1];
                    vectors.add(vector);
                }
            }
        }
        return vectors;
    }
}