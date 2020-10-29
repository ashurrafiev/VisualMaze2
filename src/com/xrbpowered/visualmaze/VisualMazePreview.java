package com.xrbpowered.visualmaze;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UILayersContainer;
import com.xrbpowered.zoomui.base.UIPanView;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class VisualMazePreview extends UIElement {

	public static final String templatePath = "templates/tiles.txt";
	
	private static boolean drawGrid = false;

	private ImageTemplate template = null;
	private Grid<BufferedImage> grid = null;
	public int gridWidth, gridHeight;
	
	public VisualMazePreview(UIContainer parent, ImageTemplate template, int gridWidth, int gridHeight) {
		super(new UIPanView(parent) {
			@Override
			protected void paintSelf(GraphAssist g) {
				g.fill(this, new Color(0xeeeeee));
			}
		});
		this.template = template;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
	}
	
	@Override
	public boolean isVisible(Rectangle clip) {
		return true;
	}
	
	@Override
	public boolean isInside(float x, float y) {
		return true;
	}
	
	@Override
	public void paint(GraphAssist g) {
		/*List<Tile<BufferedImage>> images = t.index.filter(Template.allKey);
		int count = images.size();
		int cols = (int)Math.round(Math.sqrt(count));
		int rows = (int)Math.ceil(count/(double)cols);
		int index = 0;
		for(int y=0; y<rows; y++)
			for(int x=0; x<cols; x++) {
				if(index>=count)
					continue;
				BufferedImage img = images.get(index).resource;
				g.graph.drawImage(img, x*t.tileSize, y*t.tileSize, null);
				index++;
			}
		*/
		if(template==null || grid==null)
			return;
		g.pushAntialiasing(false);
		g.resetStroke();
		int ts = template.tileSize;
		for(int y=0; y<grid.width; y++)
			for(int x=0; x<grid.height; x++) {
				Tile<BufferedImage> tile = grid.cells[x][y].tile;
				if(tile==null) {
					g.setColor(new Color(0xffdddd));
					g.fillRect(x*ts, y*ts, ts, ts);
					g.setColor(Color.RED);
					g.line(x*ts, y*ts, (x+1)*ts, (y+1)*ts);
					g.line((x+1)*ts, y*ts, x*ts, (y+1)*ts);
				}
				else {
					BufferedImage img =  tile.resource;
					g.graph.drawImage(img, x*ts, y*ts, null);
					if(drawGrid) {
						g.setColor(new Color(0x11000000, true));
						g.line(x*ts, y*ts, x*ts, (y+1)*ts);
						g.line(x*ts, y*ts, (x+1)*ts, y*ts);
					}
				}
			}
		g.popAntialiasing();
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		if(button==Button.left) {
			if(template!=null)
				grid = template.generateGrid(gridWidth, gridHeight, System.currentTimeMillis());
			repaint();
			return true;
		}
		else
			return super.onMouseDown(x, y, button, mods);
	}
	
	private static class HotkeyPane extends UILayersContainer implements KeyInputHandler {
		public HotkeyPane(UIContainer parent) {
			super(parent);
			getBase().setFocus(this);
		}
		@Override
		public void onFocusGained() {
		}
		@Override
		public void onFocusLost() {
		}
		@Override
		public boolean onKeyPressed(char c, int code, int mods) {
			switch(code) {
				case KeyEvent.VK_G:
					drawGrid = !drawGrid;
					repaint();
					return true;
				default:
					return false;
			}
		}
	}

	public static SwingFrame startFrame(String templateName, ImageTemplate template, int gridWidth, int gridHeight) {
		SwingFrame frame = new SwingFrame(SwingWindowFactory.use(1), "VisualMaze2 template preview: "+templateName,
				gridWidth*template.tileSize, gridHeight*template.tileSize, true, false) {};
		UIContainer hotkeys = new HotkeyPane(frame.getContainer());
		new VisualMazePreview(hotkeys, template, gridWidth, gridHeight);
		frame.show();
		return frame;
	}
	
	public static void main(String[] args) {
		File file = new File(templatePath);
		ImageTemplate template = new ImageTemplate.Parser(true).parse(file);
		System.out.println("Corner types = "+template.listCornerTypesAsString());
		startFrame(file.getName(), template, 32, 32);
	}


}
