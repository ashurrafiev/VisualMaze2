package com.xrbpowered.visualmaze.render;

import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.comp.ComponentRenderer;
import com.xrbpowered.visualmaze.Grid;
import com.xrbpowered.visualmaze.Tile;

public class TileRenderer extends ComponentRenderer<RenderTileComponent> {
	
	public final MaterialDefTileShader shader;
	public Texture texMaterialDefs = null;
	
	public float tileSize = 0f;
	
	public TileRenderer(MaterialDefTileShader shader) {
		this.shader = shader;
	}
	
	@Override
	protected Shader getShader() {
		return shader;
	}
	
	@Override
	protected void startDrawInstances(Shader shader) {
		super.startDrawInstances(shader);
		if(texMaterialDefs!=null)
			texMaterialDefs.bind(0);
	}
	
	@Override
	public void releaseResources() {
		super.releaseResources();
		if(texMaterialDefs!=null) {
			texMaterialDefs.release();
			texMaterialDefs = null;
		}
	}
	
	public void createInstances(Grid<RenderTemplate.RotatedComponent> grid) {
		for(RenderTileComponent comp : components)
			comp.startCreateInstances();
		
		for(int y=0; y<grid.width; y++)
			for(int x=0; x<grid.height; x++) {
				Tile<RenderTemplate.RotatedComponent> tile = grid.cells[x][y].tile;
				if(tile!=null) {
					RenderTileComponent comp = tile.resource.comp;
					TileInstance obj = new TileInstance();
					obj.x = (x-grid.width/2)*tileSize;
					obj.z = (y-grid.height/2)*tileSize;
					obj.rotate = tile.resource.rotate*(float)Math.PI/2;
					comp.addInstance(obj);
				}
			}
		
		for(RenderTileComponent comp : components)
			comp.finishCreateInstances();
	}

}
