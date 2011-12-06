/**
 * 
 */
package planetDefense.objects;

import java.util.Vector;

import com.jogamp.graph.math.Quaternion;

import planetDefense.geometry.Matrix;
import planetDefense.geometry.Vector3d;

/**
 * @author Justin
 *
 */
public abstract class GameObject {
	
	
	Vector3d position = null;
	Vector3d rollAxis = null; // forward (doubles as direction vector)
	Vector3d pitchAxis = null; // left
	Vector3d yawAxis = null; // up
	
	double rotateX = 0.0;
	double rotateY = 0.0;
	
	Matrix rotationMat;
	Quaternion rotationQuat;

	double speed = 0;

	
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
	
//	
//	public Vector3d[] matrixInverse3d(Vector3d[] a){
//		double det = a.x.x*(a.z.z*a.y.y-a.z.y*a.y.z) -
//					a.y.x*(a.z.z*a.x.y-a.z.y*a.x.z) +
//					a.z.x*(a.y.z*a.x.y-a.y.y*a.x.z);
//		
//		return new Vector3d[]{
//				{det*(a.z.z*a.y.y-a.z.y*a.y.z),
//				det*(-(a.z.z*a.x.y-a.z.y*a.x.z)),
//				det*(a.y.z*a.x.y-a.y.y*a.x.z)},
//				
//				{det*(-(a.z.z*a.y.x-a.z.x*a.y.z)),
//				det*(a.z.z*a.x.x-a.z.x*a.x.z),
//				det*(-(a.y.z*a.x.x-a.y.x*a.x.z))},
//				
//				{det*(a.z.y*a.y.x-a.z.x*a.y.y),
//				det*(-(a.z.y*a.x.x-a.z.x*a.x.y)),
//				det*(a.y.y*a.x.x-a.y.x*a.x.y)}
//		};
//
//	}
	
	
	
	
	public Vector3d getPosition() {
		return position;
	}




	public void setPosition(Vector3d position) {
		this.position = position;
	}




	public Vector3d getRollAxis() {
		return rollAxis;
	}




	public void setRollAxis(Vector3d rollAxis) {
		this.rollAxis = rollAxis;
	}




	public Vector3d getPitchAxis() {
		return pitchAxis;
	}




	public void setPitchAxis(Vector3d pitchAxis) {
		this.pitchAxis = pitchAxis;
	}




	public Vector3d getYawAxis() {
		return yawAxis;
	}




	public void setYawAxis(Vector3d yawAxis) {
		this.yawAxis = yawAxis;
	}




	public Matrix getRotationMatrix() {
		return rotationMat;
	}



	public double getSpeed() {
		return speed;
	}




	public void setSpeed(double speed) {
		this.speed = speed;
	}




	public Vector3d[] getVertices() {
		return vertices;
	}




	public void setVertices(Vector3d[] vertices) {
		this.vertices = vertices;
	}




	public Vector3d[] getVertexNormals() {
		return vertexNormals;
	}




	public void setVertexNormals(Vector3d[] vertexNormals) {
		this.vertexNormals = vertexNormals;
	}




	public Vector3d[] getFaceNormals() {
		return faceNormals;
	}




	public void setFaceNormals(Vector3d[] faceNormals) {
		this.faceNormals = faceNormals;
	}




	public int getHealth() {
		return health;
	}




	public void setHealth(int health) {
		this.health = health;
	}




	public int getDamage() {
		return damage;
	}




	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	/**
	 * @return
	 */
	public double[] currentTransformationMatrix() {
		double[]m = {
			pitchAxis.getX(), pitchAxis.getY(), pitchAxis.getZ(), 0,
			yawAxis.getX(), yawAxis.getY(), yawAxis.getZ(), 0,
			rollAxis.getX(), rollAxis.getY(), rollAxis.getZ(), 0,
			position.getX(), position.getY(), position.getZ(), 1
			
		};
		return m;
	}

	/**
	 * @return
	 */
	public void calculateCurrentVertices() {
		// get current transformation matrix
		double[] transform = currentTransformationMatrix();
		Matrix transMat = new Matrix(4, 4);
		for(int i = 0; i < 4; ++i){
			for(int j = 0; j < 4; ++j){
				transMat.set(j, i, transform[4*i+j]);
			}
		}
		currentVerts = new Vector3d[vertices.length];
		for(int i = 0; i < vertices.length; ++i){
			currentVerts[i] = transMat.multiply(vertices[i]);
		}
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
			currentNorms[i] = rotationMat.multiply(faceNormals[i]);
		}
		return currentNorms;
	}
	

	/**
	 * @return
	 */
	private double[] currentRotatationMatrix() {
		double[]m = {
				pitchAxis.getX(), pitchAxis.getY(), pitchAxis.getZ(), 0,
				yawAxis.getX(), yawAxis.getY(), yawAxis.getZ(), 0,
				rollAxis.getX(), rollAxis.getY(), rollAxis.getZ(), 0,
				0, 0, 0, 1
				
			};
		return m;
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
	
	/**
	 * @return
	 */
	public Vector3d[] getCurrentVertices() {
		return currentVerts;
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


	public double[] getMaxProjectedVals() {
		return maxProjectedVals;
	}



}
