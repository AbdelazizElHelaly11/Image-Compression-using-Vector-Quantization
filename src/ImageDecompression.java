package src;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;




public class ImageDecompression {
    public static int[][] reconstructChannel(int[][] indices, double[][] codebook, int height, int width) {
        int compressedHeight = height / 2;
        int compressedWidth = width / 2;
        int[][] channel = new int[height][width];
        for (int y = 0; y < compressedHeight; y++) {
            for (int x = 0; x < compressedWidth; x++) {
                int index = indices[y][x];

                if (index < 0 || index >= codebook.length) {
                    System.err.println("Warning: invalid codebook index " + index + ", clamping to " + (codebook.length - 1));
                    index = Math.max(0, Math.min(codebook.length - 1, index));
                }

                double[] vector = codebook[index];
                channel[y * 2][x * 2] = (int) Math.round(vector[0]);
                channel[y * 2][x * 2 + 1] = (int) Math.round(vector[1]);
                channel[y * 2 + 1][x * 2] = (int) Math.round(vector[2]);
                channel[y * 2 + 1][x * 2 + 1] = (int) Math.round(vector[3]);
            }
        }
        return channel;
    }

    public static BufferedImage combineChannels(int[][] r, int[][] g, int[][] b, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = (r[y][x] << 16) | (g[y][x] << 8) | b[y][x];
                img.setRGB(x, y, rgb);
            }
        }
        return img;
    }
}