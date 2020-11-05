package com.xrbpowered.visualmaze.ice;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import com.xrbpowered.visualmaze.FillPattern;
import com.xrbpowered.visualmaze.Grid;
import com.xrbpowered.visualmaze.preview.ImageTemplate;
import com.xrbpowered.visualmaze.preview.VisualMazePreview;

public class IceTemplate extends ImageTemplate {

	public static final String templatePath = "templates/ice.tiles";
	public static final int gridSize = 32;
	public static final String gridEdge = "w";

	public IceTemplate() {
		fillPattern = FillPattern.quadLoom;
		generatorAttempts = 100;
	}
	
	@Override
	protected Grid<BufferedImage> createGrid(int width, int height, Random random) {
		Grid<BufferedImage> grid = new Grid<>(width, height);
		grid.fixEdges(gridEdge);
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
