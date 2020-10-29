package com.xrbpowered.visualmaze;

public class Grid<R> {

	public class Cell {
		public String corner = null;
		public boolean fixed = false;
		public Tile<R> tile = null;
	}
	
	public final int width, height;
	public final Cell[][] cells;
	
	@SuppressWarnings("unchecked")
	public Grid(int width, int height) {
		this.width = width;
		this.height = height;
		cells = (Cell[][]) new Grid<?>.Cell[width+1][height+1];
		for(int x=0; x<=width; x++)
			for(int y=0; y<=height; y++)
				cells[x][y] = new Cell();
	}
	
	public boolean isInside(int x, int y) {
		return x>=0 && x<width && y>=0 && y<height;
	}

	public void fix(int x, int y, String c)  {
		cells[x][y].corner = c;
		cells[x][y].fixed = true;
	}

	public String[] getKey(int x, int y) {
		return new String[] {
				cells[x][y].corner,
				cells[x+1][y].corner,
				cells[x+1][y+1].corner,
				cells[x][y+1].corner
			};
	}
	
	public void resetKey(int x, int y, int c) {
		switch(c) {
			case 0:
				if(!cells[x][y].fixed)
					cells[x][y].corner = null;
				break;
			case 1:
				if(!cells[x+1][y].fixed)
					cells[x+1][y].corner = null;
				break;
			case 2:
				if(!cells[x+1][y+1].fixed)
					cells[x+1][y+1].corner = null;
				break;
			case 3:
				if(!cells[x][y+1].fixed)
					cells[x][y+1].corner = null;
				break;
		}
	}
	
	public void resetKey(int x, int y) {
		for(int c=0; c<4; c++)
			resetKey(x, y, c);
	}
	
	public void setTile(int x, int y, Tile<R> tile) {
		cells[x][y].tile = tile;
		cells[x][y].corner = tile.key[0];
		cells[x+1][y].corner = tile.key[1];
		cells[x+1][y+1].corner = tile.key[2];
		cells[x][y+1].corner = tile.key[3];
	}
	
	public int countMissingTiles() {
		int count = 0;
		for(int x=0; x<width; x++)
			for(int y=0; y<height; y++) {
				if(cells[x][y].tile==null)
					count++;
			}
		return count;
	}

}
