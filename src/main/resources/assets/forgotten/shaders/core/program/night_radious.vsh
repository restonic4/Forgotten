#version 150

#moj_import <fog.glsl>

in vec3 Position;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;
uniform int FogShape;
uniform int Time;
uniform vec3 BeamCenter;
uniform vec3 PlayerPos;

out float vertexDistance;
out vec3 vPosition;
out vec3 CenterPoint;

void main() {
    float limit = 5000;

    vec3 distance = BeamCenter - PlayerPos;
    distance.x = clamp(distance.x, -limit, limit);
    distance.z = clamp(distance.z, -limit, limit);

    float scale = 0.001 + 0.0001 * Time;

    vec3 scaledPosition = vec3(Position.x * scale, Position.y, Position.z * scale);

    vec3 testPos = scaledPosition + vec3(distance.x, 1000, distance.z);

    gl_Position = ProjMat * ModelViewMat * vec4(testPos, 1.0);

    vertexDistance = fog_distance(ModelViewMat, Position, FogShape);
    vPosition = Position;
    CenterPoint = BeamCenter;
}