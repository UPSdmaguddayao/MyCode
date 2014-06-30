uniform mat4 uMVMatrix;				//A constant representing the modelview matrix. Used for calculating lights/shading
uniform mat4 uMVPMatrix;			//A constant representing the combined modelview/projection matrix. We use this for positioning

attribute vec4 aPosition;			//Per-vertex position information we will pass in
attribute vec3 aNormal;				//Per-vertex normal information we will pass in.
attribute vec4 aColor;				//Per-vertex color information we will pass in.
attribute vec4 ambient;				//Ambience Value for an object

varying vec4 vColor; 				//out : the ultimate color of the vertex
varying vec3 vNormal;			//normal to interpolate
varying vec3 vPosition;			//pass the position around

//Position of the Light
attribute vec3 lightPos;
varying vec3 vLightPos;

varying vec4 La;


void main() {
  vec3 modelViewVertex = vec3(uMVMatrix * aPosition);						//position modified by modelview
  vec3 modelViewNormal = normalize(vec3(uMVMatrix * vec4(aNormal, 0.0))); 	//normal modified by modelview
  //vec3 lightVector = normalize(lightPos - modelViewVertex); 				//the normalized vector between the light and the vertex
  
  //float diffuse = max(dot(modelViewNormal, lightVector), 0.1); 				//the amount of diffuse light to give (based on angle between light and normal)
																			//scale the color by the light factor and set to output
  gl_PointSize = 3.0; 														//for drawing points
  gl_Position = uMVPMatrix * aPosition; 									//gl_Position is built-in variable for the transformed vertex's position.
 
  vNormal = modelViewNormal;
  vPosition = modelViewVertex;
   vLightPos = normalize(lightPos);
  vColor = aColor;
  La = ambient;
}
