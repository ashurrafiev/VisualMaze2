package com.xrbpowered.visualmaze.ice;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import com.xrbpowered.visualmaze.FillPattern;
import com.xrbpowered.visualmaze.Grid;
import com.xrbpowered.visualmaze.ImageTemplate;
import com.xrbpowered.visualmaze.VisualMazePreview;

public class IceTemplate extends ImageTemplate {

	public static final String templatePath = "templates/tiles.txt";
	public static final int gridSize = 16;
	public static final String gridEdge = "o";

	public IceTemplate() {
		fillPattern = FillPattern.quadLoom;
		generatorAttempts = 10;
	}
	
	@Override
	protected Grid<BufferedImage> createGrid(int width, int height, Random random) {
		Grid<BufferedImage> grid = new Grid<>(width, height);
		if(gridEdge!=null) {
			for(int x=0; x<=width; x++) {
				grid.fix(x, 0, gridEdge);
				grid.fix(x, height, gridEdge);
			}
			for(int y=0; y<=height; y++) {
				grid.fix(0, y, gridEdge);
				grid.fix(width, y, gridEdge);
			}
		}
		return grid;
	}
	
	public static IceTemplate load() {
		return (IceTemplate) new ImageTemplate.Parser(true) {
			@Override
			protected ImageTemplate createTemplate() {
				return new IceTemplate();
			}
		}.parse(new File(templatePath));
	}

	public static void main(String[] args) {
		IceTemplate template = load();
		System.out.println("Corner types = "+template.listCornerTypesAsString());
		VisualMazePreview.startFrame(IceTemplate.class.getSimpleName(), template, gridSize, gridSize);
	}

}
