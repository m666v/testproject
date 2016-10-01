attribute vec4 a_Position;
attribute vec4 a_Color;
uniform mat4 u_MVP;
varying vec4 color;
void main ()
{
  gl_Position = u_MVP * a_Position;
  color = a_Color;
}

