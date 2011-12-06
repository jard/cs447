/**
 * 
 */
package planetDefense.objects;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.DoubleBuffer;
import java.util.EventListener;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

import planetDefense.geometry.Matrix;
import planetDefense.geometry.Quaternion;
import planetDefense.geometry.Vector3d;

/**
 * @author Justin
 *
 */
public class EnemyShip extends GameObject{
	
	private static final double MAX_SPEED = 2.0f;
	private static final double ROTATE_DELTA = Math.PI/100;
	private static final double ACCELERATION = 0.3;
	
	private static final double[] START_POS = {1, 0, 500};

    private final float[] materialAmbient = {0.2f, 0.2f, 0.2f, 1.0f};
    private final float[] materialDiffuse = {0.5f, 0.2f, 0.3f, 1.0f};
    private final float[] materialSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float[] materialShininess = {80.0f};


	private boolean thrust;

	
	public EnemyShip(GL2 gl){
		initializeVectors();
		createVertices();
		createNormals();

	}

	/**
	 * 
	 */
	private void initializeVectors() {
		rollAxis = new Vector3d(0, 0, 1); // forward (doubles as direction vector)
		pitchAxis = new Vector3d(1, 0, 0); // left
		yawAxis = new Vector3d(0, 1, 0); // up
		position = new Vector3d(START_POS[0], START_POS[1], START_POS[2]);
	}

	/**
	 * 
	 */
	private void createNormals() {
		//Forward Triangle Fan (Nose of Ship)
		vertexNormals = new Vector3d[7];

		// initialize all vertex normals to (0, 0, 0)
		for(int i = 0; i < vertexNormals.length; ++i){
			vertexNormals[i] = new Vector3d(0, 0, 0);
		}		
		faceNormals = new Vector3d[10];
		
		// calculate a normal for each face
		// (0, 1, 2)
		// (0, 2, 3)
		// (0, 3, 4)
		// (0, 4, 5)
		// (0, 5, 1)
		for(int i = 0; i < 5; ++i){
			int a = 0;	// point of triangle fan
			int b = i+1;
			int c = i+2;
			if(c == 6)c=1;
			Vector3d faceNorm = faceNormal(vertices[a], vertices[b], vertices[c]); 
			faceNormals[i] = faceNorm;
			vertexNormals[i] = Vector3d.add(vertexNormals[i], faceNorm);
		}
		
		//Rear Triangle Fan (Tail of Ship)
		// calculate a normal for each face
		// (6, 1, 2)
		// (6, 2, 3)
		// (6, 3, 4)
		// (6, 4, 5)
		// (6, 5, 1)
		for(int i = 0; i < 5; ++i){
			int a = 6;	// point of triangle fan
			int b = i+1;
			int c = i+2;
			if(c == 6)c=1;
			Vector3d faceNorm = this.faceNormal(vertices[a], vertices[b], vertices[c]);
			faceNormals[5+i] = faceNorm;
			// add adjacent faceNormals to the vertexNormals;
			vertexNormals[i] = Vector3d.add(vertexNormals[i], faceNorm);
		}
		// normalize all the vertexNormals
		for(int i = 0; i < vertexNormals.length; ++i){
			vertexNormals[i] = Vector3d.normalize(vertexNormals[i]);
		}
		
	}

	/**
	 * 
	 */
	private void createVertices() {
		vertices = new Vector3d[7];
		vertices[0] = new Vector3d(0, 0, -1.0);	//.add(new Float(0));
		vertices[1] = new Vector3d(-.4, 0, 0);
		vertices[2] = new Vector3d(0, .1, 0);
		vertices[3] = new Vector3d(.4, 0, 0);
		vertices[4] = new Vector3d(.2, -.05, 0);
		vertices[5] = new Vector3d(-.2, -.05, 0);
		vertices[6] = new Vector3d(0, 0, .2);		
	}


	public void update(long delta){

//		if(thrust){
//			speed += ACCELERATION;
//			if(speed > MAX_SPEED){
//				speed = MAX_SPEED;
//			}
//		} else {
//			speed = 0;
//		}
//		position = Vector3d.add(position, Vector3d.scale(-speed, rollAxis));

		
	}
	


	/**
	 * @param radians
	 */
	private void pitch(double radians) {
		// rotate other two orientation axes about the pitch axis
		Matrix rotationMatrix = rotate(radians, pitchAxis);
		rollAxis = Vector3d.normalize(rotationMatrix.multiply(rollAxis)); 
		yawAxis = Vector3d.normalize(rotationMatrix.multiply(yawAxis));

	}
	
	private void roll(double radians){
		// rotate other two orientation axes about the roll axis
		Matrix rotationMatrix = rotate(radians, rollAxis);
		pitchAxis = Vector3d.normalize(rotationMatrix.multiply(pitchAxis)); 
		yawAxis = Vector3d.normalize(rotationMatrix.multiply(yawAxis));

	}
	
	private void yaw(double radians) {
		// rotate other two orientation axes about the yaw axis
		Matrix rotationMatrix = rotate(radians, yawAxis);
		rollAxis = Vector3d.normalize(rotationMatrix.multiply(rollAxis)); 
		pitchAxis = Vector3d.normalize(rotationMatrix.multiply(pitchAxis));

	}

	private Matrix rotate(double radians, Vector3d rotationVector){
		// using euler angles
		double c = Math.cos(radians);
		double s = Math.sin(radians);
		double u = rotationVector.getX();
		double v = rotationVector.getY();
		double w = rotationVector.getZ();
		double[][] matrix = {
			// row 1
			{u*u*(1-c)+c,
			u*v*(1-c)-w*s,
			u*w*(1-c)+v*s,
			0},
			// row 2
			{v*u*(1-c)+w*s,
			v*v*(1-c)+c,
			v*w*(1-c)-u*s,
			0},
			// row 3
			{u*w*(1-c)-v*s,
			v*w*(1-c)+u*s,
			w*w*(1-c)+c,
			0},
			// row 4
			{0, 0, 0, 1}
		};
		Matrix m = new Matrix(4, 4);
		for(int i = 0; i < 4; ++i){
			for(int j = 0; j < 4; ++j){
				m.set(i, j, matrix[i][j]);
			}
		}
		return m;
	}




	public void display(GL2 gl){
		// set material properties
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, materialAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, materialDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, materialSpecular, 0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, materialShininess, 0);
		gl.glPushMatrix();

		gl.glTranslated(position.getX(), position.getY(), position.getZ());
		gl.glMultMatrixd(currentRotationMatrix(), 0);
//		gl.glScaled(2.0, 2.0, 2.0);
		
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		int a,b,c;
		a = 0;

		gl.glNormal3d(faceNormals[0].getX(), faceNormals[0].getY(), faceNormals[0].getZ());

		for(int i = 0; i < 7; ++i){
			b = i;
			if(b == 6) b=1;
			if(i>1){
				gl.glNormal3d(faceNormals [i-2].getX(), faceNormals[i-2].getY(), faceNormals[i-2].getZ());
			}
			gl.glVertex3d(vertices[b].getX(), vertices[b].getY(), vertices[b].getZ());

		}
		gl.glEnd();
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		a = 6;
		gl.glNormal3d(-faceNormals[5].getX(), -faceNormals[5].getY(), -faceNormals[5].getZ());
		gl.glVertex3d(vertices[a].getX(), vertices[a].getY(), vertices[a].getZ());
		for(int i = 1; i < 7; ++i){
			b = i;
			if(b == 6) b=1;
			if(i>1){
				gl.glNormal3d(-faceNormals[5+i-2].getX(), -faceNormals[5+i-2].getY(), -faceNormals[5+i-2].getZ());
			}
			gl.glVertex3d(vertices[b].getX(), vertices[b].getY(), vertices[b].getZ());
		}
		gl.glEnd();
		gl.glPopMatrix();		
	}


	/**
	 * @return
	 */
	private double[] currentRotationMatrix() {
		double[]m = {
			pitchAxis.getX(), pitchAxis.getY(), pitchAxis.getZ(), 0,
			yawAxis.getX(), yawAxis.getY(), yawAxis.getZ(), 0,
			rollAxis.getX(), rollAxis.getY(), rollAxis.getZ(), 0,
			0, 0, 0, 1
			
		};
		return m;
	}

	
}



//
//
//package planetDefense.objects;
//
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.util.EventListener;
//import java.util.Vector;
//import javax.media.opengl.glu.*;
//import com.jogamp.opengl.util.texture.*;
//import java.util.Random;
//
//import javax.media.opengl.GL2;
//
//import planetDefense.geometry.Vector3d;
//
//public class AlienShip extends GameObject {
//        public boolean start = true;    
//        public double placement = 600;
//        private final double[] START_POS = {random(100,100), random(100,0), placement}; //Will Make Random
//
//        public AlienShip(GL2 gl){
//    
//
//        }
//        private double random(double min, double max){
//            Random rand =  new Random();
//            double randomNumber = rand.nextDouble() + min;
//            return randomNumber;
//            //double num = 0;
//            //return num;
//           //return min + (int)(Math.random() * ((max - min) + 1));
//        }
//        private void initializeVectors() {
//
//            position = new Vector3d(START_POS[0], START_POS[1], START_POS[2]);
//        }
//
//        public void display(GL2 gl,GLUquadric SOLID,GLU glu,Texture alienTexture, int number, double xPos, double yPos){
//       
//          
//            
//            //gl.glPushMatrix();
//            
//            alienTexture.enable(gl);
//            alienTexture.bind(gl);
//                       
//            gl.glPushMatrix();
//            gl.glTranslated(xPos, yPos,placement);//random(0,100),random(0,100),placement);
//            //gl.glTranslated(0,-5,400);
//            
//            
//            //gl.glPushMatrix();
//            gl.glBegin(gl.GL_TRIANGLES);
//            gl.glColor3f(1,0,1);
//            gl.glVertex3f(0,0,0);
//            gl.glVertex3f(0,0,-10);
//            gl.glVertex3f(10,0,-10);
//            gl.glVertex3f(0,0,0);
//            gl.glVertex3f(10,0,0);
//            gl.glVertex3f(10,0,-10);
//            
//            
//            
//            
//            gl.glColor3f(1,0,0);
//            gl.glVertex3f(10,0,-10);
//            gl.glVertex3f(10,0,0);
//            gl.glVertex3f(5,5,-5);
//            
//            gl.glColor3f(0,1,0);
//            gl.glVertex3f(0,0,0);
//            gl.glVertex3f(0,0,-10);
//            gl.glVertex3f(5,5,-5);
//            
//            gl.glColor3f(0,0,1);
//            gl.glVertex3f(0,0,-10);
//            gl.glVertex3f(10,0,-10);
//            gl.glVertex3f(5,5,-5);
//            
//            gl.glColor3f(1,1,0);
//            gl.glVertex3f(0,0,0);
//            gl.glVertex3f(10,0,0);
//            gl.glVertex3f(5,5,-5);
//            
//            
//            gl.glColor3f(1,1,1);
//            
//            gl.glPopMatrix();
//            alienTexture.disable(gl);
//            
//            //gl.glPopMatrix();
//      // }
//        }
//        public void update(long delta){
//            
//                placement -= 1;
//            
//        }
//
//
//}
