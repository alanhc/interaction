Star[] stars = new Star[400];
class Star {
  float x;
  float y;
  float z;
  float pz=0.001;
  float r,g,b;
  
  Star() {
    x=random(-w,w);
    y=random(-h,h);
    z=random(w);  
    r=random(255);
    g=random(255);
    b=random(255);
   
  }
  void update() {
    z=z-speed;
    if (z<1) {
      z=w;
      x=random(-w,w);
      y=random(-h,h);
      pz=z;
    }
  }
  void show() {
    fill(r,g,b);
    noStroke();
    
    float sx = map(x/z, 0,1,0,w);
    float sy = map(y/z, 0,1,0,h);
    float r= map(z, 0, w, 16,0);
    
    noStroke();
    
    ellipse(sx,sy,r,r);
    
    float px = map(x/pz, 0,1,0,w);
    float py = map(y/pz, 0,1,0,h);
    stroke(255);
    line(px,py,sx,sy);
    noStroke();
    textFont(myFont);
    textAlign(CENTER);
    fill(255-r,255-g,255-b);textSize(45);text("LANDING PLANET",0,0,0);
    fill(r,g,b);textSize(25); text("\nPress to Start",0,0,0 );
    
  }
  
}


float speed;


void start_scene_animation()
{
  
  float d = dist(mouseX, mouseY, w/2,h/2);
  speed = map(d, 0,w, 10,0);
  background(0);
  translate(w/2, h/2);
  for (int i=0; i<stars.length; i++) {
    stars[i].update();
    stars[i].show();
    
  }
}
