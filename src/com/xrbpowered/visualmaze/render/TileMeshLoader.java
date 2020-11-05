package com.xrbpowered.visualmaze.render;

import java.util.List;

import org.joml.Vector3f;

import com.xrbpowered.gl.res.mesh.MaterialObjMeshLoader;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class TileMeshLoader extends MaterialObjMeshLoader {

	public final float tileSkip;
	
	public TileMeshLoader(VertexInfo.Attribute materialAttrib, List<String> materials, float tileSkip, float scale) {
		super(materialAttrib, materials, scale);
		this.tileSkip = tileSkip;
	}

	protected float tmod(float x) {
		return (x>=0) ? x%tileSkip : tileSkip-((-x)%tileSkip);
	}

	protected float ctmod(float x) {
		return tmod(x+tileSkip/2) - tileSkip/2;
	}
	
	@Override
	protected Vector3f adjustPosition(Vector3f v) {
		v.x = ctmod(v.x);
		v.z = ctmod(v.z);
		return super.adjustPosition(v);
	}
	
}
