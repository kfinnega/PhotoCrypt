import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Steganography {
//    public static void main(String[] args) throws IOException {
//        Scanner sc = new Scanner(System.in);
//        System.out.println(":: Welcome to Steganography ::");
//        System.out.println("1. Encode\n2. Decode");
//        int choice = sc.nextInt();
//
//        if (choice == 1) {
//            encode();
//        } else if (choice == 2) {
//            String decodedData = decode();
//            System.out.println("Decoded data: " + decodedData);
//        } else {
//            throw new IllegalArgumentException("Enter correct input");
//        }
//    }

    // Convert encoding data into 8-bit binary form using ASCII value of characters
    public static List<String> genData(String data) {
        // List of binary codes of given data
        List<String> newData = new ArrayList<>();

        for (char c : data.toCharArray()) {
            newData.add(String.format("%08d", Integer.parseInt(Integer.toBinaryString(c))));
        }

        return newData;
    }

    public static List<int[]> modPix(int[] pixels, String data) {
        List<String> dataList = genData(data);
        int dataLength = dataList.size();
        List<int[]> newPixels = new ArrayList<>();

        int dataIndex = 0;
        for (int[] pixelSet : getPixelSets(pixels)) {
            if (dataIndex < dataLength) {
                for (int j = 0; j < 8; j++) {
                    if (dataList.get(dataIndex).charAt(j) == '0' && pixelSet[j] % 2 != 0) {
                        pixelSet[j]--;
                    } else if (dataList.get(dataIndex).charAt(j) == '1' && pixelSet[j] % 2 == 0) {
                        if (pixelSet[j] != 0) {
                            pixelSet[j]--;
                        } else {
                            pixelSet[j]++;
                        }
                    }
                }

                if (dataIndex == dataLength - 1) {
                    if (pixelSet[8] % 2 == 0) {
                        if (pixelSet[8] != 0) {
                            pixelSet[8]--;
                        } else {
                            pixelSet[8]++;
                        }
                    }
                } else {
                    if (pixelSet[8] % 2 != 0) {
                        pixelSet[8]--;
                    }
                }

                dataIndex++;
            }

            newPixels.add(new int[] { pixelSet[0], pixelSet[1], pixelSet[2] });
            newPixels.add(new int[] { pixelSet[3], pixelSet[4], pixelSet[5] });
            newPixels.add(new int[] { pixelSet[6], pixelSet[7], pixelSet[8] });
        }

        return newPixels;
    }


    private static Iterable<int[]> getPixelSets(int[] pixels) {
        List<int[]> pixelSets = new ArrayList<>();
        for (int i = 0; i < pixels.length; i += 3) {
            pixelSets.add(new int[] {
                    (pixels[i] >> 16) & 0xff,
                    (pixels[i] >> 8) & 0xff,
                    pixels[i] & 0xff,
                    (pixels[i + 1] >> 16) & 0xff,
                    (pixels[i + 1] >> 8) & 0xff,
                    pixels[i + 1] & 0xff,
                    (pixels[i + 2] >> 16) & 0xff,
                    (pixels[i + 2] >> 8) & 0xff,
                    pixels[i + 2] & 0xff
            });
        }

        return pixelSets;
    }


    //Create the new image with the data
    public static void encodeEnc(BufferedImage newImg, String data) {
        int width = newImg.getWidth();
        int height = newImg.getHeight();
        int[] originalPixels = new int[width * height];
        newImg.getRGB(0, 0, width, height, originalPixels, 0, width);
        List<int[]> newPixels = modPix(originalPixels, data);
        int index = 0;

        for (int[] pixel : newPixels) {
            int x = index % width;
            int y = index / width;
            newImg.setRGB(x, y, ((pixel[0] & 0xff) << 16) + ((pixel[1] & 0xff) << 8) + (pixel[2] & 0xff));
            index++;
        }
    }


    public static void encode(String inputImage, String outputImage, String data) throws IOException {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Data is empty");
        }

        BufferedImage image = ImageIO.read(new File(inputImage));

        BufferedImage newImg = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        newImg.setData(image.getData());
        encodeEnc(newImg, data);

        ImageIO.write(newImg, outputImage.substring(outputImage.lastIndexOf(".") + 1), new File(outputImage));
    }

    public static String decode(String inputImage) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputImage));
        Iterator<int[]> imgData = modPix(image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth()), "").iterator();

        StringBuilder data = new StringBuilder();
        while (imgData.hasNext()) {
            int[] pixels = new int[9];
            System.arraycopy(imgData.next(), 0, pixels, 0, 3);
            System.arraycopy(imgData.next(), 0, pixels, 3, 3);
            System.arraycopy(imgData.next(), 0, pixels, 6, 3);

            StringBuilder binStr = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                binStr.append(pixels[i] % 2 == 0 ? '0' : '1');
            }

            data.append((char) Integer.parseInt(binStr.toString(), 2));
            if (pixels[8] % 2 != 0) {
                return data.toString();
            }
        }

        return "";
    }
}