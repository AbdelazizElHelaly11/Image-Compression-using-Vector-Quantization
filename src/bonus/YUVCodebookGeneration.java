package src.bonus;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import java.util.stream.Collectors;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class YUVCodebookGeneration {
    public static double[][] generateCodebook(List<BufferedImage> images, int channel) {
        List<double[]> vectors = YUVVectorQuantization.collectTrainingVectors(images, channel);
        List<DoublePoint> points = vectors.stream().map(DoublePoint::new).collect(Collectors.toList());
        
        KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(256, 50000);
        List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(points);
        
        double[][] codebook = new double[256][4];
        for (int i = 0; i < Math.min(clusters.size(), 256); i++) {
            codebook[i] = clusters.get(i).getCenter().getPoint();
        }
        for (int i = clusters.size(); i < 256; i++) {
            codebook[i] = codebook[clusters.size() - 1].clone();
        }
        return codebook;
    }
}