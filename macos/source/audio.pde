
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;

Minim minim;
AudioPlayer mp3player;
AudioPlayer collision_sound;
AudioPlayer landing_sound;


void music_setup()
{
  minim = new Minim(this);
  mp3player = minim.loadFile("Escape.mp3");
  collision_sound = minim.loadFile("Cartoon-Space-Boing.mp3");
  landing_sound = minim.loadFile("Bottle Cork.mp3");
  
}
