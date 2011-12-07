/**
 * 
 */
package planetDefense.objects;

import java.util.Vector;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.graph.math.Quaternion;

import planetDefense.geometry.Matrix;
import planetDefense.geometry.Vector3d;

/**
 * @author Justin
 *
 */
public abstract class GameObject {
	// constants
	double mass;
	// state variables;
	Vector3d position = null;	// x(t)
	Vector3d rollAxis = null; // forward (doubles as direction vector)
	Vector3d pitchAxis = null; // left
	Vector3d yawAxis = null; // up	
	Vector3d velocity;	// v(t)
	Vector3d momentum;	// P(t)
	Vector3d acceleration;
	
	
	Matrix Ibody;	// 3x3 matrix for inertia tensor 
	Matrix Ibodyinv; // inverse of inertia tensor
	

	
	Matrix rotation;	// R(t)

	Vector3d angular_momentum; // L(t)
	
	// auxiliary variables;
	Matrix Iinv; // I^-1(t)

	Vector3d angular_velocity;	// omega(t)
	
	// computed quantities
	Vector3d force;	// F(t)
	Vector3d torque;	// tau(t)
	protected double speed;
	
//	Quaternion rotationQuat;

//	double speed = 0;

	
	Vector3d[] vertices = null;
	Vector3d[] vertexNormals = null;
	Vector3d[] faceNormals = null;
//	Vector3d[] textureCoordinates;
	
	int health;
	int damage;
	private double[][] projectedVertScales;
	private double[] minProjectedVals;
	private double[] maxProjectedVals;
	private Vector3d[] currentVerts;
	protected boolean destroyed;
	protected long explosionCount;
	private double explosionRadius;
	public boolean exploded;
	protected boolean damaged;
	protected int damageCount;

	
	public GameObject(double mass){
		this.mass = mass;
//		this.Ibodyinv = Matrix.inverse33(I);
		initializeState();
	}
	
	/**
	 * @return
	 */
	public void calculateCurrentVertices() {
		// get current transformation matrix
		double[] transform = getCurrentTransformationMatrix();
		Matrix transMat = new Matrix(4, 4);
		for(int i = 0; i < 4; ++i){
			for(int j = 0; j < 4; ++j){
				transMat.set(j, i, transform[4*i+j]);
			}
		}
		currentVerts = new Vector3d[vertices.length];
		for(int i = 0; i < vertices.length; ++i){
			currentVerts[i] = Matrix.multiply4(transMat, vertices[i]);
		}
	}

	/**
	 * @return
	 */
	public double[] currentRotatationMatrix() {
		double[]m = {
				pitchAxis.getX(), pitchAxis.getY(), pitchAxis.getZ(), 0,
				yawAxis.getX(), yawAxis.getY(), yawAxis.getZ(), 0,
				rollAxis.getX(), rollAxis.getY(), rollAxis.getZ(), 0,
				0, 0, 0, 1
				
			};
		return m;
	}
	
	public Vector3d getMomentum(){
		return momentum;
	}
	
	public void applyForce(Vector3d f){
		force = Vector3d.add(force, f);
	}
	
	public void applyTorque(Vector3d t){
		torque = Vector3d.add(torque, t);
	}
	
	/**
	 * @param delta - time between frames
	 * this method should only be called after all collisions have been performed and all
	 * forces and torques have been applied for this frame
	 */
	public void updateState(long delta){
		double frameScale = delta/1000.0;
		// update position
		position = Vector3d.add(position, Vector3d.scale(frameScale, velocity));
		
		// update linear momentum
		momentum = Vector3d.add(momentum, Vector3d.scale(frameScale, force));
		
		// update angular momentum
		angular_momentum = Vector3d.add(angular_momentum, Vector3d.scale(frameScale, torque));
				
		// update velocity using v = p/m
		velocity = Vector3d.scale(1.0/mass, momentum);

		// update Iinv using R * Ibody^-1 * R^T
		Iinv = Matrix.multiply(Matrix.multiply(rotation, Ibodyinv), Matrix.transpose(rotation));
		
		// update angular velocity using omega = I^-1 * L
		angular_velocity = Matrix.multiply3(Iinv, angular_momentum);
		
		// update rotation matrix
		rotation = normalize(Matrix.add(Matrix.multiply(Matrix.star(angular_velocity), rotation), rotation));
		

		
		// clear force and torque
		force.clear();
		torque.clear();
	}
	
	
	/**
	 * @param add
	 * @return
	 */
	private Matrix normalize(Matrix m) {
		Vector3d x = new Vector3d(m.get(0,0), m.get(1,0), m.get(2,0));
		Vector3d y = new Vector3d(m.get(0,1), m.get(1,1), m.get(2,1));
		Vector3d z = new Vector3d(m.get(0,2), m.get(1,2), m.get(2,2));
		x = Vector3d.normalize(x);
		y = Vector3d.normalize(y);
		z = Vector3d.normalize(z);
		Matrix newM = new Matrix(3, 3);
		newM.set(0,0, x.getX());
		newM.set(1, 0, x.getY());
		newM.set(2, 0, x.getZ());
		newM.set(0,1, y.getX());
		newM.set(1,1, y.getY());
		newM.set(2,1, y.getZ());
		newM.set(0,2, z.getX());
		newM.set(1,2, z.getY());
		newM.set(2,2, z.getZ());
		return newM;
	}

	/**
	 * Calculates the normal by taking the cross product of the 3D vectors
	 * (v1-v2)x(v1-v3).
	 * 
	 * @param v1 - 3D vector
	 * @param v2 - 3D vector
	 * @param v3 - 3D vector
	 * @return - 3D vector representing the normalized vector (v1-v2)x(v1-v3)
	 */
//	protected Vector3d faceNormal(Vector3d v1,
//										Vector3d v2,
//										Vector3d v3) {
//		// should only be called after vertices have been created
//		assert(vertices != null);
//		Vector3d A = new Vector3d();
//		A.add(new Float(v1.get(0).floatValue() - v2.get(0).floatValue()));
//		A.add(new Float(v1.get(1).floatValue() - v2.get(1).floatValue()));
//		A.add(new Float(v1.get(2).floatValue() - v2.get(2).floatValue()));
//		Vector3d B = new Vector3d();
//		B.add(new Float(v1.get(0).floatValue() - v3.get(0).floatValue()));
//		B.add(new Float(v1.get(1).floatValue() - v3.get(1).floatValue()));
//		B.add(new Float(v1.get(2).floatValue() - v3.get(2).floatValue()));		
//		return normalize(cross (A, B));
//	}
	
	protected Vector3d faceNormal(Vector3d v1,
			Vector3d v2,
			Vector3d v3) {
		// should only be called after vertices have been created
		assert(vertices != null);
		Vector3d a = new Vector3d(v1.getX() - v2.getX(),
								v1.getY() - v2.getY(),
								v1.getZ() - v2.getZ());
		Vector3d b = new Vector3d(v1.getX() - v3.getX(),
								v1.getY() - v3.getY(),
								v1.getZ() - v3.getZ());
		return Vector3d.normalize(Vector3d.cross(a, b));
	}




	/**
	 * @return
	 */
	public Vector3d[] getCurrentFaceNormals() {
		double[] rotate = currentRotatationMatrix();
		Matrix rotationMat = new Matrix(4, 4);
		for(int i = 0; i < 4; ++i){
			for(int j = 0; j < 4; ++j){
				rotationMat.set(j, i, rotate[4*i+j]);
			}
		}
		Vector3d[] currentNorms = new Vector3d[faceNormals.length];
		for(int i = 0; i < faceNormals.length; ++i){
			currentNorms[i] = Matrix.multiply4(rotationMat, faceNormals[i]);
		}
		return currentNorms;
	}




	/**
	 * @return
	 */
	public double[] getCurrentTransformationMatrix() {
		double[]m = {
			pitchAxis.getX(), pitchAxis.getY(), pitchAxis.getZ(), 0,
			yawAxis.getX(), yawAxis.getY(), yawAxis.getZ(), 0,
			rollAxis.getX(), rollAxis.getY(), rollAxis.getZ(), 0,
			position.getX(), position.getY(), position.getZ(), 1
			
		};
		return m;
//		double[] n = {
//			rotation.get(0, 0), rotation.get(1, 0), rotation.get(2, 0), 0,
//			rotation.get(0, 1), rotation.get(1, 1), rotation.get(2, 1), 0,
//			rotation.get(0, 2), rotation.get(1, 2), rotation.get(2, 2), 0,
//			position.getX(), position.getY(), position.getZ(), 1
//		};
//		return n;
	}




	/**
	 * @return
	 */
	public Vector3d[] getCurrentVertices() {
		return currentVerts;
	}




	public int getDamage() {
		return damage;
	}




	public Vector3d[] getFaceNormals() {
		return faceNormals;
	}




	public int getHealth() {
		return health;
	}




	/**
	 * @param vals
	 * @return
	 */
	public double getMax(double[] vals) {
		double max = vals[0];
		for(int i = 1; i < vals.length; ++i){
			if(vals[i] > max){
				max = vals[i];
			}
		}
		return max;
	}




	public double[] getMaxProjectedVals() {
		return maxProjectedVals;
	}

	/**
	 * @param damage
	 */
	public void inflictDamage(int damage) {
		if(damage <= 0 || this.damaged){
			return;
		}
		this.health -= damage;
		if(health <= 0){
			explode();
		}
		this.damaged = true;
		this.damageCount = 0;
		
	}

//	public double getSpeed() {
//		return speed;
//	}
//
//
//
//
//	public void setSpeed(double speed) {
//		this.speed = speed;
//	}




	/**
	 * @param vals
	 * @return
	 */
	public double getMin(double[] vals) {
		double min = vals[0];
		for(int i = 1; i < vals.length; ++i){
			if(vals[i] < min){
				min = vals[i];
			}
		}
		return min;
	}




	public double[] getMinProjectedVals() {
		return minProjectedVals;
	}




	public Vector3d getPitchAxis() {
		return pitchAxis;
	}




	public Vector3d getPosition() {
		return position;
	}




	/**
	 * @param norm
	 * @param currentVerts
	 * @return
	 */
	public double[] getProjectedValues(Vector3d norm) {
		double[] scales = new double[currentVerts.length];
		int i = 0;
		for(Vector3d vert : currentVerts){
			scales[i] = Vector3d.dot(vert, norm);
			++i;
		}
		return scales;
	}




	public Vector3d getRollAxis() {
		return rollAxis;
	}




	public Matrix getRotationMatrix() {
		return rotation;
	}




	public Vector3d[] getVertexNormals() {
		return vertexNormals;
	}




	public Vector3d[] getVertices() {
		return vertices;
	}




	public Vector3d getYawAxis() {
		return yawAxis;
	}
	
	public abstract void initializeState();

	/**
	 * 
	 */
	public void loadRotationMatrix() {
		// pitchAxis in leftmost column
		rotation.set(0, 0, pitchAxis.getX());
		rotation.set(1, 0, pitchAxis.getY());
		rotation.set(2, 0, pitchAxis.getZ());
		
		// yawAxis in center column
		rotation.set(0, 1, yawAxis.getX());
		rotation.set(1, 1, yawAxis.getY());
		rotation.set(2, 1, yawAxis.getZ());
		
		// rollAxis in right column
		rotation.set(0, 2, rollAxis.getX());
		rotation.set(1, 2, rollAxis.getY());
		rotation.set(2, 2, rollAxis.getZ());		
	}
	


	/**
	 * 
	 */
	public void projectVertices() {
		calculateCurrentVertices();
		Vector3d[] transformedNorms = getCurrentFaceNormals();
		// for each face normal
		projectedVertScales = new double[transformedNorms.length][];
		minProjectedVals = new double[transformedNorms.length];
		maxProjectedVals = new double[transformedNorms.length];
		for(int i = 0; i < transformedNorms.length; ++i){
			projectedVertScales[i] = getProjectedValues(transformedNorms[i]);
			minProjectedVals[i] = getMin(projectedVertScales[i]);
			maxProjectedVals[i] = getMax(projectedVertScales[i]);
		}
		
	}
	

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setFaceNormals(Vector3d[] faceNormals) {
		this.faceNormals = faceNormals;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}

	public void setPitchAxis(Vector3d pitchAxis) {
		this.pitchAxis = pitchAxis;
	}
	
	public void setPosition(Vector3d position) {
		this.position = position;
	}
	public void setRollAxis(Vector3d rollAxis) {
		this.rollAxis = rollAxis;
	}

	public void setVertexNormals(Vector3d[] vertexNormals) {
		this.vertexNormals = vertexNormals;
	}


	public void setVertices(Vector3d[] vertices) {
		this.vertices = vertices;
	}

	public void setYawAxis(Vector3d yawAxis) {
		this.yawAxis = yawAxis;
	}

	/**
	 * @return
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * @return
	 */
	public Vector3d getVelocity() {
		return velocity;
	}

	/**
	 * @param add
	 */
	public void setVelocity(Vector3d v) {
		velocity = v;		
	}
	
	/**
	 * 
	 */
	public void explode() {
		this.destroyed = true;
		this.explosionRadius = 0;
		this.explosionCount = 0;
		
	}
	
	/**
	 * 
	 */
	protected void drawExplosion(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslated(position.getX(), position.getY(), position.getZ());
		GLU glu = new GLU();
		gl.glColor4d(1.0, 0, 0, 1.0);
		gl.glDisable(GL2.GL_LIGHTING);
        GLUquadric explosion = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle( explosion, GLU.GLU_FILL);
        glu.gluSphere(explosion, explosionRadius, 5, 5);
		explosionRadius += 0.1;
		if(explosionRadius > 1){
			this.exploded = true;
		}
		gl.glPopMatrix();
		
	}

}
