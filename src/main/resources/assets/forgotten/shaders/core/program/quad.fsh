#version 150

#moj_import <fog.glsl>
#moj_import <lodestone:common_math.glsl>

// Uniforms
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

out vec4 fragColor;

void main() {
    fragColor = vec4(0, 1, 0, 1);
}