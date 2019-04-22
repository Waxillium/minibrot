precision highp float;
precision highp vec2;
uniform vec2 viewportDimensions;
uniform float minX;
uniform float maxX;
uniform float minY;
uniform float maxY;
uniform float limit;
uniform float rMod;
uniform float gMod;
uniform float bMod;
uniform float br;
uniform bool theme2;
uniform bool theme3;
uniform bool inv;
uniform vec2 cxy;
uniform bool julia;
uniform bool rainbow;
uniform bool trip;

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 rgb2hsv(vec3 c){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 decRGB(vec3 c, bool inv){
    if(inv){
        return  vec3(1-(c.x/255), 1-(c.y/255), 1-(c.z/255));
    }
    return vec3(c.x/255, (c.y/255), (c.z/255));

}

vec2 ds_add (vec2 dsa, vec2 dsb){
    vec2 dsc;
    float t1, t2, e;

    t1 = dsa.x + dsb.x;
    e = t1 - dsa.x;
    t2 = ((dsb.x - e) + (dsa.x - (t1 - e))) + dsa.y + dsb.y;

    dsc.x = t1 + t2;
    dsc.y = t2 - (dsc.x - t1);
    return dsc;
}

vec2 ds_mul (vec2 dsa, vec2 dsb){
    vec2 dsc;
    float c11, c21, c2, e, t1, t2;
    float a1, a2, b1, b2, cona, conb, split = 8193.;

    cona = dsa.x * split;
    conb = dsb.x * split;
    a1 = cona - (cona - dsa.x);
    b1 = conb - (conb - dsb.x);
    a2 = dsa.x - a1;
    b2 = dsb.x - b1;

    c11 = dsa.x * dsb.x;
    c21 = a2 * b2 + (a2 * b1 + (a1 * b2 + (a1 * b1 - c11)));

    c2 = dsa.x * dsb.y + dsa.y * dsb.x;

    t1 = c11 + c2;
    e = t1 - c11;
    t2 = dsa.y * dsb.y + ((c2 - e) + (c11 - (t1 - e))) + c21;

    dsc.x = t1 + t2;
    dsc.y = t2 - (dsc.x - t1);

    return dsc;
}

vec2 ds_sub (vec2 dsa, vec2 dsb){
    vec2 dsc;
    float e, t1, t2;

    t1 = dsa.x - dsb.x;
    e = t1 - dsa.x;
    t2 = ((-dsb.x - e) + (dsa.x - (t1 - e))) + dsa.y - dsb.y;

    dsc.x = t1 + t2;
    dsc.y = t2 - (dsc.x - t1);
    return dsc;
}

vec2 ds_set(float a){
    vec2 z;
    z.x = a;
    z.y = 0.0;
    return z;
}

float ds_compare(vec2 dsa, vec2 dsb){
    if (dsa.x < dsb.x) return -1.;
    else if (dsa.x == dsb.x)
    {
        if (dsa.y < dsb.y) return -1.;
        else if (dsa.y == dsb.y) return 0.;
        else return 1.;
    }
    else return 1.;
}

void main(){
    vec2 pos = gl_FragCoord.xy;
    vec2 posx = vec2(ds_set(pos.x/viewportDimensions.x));
    vec2 posy = vec2(ds_set(pos.y/viewportDimensions.y));

    vec2 _minX = ds_set(minX);
    vec2 _minY = ds_set(minY);
    vec2 _maxX = ds_set(maxX);
    vec2 _maxY = ds_set(maxY);

    vec2 cx = ds_add(_minX, ds_mul(posx, (ds_sub(_maxX, _minX))));
    vec2 cy = ds_add(_minY, ds_mul(posy, (ds_sub(_maxY, _minY))));

    vec2 zx = cx;
    vec2 zy = cy;

    if(julia){
        vec4 _cxy = vec4(vec2(ds_set(cxy.x/viewportDimensions.x)), vec2(ds_set(cxy.y/viewportDimensions.y)));
        cx = ds_add(ds_set(-2.0), ds_mul(_cxy.xy, (ds_set(3.0))));
        cy = ds_add(ds_set(-1.0), ds_mul(_cxy.zw, (ds_set(2.0))));
    }

    vec2 two = vec2(ds_set(2.0));
    vec2 four = vec2(ds_set(4.0));
    vec2 tmp;
    float n = 0;
    float iterations = 0;
    for(int i = 0; i < int(limit); i++){
        tmp = zx;
        zx = vec2(ds_add(ds_sub(ds_mul(zx, zx), ds_mul(zy, zy)), cx));
        zy = vec2(ds_add(ds_mul(ds_mul(zy, tmp), two), cy));

        if (rainbow || trip){
            if (ds_compare(ds_add(ds_mul(zx, zx), ds_mul(zy, zy)), four)>0.){
                n = (float(i) + 1. - log(log(length(vec2(zx.x, zy.x))))/log(2.));// http://linas.org/art-gallery/escape/escape.html
                break;
            }
            else {
                n = 0;
            }
        } else {
            if( ds_compare(ds_add(ds_mul(zx, zx), ds_mul(zy, zy)), four)>0.){
                break;
            }
            iterations += 1.0;
        }
    }
    if(!rainbow){

        float itRGB = iterations/limit;
        float mixVal = itRGB;
        vec3 rgb = vec3(0, 0, 0);

        vec3 rgb1 = vec3(0, 0, 0);//first rgb val to mix
        vec3 rgb2 = decRGB(vec3(255, 45, 30), inv);//second
        vec3 rgb3 = decRGB(vec3(255, 192, 27), inv);//third
        vec3 rgb4 = decRGB(vec3(253, 100, 32), inv);//fourth
        if (theme2){
            rgb2 = decRGB(vec3(65, 105, 225), inv);//second
            rgb3 = vec3(1, 1, 1);//third:x
            rgb4 = decRGB(vec3(253, 253, 13), inv);
        } else if (theme3){
            rgb2 = decRGB(vec3(255, 121, 210), inv);//second
            rgb3 = decRGB(vec3(112, 43, 183), inv);//third outside color
            rgb4 = vec3(0, 0, 0);
        }
        vec3 hsv1 = rgb2hsv(rgb1);
        vec3 hsv2 = rgb2hsv(rgb2);
        vec3 hsv3 = rgb2hsv(rgb3);
        vec3 hsv4 = rgb2hsv(rgb4);

        rgb1 = hsv2rgb(vec3(hsv1.x+rMod, hsv1.y, hsv1.z));
        rgb2 = hsv2rgb(vec3(hsv2.x+gMod, hsv2.y, hsv2.z));
        rgb3 = hsv2rgb(vec3(hsv3.x+rMod, hsv3.y, hsv3.z));
        rgb4 = hsv2rgb(vec3(hsv4.x+gMod, hsv4.y, hsv4.z));
        float radius = 1.0;
        if (mixVal < (1.0/3.0)*(1.0/3.0)){
            rgb = mix(rgb1, rgb2, (mixVal*3.0*3.0));
        } else if (mixVal < (2.0/radius)*(2.0/radius)){
            rgb = mix(rgb2, rgb3, (mixVal- (1.0/3.0)*(1.0/3.0)) *3.0*3.0);
        } else {
            rgb = mix(rgb3, rgb4, (mixVal- (2.0/radius)*(2.0*radius)) *radius*radius);
        }
        vec3 hsv = rgb2hsv(rgb);
        hsv2 = vec3((hsv[0]), (hsv[1]), (hsv[2] + br));
        rgb = hsv2rgb(hsv2);
        if (iterations==limit){
            rgb = vec3(0, 0, 0);
        }
        gl_FragColor = vec4(rgb, 1);
    }
    if(rainbow){
        vec3 rgb = vec3((-cos((0.025*rMod)*n)+1.0)/2.0,
        (-cos((0.08*gMod)*n)+1.0)/2.0,
        (-cos((0.12*bMod)*n)+1.0)/2.0);
        vec3 hsv = rgb2hsv(rgb);
        vec3 hsv2 = vec3((hsv[0]), (hsv[1]), (hsv[2] + br));
        rgb = hsv2rgb(hsv2);
        gl_FragColor = vec4(rgb, 1);
    }
    if(trip){
        vec3 rgb = vec3((-cos((0.025+rMod)*n)+1.0)/2.0,
        (-cos((0.08+gMod)*n)+1.0)/2.0,
        (-cos((0.12+bMod)*n)+1.0)/2.0);
        vec3 hsv = rgb2hsv(rgb);
        vec3 hsv2 = vec3((hsv[0]), (hsv[1]), (hsv[2] + br));
        rgb = hsv2rgb(hsv2);
        gl_FragColor = vec4(rgb, 1);
    }
}
