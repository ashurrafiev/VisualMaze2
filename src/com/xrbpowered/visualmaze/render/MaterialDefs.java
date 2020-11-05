package com.xrbpowered.visualmaze.render;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.xrbpowered.gl.res.texture.Texture;

public class MaterialDefs {

	public static final Color defaultDiffuse = new Color(0xdddddd);
	
	public static class Material {
		public String name;
		public Color diffuse = defaultDiffuse;
		public float specIntensity = 0f;
		public int specPower = 20;
		public float specWhite = 1f;
		
		public int getSpecMask() {
			int sp = specPower;
			if(sp<1) sp = 1;
			if(sp>256) sp = 256;
			return 0xff000000 |
					((sp-1)<<16) |
					((int)(specIntensity*255f)<<8) |
					(int)(specWhite*255f);
		}
	}

	public final ArrayList<Material> materials = new ArrayList<>();
	public final ArrayList<String> materialNames = new ArrayList<>();

	public void add(String name, Color diffuse, float specIntensity, int specPower, float specWhite) {
		Material m = new Material();
		m.name = name;
		m.diffuse = diffuse;
		m.specIntensity = specIntensity;
		m.specPower = specPower;
		m.specWhite = specWhite;
		materials.add(m);
		materialNames.add(name);
	}
	
	public Texture createTexture() {
		int count = materials.size();
		IntBuffer buf = ByteBuffer.allocateDirect(4*2*count).order(ByteOrder.nativeOrder()).asIntBuffer();
		for(Material m : materials) {
			buf.put(m.diffuse.getRGB());
			buf.put(m.getSpecMask());
		}
		buf.flip();
		return new Texture(2, count, buf, false, false);
	}
	
}
