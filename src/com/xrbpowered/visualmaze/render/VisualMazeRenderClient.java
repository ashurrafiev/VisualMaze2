package com.xrbpowered.visualmaze.render;

import static org.lwjgl.opengl.GL11.*;

import java.awt.event.KeyEvent;
import java.io.File;

import org.joml.Vector3f;

import com.xrbpowered.gl.client.Client;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.visualmaze.Grid;

public class VisualMazeRenderClient extends Client {

	public static final String templatePath = "templates/ice.tiles";
	public static final int gridSize = 32;
	
	private MaterialDefTileShader shader;

	private CameraActor camera = null; 
	private Controller controller;

	private RenderTemplate template;
	
	public VisualMazeRenderClient(String templateName, RenderTemplate template) {
		super("VisualMaze2 template preview: "+templateName);
		multisample = 2;
		AssetManager.defaultAssets = new FileAssetManager("assets", AssetManager.defaultAssets);
	}
	
	@Override
	public void createResources() {
		glClearColor(0.8f, 0.82f, 0.85f, 1f);
		
		camera = new CameraActor.Perspective().setRange(0.5f, 160).setAspectRatio(getFrameWidth(), getFrameHeight());
		camera.position = new Vector3f(0, 1.75f, 3);
		camera.updateTransform();
		controller = new Controller(input).setActor(camera);
		controller.moveSpeed = 16;
		controller.setMouseLook(true);

		shader = new MaterialDefTileShader();
		shader.setCamera(camera);
		template = new RenderTemplate.Parser(shader, "in_Material").parse(new File(templatePath));
		
		Grid<RenderTemplate.RotatedComponent> grid = template.generateGrid(gridSize, gridSize, System.currentTimeMillis());
		template.renderer.createInstances(grid);
		
		super.createResources();
	}
	
	@Override
	public void keyPressed(char c, int code) {
		if(code==KeyEvent.VK_ESCAPE)
			requestExit();
	}
	
	@Override
	public void resizeResources() {
		camera.setAspectRatio(getFrameWidth(), getFrameHeight());
	}
	
	@Override
	public void render(float dt) {
		controller.update(dt);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_DEPTH_TEST);
		
		template.renderer.drawInstances();
	}

	public static void main(String[] args) {
		new VisualMazeRenderClient(new File(templatePath).getName(), null).run();
	}

}
