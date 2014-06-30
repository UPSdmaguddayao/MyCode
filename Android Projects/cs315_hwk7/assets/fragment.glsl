precision mediump float; 	//don't need high precision

varying vec4 vColor; 		//color for the fragment; this was output from the vertexShader
varying vec3 vLightPos; //light position is received that is normalized
varying vec3 vNormal;
varying vec3 vPosition;
varying vec4 La; //ambient light

const vec3 EYE_DIR = normalize(vec3(0.0,0.0,20.0)); //direction of the eye

const vec4 Ls = vec4(1.0);
const float Ks = 1.0;
const float shininess = 100;
const vec4 Ld = vec4(1.0); //color of light.  It's white

void main() {
	vec3 norm = normalize(vNormal);
	vec3 lNorm = normalize (vLightPos-vPosition); //normal of the Light Position and the ModelViewMatrix
	vec3 lightNorm = normalize(vLightPos); //normal of the light
	
	float diffuse = max(dot(norm, lNorm), 0.1); //the amount of diffuse light to give (based on angle between light and normal)
	vec4 newColor = vColor*diffuse;
	
	vec3 r = reflect(-lightNorm, norm);
	float specular = max(dot(r,EYE_DIR),0.0);
	specular = pow(specular, shininess);
	specular = Ks*specular;

	gl_FragColor = vec4((La*Ld*newColor + max(Ls*specular,0.0)).xyz, 1.0);
}