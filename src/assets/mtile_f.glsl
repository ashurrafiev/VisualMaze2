#version 150 core

uniform mat4 viewMatrix;

uniform sampler2D texMaterialDef;
#define DIFFUSE 0
#define SPEC_MASK 1
// r: specular power (1.0 - 256.0)
// g: specular intensity
// b: white specular (0 - from diffuse, 1 - white)

uniform vec3 lightDirection = vec3(0.5, -0.5, -0.5);
uniform vec4 lightColor;
uniform vec4 midColor;
uniform vec4 shadowColor;

uniform float fogNear = 0;
uniform float fogFar = 160;
uniform vec4 bgColor;
uniform float fogShadow = 0;

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
	float diffuse = dot(normal, lightDir);
	vec4 diffuseLight = diffuse>=0 ? mix(midColor, lightColor, diffuse) : mix(midColor, shadowColor, -diffuse);
	float spec = pow(max(dot(viewDir, normalize(reflect(-lightDir, normal))), 0), specPower);
	
	out_Color = diffuseColor * diffuseLight + specColor * lightColor * spec;
	out_Color.a = diffuseColor.a; // + spec * specColor.r;
	
	if(fogFar>0 && fogFar>fogNear) {
		vec4 fogColor = mix(bgColor, shadowColor, fogShadow);
		out_Color = mix(out_Color, fogColor, clamp((viewDist - fogNear) / (fogFar - fogNear), 0, 1));
	}
}
