package src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VectorQuantization {

    // Read images from a directory
    public static List<BufferedImage> readImages(String directory) throws IOException {
        List<BufferedImage> images = new ArrayList<>();
        File dir = new File(directory);

        // Check if the directory exists and is valid
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Directory not found or is not a valid directory: " + dir.getAbsolutePath());
            return images; // Return empty list if directory is invalid
        }

        // Get the list of files and check for null
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.err.println("No files found in directory: " + dir.getAbsolutePath());
            return images; // Return empty list if no files found
        }

        // Iterate over files and add images
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

    // Extract 2x2 blocks for a specific channel (0=R, 1=G, 2=B) // training
    public static List<double[]> collectTrainingVectors(List<BufferedImage> images, int channel) {
        List<double[]> vectors = new ArrayList<>();
        for (BufferedImage img : images) {
            int width = img.getWidth();
            int height = img.getHeight();
            // Pad dimensions if odd
            int paddedWidth = width + (width % 2);
            int paddedHeight = height + (height % 2);
            BufferedImage paddedImg = new BufferedImage(paddedWidth, paddedHeight, BufferedImage.TYPE_INT_RGB);
            paddedImg.getGraphics().drawImage(img, 0, 0, null);

            for (int y = 0; y < paddedHeight; y += 2) { // no overlapping
                for (int x = 0; x < paddedWidth; x += 2) {
                    double[] vector = new double[4];
                    vector[0] = getChannel(paddedImg, x, y, channel);
                    vector[1] = getChannel(paddedImg, x + 1, y, channel);
                    vector[2] = getChannel(paddedImg, x, y + 1, channel);
                    vector[3] = getChannel(paddedImg, x + 1, y + 1, channel);
                    vectors.add(vector);
                }
            }
        }
        return vectors;
    }

    // Get pixel value for a specific channel
    public static int getChannel(BufferedImage img, int x, int y, int channel) {
        if (x >= img.getWidth() || y >= img.getHeight()) {
            return 0; // Return 0 for padded areas
        }
        int rgb = img.getRGB(x, y);
        switch (channel) {
            case 0: return (rgb >> 16) & 0xFF; // Red
            case 1: return (rgb >> 8) & 0xFF;  // Green
            case 2: return rgb & 0xFF;         // Blue
            default: throw new IllegalArgumentException("Invalid channel");
        }
    }
}