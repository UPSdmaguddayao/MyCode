uniform mat4 uMVPMatrix; 	// A constant representing the combined modelview/projection matrix. We use this for positioning
attribute vec4 aPosition; 	// Per-vertex position information we will pass in
attribute vec4 aColor; 		// Per-vertex color information we will pass in.
varying vec4 vColor; 		// out : the ultimate color of the vertex
void main() {
	vColor = aColor;
  	gl_PointSize = 3.0f; //for points
  	gl_Position = uMVPMatrix * aPosition;
}