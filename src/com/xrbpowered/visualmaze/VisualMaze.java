package com.xrbpowered.visualmaze;

import com.xrbpowered.visualmaze.preview.VisualMazePreview;
import com.xrbpowered.visualmaze.render.VisualMazeRenderClient;

public class VisualMaze {

	public static final String defaultTemplate = "templates/ice.tiles";
	
	public static void main(String[] args) {
		boolean render = true;
		String templatePath = defaultTemplate;
		
		for(int i=0; i<args.length; i++) {
			if(args[i].equals("-2d")) {
				render = false;
				continue;
			}
			else if(args[i].equals("-3d")) {
				render = true;
				continue;
			}
			else if(!args[i].startsWith("-")) {
				i++;
				if(i<args.length) {
					templatePath = args[i];
					continue;
				}
			}
			System.err.println("Usage:\njava -jar visualmaze.jar [-2d|-3d] [templatePath]");
			return;
		}
		
		if(render)
			new VisualMazeRenderClient(templatePath).run();
		else
			VisualMazePreview.startFrame(VisualMaze.defaultTemplate);
	}

}
