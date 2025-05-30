package src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        // Debug: Print working directory
        System.out.println("Current working directory: " + new File(".").getAbsolutePath());

        // Load training images
        List<BufferedImage> natureTrainingImages = VectorQuantization.readImages("training/nature");
        List<BufferedImage> facesTrainingImages = VectorQuantization.readImages("training/faces");
        List<BufferedImage> animalTrainingImages = VectorQuantization.readImages("training/animals");

        // Debug: Print number of training images loaded
        System.out.println("Nature training images loaded: " + natureTrainingImages.size());
        System.out.println("Faces training images loaded: " + facesTrainingImages.size());
        System.out.println("Animal training images loaded: " + animalTrainingImages.size());

        // Check if enough training images are available
        if (natureTrainingImages.size() < 10 || facesTrainingImages.size() < 10 || animalTrainingImages.size() < 10) {
            throw new IllegalStateException("Each training category must have at least 10 images");
        }

        List<BufferedImage> trainingImages = new ArrayList<>();
        trainingImages.addAll(natureTrainingImages.subList(0, 10));
        trainingImages.addAll(facesTrainingImages.subList(0, 10));
        trainingImages.addAll(animalTrainingImages.subList(0, 10));

        // Generate codebooks
        double[][] codebookR = CodebookGeneration.generateCodebook(
            VectorQuantization.collectTrainingVectors(trainingImages, 0));
        double[][] codebookG = CodebookGeneration.generateCodebook(
            VectorQuantization.collectTrainingVectors(trainingImages, 1));
        double[][] codebookB = CodebookGeneration.generateCodebook(
            VectorQuantization.collectTrainingVectors(trainingImages, 2));

        // Load test images
        List<BufferedImage> natureTestImages = VectorQuantization.readImages("testing/nature");
        List<BufferedImage> facesTestImages = VectorQuantization.readImages("testing/faces");
        List<BufferedImage> animalTestImages = VectorQuantization.readImages("testing/animals");

        // Debug: Print number of test images loaded
        System.out.println("Nature test images loaded: " + natureTestImages.size());
        System.out.println("Faces test images loaded: " + facesTestImages.size());
        System.out.println("Animal test images loaded: " + animalTestImages.size());

        // Check if enough test images are available
        if (natureTestImages.size() < 5 || facesTestImages.size() < 5 || animalTestImages.size() < 5) {
            throw new IllegalStateException("Each testing category must have at least 5 images");
        }

        List<BufferedImage> testImages = new ArrayList<>();
        testImages.addAll(natureTestImages.subList(0, 5));
        testImages.addAll(facesTestImages.subList(0, 5));
        testImages.addAll(animalTestImages.subList(0, 5));

        double totalMSE = 0;

        // Process test images
        for (int i = 0; i < testImages.size(); i++) {
            BufferedImage testImage = testImages.get(i);
            int width = testImage.getWidth();
            int height = testImage.getHeight();

            // Compress
            int[][] indicesR = ImageCompression.compressChannel(testImage, 0, codebookR);
            int[][] indicesG = ImageCompression.compressChannel(testImage, 1, codebookG);
            int[][] indicesB = ImageCompression.compressChannel(testImage, 2, codebookB);

            // Decompress
            int[][] reconstructedR = ImageDecompression.reconstructChannel(indicesR, codebookR, height, width);
            int[][] reconstructedG = ImageDecompression.reconstructChannel(indicesG, codebookG, height, width);
            int[][] reconstructedB = ImageDecompression.reconstructChannel(indicesB, codebookB, height, width);

            // Combine channels
            BufferedImage reconstructedImage = ImageDecompression.combineChannels(
                reconstructedR, reconstructedG, reconstructedB, width, height);

            // Save images
            ImageIO.write(testImage, "jpg", new File("original_" + i + ".jpg"));
            ImageIO.write(reconstructedImage, "jpg", new File("reconstructed_" + i + ".jpg"));

            // Compute and print MSE
            double mse = calculateMSE(testImage, reconstructedImage);
            System.out.printf("Image %d MSE: %.2f\n", i, mse);
            totalMSE += mse;
        }

        // Print average MSE
        double avgMSE = totalMSE / testImages.size();
        System.out.printf("Average MSE: %.2f\n", avgMSE);

        // Compression ratio
        System.out.println("Compression ratio: 4");
    }

    // MSE calculation method
    public static double calculateMSE(BufferedImage original, BufferedImage reconstructed) {
        int width = original.getWidth();
        int height = original.getHeight();
        double mse = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgbOriginal = original.getRGB(x, y);
                int rgbReconstructed = reconstructed.getRGB(x, y);

                int rOrig = (rgbOriginal >> 16) & 0xFF;
                int gOrig = (rgbOriginal >> 8) & 0xFF;
                int bOrig = rgbOriginal & 0xFF;

                int rReconst = (rgbReconstructed >> 16) & 0xFF;
                int gReconst = (rgbReconstructed >> 8) & 0xFF;
                int bReconst = rgbReconstructed & 0xFF;

                mse += Math.pow(rOrig - rReconst, 2);
                mse += Math.pow(gOrig - gReconst, 2);
                mse += Math.pow(bOrig - bReconst, 2);
            }
        }

        mse /= (width * height * 3.0); // 3 for RGB channels
        return mse;
    }
}
