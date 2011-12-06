

package planetDefense.objects;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.Vector;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.texture.*;
import java.util.Random;

import javax.media.opengl.GL2;

public class Asteroid extends GameObject {
        public boolean start = true;    
        public double placement = 600;
        

        public Asteroid(GL2 gl){
    

        }
        
        private void initializeVectors() {

            //position = new double[]{START_POS[0], START_POS[1], START_POS[2]};
        }

        public void display(GL2 gl,GLUquadric SOLID,GLU glu,Texture asteroidTexture, int number, double xPos, double yPos){
       
          
            
            
            gl.glPushMatrix();
            
            asteroidTexture.enable(gl);
            asteroidTexture.bind(gl);
            gl.glPushMatrix();
            gl.glTranslated(xPos, yPos,placement);//random(0,100),random(0,100),placement);
            
            //gl.glTranslated(position[0], position[1], position[2]);
            glu.gluSphere(SOLID, 8f, 5, 5);
            gl.glPopMatrix();
            asteroidTexture.disable(gl);
            
            
            gl.glPopMatrix();
      // }
        }
        public void update(long delta){
            
                placement -= 1;
            
        }


}
