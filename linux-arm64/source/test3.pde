PFont myFont;

PImage img_planet;
PImage img_player;
PImage img_home;
PImage img_land;


boolean docking=false;

ArrayList<Particle> particle =  new ArrayList<Particle>();


int plantPerView=10;
void create_planet(int start, int r)
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
                      int(random(20,100)),int(random(200,500)),i));
  }
}

void small_map()
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

void setup()
{
  size(912,513, P3D);
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
void draw()
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
void mouseClicked()
{
  clicked=true; 

}
void mousePressed()
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
void keyPressed()
{
  if (keyCode==LEFT) stage--;
  if (keyCode==RIGHT) stage++;
  if (keyCode==UP) camera_h+=100;
  if (keyCode==DOWN) camera_h-=100;
  if (key=='d') player.life=0;
  if (key=='n') {player.fuel=0;player.life=0;};
 
}
float discover_rate=0;
void score_scene()
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
void start_scene()
{
  for (int i=0; i<p.size(); i++) {
    p.remove(i);  
  }
  background(0);
  myFont = createFont("Georgia", 50);
  start_scene_animation();
  
}

void init_play_scene()
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

void play_scene()
{
  if (!mp3player.isPlaying()) { mp3player.rewind();
    mp3player.play(); }
  inside=false;
  
  float minD_player_plant=1e9;
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
    create_planet(int(pre)*3,0);
    pre+=min(h,w);
  }
  if (!inside) {
    imageMode(CENTER);
    image(img_home, player.pos.x+20, player.pos.y+20,30,30);
    imageMode(CENTER);
    image(img_land, player.pos.x+20, player.pos.y+65,30,30);
    textSize(30);textAlign(LEFT);
    fill(0,255,0);text(""+d+"\n"+landing_times, 10.0+player.pos.x+30, 30.0+player.pos.y); 
  }
 
}
