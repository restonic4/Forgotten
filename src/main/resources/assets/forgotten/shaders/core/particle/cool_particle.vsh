#version 150

#moj_import <lodestone:common_math.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in ivec2 UV2;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat3 IViewRotMat;
uniform mat4 ProjMat;
uniform int FogShape;

out vec4 vertexColor;
out vec2 texCoord0;
out float pixelDepthClip;

void main() {
    vec4 localSpacePos = vec4(Position, 1.0);
    vec4 viewSpacePos = ModelViewMat * localSpacePos;
    vec4 clipSpacePos = ProjMat * viewSpacePos;
    gl_Position = clipSpacePos;


    vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);

    texCoord0 = UV0;
}