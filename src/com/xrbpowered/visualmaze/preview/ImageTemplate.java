package com.xrbpowered.visualmaze.preview;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xrbpowered.visualmaze.Template;
import com.xrbpowered.visualmaze.TemplateParser;

public class ImageTemplate extends Template<BufferedImage> {

	protected int tileSize = 0;
	
	public int getTileSize() {
		return tileSize;
	}

	public static class Parser extends TemplateParser<BufferedImage, ImageTemplate> {

		public final boolean rotateVariants;
		
		protected BufferedImage sourceImage = null;
		
		public Parser(boolean rotate) {
			this.rotateVariants = rotate;
		}
		
		@Override
		protected ImageTemplate createTemplate() {
			return new ImageTemplate();
		}

		@Override
		protected String[] keyVariant(String[] key, int makeVariant) {
			return rotateKey(key, makeVariant);
		}
		
		@Override
		protected BufferedImage makeResource(int resIndex, int makeVariant) {
			if(sourceImage==null || template.tileSize<=0)
				return null;
			int size = template.tileSize;
			int col = resIndex % (sourceImage.getWidth()/size);
			int row = resIndex / (sourceImage.getWidth()/size);

			BufferedImage res = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = (Graphics2D) res.getGraphics();
			if(makeVariant>0)
				g2.setTransform(AffineTransform.getRotateInstance(makeVariant*Math.PI/2.0, size/2.0, size/2.0));
			g2.drawImage(sourceImage, 0, 0, size, size, col*size, row*size, col*size+size, row*size+size, null);
			
			return res;
		}
		
		@Override
		protected int defaultVariants() {
			return rotateVariants ? 4 : 1;
		}
		
		@Override
		protected void registerResourceVariants(String[] key, int resIndex) {
			if(sourceImage==null)
				error("Source image is not set");
			else if(template.tileSize<=0)
				error("Tile size is not set");
			else
				super.registerResourceVariants(key, resIndex);
		}
		
		@Override
		protected void command(String[] args) {
			if(args[0].equals("@size")) {
				if(template.tileSize>0)
					error("Cannot change tile size");
				else
					template.tileSize = intArg(args, 1, 0);
			}
			
			else if(args[0].equals("@image")) {
				String filename = stringArg(args, 1, null);
				if(filename!=null) {
					try {
						sourceImage = ImageIO.read(new File(rootDir, filename));
					} catch(IOException e) {
						error("Cannot load "+filename);
					}
				}
			}
			
			else
				super.command(args);
		}
		
	}
}
