/**
 * 
 */
package planetDefense.objects;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.Vector;

import javax.media.opengl.GL2;

/**
 * @author Justin
 *
 */
public class UserShip extends GameObject implements KeyListener, MouseListener{
	
	private static final double MAX_SPEED = 2.0f;
	private static final double ROTATE_DELTA = 0.05;
	private static final double ACCELERATION = 0.3;
	
	private static final double[] START_POS = {0, 0, 500};

    private final float[] materialAmbient = {0.6f, 0.6f, 1.0f, 1.0f};
    private final float[] materialDiffuse = {0.2f, 0.2f, 0.3f, 1.0f};
    private final float[] materialSpecular = {0.2f, 0.2f, 0.2f, 1.0f};
	private final float[] materialShininess = {20.0f};

	private boolean leftKeyPressed;
	private boolean rightKeyPressed;
	private boolean upKeyPressed;
	private boolean downKeyPressed;
	private boolean thrust;
	
	public UserShip(GL2 gl){
		initializeVectors();
		createVertices();
		createNormals();
		// set material properties	
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, materialAmbient, 0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, materialDiffuse, 0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, materialSpecular, 0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, materialShininess, 0);
	}

	/**
	 * 
	 */
	private void initializeVectors() {
		rollAxis = new double[]{0, 0, -1}; // forward (doubles as direction vector)
		pitchAxis = new double[]{1, 0, 0}; // left
		yawAxis = new double[]{0, 1, 0}; // up
		position = new double[]{START_POS[0], START_POS[1], START_POS[2]};
	}

	/**
	 * 
	 */
	private void createNormals() {
		//Forward Triangle Fan (Nose of Ship)
		vertexNormals = new double[7][3];
		// initialize all normals to (0, 0, 0)
		for(int i = 0; i < vertexNormals.length; ++i){
			for(int j = 0; j < 3; ++j){
				vertexNormals[i][j] = 0;
			}
		}		
		faceNormals = new double[10][3];
		
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
			double[] faceNorm = faceNormal(vertices[a], vertices[b], vertices[c]); 
			faceNormals[i] = faceNorm;
			for(int j = 0; j < 3; ++j){
				vertexNormals[a][j] = faceNorm[j]+vertexNormals[a][j];
				vertexNormals[b][j] = faceNorm[j]+vertexNormals[b][j];
				vertexNormals[c][j] = faceNorm[j]+vertexNormals[c][j];
			}
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
			double[] faceNorm = this.faceNormal(vertices[a], vertices[b], vertices[c]);
			faceNormals[5+i] = faceNorm;
			// add adjacent faceNormals to the vertexNormals;
			for(int j = 0; j < 3; ++j){
				vertexNormals[a][j] = faceNorm[j]+vertexNormals[a][j];
				vertexNormals[b][j] = faceNorm[j]+vertexNormals[b][j];
				vertexNormals[c][j] = faceNorm[j]+vertexNormals[c][j];
			}
		}
		// normalize all the vertexNormals
		for(int i = 0; i < vertexNormals.length; ++i){
			vertexNormals[i] = normalize(vertexNormals[i]);
		}
		
	}

	/**
	 * 
	 */
	private void createVertices() {
		vertices = new double[7][];
		vertices = new double[7][];
		vertices[0] = new double[]{0, 0, -1.0};	//.add(new Float(0));
		vertices[1] = new double[]{-.4, 0, 0};
		vertices[2] = new double[]{0, .2, 0};
		vertices[3] = new double[]{.4, 0, 0};
		vertices[4] = new double[]{.2, -.1, 0};
		vertices[5] = new double[]{-.2, -.1, 0};
		vertices[6] = new double[]{0, 0, .2};		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		thrust = true;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		thrust = false;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		updateKeyPressed(e.getKeyCode(), true);
	}
	
	private void updateKeyPressed(int keyCode, boolean isPressed){
		switch(keyCode){
		case(KeyEvent.VK_A):
			leftKeyPressed = isPressed;
			break;
		case(KeyEvent.VK_D):
			rightKeyPressed = isPressed;
			break;
//		case(KeyEvent.VK_W):
//			upKeyPressed = isPressed;
//			break;
//		case(KeyEvent.VK_S):
//			downKeyPressed = isPressed;			
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		updateKeyPressed(e.getKeyCode(), false);		
	}

	public void update(long delta){
		if(leftKeyPressed || rightKeyPressed || upKeyPressed || downKeyPressed){
			rotateShip(delta);
		}
		if(thrust){
			speed += ACCELERATION;
			if(speed > MAX_SPEED){
				speed = MAX_SPEED;
			}
		} else {
			speed = 0;
		}
		for(int i = 0; i < 3; ++i){
			position[i] += rollAxis[i]*speed;
		}

		
	}
	
	/**
	 * @param delta
	 */
	private void rotateShip(long delta) {
		if(leftKeyPressed){
//			rotateLeft
			yaw(ROTATE_DELTA);
//			rotateDirectionVectorY(ROTATE_DELTA);
		} else if(rightKeyPressed){
			// rotate right
			yaw(-ROTATE_DELTA);
		}
		if(upKeyPressed){
			// rotate up
			pitch(ROTATE_DELTA);
		}else if(downKeyPressed){
			// rotate down
			pitch(-ROTATE_DELTA);
		}
	}


	/**
	 * @param radians
	 */
	private void pitch(double radians) {
		// rotate other two orientation axes about the pitch axis
		double[][] rotate = rotate(radians, pitchAxis);
		rollAxis = normalize(matrixMultiply(rotate, rollAxis)); 
		yawAxis = normalize(matrixMultiply(rotate, yawAxis));
		rotateX += radians;
//		appendRotationMatrix(rotate);
	}
	
	private void roll(double rotateDelta){
		// rotate other two orientation axes about the roll axis
		double[][] rotate = rotate(rotateDelta, rollAxis);
		yawAxis = matrixMultiply(rotate, yawAxis);
		pitchAxis = matrixMultiply(rotate, pitchAxis);
//		appendRotationMatrix(rotate);
	}
	
	private void yaw(double radians) {
		// rotate other two orientation axes about the yaw axis
		double[][] rotate = rotate(radians, yawAxis);
		rollAxis = matrixMultiply(rotate, rollAxis);
		pitchAxis = matrixMultiply(rotate, pitchAxis);
		rotateY += radians;
//		appendRotationMatrix(rotate);
	}
	
	/**
	 * @param rotate
	 */
	private void appendRotationMatrix(double[][] rotate) {
		double[][] newMatrix = new double[4][4];
		
		for(int i = 0; i < 4; ++i){
			for(int j = 0; j < 4; ++j){
				double sum = 0;
				for(int k = 0; k < 4; ++k){
					sum += rotate[k][i]*rotationMatrix[j][k];
				}
				newMatrix[j][i] = sum;
			}
		}
		rotationMatrix = newMatrix;
	}



	private double[][] rotate(double radians, double[] rotationVector){
		
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
		double u = rotationVector[0];
		double v = rotationVector[1];
		double w = rotationVector[2];
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
		return matrix;
	}



	/**
	 * @param q	- a 4x4 transformation vector
	 * @param v - a 3d vector
	 * @return - resulting vector of v transformed by q
	 */
	private double[] matrixMultiply(double[][] q, double[] v) {
		double[] returnV = new double[4];
		double[] pad = {v[0], v[1], v[2], 1};
		for(int i = 0; i < 4; ++i){
			double sum = 0.0f;
			for(int j = 0; j < 4; ++j){
				sum += q[i][j]*pad[j];
			}
			returnV[i] = sum;
		}
		double[] trim = {returnV[0], returnV[1], returnV[2]};
		return trim;
	}



	public void display(GL2 gl){
		gl.glPushMatrix();
		gl.glTranslated(position[0], position[1], position[2]);
		gl.glRotated(rotateY*180/Math.PI, yawAxis[0], yawAxis[1], yawAxis[2]);
//		gl.glRotated(rotateX*180/Math.PI, pitchAxis[0], pitchAxis[1], pitchAxis[2]);
//		rotateY(gl);
//		rotateX(gl);
//		double[] forward = {0, 0, -1};
//		double angle = Math.acos(dot(forward, rollAxis))*180/Math.PI;
//		double[] axis = cross(forward, rollAxis);

//		gl.glRotated(angle, axis[0], axis[1], axis[2]);

//		double[] glRotationMatrix = currentRotationMatrix();
//		gl.glMultMatrixd(glRotationMatrix, 0);


		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		int a,b,c;
		a = 0;
//		gl.glNormal3d(vertexNormals[a][0], vertexNormals[a][1], vertexNormals[a][2]);
		gl.glNormal3d(faceNormals [0][0], faceNormals[0][1], faceNormals[0][2]);
//		gl.glVertex3d(vertices[a][0], vertices[a][1], vertices[a][2]);
		for(int i = 0; i < 7; ++i){
			b = i;
			if(b == 6) b=1;
//			gl.glNormal3d(vertexNormals[b][0], vertexNormals[b][1], vertexNormals[b][2]);
			if(i>1){
				gl.glNormal3d(faceNormals [i-2][0], faceNormals[i-2][1], faceNormals[i-2][2]);
			}
			gl.glVertex3d(vertices[b][0], vertices[b][1], vertices[b][2]);

		}
		gl.glEnd();
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		a = 6;
//		gl.glNormal3d(vertexNormals[a][0], vertexNormals[a][1], vertexNormals[a][2]);
		gl.glNormal3d(-faceNormals[5][0], -faceNormals[5][1], -faceNormals[5][2]);
		gl.glVertex3d(vertices[a][0], vertices[a][1], vertices[a][2]);
		for(int i = 1; i < 7; ++i){
			b = i;
			if(b == 6) b=1;
//			gl.glNormal3d(vertexNormals[b][0], vertexNormals[b][1], vertexNormals[b][2]);
			if(i>1){
				gl.glNormal3d(-faceNormals[5+i-2][0], -faceNormals[5+i-2][1], -faceNormals[5+i-2][2]);
			}
			gl.glVertex3d(vertices[b][0], vertices[b][1], vertices[b][2]);
		}
		gl.glEnd();
		gl.glPopMatrix();		
	}



	/**
	 * Rotate about the y axis
	 */
	private void rotateY(GL2 gl) {
		// project current direction vector into x-z plane
//		double v[] = rollAxis;	// current direction vector
//		double[] n = {0, 1, 0}; // normal to the x-z plane
//		double[] u = new double[3];
//		double dot = dot(v, n);
//		for(int i = 0; i < 3; ++i){
//			u[i] = v[i] - dot*n[i];
//		}
//		// find angle 
//		double[] forward = {0, 0, -1};
//		double angle = angleBetween(u, forward);
//		// rotate 
//		gl.glRotated(angle, 0, 1, 0);
		
		double[] u = new double[]{rollAxis[0], 0, rollAxis[2]};
		double[] v = new double[]{0, 0, -1};
		double angle = angleBetween(u, v);
		// rotate 
		gl.glRotated(angle, 0, 1, 0);
		
		
//		double[] axis = cross(forward, rollAxis);
//		gl.glTranslatef(300, 0, 0);
//		gl.glRotated(angle, axis[0], axis[1], axis[2]);
		
	}



	/**
	 * @param gl 
	 * 
	 */
	private void rotateX(GL2 gl) {
//		// project current direction vector into y-z plane
//		double v[] = rollAxis;	// current direction vector
//		double[] n = {1, 0, 0}; // normal to the y-z plane
//		double[] u = new double[3];
//		double dot = dot(v, n);
//		for(int i = 0; i < 3; ++i){
//			u[i] = v[i] - dot*n[i];
//		}
//		// find angle 
//		double[] forward = {0, 0, -1};
		double[] u = new double[]{0, rollAxis[1], rollAxis[2]};
		double[] v = new double[]{0, 0, -1};
		double angle = angleBetween(u, v);
		// rotate 
		gl.glRotated(angle, 1, 0, 0);
		
	}



	/**
	 * @return
	 */
	private double[] currentRotationMatrix() {
		double[] a = rollAxis;
		double[] b = new double[]{0, 0, -1};
		double[] rotateAxis = cross(a, b);
		rotateAxis = normalize(rotateAxis);
		double degrees = Math.acos(dot(a,b)/(magnitude(a)*magnitude(b)));
		double[][] rotation = rotate(degrees, rotateAxis);
		double[] matrix = 
		{
			rotation[0][0], rotation[1][0], rotation[2][0], rotation[3][0],
			rotation[0][1], rotation[1][1], rotation[2][1], rotation[3][1],	
			rotation[0][2], rotation[1][2], rotation[2][2], rotation[3][2],	
			rotation[0][3], rotation[1][3], rotation[2][3], rotation[3][3]
		};
		return matrix;
	}
	

}
