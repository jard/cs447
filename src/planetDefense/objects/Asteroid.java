

package planetDefense.objects;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.EventListener;
import java.util.Vector;
import javax.media.opengl.glu.*;

import com.jogamp.opengl.util.texture.*;

import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import planetDefense.geometry.Matrix;
import planetDefense.geometry.Vector3d;

public class Asteroid extends GameObject {
        public boolean start = true;    
        public double placement = 600;
		private Texture asteroidTexture;
		private GLProfile glp = GLProfile.getDefault();
		private GLU glu;

        public Asteroid(double mass, Vector3d position, Texture t){
        	super(mass);
        	this.position = position;
        	velocity = Vector3d.scale(-.03, Vector3d.normalize(position));
    		glu = new GLU();
            asteroidTexture = t;
        }
        
 

        public void display(GL2 gl){
                  
            
            gl.glPushMatrix();
            gl.glTranslated(position.getX(), position.getY(), position.getZ());//random(0,100),random(0,100),placement);
			// draw planet
	        GLUquadric SOLID = glu.gluNewQuadric();
	        glu.gluQuadricDrawStyle( SOLID, GLU.GLU_FILL);
	        glu.gluQuadricNormals( SOLID, GLU.GLU_SMOOTH );
	        glu.gluQuadricTexture(SOLID, true);
	        
	        asteroidTexture.enable(gl);
	        asteroidTexture.bind(gl);
	       
	        
	        gl.glDisable(GL2.GL_LIGHTING);
	        gl.glColor3f(1, 1, 1);
	        glu.gluSphere(SOLID, 3, 5, 5);
	        asteroidTexture.disable(gl);
	        
//	        
//            asteroidTexture.enable(gl);
//            asteroidTexture.bind(gl);
//            gl.glPushMatrix();

            
            //gl.glTranslated(position[0], position[1], position[2]);
//            glu.gluSphere(SOLID, 8f, 5, 5);
      
            
            gl.glPopMatrix();
      // }
        }
        public void update(long delta){
            double scale = delta/1000;
            position = Vector3d.add(position, Vector3d.scale(scale, velocity));
            
        }

		/* (non-Javadoc)
		 * @see planetDefense.objects.GameObject#initialize()
		 */
		@Override
		public void initializeState() {
			// TODO Auto-generated method stub
			
		}


}
