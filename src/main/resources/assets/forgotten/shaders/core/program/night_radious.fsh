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

// Inherited data
in vec2 texCoord;
in float vertexDistance;
in vec3 vPosition;

out vec4 fragColor;


// Función para aplicar un borde
vec4 apply_border(vec4 baseColor, float vertexDistance, float fogStart, float fogEnd, vec4 borderColor, float borderWidth) {
    float fogValue = smoothstep(fogStart, fogEnd, vertexDistance);

    // Determinar si está dentro del rango del borde
    if (vertexDistance > fogStart - borderWidth && vertexDistance < fogStart) {
        return mix(baseColor, borderColor, 1.0 - fogValue);
    } else if (vertexDistance > fogEnd && vertexDistance < fogEnd + borderWidth) {
        return mix(baseColor, borderColor, fogValue);
    }

    return baseColor;
}

void main() {
    fragColor = apply_border(vec4(0, 0, 0, 1), vertexDistance, 512, 512, vec4(1, 0, 0, 1), 5);
}