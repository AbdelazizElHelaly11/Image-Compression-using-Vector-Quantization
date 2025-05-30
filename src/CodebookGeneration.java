package src;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import java.util.List;
import java.util.stream.Collectors;

public class CodebookGeneration {
    public static double[][] generateCodebook(List<double[]> vectors) {// 8-bit quantization
        int k = Math.min(256, vectors.size()); // Prevent k > vector count

        // Convert to DoublePoint for clustering
        List<DoublePoint> points = vectors.stream()
                .map(DoublePoint::new)
                .collect(Collectors.toList());

        // K-Means clustering
        KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(k, 10000);
        List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(points);

        // Build codebook from cluster centers
        double[][] codebook = new double[clusters.size()][4];
        for (int i = 0; i < clusters.size(); i++) {
            codebook[i] = clusters.get(i).getCenter().getPoint();
        }

        // Debug: Print actual codebook size
        System.out.println("Generated codebook size: " + codebook.length);

        return codebook;
    }
}
