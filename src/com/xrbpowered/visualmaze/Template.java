package com.xrbpowered.visualmaze;

import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public abstract class Template<R> {

	public final MultiIndexList<String, Tile<R>> index = new MultiIndexList<>(4);
	
	public FillPattern fillPattern = FillPattern.basics[0];
	public int generatorAttempts = 1;
	public int gridSize = 16;
	public String gridEdge = null;
	
	protected Grid<R> createGrid(int width, int height, Random random) {
		Grid<R> grid = new Grid<>(width, height);
		grid.fixEdges(gridEdge);
		return grid;
	}
	
	protected Generator<R> createGenerator() {
		if(fillPattern==null)
			throw new RuntimeException("Fill pattern not defined");
		return new Generator<>(this, fillPattern);
	}

	public Grid<R> generateGrid(int width, int height, long seed) {
		return generateGrid(width, height, new Random(seed));
	}
	
	public Grid<R> generateGrid(long seed) {
		return generateGrid(gridSize, gridSize, seed);
	}

	public Grid<R> generateGrid(int width, int height, Random random) {
		Generator<R> gen = createGenerator();
		Grid<R> grid = null;
		Grid<R> bestGrid = null;
		int minMissing = width*height;
		for(int att=0; att<generatorAttempts; att++) {
			grid = createGrid(width, height, random);
			if(gen.generate(grid, random)) {
				if(Generator.verbose)
					System.out.printf("Success after %d attempts\n", att+1);
				return grid;
			}
			else {
				int missing = grid.countMissingTiles();
				if(missing<minMissing) {
					minMissing = missing;
					bestGrid = grid;
				}
			}
		}
		if(Generator.verbose)
			System.out.printf("Failed after %d attempts, missing %d tiles\n", generatorAttempts, minMissing);
		return bestGrid;
	}
	
	public TreeSet<String> listCornerTypes() {
		TreeSet<String> set = new TreeSet<>();
		List<Tile<R>> tiles = index.filter(Template.allKey);
		for(Tile<R> tile : tiles) {
			for(int i=0; i<4; i++)
				set.add(tile.key[i]);
		}
		return set;
	}
	
	public String listCornerTypesAsString() {
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for(String s : listCornerTypes()) {
			if(!first) sb.append(",");
			sb.append(s);
			first = false;
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static final String[] allKey = {null, null, null, null};
	
}
