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

		URL url = new URL("https://scontent.fsin10-1.fna.fbcdn.net/v/t31.18172-8/14633226_1232078330168467_1704321397909539930_o.jpg?_nc_cat=110&ccb=1-7&_nc_sid=9267fe&_nc_ohc=lF3S--8YeqgAX9EbH2d&_nc_ht=scontent.fsin10-1.fna&oh=00_AfDktn0bKl7br_vFr4PEK61tkr0qv96pLRcOLl1ODf_CBQ&oe=63E77796");

		InputStream is = url.openStream();
		BufferedImage image = ImageIO.read(is);

		// initializing array to hold subimages
		BufferedImage imgs[] = new BufferedImage[imgCount];

		// Equally dividing original image into subimages

		int First_img_Width = (int) Math.round(image.getWidth() * 0.6);
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
				imgs[i] = changeImageCoulor(imageToBufferedImage(makeColorTransparent(imgs[i], Color.white)));
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
  
                // set new RGB keeping the r
                // value same as in original image
                // and setting g and b as 0.
                p = (a << 24) | (0 << 16) | (0 << 8) | 0;
  
                img.setRGB(x, y, p);
            }
        }
        
        
        
        return img;
	}
	
}