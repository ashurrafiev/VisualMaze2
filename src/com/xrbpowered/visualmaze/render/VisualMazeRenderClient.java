package com.xrbpowered.visualmaze.render;

import static org.lwjgl.opengl.GL11.*;

import java.awt.event.KeyEvent;
import java.io.File;

import org.joml.Vector3f;

import com.xrbpowered.gl.client.Client;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.CPAssetManager;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.visualmaze.Grid;
import com.xrbpowered.visualmaze.VisualMaze;

public class VisualMazeRenderClient extends Client {

	public String templatePath;
	
	private MaterialDefTileShader shader;

	private CameraActor camera = null; 
	private Controller controller;

	private RenderTemplate template = null;
	
	public VisualMazeRenderClient(String templateName, String templatePath) {
		super("VisualMaze2 template preview: "+templateName);
		this.templatePath = templatePath;
		multisample = 2;
		AssetManager.defaultAssets = new CPAssetManager("assets", AssetManager.defaultAssets);
	}
	
	public VisualMazeRenderClient(String templatePath) {
		this(new File(templatePath).getName(), templatePath);
	}
	
	@Override
	public void createResources() {
		super.createResources();
		glClearColor(0.93f, 0.93f, 0.93f, 1f);
		
		camera = new CameraActor.Perspective().setRange(0.5f, 160).setAspectRatio(getFrameWidth(), getFrameHeight());
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
		template.borderMesh.create();
		generate();
	}
	
	public void generate() {
		template.renderer.releaseInstances();
		Grid<RenderTemplate.RotatedComponent> grid = template.generateGrid(System.currentTimeMillis());
		template.renderer.createInstances(grid);
		
		camera.position = new Vector3f(0, 25f, 0);
		camera.rotation.y = -(float)Math.PI*0.75f;
		camera.updateTransform();
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
		template.borderMesh.draw();
	}

	public static void main(String[] args) {
		new VisualMazeRenderClient(VisualMaze.defaultTemplate).run();
	}

}
