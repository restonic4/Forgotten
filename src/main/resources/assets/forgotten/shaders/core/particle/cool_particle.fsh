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

void main() {
    vec4 color = transformColor(texture(Sampler0, texCoord0), LumiTransparency, vertexColor, ColorModulator);
    fragColor = color;
}