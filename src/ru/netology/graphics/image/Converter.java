package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {

    private TextColorSchema schema;
    private int width;
    private int height;
    private double maxRatio;
    private double imgRatio;
    private double fieldRatio;

    public Converter() {
        schema = new ColorSchema();
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {

        BufferedImage img = ImageIO.read(new URL(url));

        checkRatio(img);

        int newWidth = newWidth(img);
        int newHeight = newHeight(img);

        WritableRaster bwRaster = imgToRaster(img, newWidth, newHeight);

        char[][] charsArray = getCharsArray(bwRaster, newWidth, newHeight);

        return constructImage(charsArray);
    }

    private char[][] getCharsArray(WritableRaster bwRaster, int newWidth, int newHeight) {

        char[][] charsArray = new char[newHeight][newWidth];

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                int color = bwRaster.getPixel(j, i, new int[3])[0];
                charsArray[i][j] = schema.convert(color);
            }
        }
        return charsArray;
    }

    private void checkRatio(BufferedImage img) throws BadImageSizeException {
        imgRatio = (double) img.getWidth() / img.getHeight();
        fieldRatio = (double) width / height;
        if (imgRatio > maxRatio) throw new BadImageSizeException(imgRatio, maxRatio);
    }

    private int newWidth(BufferedImage img) {
        if (imgRatio >= fieldRatio) {
            return Math.min(img.getWidth(), width);
        }
        return (int) (imgRatio * newHeight(img));
    }

    private int newHeight(BufferedImage img) {
        if (imgRatio < fieldRatio) {
            return Math.min(img.getHeight(), height);
        }
        return (int) (newWidth(img) / imgRatio);
    }

    private WritableRaster imgToRaster(Image img, int width, int height) {
        Image scaledImage = img.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        return bwImg.getRaster();
    }

    private String constructImage(char[][] charsArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char[] c : charsArray) {
            for (char b : c) {
                stringBuilder.append(b).append(b);
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}