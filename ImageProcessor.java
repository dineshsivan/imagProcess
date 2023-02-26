package com.dinesh.intrvw;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvinplugins.MarvinPluginCollection;
import marvinplugins.MarvinPluginCollection.*;


public class ImageProcessor {
	public static void main(String[] args) throws IOException {
		System.setProperty("http.agent", "Chrome");
		int imgCount = 2;

//		URL url = new URL("E:\\Test Img\\Source.jpg");
		File file = new File("E:\\Test Img\\Source.jpg");

		// InputStream is = url.openStream();
		BufferedImage image = ImageIO.read(file);

		// initializing array to hold subimages
		BufferedImage imgs[] = new BufferedImage[imgCount];

		// Equally dividing original image into subimages

		int First_img_Width = (int) Math.round(image.getWidth() * 0.4);
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
			//	imgs[i] =  imageToBufferedImage(makeColorTransparent(imgs[i],Color.BLUE));
			//	imgs[i] =  changeImageCoulor(imgs[i] );
				printTRansparent(imgs[i], filename.toString());
			}else {
				ImageIO.write(convertToGrayScale(imgs[i]), type, outputFile);
			}

			
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

		ImageFilter filter = new RGBImageFilter() {

			public int markerRGB = color.getRGB() | 0xFFFFFFFF;

			public final int filterRGB(final int x, final int y, final int rgb) {
				if ((rgb | 0xFF000000) == markerRGB) {
					return 0x00FFFFFF & rgb;
				} else {
					return rgb;
				}
			}
		};

		final ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}

	public static BufferedImage makeImageTranslucent(BufferedImage source, double alpha) {
		BufferedImage target = new BufferedImage(source.getWidth(), source.getHeight(),
				java.awt.Transparency.TRANSLUCENT);
		// Get the images graphics
		Graphics2D g = target.createGraphics();
		// Set the Graphics composite to Alpha
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
		// Draw the image into the prepared reciver image
		g.drawImage(source, null, 0, 0);
		// let go of all system resources in this Graphics
		g.dispose();
		// Return the image
		return target;
	}

	public static BufferedImage changeImageCoulor(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();

		// convert to red image
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int p = img.getRGB(x, y);

				int a = (p >> 24) & 0xff;
				int r = (p >> 16) & 0xff;
				  int g = (p >> 8) & 0xff;
			        // get blue
			        int b = p & 0xff;
				// set new RGB keeping the r
				// value same as in original image
				// and setting g and b as 0.
				 p = (a << 24) | (r << 16) | (g << 8) | b;
			        img.setRGB(x, y, p);
			}
		}

		return img;
	}

	public static BufferedImage convertToGrayScale(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int p = image.getRGB(x, y);
				int a = (p >> 24) & 0xff;
				int r = (p >> 16) & 0xff;
				int g = (p >> 8) & 0xff;
				int b = p & 0xff;
				int avg = (r + g + b) / 3;
				p = (avg << 24) | (avg << 16) | (avg << 8) | avg;
				image.setRGB(x, y, p);
			}
		}
		return image;
	}
	
	public static void printTRansparent(BufferedImage img,String outputString) {
		
		//MarvinImage mImg = MarvinImageIO.loadImage("E:\\Test Img\\Source.png");
		MarvinImage mImg = new MarvinImage(img,"png");
		MarvinPluginCollection.blackAndWhite(mImg, 150);
		MarvinPluginCollection.boundaryFill(mImg.clone(), mImg, 0, 0, Color.white, 150);
		mImg.setColorToAlpha(0, 0xFFFFFFFF);
		MarvinPluginCollection.alphaBoundary(mImg, 5);
		MarvinImageIO.saveImage(mImg, outputString);
	}

	
	
}
