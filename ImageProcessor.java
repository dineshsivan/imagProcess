package com.dinesh.intrvw;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.*;
import java.awt.*;
import java.net.URL;

public class ImageProcessor {
	public static void main(String[] args) throws IOException {
		System.setProperty("http.agent", "Chrome");
		int imgCount = 2;

		URL url = new URL("https://docs.groupdocs.com/signature/net/images/esign-document-with-image-signature.png");

		InputStream is = url.openStream();
		BufferedImage image = ImageIO.read(is);

		// initializing array to hold subimages
		BufferedImage imgs[] = new BufferedImage[imgCount];

		// Equally dividing original image into subimages

		int First_img_Width = (int) Math.round(image.getWidth() * 0.7);
		int Second_img_Width = image.getWidth() - First_img_Width;
		int subimage_Height = image.getHeight();

		createImage(image, imgs, First_img_Width, subimage_Height, 0, 0, 0, First_img_Width, subimage_Height);
		createImage(image, imgs, Second_img_Width, subimage_Height, 1, First_img_Width, 0,
				(First_img_Width + Second_img_Width), subimage_Height);

		printImg(imgs, "jpg", imgCount);
		printImg(imgs, "png", imgCount);

		System.out.println("Sub-images have been created.");
	}

	private static void printImg(BufferedImage[] imgs, String type, int imgCount) throws IOException {
		for (int i = 0; i < imgCount; i++) {
			StringBuilder filename = new StringBuilder();
			filename.append("E:\\Test Img\\img").append(i).append(".").append(type);
			File outputFile = new File(filename.toString());
			if ("png".equalsIgnoreCase(type)) {
				final Color color = Color.white;
				imgs[i] = imageToBufferedImage(makeColorTransparent(imgs[i], color));
			}
			ImageIO.write(imgs[i], type, outputFile);
		}
	}

	private static void createImage(BufferedImage image, BufferedImage[] imgs, int subimage_Width, int subimage_Height,
			int current_img, int src_first_x, int src_first_y, int dst_corner_x, int dst_corner_y) {
		imgs[current_img] = new BufferedImage(subimage_Width, subimage_Height, image.getType());
		Graphics2D img_creator = imgs[current_img].createGraphics();

		img_creator.drawImage(image, 0, 0, subimage_Width, subimage_Height, src_first_x, src_first_y, dst_corner_x,
				dst_corner_y, null);
	}

	private static BufferedImage imageToBufferedImage(final Image image) {
		final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return bufferedImage;
	}

	public static Image makeColorTransparent(Image im, final Color color) {

		ImageFilter filter = null;

		if (Color.white.equals(color)) {
			filter = new RGBImageFilter() {

				public int markerRGB = color.getRGB() | 0xFFFFFFFF;

				public final int filterRGB(final int x, final int y, final int rgb) {
					if ((rgb | 0xFF000000) == markerRGB) {
						return 0x00FFFFFF & rgb;
					} else {
						return rgb;
					}
				}
			};
		} else if (Color.black.equals(color)) {


			filter = new RGBImageFilter() {

				public int markerRGB = Color.white.getRGB() | 0x00000000;

				public final int filterRGB(final int x, final int y, final int rgb) {
					if ((rgb | 0xFF000000) == markerRGB) {
						return  0x00FFFFFF & rgb;
					} else  {
						return  0x00FFFFFF; 
					}
				}
			};
			
			
		} else {
			System.out.println("Making white to transparent, since no colour identified!");
			filter = new RGBImageFilter() {

				public int markerRGB = color.getRGB() | 0xFFFFFFFF;

				public final int filterRGB(final int x, final int y, final int rgb) {
					if ((rgb | 0xFF000000) == markerRGB) {
						return 0x00FFFFFF & rgb;
					} else {
						return rgb;
					}
				}
			};
		}

		final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}
}