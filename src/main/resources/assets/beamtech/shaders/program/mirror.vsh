#version 150

in vec3 inPosition;
in vec3 inNormal;
in vec2 inTexCoord;

out vec3 vNormal;
out vec2 vTexCoord;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat3 normalMatrix;

void main() {
    gl_Position = modelViewProjectionMatrix * vec4(inPosition, 1.0);
    vNormal = normalize(normalMatrix * inNormal);
    vTexCoord = inTexCoord;
}
