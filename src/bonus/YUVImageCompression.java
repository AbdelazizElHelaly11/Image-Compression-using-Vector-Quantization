package src.bonus;

public class YUVImageCompression {
    public static int[][] compressChannel(double[][] component, double[][] codebook, int blockSize) {
        int height = component.length;
        int width = component[0].length;
        int compressedHeight = height / blockSize;
        int compressedWidth = width / blockSize;
        int[][] indices = new int[compressedHeight][compressedWidth];

        for (int y = 0; y < height; y += blockSize) {
            for (int x = 0; x < width; x += blockSize) {
                double[] vector = new double[blockSize * blockSize];
                int idx = 0;
                for (int dy = 0; dy < blockSize; dy++) {
                    for (int dx = 0; dx < blockSize; dx++) {
                        int py = y + dy;
                        int px = x + dx;
                        if (py < height && px < width) {
                            vector[idx++] = component[py][px];
                        }
                    }
                }
                indices[y / blockSize][x / blockSize] = findNearestCluster(vector, codebook);
            }
        }
        return indices;
    }

    public static int findNearestCluster(double[] vector, double[][] codebook) {
        int bestIndex = 0;
        double minDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < codebook.length; i++) {
            double distance = 0;
            for (int j = 0; j < vector.length; j++) {
                double diff = vector[j] - codebook[i][j];
                distance += diff * diff;
            }
            if (distance < minDistance) {
                minDistance = distance;
                bestIndex = i;
            }
        }
        return bestIndex;
    }
}