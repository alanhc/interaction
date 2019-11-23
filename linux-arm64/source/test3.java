import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.effects.*; 
import ddf.minim.signals.*; 
import ddf.minim.spi.*; 
import ddf.minim.ugens.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class test3 extends PApplet {

PFont myFont;

PImage img_planet;
PImage img_player;
PImage img_home;
PImage img_land;


boolean docking=false;

ArrayList<Particle> particle =  new ArrayList<Particle>();


int plantPerView=10;
public void create_planet(int start, int r)
{
  float[] R= new float[plantPerView];
  float[] angle= new float[plantPerView];
  for (int i=0; i<plantPerView; i++) {
    
    R[i]=random(start, start+w);
    angle[i]=random(PI*2);
  }
  for (int i=0; i<plantPerView; i++) {
    
    p.add(new Planet( homeX+(R[i]+r)*cos(angle[i]), 
                      homeY+(R[i]+r)*sin(angle[i]),
                      PApplet.parseInt(random(20,100)),PApplet.parseInt(random(200,500)),i));
  }
}

public void small_map()
{
  int mapSize=150;
  float scale=50;
  float centralX=player.pos.x+w-mapSize/2;
  float centralY=player.pos.y+h-mapSize/2;
  float shiftX=player.pos.x+w/2;
  float shiftY=player.pos.y+h/2;
  
  ///map
  fill(255,50); circle(centralX,centralY , mapSize);
  ///player
  fill(255,255,0); circle(centralX,centralY , mapSize/scale);
  ///home
  fill(0,245,255); circle(centralX+(homeX-shiftX)/scale,centralY+(homeY-shiftY)/scale , mapSize/scale);
  for (int i=0; i<p.size(); i++) {
    float x=centralX+(p.get(i).x-shiftX)/scale;
    float y=centralY+(p.get(i).y-shiftY)/scale;
    if(dist(x,y,centralX,centralY)<mapSize/2) {
      fill(255*(1-p.get(i).discover/100),255*p.get(i).discover/100,0); circle(x,y, mapSize/scale);
      p.get(i).enable=true;
    } else {
      p.get(i).enable=false;  
    }
  
  }
}



Player player;
  ArrayList<Planet> p = new ArrayList<Planet>();

int w=913, h=512;





int stage=-1;

public void setup()
{
  
  img_player= loadImage("ship.png");
  img_home= loadImage("home.png");
  img_land= loadImage("helipad.png");
  img_planet= loadImage("mars.png");
  img_heart= loadImage("heart.png");
  img_fuel= loadImage("fuel.png");
  noStroke();
  music_setup();
}

float x=0;
float homeX=w/2, homeY=h/2;
int pre=300;
boolean initplay=false;
public void draw()
{
  switch(stage) {
    case -1:
    for (int i=0; i<stars.length; i++) {
      stars[i] = new Star();
    }
    stage=0;
    case 0:
    
    mp3player.play();
    initplay=false;
    start_scene();
    
    break; case 1:
    discover_rate=0;
    
    if (!initplay) init_play_scene();
    play_scene();
    break; case 2:
    score_scene();
    initplay=false;
    //init_play_scene();
    break;
    
  }
  
  clicked=false;
 
}
PVector shift;
float gas_out=0;
boolean clicked=false;
public void mouseClicked()
{
  clicked=true; 

}
public void mousePressed()
{
  // println(p.size());
  if (stage==1) {
    if (!docking) {
      shift=new PVector( (mouseX-w/2),(mouseY-h/2) ) ;
      
      
      gas_out=shift.mag()/50;
      
      
      if (player.fuel<0) stage=2;
      else { 
        player.v.add(shift.div(100)); 
        
       
        for (int i=0; i<5; i++) {
          float x=player.pos.x+homeX;
          float y=player.pos.y+homeY;
          
          //fill(255,0,0);circle(x,y,50);
          PVector v=new PVector(0,0);
          v.add(shift);
          
          
          
          v.normalize();
          v.rotate(random(-PI/6,PI/6));
          v.mult(-random(1,5));
          //stroke(255);strokeWeight(3); line(x,y,x+v.x*10,y+v.y*10); noStroke();
          v.add(player.v);
          particle.add( new Particle( x,y,v) );
          
        }
      }
      
     
    }
  }
  else stage = abs(1-stage);
}


float camera_h=0;
public void keyPressed()
{
  if (keyCode==LEFT) stage--;
  if (keyCode==RIGHT) stage++;
  if (keyCode==UP) camera_h+=100;
  if (keyCode==DOWN) camera_h-=100;
  if (key=='d') player.life=0;
  if (key=='n') {player.fuel=0;player.life=0;};
 
}
float discover_rate=0;
public void score_scene()
{

  for (int i=0; i<p.size(); i++) {
    float discover=p.get(i).discover;
    if (discover>0) {
      discover_rate+=discover;
      
    }
    
  }
  
  background(255);
  imageMode(CENTER);
  image(img_planet, w/4, h/2,200,200);
  fill(128,128,255); textSize(45);textAlign(LEFT); text("Press to restart\n",w/2, h/2-25);
  
  fill(0,0,255); textSize(25);textAlign(LEFT); text("Discover rate:"+floor(discover_rate)+"%\n",w/2, h/2+25);
  
  fill(0,0,255); textSize(25);textAlign(LEFT); text("Landing planets:"+landing_times,w/2, h/2+50);
  
  
  if (mousePressed) {
    
    stage=1;
    
    
  }
  
  for (int i=0; i<p.size(); i++) p.remove(i);
  
}
public void start_scene()
{
  for (int i=0; i<p.size(); i++) {
    p.remove(i);  
  }
  background(0);
  myFont = createFont("Georgia", 50);
  start_scene_animation();
  
}

public void init_play_scene()
{
  landing_times=0;
  for (int i=0; i<p.size(); i++) {
    p.remove(i);  
  }
  player = new Player(0,0, new PVector(0,0));
  create_planet(0,300);
  initplay=true;
  
}
boolean inside=false;

public void play_scene()
{
  if (!mp3player.isPlaying()) { mp3player.rewind();
    mp3player.play(); }
  inside=false;
  
  float minD_player_plant=1e9f;
  int id=-1;
  for (int i=0; i<p.size(); i++) {
    if (p.get(i).enable) {
      p.get(i).gravity();
      
      if (p.get(i).dist_Player_P<minD_player_plant && inside) {
        minD_player_plant=(p.get(i).dist_Player_P);
        id=i;
      }
     }
  }

  if (inside) {
    
    camera_h=(150-map(minD_player_plant, p.get(id).inner,
                    p.get(id).outter-p.get(id).inner,
                    0,300));
    if (camera_h>300) camera_h=300;
  }
  else {
    camera_h=0;
  }
  
  player.update();
 
  
  background(0);
  
  camera(); 
  translate(-player.pos.x, -player.pos.y, camera_h);
  
  
  
  
  fill(0,245,255); circle(homeX,homeY, 10);
  for (int i=0; i<p.size();i++) {
    if (p.get(i).enable) p.get(i).draw();
  }
  player.draw();
  for (int i=0; i<particle.size(); i++) {
    particle.get(i)._draw();
    if (i>=10) particle.remove(i-10);
    
  }
  
 
  
  
 
  
  if (!inside) small_map();
  int d = floor(dist(homeX, homeY, player.pos.x+w/2, player.pos.y+h/2)/1);
  if (d>pre+min(h,w)/4) {
    create_planet(PApplet.parseInt(pre)*3,0);
    pre+=min(h,w);
  }
  if (!inside) {
    imageMode(CENTER);
    image(img_home, player.pos.x+20, player.pos.y+20,30,30);
    imageMode(CENTER);
    image(img_land, player.pos.x+20, player.pos.y+65,30,30);
    textSize(30);textAlign(LEFT);
    fill(0,255,0);text(""+d+"\n"+landing_times, 10.0f+player.pos.x+30, 30.0f+player.pos.y); 
  }
 
}








Minim minim;
AudioPlayer mp3player;
AudioPlayer collision_sound;
AudioPlayer landing_sound;


public void music_setup()
{
  minim = new Minim(this);
  mp3player = minim.loadFile("Escape.mp3");
  collision_sound = minim.loadFile("Cartoon-Space-Boing.mp3");
  landing_sound = minim.loadFile("Bottle Cork.mp3");
  
}
PImage img_heart;
PImage img_fuel;

class Particle {
  PVector v;
  float x,y;
  float _color;
  Particle(float xin, float yin, PVector vin) {
    _color=255;
    x=xin;
    y=yin;
    v=vin;
  }
  public void _draw()
  {
    noStroke();
    x+=v.x;
    y+=v.y;
    
    _color-=5;
    fill(255,_color);circle(x,y,5);
  }
}

class Player {

  PVector v=new PVector(0,0);
  PVector pos=new PVector(0,0);
  float mag;
  int life=100;
  float fuel;
  
  Player(float xin, float yin, PVector vin) {
    fuel=100;
    pos.x=xin;
    pos.y=yin;
    v.add(vin);
  }
  public void update()
  {
    pos.add(v);
    mag=v.mag();
    if (clicked) player.fuel-=gas_out;
    if (life<=0||fuel<0) stage=2;
   
  }
  public void draw() {
    //direction
    stroke(255); strokeWeight(1);
    line(pos.x+homeX, pos.y+homeY,pos.x+homeX+v.x*2, pos.y+homeY+v.y*2);
    noStroke();  
    
    fill(255,255,0); circle(pos.x+w/2.0f, pos.y+h/2.0f, 10);
    fill(255); textSize(12);  text("v:"+nfc(mag,1),pos.x+w/2.0f+50, pos.y+h/2.0f);
    //life bar
    
    image(img_heart,pos.x+w/2.0f+10-1, pos.y+h/2-40.0f+5-1, 10,10 );
    fill(255,0,0,75); rect(pos.x+w/2.0f+15, pos.y+h/2-40.0f,(100)/2,5);
    fill(0,255,0);    rect(pos.x+w/2.0f+15, pos.y+h/2.0f-40,(life)/2,5);
    //fuel bar
    image(img_fuel,pos.x+w/2.0f+10-1, pos.y+h/2-40.0f+5-1+10, 10,10 );
    
    fill(255,0,0,75); rect(pos.x+w/2.0f+15, pos.y+h/2-30.0f,(100)/2,5);
    fill(128,128,255);    rect(pos.x+w/2.0f+15, pos.y+h/2.0f-30,(fuel)/2,5);
    
    
  }
 
  

}

PVector nv;

float landing=0;int landing_times=0;
boolean land=false;
class Planet {
   boolean enable=false; 
   float x, y;
   int inner, outter;
   float discover=0;
   
   
  
   int id;
   
   
   Planet(float xin, float yin, int inner_in, int outter_in, int idin) {
     x=xin;y=yin;
     inner=inner_in;
     outter=outter_in;
     id=idin;
     
   }
   public void draw()
   {
     fill(255*(1-discover/100),255*discover/100,0,60); circle(x,y,outter);
     fill(255*(1-discover/100),255*discover/100,0); circle(x,y,inner+5);
     imageMode(CENTER);
     image(img_planet, x, y,inner,inner);
     textFont(myFont); fill(0); textSize(12); textAlign(CENTER); text(floor(discover)+"%",x,y);
     
    
   }
   float dist_Player_P;
   
   public float gravity()
   { 
    
    float ax, ay,len2=0;
    dist_Player_P = dist( player.pos.x+homeX,player.pos.y+homeY,
                        x, y);
    
    if( dist_Player_P<outter/2 && dist_Player_P>inner/2) {
      docking=false;
      inside=true;
      ax=x-(player.pos.x+homeX);
      ay=y-(player.pos.y+homeY);
      len2 = ax*ax+ay*ay;
      
      float c=9.0f;
      player.v.add( new PVector(ax/len2*c,ay/len2*c) );
      if (discover<100) discover+=(500-inner)/1000.0f;
      else discover=100;
      land=false;
    } else if (dist_Player_P<=inner/2) {
      inside=true;
       collision_sound.play();
       collision_sound.rewind();
      nv=new PVector(player.pos.x+homeX-x,player.pos.y+homeY-y);
      nv.normalize();
      float len = PVector.dot(nv, player.v);
      player.v.add(nv.mult( -len*2 ));
      
      if (player.mag<4.5f) {
        if (!land) {
          
          landing_sound.play();
          landing_sound.rewind();
          land=true;
          
        }
        
        player.v=new PVector(0,0);
        if (player.fuel<100)player.fuel+=0.5f;
        
        docking=true;
        if (mousePressed) {
          nv=new PVector(player.pos.x+homeX-x,player.pos.y+homeY-y);
          nv.normalize();
          landing_times++;
          player.v=nv.mult(8);
        } 
        
        
      } else {
        player.life-=100/5;  
      }

      
    } 
    
    return len2;
   }
   
}
Star[] stars = new Star[400];
class Star {
  float x;
  float y;
  float z;
  float pz=0.001f;
  float r,g,b;
  
  Star() {
    x=random(-w,w);
    y=random(-h,h);
    z=random(w);  
    r=random(255);
    g=random(255);
    b=random(255);
   
  }
  public void update() {
    z=z-speed;
    if (z<1) {
      z=w;
      x=random(-w,w);
      y=random(-h,h);
      pz=z;
    }
  }
  public void show() {
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


public void start_scene_animation()
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
  public void settings() {  size(912,513, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "test3" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
