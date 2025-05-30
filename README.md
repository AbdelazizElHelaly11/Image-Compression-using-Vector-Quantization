# Image Compression using Vector Quantization

This project implements an image compression system using **Vector Quantization (VQ)** techniques. The system compresses and decompresses images while maintaining acceptable quality levels. It supports both **RGB** and **YUV** color spaces, offering a comparison between standard and perceptually optimized compression approaches.

## 📌 Overview

Vector Quantization is a **lossy compression technique** that exploits spatial correlation in images by mapping small image blocks to representative vectors in a finite **codebook**. This Java-based system divides images into 2×2 pixel blocks (4D vectors), applies clustering (K-Means++), and encodes them using indices pointing to the nearest code vector.

## 🧠 Methodology

### 🔹 Data Preparation

Images are categorized into:
- **Nature**
- **Faces**
- **Animals**

Each category includes:
- **Training images**: used to generate codebooks.
- **Testing images**: used to evaluate compression quality.

### 🔹 Color Space Transformation

#### RGB Approach
- Standard pipeline using **separate codebooks** for **Red, Green, and Blue** channels.
- Each 2×2 block is compressed individually per channel.

#### YUV Approach (Bonus)
- Converts images to **YUV color space**:
  - **Y (luminance)** — holds most of the visual information.
  - **U, V (chrominance)** — hold color info and are **subsampled** to reduce data.
- Achieves better visual quality at higher compression rates.

### 🔹 Codebook Generation

- Each color channel (or YUV component) is quantized using **K-Means++** from Apache Commons Math.
- Codebooks have up to **256 entries**, allowing each block to be indexed using 8 bits.

### 🔹 Compression Process

1. Split image into 2×2 pixel blocks.
2. Convert to RGB or YUV (optional).
3. Find closest matching codebook vector.
4. Store indices as compressed data.

### 🔹 Decompression Process

1. Retrieve vectors using stored indices.
2. Reconstruct 2×2 pixel blocks.
3. Merge channels/components.
4. (YUV only) Upsample U and V, convert back to RGB.

## 📂 Project Structure

- `src/` – Source code
  - `Main.java` – Entry point (RGB version)
  - `VectorQuantization.java` – Block processing logic
  - `ImageCompression.java` – Handles compression
  - `ImageDecompression.java` – Handles decompression
  - `CodebookGeneration.java` – K-Means++ clustering for codebook
  - `bonus/` – YUV-based implementation
    - `YUVMain.java`
    - `YUVConverter.java`
    - `YUVVectorQuantization.java`
    - `YUVCodebookGeneration.java`
    - `YUVImageCompression.java`
    - `YUVImageDecompression.java`
- `training/` – Training image datasets
  - `nature/`
  - `faces/`
  - `animals/`
- `testing/` – Testing image datasets
  - `nature/`
  - `faces/`
  - `animals/`
- `lib/` – External libraries (e.g., Apache Commons Math)


## ✨ Features

- **Separate codebooks per channel** for optimized compression
- **Bonus YUV implementation** using chrominance sub-sampling (4:2:0)
- **K-Means++** clustering for better initialization
- **Mean Squared Error (MSE)** and visual comparison of output
- **Compression ratios** of up to **8:1** with perceptual quality preserved

## 📈 Results

### Compression Ratios
- **RGB**: ~4:1 (theoretical), ~3.5–4.2:1 (actual)
- **YUV**: ~8:1 (theoretical), ~7.6–8.3:1 (actual)

### Visual Quality
- Smooth regions compress well with low error.
- Fine details (edges, faces) show slightly higher MSE.
- **YUV approach** maintains better visual quality due to human perception modeling.

### Performance Observations
- **Blocking artifacts** may appear in RGB.
- **YUV** yields **better perceptual quality** due to luminance/chrominance separation.
- Codebook training impacts quality—**domain-specific training improves results**.



## 📤 Output

For each test image, the system produces:

- **Original images**  
  - Saved as: `original_[index].jpg`

- **Reconstructed images**  
  - Saved as: `reconstructed_[index].jpg`

- **Compression statistics**  
  - Mean Squared Error (MSE) for each image
  - Average MSE across all processed images
  - Compression ratio details (actual vs. theoretical)



## 📊 RGB vs. YUV Comparison

- **Compression Efficiency**
  - RGB: ~4:1
  - YUV (with 4:2:0 subsampling): ~8:1

- **Visual Quality**
  - RGB: Moderate — visible color artifacts in detailed areas
  - YUV: Higher — better perceptual quality, smoother color transitions

- **Perceptual Alignment**
  - RGB: Processes all channels equally
  - YUV: Exploits human sensitivity to luminance > chrominance

- **Computational Complexity**
  - RGB: Faster, no color space conversion
  - YUV: Slightly slower due to RGB ↔ YUV conversion and chroma subsampling

- **Artifacts**
  - RGB: More blocking and color banding in gradients
  - YUV: Slight blurring in color edges, but fewer visual artifacts overall

- **Best Use Cases**
  - RGB: Simple use, quick evaluation
  - YUV: Better for storage or perceptual accuracy-focused tasks



## 👨‍💻 Author

- Abdelaziz ElHelaly
