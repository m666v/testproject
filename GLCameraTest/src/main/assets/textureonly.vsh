attribute vec4 a_Position;
attribute vec2 a_UV;
uniform mat4 u_Model;
varying vec2 uv;

void main () {
  gl_Position = a_Position;
  uv = (u_Model*vec4(a_UV, 0,1)).xy;
}