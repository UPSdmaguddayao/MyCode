uniform mat4 TuMVMatrix;		//MVMatrix
uniform mat4 TuMVPMatrix;	//MVPMatrix

attribute vec4 TaPosition;	//Per-vertex position information we will pass in
attribute vec3 TaNormal;		//Per-vertex normal information we will pass in.
attribute vec4 TaColor;		//Per-vertex color information we will pass in.
attribute vec2 aTexCoord;	//Per-vertex texture information we will pass in
attribute vec3 Tlight;

varying vec4 vColor; 			//color to interpolate
varying vec3 vNormal;			//normal to interpolate
varying vec4 vPosition;			//pass the position around
varying vec2 vTexCoord;			//interpolate the tex coords	
varying vec3 vLight;

void main() {

  vec4 modelViewVertex = vec4(TuMVMatrix * TaPosition);
  vec3 modelViewNormal = vec3(TuMVMatrix * vec4(TaNormal, 0.0));
  
  vNormal = modelViewNormal;
  vPosition = modelViewVertex;
  vColor = TaColor;
  vTexCoord = aTexCoord; //pass through
  vLight = Tlight;

  gl_Position = TuMVPMatrix * TaPosition;
}
