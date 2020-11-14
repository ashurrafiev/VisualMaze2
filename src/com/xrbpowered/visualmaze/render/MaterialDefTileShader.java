package com.xrbpowered.visualmaze.render;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL20.*;

import java.awt.Color;

import org.joml.Vector3f;

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
	
	public void setEnvColors(Color light, Color mid, Color shadow) {
		glUseProgram(pId);
		uniform(glGetUniformLocation(pId, "lightColor"), light);
		uniform(glGetUniformLocation(pId, "midColor"), mid);
		uniform(glGetUniformLocation(pId, "shadowColor"), shadow);
		glUseProgram(0);
	}

	public void setEnvLightDir(Vector3f lightDir) {
		glUseProgram(pId);
		uniform(glGetUniformLocation(pId, "lightDirection"), lightDir);
		glUseProgram(0);
	}

	public void setEnvFog(Color bgColor, float near, float far, float shadow) {
		glUseProgram(pId);
		uniform(glGetUniformLocation(pId, "bgColor"), bgColor);
		glUniform1f(glGetUniformLocation(pId, "fogNear"), near);
		glUniform1f(glGetUniformLocation(pId, "fogFar"), far);
		glUniform1f(glGetUniformLocation(pId, "fogShadow"), shadow);
		glUseProgram(0);
		glClearColor(bgColor.getRed()/255f, bgColor.getGreen()/255f, bgColor.getBlue()/255f, 0.0f);
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
