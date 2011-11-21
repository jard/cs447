/**
 * 
 */
package planetDefense.objects;

import java.util.Vector;

/**
 * @author Justin
 *
 */
public abstract class GameObject {
	
	
	double[] position = null;
	double[] rollAxis = null; // forward (doubles as direction vector)
	double[] pitchAxis = null; // left
	double[] yawAxis = null; // up
	
	double rotateX = 0.0;
	double rotateY = 0.0;
	
	double[][] rotationMatrix = {
		{1, 0, 0, 0},
		{0, 1, 0, 0},
		{0, 0, 1, 0},
		{0, 0, 0, 1}
	};

	double speed = 0;

	
	double[][] vertices = null;
	double[][] vertexNormals = null;
	double[][] faceNormals = null;
//	double[][] textureCoordinates;
	
	int health;
	int damage;

	
	

	/**
	 * Calculates the normal by taking the cross product of the 3D vectors
	 * (v1-v2)x(v1-v3).
	 * 
	 * @param v1 - 3D vector
	 * @param v2 - 3D vector
	 * @param v3 - 3D vector
	 * @return - 3D vector representing the normalized vector (v1-v2)x(v1-v3)
	 */
//	protected double[] faceNormal(double[] v1,
//										double[] v2,
//										double[] v3) {
//		// should only be called after vertices have been created
//		assert(vertices != null);
//		double[] A = new double[]();
//		A.add(new Float(v1.get(0).floatValue() - v2.get(0).floatValue()));
//		A.add(new Float(v1.get(1).floatValue() - v2.get(1).floatValue()));
//		A.add(new Float(v1.get(2).floatValue() - v2.get(2).floatValue()));
//		double[] B = new double[]();
//		B.add(new Float(v1.get(0).floatValue() - v3.get(0).floatValue()));
//		B.add(new Float(v1.get(1).floatValue() - v3.get(1).floatValue()));
//		B.add(new Float(v1.get(2).floatValue() - v3.get(2).floatValue()));		
//		return normalize(cross (A, B));
//	}
	
	protected double[] faceNormal(double[] v1,
			double[] v2,
			double[] v3) {
		// should only be called after vertices have been created
		assert(vertices != null);
		double[] A = new double[3];
		A[0] = v1[0] - v2[0];
		A[1] = v1[1] - v2[1];
		A[2] = v1[2] - v2[2];
		double[] B = new double[3];
		B[0] = v1[0] - v3[0];
		B[1] = v1[1] - v3[1];
		B[2] = v1[2] - v3[2];
		return normalize(cross (A, B));
}
	
	
	
	
	public double[] getPosition() {
		return position;
	}




	public void setPosition(double[] position) {
		this.position = position;
	}




	public double[] getRollAxis() {
		return rollAxis;
	}




	public void setRollAxis(double[] rollAxis) {
		this.rollAxis = rollAxis;
	}




	public double[] getPitchAxis() {
		return pitchAxis;
	}




	public void setPitchAxis(double[] pitchAxis) {
		this.pitchAxis = pitchAxis;
	}




	public double[] getYawAxis() {
		return yawAxis;
	}




	public void setYawAxis(double[] yawAxis) {
		this.yawAxis = yawAxis;
	}




	public double[][] getRotationMatrix() {
		return rotationMatrix;
	}




	public void setRotationMatrix(double[][] rotationMatrix) {
		this.rotationMatrix = rotationMatrix;
	}




	public double getSpeed() {
		return speed;
	}




	public void setSpeed(double speed) {
		this.speed = speed;
	}




	public double[][] getVertices() {
		return vertices;
	}




	public void setVertices(double[][] vertices) {
		this.vertices = vertices;
	}




	public double[][] getVertexNormals() {
		return vertexNormals;
	}




	public void setVertexNormals(double[][] vertexNormals) {
		this.vertexNormals = vertexNormals;
	}




	public double[][] getFaceNormals() {
		return faceNormals;
	}




	public void setFaceNormals(double[][] faceNormals) {
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
	 * @param a
	 * @param b
	 * @return
	 */
	protected double[] cross(double[] a, double[] b) {
		double[] cross = new double[3];
		cross[0] = a[1]*b[2] - a[2]*b[1];
		cross[1] = a[2]*b[0] - a[0]*b[2];
		cross[2] = a[0]*b[1] - a[1]*b[0];
		return cross;

	}
	protected double[] normalize(double[] v){
		float sum = 0f;
		for(int i = 0; i < v.length; ++i){
			sum += v[i]*v[i];
		}
		double[] normalVector = new double[3];
		for(int i = 0; i < v.length; ++i){
			normalVector[i] = v[i]/Math.sqrt(sum);
		}
		return normalVector;
	}
	
	protected double magnitude(double[] v){
		double sum = 0;
		for(int i = 0; i < v.length; ++i){
			sum += v[i]*v[i];
		}
		return Math.sqrt(sum);
	}
	
	public double angleBetween(double[] a, double[] b) {
		  double dotProd = dot(a, b);
		  double magProd = magnitude(a)*magnitude(b);
		  double divOperation = dotProd/magProd;
		  return Math.acos(divOperation) * (180.0 / Math.PI);
		}
	

	protected double dot(double[] vec1, double[] vec2) {
		double sum = 0.0f;
		for(int i = 0; i < vec1.length; i++){
			sum += vec1[i]*vec2[i];
		}
		return sum;
	}

}
