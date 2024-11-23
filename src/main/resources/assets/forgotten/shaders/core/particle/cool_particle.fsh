#version 150

#moj_import <lodestone:common_math.glsl>

uniform sampler2D Sampler0;
uniform sampler2D SceneDepthBuffer;

uniform float LumiTransparency;
uniform float DepthFade;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform mat4 InvProjMat;
uniform vec2 ScreenSize;

in vec4 vertexColor;
in vec2 texCoord0;
in float pixelDepthClip;

out vec4 fragColor;

vec4 transformColorFixed(vec4 initialColor, float lumiTransparent, vec4 vertexColor, vec4 colorModulator) {
    initialColor = lumiTransparent == 1. ? vec4(initialColor.rgb, initialColor.a * (0.21 * initialColor.r + 0.71 * initialColor.g + 0.07 * initialColor.b)) : initialColor;
    vec4 result = initialColor * vertexColor * colorModulator;
    result.rgb /= max(result.a, 0.001); // Normaliza los colores para evitar oscurecimiento
    return result;
}
void main() {
    vec4 color = transformColorFixed(texture(Sampler0, texCoord0), LumiTransparency, vertexColor, ColorModulator);
    fragColor = color;
}
