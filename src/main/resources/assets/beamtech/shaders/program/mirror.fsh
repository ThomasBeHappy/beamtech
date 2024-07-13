#version 150

in vec3 vNormal;
in vec2 vTexCoord;

uniform sampler2D DiffuseSampler;

out vec4 fragColor;

void main() {
    vec4 baseColor = texture(DiffuseSampler, vTexCoord);
    vec4 pinkColor = vec4(1.0, 0.0, 1.0, 1.0); // RGBA for pink
    fragColor = pinkColor;
}
