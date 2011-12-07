/**
 * 
 */
package planetDefense.geometry;



/**
 * @author Justin
 *
 */
public class Vector3d {

	private double x;
	private double y;
	private double z;
	
	
	public Vector3d(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	/**
	 * @param force
	 */
	public Vector3d(Vector3d v) {
		this(v.getX(), v.getY(), v.getZ());
	}


	public static double dot(Vector3d a, Vector3d b){
		return a.x*b.x + a.y*b.y + a.z*b.z;
	}
	
	public static Vector3d cross(Vector3d a, Vector3d b){
		double x = a.y*b.z - a.z*b.y;
		double y = a.z*b.x - a.x*b.z;
		double z = a.x*b.y - a.y*b.x;
		return new Vector3d(x, y, z);
	}
	
	public static Vector3d add(Vector3d a, Vector3d b){
		return new Vector3d(a.x+b.x, a.y+b.y, a.z+b.z);
	}
	
	public static Vector3d subtract(Vector3d a, Vector3d b){
		return new Vector3d(a.x-b.x, a.y-b.y, a.z-b.z);
	}
	
	public static Vector3d scale(double s, Vector3d v){
		return new Vector3d(v.x*s, v.y*s, v.z*s);
	}
	
	public static Vector3d normalize(Vector3d v){
		double l = Vector3d.magnitude(v);
		return new Vector3d(v.x/l, v.y/l, v.z/l);
	}
	
	public static double magnitude(Vector3d v){
		return Math.sqrt(v.x*v.x+v.y*v.y+v.z*v.z);
	}	

	
	public static double angleBetween(Vector3d a, Vector3d b) {
		a = Vector3d.normalize(a);
		b = Vector3d.normalize(b);
		double dotProd = Vector3d.dot(a, b);
		return Math.acos(dotProd)*(180/Math.PI);
	}
	
	public static Vector3d mirror(Vector3d norm, Vector3d v){
		Vector3d reflection = Vector3d.subtract(Vector3d.scale(2*Vector3d.dot(v, norm), norm), v);
		return reflection;
	}


	public double getX() {
		return x;
	}


	public void setX(double x) {
		this.x = x;
	}


	public double getY() {
		return y;
	}


	public void setY(double y) {
		this.y = y;
	}


	public double getZ() {
		return z;
	}


	public void setZ(double z) {
		this.z = z;
	}


	/**
	 * 
	 */
	public void clear() {
		x = y = z = 0;
		
	}


	/**
	 * @return
	 */
	public Matrix getProjectionMatrix() {
		Matrix m = new Matrix(4, 4);
		double xx = x*x;
		double yy = y*y;
		double zz = z*z;
		double xy = x*y;
		double xz = x*z;
		double yz = y*z;
		double[][] proj = new double[][]{
				{xx, xy, xz, 0},
				{xy, yy, yz, 0},
				{xz, yz, zz, 0},
				{0, 0, 0, 1}
		};
		for(int i = 0; i < 4; ++i){
			for(int j = 0; j < 4; ++j){
				m.set(i, j, proj[i][j]);
			}
		}
		return m;
	}
	

}
