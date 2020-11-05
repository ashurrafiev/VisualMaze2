#version 150 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

in vec3 in_Position;
in vec3 in_Normal;
in float in_Material;

in vec2 ins_Position;
in float ins_RotationY;

out vec4 pass_Position;
out vec3 pass_Normal;
flat out float pass_Material;

mat4 translationMatrix(vec2 t) {
	mat4 m = mat4(1);
	m[3] = vec4(t.x, 0, t.y, 1);
	return m;
}

mat4 rotationYMatrix(float a) {
	mat4 m = mat4(1);
	m[0][0] = cos(a);
	m[0][2] = sin(a);
	m[2][0] = -m[0][2];
	m[2][2] = m[0][0];
	return m;
}

void main(void) {
	mat4 modelMatrix = translationMatrix(ins_Position) * rotationYMatrix(ins_RotationY);
	pass_Position = viewMatrix * modelMatrix * vec4(in_Position, 1);
	gl_Position = projectionMatrix * pass_Position;
	
	pass_Normal = normalize(vec3(viewMatrix * modelMatrix * vec4(in_Normal, 0)));
	pass_Material = in_Material;
}
