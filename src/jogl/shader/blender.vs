
varying vec2 texp;

void main(){
   texp = gl_MultiTexCoord0.xy;
   gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
    