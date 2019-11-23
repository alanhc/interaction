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
  void _draw()
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
  void update()
  {
    pos.add(v);
    mag=v.mag();
    if (clicked) player.fuel-=gas_out;
    if (life<=0||fuel<0) stage=2;
   
  }
  void draw() {
    //direction
    stroke(255); strokeWeight(1);
    line(pos.x+homeX, pos.y+homeY,pos.x+homeX+v.x*2, pos.y+homeY+v.y*2);
    noStroke();  
    
    fill(255,255,0); circle(pos.x+w/2.0, pos.y+h/2.0, 10);
    fill(255); textSize(12);  text("v:"+nfc(mag,1),pos.x+w/2.0+50, pos.y+h/2.0);
    //life bar
    
    image(img_heart,pos.x+w/2.0+10-1, pos.y+h/2-40.0+5-1, 10,10 );
    fill(255,0,0,75); rect(pos.x+w/2.0+15, pos.y+h/2-40.0,(100)/2,5);
    fill(0,255,0);    rect(pos.x+w/2.0+15, pos.y+h/2.0-40,(life)/2,5);
    //fuel bar
    image(img_fuel,pos.x+w/2.0+10-1, pos.y+h/2-40.0+5-1+10, 10,10 );
    
    fill(255,0,0,75); rect(pos.x+w/2.0+15, pos.y+h/2-30.0,(100)/2,5);
    fill(128,128,255);    rect(pos.x+w/2.0+15, pos.y+h/2.0-30,(fuel)/2,5);
    
    
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
   void draw()
   {
     fill(255*(1-discover/100),255*discover/100,0,60); circle(x,y,outter);
     fill(255*(1-discover/100),255*discover/100,0); circle(x,y,inner+5);
     imageMode(CENTER);
     image(img_planet, x, y,inner,inner);
     textFont(myFont); fill(0); textSize(12); textAlign(CENTER); text(floor(discover)+"%",x,y);
     
    
   }
   float dist_Player_P;
   
   float gravity()
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
      
      float c=9.0;
      player.v.add( new PVector(ax/len2*c,ay/len2*c) );
      if (discover<100) discover+=(500-inner)/1000.0;
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
      
      if (player.mag<4.5) {
        if (!land) {
          
          landing_sound.play();
          landing_sound.rewind();
          land=true;
          
        }
        
        player.v=new PVector(0,0);
        if (player.fuel<100)player.fuel+=0.5;
        
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
