package com.xrbpowered.visualmaze.render;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import org.joml.Vector3f;

import com.xrbpowered.gl.res.mesh.AdvancedMeshBuilder;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.visualmaze.Template;
import com.xrbpowered.visualmaze.TemplateParser;

public class RenderTemplate extends Template<RenderTemplate.RotatedComponent> {

	public static class RotatedComponent {
		public RenderTileComponent comp;
		public int rotate;
		
		public RotatedComponent(RenderTileComponent comp, int rotate) {
			this.comp = comp;
			this.rotate = rotate;
		}
	}
	
	public final TileRenderer renderer;
	public final BorderMesh borderMesh;
	
	public RenderTemplate(MaterialDefTileShader shader) {
		this.renderer = new TileRenderer(shader);
		this.borderMesh = new BorderMesh(this, shader);
	}
	
	public static class Parser extends TemplateParser<RotatedComponent, RenderTemplate> {

		protected final MaterialDefTileShader shader;
		protected final VertexInfo.Attribute materialAttrib;
		protected final MaterialDefs materials = new MaterialDefs();
		
		protected HashMap<String, AdvancedMeshBuilder> builders = null;
		protected HashMap<String, RenderTileComponent> compMap = null;

		protected float tileSkip = 0f;
		protected float scale = 1f;

		public Parser(MaterialDefTileShader shader, String materialAttrib) {
			this.shader = shader;
			this.materialAttrib = shader.info.get(materialAttrib);
		}
		
		@Override
		protected RenderTemplate createTemplate() {
			return new RenderTemplate(shader);
		}

		@Override
		protected String[] keyVariant(String[] key, int makeVariant) {
			return rotateKey(key, makeVariant);
		}
		
		@Override
		protected RotatedComponent makeResource(int resIndex, int makeVariant) {
			if(builders==null || template.renderer.tileSize<=0)
				return null;
			
			String compName = String.format("T.%03d", resIndex);
			RenderTileComponent comp = compMap.get(compName);
			if(comp==null) {
				AdvancedMeshBuilder b = builders.get(compName);
				if(b==null)
					return null;
				StaticMesh mesh = b.create();
				comp = new RenderTileComponent();
				comp.setMesh(mesh);
				template.renderer.add(comp);
				compMap.put(compName, comp);
			}
			return new RotatedComponent(comp, makeVariant);
		}
		
		@Override
		protected int defaultVariants() {
			return 4;
		}
		
		@Override
		protected void registerResourceVariants(String[] key, int resIndex) {
			if(builders==null)
				error("OBJ file is not set");
			else if(template.renderer.tileSize<=0)
				error("Tile size is not set");
			else
				super.registerResourceVariants(key, resIndex);
		}
		
		protected Vector3f vec3Arg(String[] args, int index, float defx, float defy, float defz) {
			float x = floatArg(args, index, defx, true);
			float y = floatArg(args, index+1, defy, true);
			float z = floatArg(args, index+2, defz, true);
			return new Vector3f(x, y, z);
		}
		
		@Override
		protected void command(String[] args) {
			if(args[0].equals("@objsize")) {
				// @objsize float
				if(template.renderer.tileSize>0)
					error("Cannot change tile size");
				else
					template.renderer.tileSize = floatArg(args, 1, 0);
			}
			
			else if(args[0].equals("@objskip")) {
				// @objskip float
				tileSkip = floatArg(args, 1, 0);
			}

			else if(args[0].equals("@scale")) {
				// @scale float
				scale = floatArg(args, 1, 1f);
			}

			else if(args[0].equals("@mtl")) {
				// @mtl str:name [color:diffuse] [float:specIntensity] [int:specPower] [float:specWhite]
				String name = stringArg(args, 1, null);
				Color diffuse = colorArg(args, 2, Color.WHITE, false);
				float specIntensity = floatArg(args, 3, 0f, false);
				int specPower = intArg(args, 4, 10, false);
				float specWhite = floatArg(args, 5, 1f, false);
				materials.add(name, diffuse, specIntensity, specPower, specWhite);
			}
			
			else if(args[0].equals("@bordermesh")) {
				// @bordermesh [none]
				// @bordermesh str:material [float:y]
				String mtl = stringArg(args, 1, null, false);
				if(mtl==null || mtl.equals("none")) {
					template.borderMesh.reset();
				}
				else {
					int materialId = materials.materialNames.indexOf(mtl);
					if(materialId>=0) {
						float y = floatArg(args, 2, 0f, false);
						template.borderMesh.set(materialId, y);
					}
					else {
						error("Unknown material: "+mtl);
					}
				}
			}

			else if(args[0].equals("@objfile")) {
				// @objfile str
				if(tileSkip<=0)
					error("OBJ skip is not set");
				if(materials==null || materials.materialNames.isEmpty()) {
					error("Materials are not set");
					return;
				}
				String filename = stringArg(args, 1, null);
				if(filename!=null) {
					try {
						Scanner in = new Scanner(new File(rootDir, filename));
						builders = new TileMeshLoader(materialAttrib, materials.materialNames, tileSkip, scale).loadBuilders(in, shader.info, null);
						compMap = new HashMap<>();
						in.close();
					}
					catch(Exception e) {
						e.printStackTrace();
						builders = null;
					}
					if(builders==null)
						error("Cannot load "+filename);
				}
			}
			
			else if(args[0].startsWith("@env:")) {
				if(args[0].equals("@env:lightdir")) {
					// @env:lightdir float:x float:y float:z
					shader.setEnvLightDir(vec3Arg(args, 1, 0.5f, -0.5f, -0.5f));
				}
				
				else if(args[0].equals("@env:colors")) {
					// @env:colors [color:light] [color:mid] [color:shadow]
					Color light = colorArg(args, 1, Color.WHITE, false);
					Color mid = colorArg(args, 2, new Color(0x777777), false);
					Color shadow = colorArg(args, 3, Color.BLACK, false);
					shader.setEnvColors(light, mid, shadow);
				}

				else if(args[0].equals("@env:fog")) {
					// @env:fog [color] [float:near] [float:far] [float:shadow]
					Color bgColor = colorArg(args, 1, new Color(0xeeeeee), false);
					float near = floatArg(args, 2, 0, false);
					float far = floatArg(args, 3, 160, false);
					float shadow = floatArg(args, 4, 0, false);
					shader.setEnvFog(bgColor, near, far, shadow);
				}

				else
					warnUnknownCommand(args[0]);
			}
			
			else
				super.command(args);
		}
		
		@Override
		protected void finish() {
			template.renderer.texMaterialDefs = materials.createTexture();
		}
		
	}

}
