package com.xrbpowered.visualmaze.render;

import org.joml.Vector3f;

import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder.Vertex;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.ActorShader;
import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class BorderMesh {

	public final RenderTemplate template;
	public final ActorShader shader;
	
	public float y = 0f;
	public int materialId = -1;
	
	private StaticMeshActor meshActor = null;
	
	public BorderMesh(RenderTemplate template, ActorShader shader) {
		this.shader = shader;
		this.template = template;
	}
	
	public void reset() {
		this.y = 0f;
		this.materialId = -1;
	}
	
	public void set(int materialId, float y) {
		this.y = y;
		this.materialId = materialId;
	}
	
	public void draw() {
		if(meshActor!=null)
			meshActor.draw();
	}
	
	private StaticMesh createMesh() {
		if(materialId<0)
			return null;
		
		int i, j;
		int segm = 3;
		VertexInfo.Attribute materialAttrib = shader.info.get("in_Material");
		float d = template.gridSize * template.renderer.tileSize;
		
		FastMeshBuilder mb = new FastMeshBuilder(shader.info, null, (segm+1) * (segm+1), (segm * segm - 1) * 6);
		
		Vector3f v = new Vector3f();
		int index = 0;
		for(i=0; i<=segm; i++) {
			for(j=0; j<=segm; j++) {
				Vertex vertex = mb.getVertex(index);
				v.x = -d+i*d - 9;//template.renderer.tileSize/2f; // FIXME why 9???
				v.y = this.y;
				v.z = -d+j*d - 9;//template.renderer.tileSize/2f;
				vertex.setPosition(v);
				vertex.setNormal(0, 1, 0);
				vertex.set(materialAttrib, 0, materialId);
				index++;
			}
		}
		
		for(i=0; i<segm; i++) {
			for(j=0; j<segm; j++) {
				if(i==1 && j==1)
					continue;
				mb.addQuad(
					(i+0) * (segm+1) + (j+0),
					(i+0) * (segm+1) + (j+1),
					(i+1) * (segm+1) + (j+1),
					(i+1) * (segm+1) + (j+0)
				);
			}
		}
		
		return mb.create();
	}
	
	public void release() {
		if(meshActor!=null)
			meshActor.getMesh().release();
		meshActor = null;
	}
	
	public StaticMeshActor create() {
		release();
		StaticMesh mesh = createMesh();
		if(mesh!=null) {
			meshActor = new StaticMeshActor();
			meshActor.setMesh(mesh);
			meshActor.setShader(shader);
			meshActor.position.set(0, 0, 0);
			meshActor.updateTransform();
		}
		return meshActor;
	}
	
}
