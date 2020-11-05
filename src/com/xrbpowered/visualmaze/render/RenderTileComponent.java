package com.xrbpowered.visualmaze.render;

import com.xrbpowered.gl.scene.comp.InstancedMeshList;

public class RenderTileComponent extends InstancedMeshList<TileInstance> {

	public RenderTileComponent() {
		super(MaterialDefTileShader.instInfo);
	}

	@Override
	protected void setInstanceData(float[] instanceData, TileInstance obj, int index) {
		int offs = getDataOffs(index);
		instanceData[offs+0] = obj.x;
		instanceData[offs+1] = obj.z;
		instanceData[offs+2] = obj.rotate;
	}

}
