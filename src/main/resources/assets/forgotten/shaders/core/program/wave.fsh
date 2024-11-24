#version 150

#moj_import <fog.glsl>
#moj_import <lodestone:common_math.glsl>

// Uniforms
uniform mat4 ModelViewMat;   // Matriz de vista del modelo
uniform mat4 ProjMat;        // Matriz de proyección
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform int Time;
uniform float Alpha;

// Inherited data
in vec2 texCoord;
in float vertexDistance;
in vec3 vPosition;

out vec4 fragColor;


void main() {
    fragColor = linear_fog(ColorModulator, vertexDistance, 0, 512, vec4(0.3, 1, 1, 1));
    fragColor.a = Alpha;
}