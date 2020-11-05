#version 150 core

uniform mat4 viewMatrix;

uniform sampler2D texMaterialDef;
#define DIFFUSE 0
#define SPEC_MASK 1
// r: specular power (1.0 - 256.0)
// g: specular intensity
// b: white specular (0 - from diffuse, 1 - white)

uniform vec3 lightDirection = vec3(0.5, -0.65, -0.5);
uniform vec4 lightColor = vec4(1, 0.95, 0.85, 1);
uniform vec4 ambientColor = vec4(0.0, 0.2, 0.5, 1);

uniform float fogNear = 16;
uniform float fogFar = 160;
uniform vec4 fogColor = vec4(0.8, 0.82, 0.85, 0);

in vec4 pass_Position;
in vec3 pass_Normal;
flat in float pass_Material;

out vec4 out_Color;

void main(void) {
	int mtl = int(pass_Material);
	vec4 diffuseColor = texelFetch(texMaterialDef, ivec2(DIFFUSE, mtl), 0);
	if(diffuseColor.a<0.5)
		discard;
	
	vec4 specMask = texelFetch(texMaterialDef, ivec2(SPEC_MASK, mtl), 0);
	float specPower = specMask.r*255+1;
	vec4 specColor = mix(diffuseColor, vec4(1, 1, 1, 1), specMask.b) * specMask.g;
	
	vec3 normal = gl_FrontFacing ? pass_Normal : -pass_Normal;
	
	float viewDist = length(pass_Position.xyz);
	vec3 viewDir = normalize(-pass_Position.xyz);
	
	vec3 lightDir = normalize((viewMatrix * vec4(-lightDirection, 0)).xyz);
	float diffuse = max(dot(normal, lightDir), 0);
	float spec = pow(max(dot(viewDir, normalize(reflect(-lightDir, normal))), 0), specPower);
	
	out_Color = diffuseColor * mix(ambientColor, lightColor, diffuse) + specColor * lightColor * spec;
	out_Color.a = diffuseColor.a; // + spec * specColor.r;
	
	if(fogFar>0 && fogFar>fogNear) {
		out_Color = mix(out_Color, fogColor, clamp((viewDist - fogNear) / (fogFar - fogNear), 0, 1));
	}
}
