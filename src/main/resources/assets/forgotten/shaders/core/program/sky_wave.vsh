#version 150

#moj_import <fog.glsl>

in vec3 Position;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;
uniform int FogShape;
uniform float Progress;
uniform vec3 Center;
uniform float Height;
uniform vec3 PlayerPos;

out float vertexDistance;
out vec3 vPosition;

void main() {
    vec3 distance = Center - PlayerPos;

    float maxScale = 100.0;
    float base = 1000;
    float scale = (pow(base, Progress) - 1.0) / (base - 1.0) * maxScale;

    vec3 scaledPosition = vec3(Position.x * scale, Position.y, Position.z * scale);

    vec3 finalPos = scaledPosition + vec3(distance.x, distance.y + Height, distance.z);

    gl_Position = ProjMat * ModelViewMat * vec4(finalPos, 1.0);

    vertexDistance = fog_distance(ModelViewMat, Position, FogShape);
    vPosition = Position;
}