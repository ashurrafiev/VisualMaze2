package com.xrbpowered.visualmaze;

import java.security.InvalidParameterException;

public interface FillPattern {

	public boolean fill(Generator<?> generator);

	public static FillPattern basic(final int dx, final int dy) {
		return new FillPattern() {
			@Override
			public boolean fill(Generator<?> generator) {
				int w = generator.grid.width;
				int h = generator.grid.height;
				boolean res = true;
				for(int y=dy>0 ? 0 : h-1; y<h && y>=0; y+=dy)
					for(int x=dx>0 ? 0 : w-1; x<w && x>=0; x+=dx)
						res &= generator.generatePoint(x, y);
				return res;
			}
		};
	}
	
	public static final FillPattern[] basics = {basic(1, 1), basic(-1, -1), basic(1, -1), basic(-1, 1)};

	public static final FillPattern loom = new FillPattern() {
		@Override
		public boolean fill(Generator<?> generator) {
			int w = generator.grid.width;
			int h = generator.grid.height;
			boolean res = true;
			for(int y=0; y<h; y++) {
				int dx = (y&1)==0 ? 1 : -1;
				for(int x=dx>0 ? 0 : w-1; x<w && x>=0; x+=dx)
					res &= generator.generatePoint(x, y);
			}
			return res;
		}
	};

	public static final FillPattern quadLoom = new FillPattern() {
		@Override
		public boolean fill(Generator<?> generator) {
			int w = generator.grid.width;
			int h = generator.grid.height;
			if(w%2>0 || h%2>0)
				throw new InvalidParameterException();
			boolean res = true;
			for(int y=0; y<h/2; y++) {
				int dx = (y&1)==0 ? 1 : -1;
				for(int x=dx>0 ? 0 : w/2-1; x<w/2 && x>=0; x+=dx) {
					res &= generator.generatePoint(x, y);
					res &= generator.generatePoint(w-1-x, y);
					res &= generator.generatePoint(x, h-1-y);
					res &= generator.generatePoint(w-1-x, h-1-y);
				}
			}
			return res;
		}
	};

	public static final FillPattern diamond = new FillPattern() {
		@Override
		public boolean fill(Generator<?> generator) {
			int s = generator.grid.width;
			if(s!=generator.grid.height || s%2>0)
				throw new InvalidParameterException();
			boolean res = true;
			for(int y=0; y<s; y++) {
				int dx = (y&1)==0 ? 1 : -1;
				for(int x=dx>0 ? 0 : s/2-1; x<s/2 && x>=0; x+=dx) {
					int dy = y-x;
					if(dy>=0 && dy<s/2) {
						res &= generator.generatePoint(x, dy);
						res &= generator.generatePoint(s-1-x, dy);
						res &= generator.generatePoint(x, s-1-dy);
						res &= generator.generatePoint(s-1-x, s-1-dy);
					}
				}
			}
			return res;
		}
	};
	
	public static final FillPattern spirals = new FillPattern() {
		@Override
		public boolean fill(Generator<?> generator) {
			int s = generator.grid.width;
			if(s!=generator.grid.height || s%2>0)
				throw new InvalidParameterException();
			boolean res = true;
			for(int y=0; y<s/2; y++) {
				for(int x=y; x<s-1-y; x++) {
					res &= generator.generatePoint(x, y);
					res &= generator.generatePoint(s-1-y, x);
					res &= generator.generatePoint(s-1-x, s-1-y);
					res &= generator.generatePoint(y, s-1-x);
				}
			}
			return res;
		}
	};
	
}
