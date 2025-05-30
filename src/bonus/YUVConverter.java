package src.bonus;

import java.awt.image.BufferedImage;



// Y = Luminance (brightness)

//U = Chrominance Blue (blue projection / color difference) // 1/2 the original size 


//V = Chrominance Red (red projection / color difference)

public class YUVConverter {
    // Convert RGB to YUV with U and V sub-sampled to 50% width and height
    public static double[][][] rgbToYUV(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Pad dimensions to nearest even number
        int paddedHeight = (height % 2 == 0) ? height : height + 1;
        int paddedWidth = (width % 2 == 0) ? width : width + 1;
        int uvHeight = (paddedHeight / 2) % 2 == 0 ? paddedHeight / 2 : (paddedHeight / 2) + 1;
        int uvWidth = (paddedWidth / 2) % 2 == 0 ? paddedWidth / 2 : (paddedWidth / 2) + 1;

        double[][] Y = new double[paddedHeight][paddedWidth];
        double[][] U = new double[uvHeight][uvWidth];
        double[][] V = new double[uvHeight][uvWidth];

        for (int y = 0; y < paddedHeight; y++) {
            for (int x = 0; x < paddedWidth; x++) {
                int srcY = Math.min(y, height - 1);
                int srcX = Math.min(x, width - 1);
                int rgb = image.getRGB(srcX, srcY);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Compute Y
                Y[y][x] = 0.299 * r + 0.587 * g + 0.114 * b;

                // Compute U and V only for every second pixel, ensuring indices are within bounds
                if (y % 2 == 0 && x % 2 == 0) {
                    int subY = y / 2;
                    int subX = x / 2;
                    if (subY < uvHeight && subX < uvWidth) {
                        U[subY][subX] = -0.14713 * r - 0.28886 * g + 0.436 * b + 128;
                        V[subY][subX] = 0.615 * r - 0.51499 * g - 0.10001 * b + 128;
                    }
                }
            }
        }
        return new double[][][] {Y, U, V};
    }

    // Convert YUV back to RGB, assuming U and V are up-sampled to full size
    public static BufferedImage yuvToRGB(double[][] Y, double[][] U_up, double[][] V_up) {
        int height = Y.length;
        int width = Y[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double yVal = Y[y][x];
                double uVal = U_up[y][x];
                double vVal = V_up[y][x];

                // Convert YUV to RGB
                int r = clamp((int) (yVal + 1.13983 * (vVal - 128)));
                int g = clamp((int) (yVal - 0.39465 * (uVal - 128) - 0.58060 * (vVal - 128)));
                int b = clamp((int) (yVal + 2.03211 * (uVal - 128)));

                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgb);
            }
        }
        return image;
    }

    // Up-sample a sub-sampled component to full size using nearest-neighbor
    public static double[][] upSample(double[][] subImage, int fullWidth, int fullHeight) {
        int subHeight = subImage.length;
        int subWidth = subImage[0].length;
        double[][] fullImage = new double[fullHeight][fullWidth];

        for (int y = 0; y < fullHeight; y++) {
            for (int x = 0; x < fullWidth; x++) {
                int subX = Math.min(x / 2, subWidth - 1);
                int subY = Math.min(y / 2, subHeight - 1);
                fullImage[y][x] = subImage[subY][subX];
            }
        }
        return fullImage;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}