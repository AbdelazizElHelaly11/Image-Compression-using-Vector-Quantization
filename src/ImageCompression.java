package src;

import java.awt.image.BufferedImage;

public class ImageCompression {
    public static int[][] compressChannel(BufferedImage image, int channel, double[][] codebook) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Ensure dimensions are even
        if (width % 2 != 0) {
            width--;
        }
        if (height % 2 != 0) {
            height--;
        }

        int[][] indices = new int[height / 2][width / 2];
        
        // Process the image in 2x2 blocks
        for (int y = 0; y < height; y += 2) {
            for (int x = 0; x < width; x += 2) {
                double[] vector = extractVector(image, x, y, channel);
                int bestIndex = findNearestCluster(vector, codebook);
                indices[y / 2][x / 2] = bestIndex;
            }
        }
        return indices;
    }

    private static double[] extractVector(BufferedImage image, int x, int y, int channel) {
        double[] vector = new double[4];
        vector[0] = VectorQuantization.getChannel(image, x, y, channel);
        vector[1] = VectorQuantization.getChannel(image, x + 1, y, channel);
        vector[2] = VectorQuantization.getChannel(image, x, y + 1, channel);
        vector[3] = VectorQuantization.getChannel(image, x + 1, y + 1, channel);
        return vector;
    }

    private static int findNearestCluster(double[] vector, double[][] codebook) {
        int bestIndex = 0;
        double minDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < codebook.length; i++) {
            double distance = calculateDistance(vector, codebook[i]);
            if (distance < minDistance) {
                minDistance = distance;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private static double calculateDistance(double[] v1, double[] v2) {
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            sum += Math.pow(v1[i] - v2[i], 2);
        }
        return Math.sqrt(sum);
    }
}
