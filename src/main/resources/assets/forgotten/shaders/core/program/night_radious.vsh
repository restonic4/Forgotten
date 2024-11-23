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
    vec3 distance = BeamCenter - PlayerPos;

    // Calcula el factor de escala según el tiempo (puedes ajustar la fórmula para personalizar el crecimiento)
    float scale = 0.001 + 0.001 * Time; // Crece un 10% por unidad de tiempo

    // Aplica la escala solo en las dimensiones X y Z
    vec3 scaledPosition = vec3(Position.x * scale, Position.y, Position.z * scale);

    // Aplica la transformación de posición adicional
    vec3 testPos = scaledPosition + vec3(distance.x, 200, distance.z);

    gl_Position = ProjMat * ModelViewMat * vec4(testPos, 1.0);

    vertexDistance = fog_distance(ModelViewMat, Position, FogShape);
    vPosition = Position;
    CenterPoint = BeamCenter;
}