package com.xrbpowered.visualmaze;

public class Tile<R> {

	public final String[] key;
	public final R resource;
	
	public Tile(String[] key, R res) {
		this.key = key;
		this.resource = res;
	}
	
	public void register(Template<R> template) {
		template.index.put(key, this);
	}

}
