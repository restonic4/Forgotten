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
uniform float Progress;
uniform float Alpha;
uniform vec4 WaveColor;

// Inherited data
in vec2 texCoord;
in float vertexDistance;
in vec3 vPosition;

out vec4 fragColor;

void main() {
    float dynamicAlpha = Alpha;
    if (Progress >= 0.5) {
        dynamicAlpha = mix(Alpha, 0.0, (Progress - 0.5) / 0.5);
    }

    fragColor = linear_fog(ColorModulator, vertexDistance, 0, 512, WaveColor);
    fragColor.a = dynamicAlpha;
}