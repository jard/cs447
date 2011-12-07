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
import com.jogamp.opengl.util.gl2.GLUT;

import planetDefense.geometry.Matrix;
import planetDefense.geometry.Quaternion;
import planetDefense.geometry.Vector3d;

/**
 * @author Justin
 *
 */
public class UserShip extends GameObject{
	
	private static final double MAX_SPEED = 0.3;
	private static final double ROTATE_DELTA = Math.PI/4;
	private static final double ACCELERATION = 0.5;
	
	private static final double[] START_POS = {0, 0, 100};
	private static final double YAW_FORCE = 0.1;

    private final float[] materialAmbient = {0.6f, 0.6f, 1.0f, 1.0f};
    private final float[] materialDiffuse = {0.2f, 0.2f, 0.3f, 1.0f};
    private final float[] materialSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float[] materialShininess = {80.0f};

	private boolean leftKeyPressed;
	private boolean rightKeyPressed;
	private boolean upKeyPressed;
	private boolean downKeyPressed;
	private boolean thrust;
	
//	private Quaternion rotation;
	private boolean cwKeyPressed;
	private boolean ccwKeyPressed;
	private int[] vertexBufferObjects;
	private boolean firing;
	private boolean stopKeyPressed;
	private boolean showNorms;
	public long score;

	
	
	
	public UserShip(double mass){
		super(mass);
		createVertices();
		createNormals();
		initializeState();
		this.health = 100;
		this.damage = 0;
//		loadVBOs(gl);
//		rotation = new Quaternion(1, 0, 0, 0);
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
			Vector3d faceNorm = this.faceNormal(vertices[a], vertices[c], vertices[b]);
			faceNormals[5+i] = faceNorm;
//			if(i > 6){
//				faceNormals[5+i] = Vector3d.scale(-1, faceNorm);
//			}
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

	public void display(GL2 gl){
		if(destroyed){
			drawExplosion(gl);
		} else {
		
			// set material properties	
			gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, materialAmbient, 0);
			gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, materialDiffuse, 0);
			gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, materialSpecular, 0);
			gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, materialShininess, 0);
			
			
			if(damaged){
				if((damageCount / 100) % 2 == 0){
					gl.glColor3f(1, 0, 0);
				} else {
					gl.glColor3f(1, 1, 1);
				}
				gl.glDisable(GL2.GL_LIGHTING);
			}		
			
			gl.glPushMatrix();
	
	//		gl.glTranslated(position.getX(), position.getY(), position.getZ());
			gl.glMultMatrixd(getCurrentTransformationMatrix(), 0);
			
			GLUT glut = new GLUT();
			gl.glColor3d(1.0, 0, 0);
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glRasterPos3d(1.5, 1.1, 0);
			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, "SCORE: " + score);
	//		gl.glScaled(2.0, 2.0, 2.0);
			gl.glEnable(GL2.GL_LIGHTING);
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
			gl.glNormal3d(faceNormals[5].getX(), faceNormals[5].getY(), faceNormals[5].getZ());
			gl.glVertex3d(vertices[a].getX(), vertices[a].getY(), vertices[a].getZ());
			for(int i = 1; i < 7; ++i){
				b = i;
				if(b == 6) b=1;
				if(i>1){
					gl.glNormal3d(faceNormals[5+i-2].getX(), faceNormals[5+i-2].getY(), faceNormals[5+i-2].getZ());
				}
				gl.glVertex3d(vertices[b].getX(), vertices[b].getY(), vertices[b].getZ());
			}
			gl.glEnd();
	
			
			// using VBOs
	//		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
	//		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBufferObjects[0]);
	//		gl.glVertexPointer(3, GL2.GL_DOUBLE, 0, 0);
	//
	//		// bind buffer containing normals
	//		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
	//		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBufferObjects[1]);
	//		gl.glNormalPointer(GL2.GL_DOUBLE, 0, 0);
	//		
	//
	//		
	//		gl.glDrawArrays(GL2.GL_TRIANGLE_FAN, 0, 7);
	//		gl.glDrawArrays(GL2.GL_TRIANGLE_FAN, 8, 7);
			
			
	
			
			if(showNorms){
				drawNorms(gl);
			}
			gl.glPopMatrix();		
		}
	}

	/**
	 * @param vector3d
	 * @param vector3d2
	 */
	private void drawNorm(GL2 gl, Vector3d vertex, Vector3d norm) {
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(vertex.getX(), vertex.getY(), vertex.getZ());
		Vector3d end = Vector3d.add(vertex, Vector3d.scale(0.1, norm));
		gl.glVertex3d(end.getX(), end.getY(), end.getZ());
		gl.glEnd();
		
	}

	/**
	 * @param gl
	 */
	private void drawNorms(GL2 gl) {
		gl.glColor3f(1, 0, 0);
		gl.glDisable(GL2.GL_LIGHTING);
		// 0,1,2 faceNorm 0
		drawNorm(gl, vertices[0], faceNormals[0]);
		drawNorm(gl, vertices[1], faceNormals[0]);
		drawNorm(gl, vertices[2], faceNormals[0]);
//		 0,2,3 faceNorm 1
		drawNorm(gl, vertices[0], faceNormals[1]);
		drawNorm(gl, vertices[2], faceNormals[1]);
		drawNorm(gl, vertices[3], faceNormals[1]);
//		 0,3,4 faceNorm 2
		drawNorm(gl, vertices[0], faceNormals[2]);
		drawNorm(gl, vertices[3], faceNormals[2]);
		drawNorm(gl, vertices[4], faceNormals[2]);
//		 0,4,5 faceNorm 3
		drawNorm(gl, vertices[0], faceNormals[3]);
		drawNorm(gl, vertices[4], faceNormals[3]);
		drawNorm(gl, vertices[5], faceNormals[3]);
//		 0,5,1 faceNorm 4
		drawNorm(gl, vertices[0], faceNormals[4]);
		drawNorm(gl, vertices[5], faceNormals[4]);
		drawNorm(gl, vertices[1], faceNormals[4]);
		// 6,1,2 faceNorm 5
		drawNorm(gl, vertices[6], faceNormals[5]);
		drawNorm(gl, vertices[1], faceNormals[5]);
		drawNorm(gl, vertices[2], faceNormals[5]);
		// 6,2,3 faceNorm 6
		drawNorm(gl, vertices[6], faceNormals[6]);
		drawNorm(gl, vertices[2], faceNormals[6]);
		drawNorm(gl, vertices[3], faceNormals[6]);
		// 6,3,4 faceNorm 7
		drawNorm(gl, vertices[6], faceNormals[7]);
		drawNorm(gl, vertices[3], faceNormals[7]);
		drawNorm(gl, vertices[4], faceNormals[7]);
		// 6,4,5 faceNorm 8
		drawNorm(gl, vertices[6], faceNormals[8]);
		drawNorm(gl, vertices[4], faceNormals[8]);
		drawNorm(gl, vertices[5], faceNormals[8]);
//		// 6,5,1 faceNorm 9
		drawNorm(gl, vertices[6], faceNormals[9]);
		drawNorm(gl, vertices[5], faceNormals[9]);
		drawNorm(gl, vertices[1], faceNormals[9]);
		
	}

	/* (non-Javadoc)
	 * @see planetDefense.objects.GameObject#initialize()
	 */
	@Override
	public void initializeState() {
		position = new Vector3d(START_POS[0], START_POS[1], START_POS[2]);
		pitchAxis = new Vector3d(1, 0, 0); // left
		yawAxis = new Vector3d(0, 1, 0); // up
		rollAxis = new Vector3d(0, 0, 1); // forward (doubles as direction vector)
		rotation = new Matrix(3,3);
		loadRotationMatrix();
		
		momentum = new Vector3d(0, 0, 0);
		angular_momentum = new Vector3d(0, 0, 0);
		
		velocity = new Vector3d(0, 0, 0);
		angular_velocity = new Vector3d(0, 0, 0);

		
		force = new Vector3d(0, 0, 0);
		torque = new Vector3d(0, 0 ,0);

	}


	/**
	 * 
	 */
	private void loadVBOs(GL2 gl) {
		vertexBufferObjects = new int[3];
		gl.glGenBuffers(3, vertexBufferObjects, 0);
		
		
		// load vertices
		double verts[] = new double[14*3];
		// first seven vertices make the forward triangle fan
		for(int i = 0; i < 6; ++i){
			verts[i*3] = vertices[i].getX();
			verts[i*3+1] = vertices[i].getY();
			verts[i*3+2] = vertices[i].getZ();
		}
		verts[6*3] = vertices[1].getX();
		verts[6*3+1] = vertices[1].getY();
		verts[6*3+2] = vertices[1].getZ();		
		
		// next seven vertices make the rear triangle fan
		verts[7*3] = vertices[6].getX();
		verts[7*3+1] = vertices[6].getY();
		verts[7*3+2] = vertices[6].getZ();
		for(int i = 1; i < 6; ++i){
			verts[(8+i)*3] = vertices[i].getX();
			verts[(8+i)*3+1] = vertices[i].getY();
			verts[(8+i)*3+2] = vertices[i].getZ();
		}
		verts[13*3] = vertices[1].getX();
		verts[13*3+1] = vertices[1].getY();
		verts[13*3+2] = vertices[1].getZ();
		
		// load into buffer		
		DoubleBuffer vertBuff = DoubleBuffer.allocate(verts.length);
		vertBuff.put(verts);
		vertBuff.rewind();
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferObjects[0]); // bind vertex buffer
		gl.glBufferData(GL.GL_ARRAY_BUFFER, verts.length*Buffers.SIZEOF_DOUBLE, vertBuff, GL.GL_STATIC_DRAW);
		
		
		// load normals
		double norms[] = new double[verts.length];
		// first seven normals make the forward triangle fan
		for(int i = 0; i < 6; ++i){
			norms[i*3] = vertexNormals[i].getX();
			norms[i*3+1] = vertexNormals[i].getY();
			norms[i*3+2] = vertexNormals[i].getZ();
		}
		norms[6*3] = vertexNormals[1].getX();
		norms[6*3+1] = vertexNormals[1].getY();
		norms[6*3+2] = vertexNormals[1].getZ();
		
		
		// next seven normals make the rear triangle fan
		norms[7*3] = vertexNormals[6].getX();
		norms[7*3+1] = vertexNormals[6].getY();
		norms[7*3+2] = vertexNormals[6].getZ();
		for(int i = 1; i < 6; ++i){
			norms[(8+i)*3] = vertexNormals[i].getX();
			norms[(8+i)*3+1] = vertexNormals[i].getY();
			norms[(8+i)*3+2] = vertexNormals[i].getZ();
		}
		norms[13*3] = vertexNormals[1].getX();
		norms[13*3+1] = vertexNormals[1].getY();
		norms[13*3+2] = vertexNormals[1].getZ();
		// load into buffer		
		DoubleBuffer normBuff = DoubleBuffer.allocate(norms.length);
		normBuff.put(norms);
		normBuff.rewind();
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferObjects[1]); // bind vertex buffer
		gl.glBufferData(GL.GL_ARRAY_BUFFER, norms.length*Buffers.SIZEOF_DOUBLE, normBuff, GL.GL_STATIC_DRAW);
		

		
	}
		
	/**
	 * @param radians
	 */
	private void pitch(double radians) {
		// rotate other two orientation axes about the pitch axis
		Matrix rotationMatrix = rotate(radians, pitchAxis);
		rollAxis = Vector3d.normalize(Matrix.multiply4(rotationMatrix, rollAxis)); 
		yawAxis = Vector3d.normalize(Matrix.multiply4(rotationMatrix, yawAxis));
		
	}
	
	private void roll(double radians){
		// rotate other two orientation axes about the roll axis
		Matrix rotationMatrix = rotate(radians, rollAxis);
		pitchAxis = Vector3d.normalize(Matrix.multiply4(rotationMatrix, pitchAxis)); 
		yawAxis = Vector3d.normalize(Matrix.multiply4(rotationMatrix, yawAxis));
		
		

	}
	

	/**
	 * @param radians
	 */
//	private void pitch(double degrees) {
//		// rotate other two orientation axes about the pitch axis
//		rotation = rotation.rotate(degrees, pitchAxis);				
//		rollAxis = rotation.rotateVector(rollAxis); 
//		yawAxis = rotation.rotateVector(yawAxis);
//	}
//	
//	private void roll(double degrees){
//		// rotate other two orientation axes about the roll axis
//		rotation = rotation.rotate(degrees, rollAxis);				
//		pitchAxis = rotation.rotateVector(pitchAxis); 
//		yawAxis = rotation.rotateVector(yawAxis);
//	}
//	
//	private void yaw(double degrees) {
//		// rotate other two orientation axes about the yaw axis
//		rotation = rotation.rotate(degrees, yawAxis);				
//		rollAxis = rotation.rotateVector(rollAxis); 
//		pitchAxis = rotation.rotateVector(pitchAxis);
//	}
	
	
	
	
	
	


	private Matrix rotate(double radians, Vector3d rotationVector){
		
		// using quaternions
//		double q0 = Math.cos(radians/2);
//		double q1 = Math.sin(radians/2)*rotationVector[0];
//		double q2 = Math.sin(radians/2)*rotationVector[1];
//		double q3 = Math.sin(radians/2)*rotationVector[2];
//		double[][] Q = {
//				{((q0*q0+q1*q1-q2*q2-q3*q3)), 2*(q1*q2-q0*q3), 2*(q1*q3+q0*q2)},
//				{2*(q2*q1+q0*q3), (q0*q0-q1*q1+q2*q2-q2*q3), 2*(q2*q3-q0*q1)},
//				{2*(q3*q1-q0*q2), 2*(q3*q2+q0*q1), (q0*q0-q1*q1-q2*q2+q3*q3)},
//		};
//		return Q;
		
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




	/**
	 * @param delta
	 */
	private void rotateShip(long delta) {
		double scale = delta/1000.0;
		if(leftKeyPressed){
//			rotateLeft
			yaw(scale*ROTATE_DELTA);
//			rotateDirectionVectorY(ROTATE_DELTA);
		} else if(rightKeyPressed){
			// rotate right
			yaw(-scale*ROTATE_DELTA);
		}
		if(upKeyPressed){
			// rotate up
			pitch(scale*ROTATE_DELTA);
		}else if(downKeyPressed){
			// rotate down
			pitch(-scale*ROTATE_DELTA);
		}
		if(cwKeyPressed){
			roll(-scale*ROTATE_DELTA);
		} else if(ccwKeyPressed){
			roll(scale*ROTATE_DELTA);
		}
	}


	public void update(long delta){
		if(leftKeyPressed || rightKeyPressed || upKeyPressed || downKeyPressed || cwKeyPressed || ccwKeyPressed){
			rotateShip(delta);
		}
		if(stopKeyPressed){
			velocity.clear();
			speed = 0;
		}
//		updateState(delta);
		if(thrust){
			speed += ACCELERATION;
			if(speed > MAX_SPEED){
				speed = MAX_SPEED;
			}
		} else {
			speed = 0;
		}
		velocity = Vector3d.add(velocity, Vector3d.scale(-speed*delta/1000.0, rollAxis));
		position = Vector3d.add(position, velocity);
		momentum = Vector3d.scale(mass, velocity);

		if(damaged){
			damageCount += delta;
			if(damageCount >= 1000){
				damaged = false;
			}
		}
		
	}

	/**
	 * 
	 */
	private void clampVelocity() {
		double magnitude = Vector3d.magnitude(velocity);
		if(magnitude > 1){
			velocity = Vector3d.normalize(velocity);
		}
		
	}

	public void updateKeyPressed(int keyCode, boolean isPressed){
		switch(keyCode){
		case(KeyEvent.VK_A):
			leftKeyPressed = isPressed;
			break;
		case(KeyEvent.VK_D):
			rightKeyPressed = isPressed;
			break;
		case(KeyEvent.VK_W):
			upKeyPressed = isPressed;
			break;
		case(KeyEvent.VK_S):
			downKeyPressed = isPressed;
			break;
		case(KeyEvent.VK_Q):
			ccwKeyPressed = isPressed;
			break;
		case(KeyEvent.VK_E):
			cwKeyPressed = isPressed;
			break;
		case(KeyEvent.VK_SPACE):
			stopKeyPressed = isPressed;
			break;
		}
	}


	private void yaw(double radians) {
		// rotate other two orientation axes about the yaw axis
		Matrix rotationMatrix = rotate(radians, yawAxis);
		rollAxis = Vector3d.normalize(Matrix.multiply4(rotationMatrix, rollAxis)); 
		pitchAxis = Vector3d.normalize(Matrix.multiply4(rotationMatrix, pitchAxis));
	}

	/**
	 * @return
	 */
	public boolean isFiring() {
		return firing;
	}

	/**
	 * @param b 
	 * 
	 */
	public void thrust(boolean b) {
		thrust = b;		
	}

	/**
	 * 
	 */
	public void toggleNorms() {
		showNorms = !showNorms;
		
	}










	
}
