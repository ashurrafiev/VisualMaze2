package com.xrbpowered.visualmaze.render;

import com.xrbpowered.gl.res.shader.ActorShader;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class MaterialDefTileShader extends ActorShader {

	public static VertexInfo vertexInfo = new VertexInfo().addAttrib("in_Position", 3).addAttrib("in_Normal", 3).addAttrib("in_Material", 1);
	public static VertexInfo instInfo = new VertexInfo(vertexInfo).addAttrib("ins_Position", 2).addAttrib("ins_RotationY", 1);
	
	public MaterialDefTileShader() {
		super(vertexInfo, "mtile_v.glsl", "mtile_f.glsl");
	}

	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		initSamplers(new String[] {"texMaterialDef"});
	}
	
	@Override
	public void use() {
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		super.use();
	}
	
	@Override
	public void unuse() {
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		super.unuse();
	}

	@Override
	protected int bindAttribLocations() {
		return instInfo.bindAttribLocations(this.pId, super.bindAttribLocations());
	}
	
}
