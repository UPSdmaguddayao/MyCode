precision mediump float; 	//don't need high precision
varying vec4 vColor; 		//color for the fragment; this was output from the vertexShader
varying vec3 vNormal;		//normal for this fragment
varying vec2 vTexCoord;			//interpolate the tex coords	
varying vec4 vPosition;
varying vec3 vLight;

uniform sampler2D uTexture; //the texture buffer (data)

const float cnstAtten = 1;
const float lineAtten = 0;
const float quadAtten = 0;
const float shininess = 64*4;

const vec3 AmbientColor = vec3(0.2, 0.2, 0.2);
const vec3 LightColor = vec3(1.0);
const vec3 SpecColor = vec3(0.3);

const vec3 EYE_DIR = normalize(vec3(0.0,0.0,20.0)); //direction of the eye

void main() 
{
	//can also add in further lighting here, to "blend"

	//gl_FragColor = texture2D(uTexture, vTexCoord);
	vec3 norm = vec3 (texture2D(uTexture, vTexCoord));  
	vec3 lightDirection = vLight - vec3(vPosition);
	float lightDistance = length(lightDirection);
	lightDirection = lightDirection/lightDistance; //normalize without recomputing length
	
	float attenuation = 1.0 / (cnstAtten + lineAtten*lightDistance + quadAtten*lightDistance*lightDistance);
	
	vec3 halfVec = normalize(lightDirection + EYE_DIR);
	
	float diffuse = max(dot(norm, lightDirection), 0.0);
	float specular = max(dot(norm, halfVec), 0.0);
	
	if(diffuse == 0.0)
		specular = 0.0;
	else
		specular = pow(specular, shininess);
	specular = 0.0;

	vec3 scatteredLight = AmbientColor + LightColor*diffuse*attenuation;
	vec3 reflectedLight = SpecColor*specular*attenuation;
	vec3 rgb = min(vColor.rgb*scatteredLight+reflectedLight, vec3(1.0));

	gl_FragColor = vec4(rgb, vColor.a);
}
