package com.xrbpowered.visualmaze;

import java.util.List;
import java.util.Random;

public class Generator<R> {

	public static boolean verbose = true;

	protected static final int[] dx = {-1, 1, 1, -1, 0, 1, 0, -1, 0};
	protected static final int[] dy = {-1, -1, 1, 1, -1, 0, 1, 0, 0};
	
	public final Template<R> template;
	public final FillPattern fillPattern;
	
	protected Grid<R> grid;
	protected Random random;
	
	public Generator(Template<R> template, FillPattern fillPattern) {
		this.template = template;
		this.fillPattern = fillPattern;
	}
	
	protected boolean rebuildDFS(int x0, int y0, int ord, boolean[] replace) {
		if(ord>=9)
			return true;
		if(replace[ord]) {
			int x = x0+dx[ord];
			int y = y0+dy[ord];
			String[] key = grid.getKey(x, y);
			List<Tile<R>> tiles = template.index.filter(key);
			if(tiles.isEmpty())
				return false;
			int n = tiles.size();
			int t0 = random.nextInt(n);
			for(int t=0; t<n; t++) {
				Tile<R> tile = tiles.get((t+t0)%n);
				grid.setTile(x, y, tile);
				if(rebuildDFS(x0, y0, ord+1, replace))
					return true;
			}
			grid.cells[x][y].tile = null;
			if(ord<4)
				grid.resetKey(x0, y0, ord);
			return false;
		}
		else {
			return rebuildDFS(x0, y0, ord+1, replace);
		}
	}
	
	protected boolean rebuild(int x0, int y0) {
		boolean[] replace = new boolean[9];
		for(int ord=0; ord<9; ord++) {
			int x = x0+dx[ord];
			int y = y0+dy[ord];
			if(grid.isInside(x, y) && grid.cells[x][y].tile!=null) {
				grid.cells[x][y].tile = null;
				replace[ord] = true;
			}
			else {
				replace[ord] = false;
			}
		}
		replace[8] = true;
		grid.resetKey(x0, y0);
		return rebuildDFS(x0, y0, 0, replace);
	}

	public boolean generatePoint(int x, int y) {
		if(grid.cells[x][y].tile!=null)
			return true;
		String[] key = grid.getKey(x, y);
		List<Tile<R>> list = template.index.filter(key);
		if(!list.isEmpty()) {
			int t = random.nextInt(list.size());
			Tile<R> tile = list.get(t);
			grid.setTile(x, y, tile);
			return true;
		}
		else {
			return rebuild(x, y);
		}
	}
	
	protected boolean generate() {
		if(!fillPattern.fill(this)) {
			for(int d=0; d<FillPattern.basics.length; d++) {
				if(FillPattern.basics[d].fill(this))
					return true;
			}
			return false;
		}
		else
			return true;
	}

	public boolean generate(Grid<R> grid, long seed) {
		return generate(grid, new Random(seed));
	}
	
	public boolean generate(Grid<R> grid, Random random) {
		this.grid = grid;
		this.random = random;
		return generate();
	}
	
}
