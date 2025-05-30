package src.bonus;

public class YUVImageDecompression {
    public static double[][] decompressChannel(int[][] indices, double[][] codebook, int height, int width) {
        double[][] channel = new double[height][width];
        int rows = indices.length;
        int cols = indices[0].length;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int index = indices[y][x];

                // Clamp index to valid range
                if (index < 0 || index >= codebook.length) {
                    System.err.println("Warning: invalid codebook index " + index + ", clamping to " + (codebook.length - 1));
                    index = Math.max(0, Math.min(codebook.length - 1, index));
                }

                double[] vector = codebook[index];

                int baseY = y * 2;
                int baseX = x * 2;

                if (baseY + 1 < height && baseX + 1 < width) {
                    channel[baseY][baseX] = vector[0];
                    channel[baseY][baseX + 1] = vector[1];
                    channel[baseY + 1][baseX] = vector[2];
                    channel[baseY + 1][baseX + 1] = vector[3];
                } else {
                    // Handle edges safely
                    if (baseY < height && baseX < width) channel[baseY][baseX] = vector[0];
                    if (baseY < height && baseX + 1 < width) channel[baseY][baseX + 1] = vector[1];
                    if (baseY + 1 < height && baseX < width) channel[baseY + 1][baseX] = vector[2];
                    if (baseY + 1 < height && baseX + 1 < width) channel[baseY + 1][baseX + 1] = vector[3];
                }
            }
        }

        return channel;
    }
}
