package com.xrbpowered.visualmaze.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;

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

	private RenderTemplate template = null;
	
	public VisualMazeRenderClient(String templateName, RenderTemplate template) {
		super("VisualMaze2 template preview: "+templateName);
		multisample = 2;
		AssetManager.defaultAssets = new FileAssetManager("assets", AssetManager.defaultAssets);
	}
	
	@Override
	public void createResources() {
		super.createResources();
		glClearColor(0.8f, 0.82f, 0.9f, 1f);
		
		camera = new CameraActor.Perspective().setRange(0.5f, 160).setAspectRatio(getFrameWidth(), getFrameHeight());
		camera.position = new Vector3f(0, 1.75f, 3);
		camera.updateTransform();
		controller = new Controller(input).setActor(camera);
		controller.moveSpeed = 16;
		controller.setMouseLook(true);

		shader = new MaterialDefTileShader();
		shader.setCamera(camera);

		reloadTemplate();
	}
	
	public void reloadTemplate() {
		if(template!=null)
			template.renderer.releaseResources();
		template = new RenderTemplate.Parser(shader, "in_Material").parse(new File(templatePath));
		generate();
	}
	
	public void generate() {
		template.renderer.releaseInstances();
		Grid<RenderTemplate.RotatedComponent> grid = template.generateGrid(gridSize, gridSize, System.currentTimeMillis());
		template.renderer.createInstances(grid);
	}
	
	@Override
	public void mouseDown(float x, float y, int button) {
		if(!controller.isMouseLook())
			controller.setMouseLook(true);
	}
	
	@Override
	public void keyPressed(char c, int code) {
		switch(code) {
			case KeyEvent.VK_ESCAPE:
				requestExit();
				return;
			case KeyEvent.VK_ALT: 
				if(controller.isMouseLook())
					controller.setMouseLook(false);
				return;
			case KeyEvent.VK_F5:
				reloadTemplate();
				return;
			case KeyEvent.VK_ENTER:
				generate();
				return;
		}
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
