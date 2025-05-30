package src.bonus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YUVMain {
    public static void main(String[] args) throws IOException {
        // Debug: Print working directory
        System.out.println("Current working directory: " + new File(".").getAbsolutePath());

        // Load training images
        List<BufferedImage> natureTrainingImages = YUVVectorQuantization.readImages("training/nature");
        List<BufferedImage> facesTrainingImages = YUVVectorQuantization.readImages("training/faces");
        List<BufferedImage> animalTrainingImages = YUVVectorQuantization.readImages("training/animals");

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

        // Generate codebooks for Y, U, V
        double[][] codebookY = YUVCodebookGeneration.generateCodebook(trainingImages, 0);
        double[][] codebookU = YUVCodebookGeneration.generateCodebook(trainingImages, 1);
        double[][] codebookV = YUVCodebookGeneration.generateCodebook(trainingImages, 2);
        System.out.println("Codebook Y size: " + codebookY.length);
        System.out.println("Codebook U size: " + codebookU.length);
        System.out.println("Codebook V size: " + codebookV.length);

        // Load test images
        List<BufferedImage> natureTestImages = YUVVectorQuantization.readImages("testing/nature");
        List<BufferedImage> facesTestImages = YUVVectorQuantization.readImages("testing/faces");
        List<BufferedImage> animalTestImages = YUVVectorQuantization.readImages("testing/animals");

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

        // Process test images
        for (int i = 0; i < testImages.size(); i++) {
            BufferedImage testImage = testImages.get(i);
            int width = testImage.getWidth();
            int height = testImage.getHeight();

            // Convert to YUV
            double[][][] yuv = YUVConverter.rgbToYUV(testImage);
            double[][] Y = yuv[0];
            double[][] U = yuv[1];
            double[][] V = yuv[2];

            // Compress
            int[][] indicesY = YUVImageCompression.compressChannel(Y, codebookY, 2);
            int[][] indicesU = YUVImageCompression.compressChannel(U, codebookU, 2);
            int[][] indicesV = YUVImageCompression.compressChannel(V, codebookV, 2);

            // Decompress
            double[][] reconstructedY = YUVImageDecompression.decompressChannel(indicesY, codebookY, height, width);
            double[][] reconstructedU = YUVImageDecompression.decompressChannel(indicesU, codebookU, height / 2, width / 2);
            double[][] reconstructedV = YUVImageDecompression.decompressChannel(indicesV, codebookV, height / 2, width / 2);

            // Up-sample U and V
            double[][] U_up = YUVConverter.upSample(reconstructedU, width, height);
            double[][] V_up = YUVConverter.upSample(reconstructedV, width, height);

            // Convert back to RGB
            BufferedImage reconstructedImage = YUVConverter.yuvToRGB(reconstructedY, U_up, V_up);

            // Save images
            ImageIO.write(testImage, "jpg", new File("yuv_original_" + i + ".jpg"));
            ImageIO.write(reconstructedImage, "jpg", new File("yuv_reconstructed_" + i + ".jpg"));
        }

        // Compression ratio
        System.out.println("YUV Compression ratio: 8");
        System.out.println("RGB Compression ratio: 4");
    }
}