package com.xrbpowered.visualmaze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MultiIndexList<K, V> {

	private int depth;
	private HashMap<K, MultiIndexList<K, V>> map;
	private List<V> leaf;
	
	public MultiIndexList(int keySize) {
		this.depth = keySize;
		if(keySize==0)
			leaf = new ArrayList<>();
		else {
			map = new HashMap<>();
			map.put(null, new MultiIndexList<>(keySize-1));
		}
	}
	
	public void put(K[] key, V value) {
		if(depth==0)
			leaf.add(value);
		else {
			MultiIndexList<K, V> t = map.get(key[depth-1]);
			if(t==null) {
				t = new MultiIndexList<>(depth-1);
				map.put(key[depth-1], t);
			}
			t.put(key, value);
			map.get(null).put(key, value);
		}
	}

	public List<V> filter(K[] key) {
		if(depth==0)
			return leaf;
		else {
			MultiIndexList<K, V> t = map.get(key[depth-1]);
			return t==null ? Collections.<V>emptyList() : t.filter(key);
		}
	}

}
