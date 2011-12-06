package planetDefense.geometry;


///**
// * @author Justin
// *
// */

	
public class Quaternion {
	
	private double w;
	private Vector3d v;
	
	public Quaternion(){

    }
    
    public Quaternion(double w, double x, double y, double z) {
        this(w, new Vector3d(x, y, z));
    }
    
    public Quaternion(double w, Vector3d v){
    	this.w = w;
    	this.v = v;
    }
    	
	public Quaternion multiply(Quaternion other){
		double a,b,c,d;
		double x1, y1, z1;
		double x2, y2, z2, w2;
		x1 = v.getX(); y1 = v.getY(); z1 = v.getZ();
		w2 = other.getW(); x2 = other.getX(); y2 = other.getY(); z2 = other.getZ();
		a = w*w2 -x1*x2 -y1*y2 -z1*z2;
		b = w*x2 +x1*w2 +y1*z2 -z1*y2;
		c = w*y2 -x1*z2 +y1*w2 +z1*x2;
		d = w*z2 +x1*y2 -y1*x2 +z1*w2;
		return new Quaternion(a, b, c, d);
	}
	
	public Quaternion add(Quaternion other){
		return new Quaternion(w+other.getW(), getX()+other.getX(), getY()+other.getY(), getZ()+other.getZ());
	}
	
	public double getMagnitude(){
		return Math.sqrt(w*w+getX()*getX()+getY()*getY()+getZ()*getZ());
	}
	
	public void normalize(){
		double m = getMagnitude();
        if (m == 0.0)
        {
            w = 1.0; 
            v.clear();
        }
		w /= m;
		v.setX(v.getX()/m);
		v.setY(v.getY()/m);
		v.setZ(v.getZ()/m);
	}
    
//    /** Constructor to create a rotation based quaternion from two vectors
//     * @param vector1
//     * @param vector2
//     */
//    public Quaternion(double[] vector1, double[] vector2) 
//    {
//        double theta = (double)Mathdouble.acos(dot(vector1, vector2));
//        double[] cross = cross(vector1,vector2);
//        cross = normalizeVec(cross);
//
//        this.x = (double)Mathdouble.sin(theta/2)*cross[0];
//        this.y = (double)Mathdouble.sin(theta/2)*cross[1];
//        this.z = (double)Mathdouble.sin(theta/2)*cross[2];
//        this.w = (double)Mathdouble.cos(theta/2);
//        this.normalize();
//    }
    


	/** Transform the rotational quaternion to axis based rotation angles
     * @return new double[4] with ,theta,Rx,Ry,Rz
     */
    public double[] toAxis()
    {
        double[] vec = new double[4];
        double x, y, z;
        x = getX(); y = getY(); z = getZ();
        
        double scale = (double)Math.sqrt(x*x + y*y + z*z);
        vec[0] =Math.acos(w)*2;
        vec[1] = x/scale;
        vec[2] = y/scale;
        vec[3] = z/scale;
        return vec;
    }


    public double getW() {
        return w;
    }
    public void setW(double w) {
        this.w = w;
    }
    public double getX() {
        return v.getX();
    }
    public void setX(double x) {
        v.setX(x);
    }
    public double getY() {
        return v.getY();
    }
    public void setY(double y) {
        v.setY(y);
    }
    public double getZ() {
        return v.getZ();
    }
    public void setZ(double z) {
        v.setZ(z);
    }

	
	public double[][] getMatrix(){
		normalize();
		double x, y, z;
		x = getX(); y = getY(); z = getZ();
		double xx = x*x;
		double yy = y*y;
		double zz = z*z;
		return new double[][]{
				{1-2*yy-2*zz, 2*x*y-2*w*z, 2*x*z+2*w*y, 0},
				{2*x*y+2*w*z, 1-2*xx-2*zz, 2*y*z+2*w*x, 0},
				{2*x*z-2*w*y, 2*y*z-2*w*x, 1-2*xx-2*yy, 0},
				{0, 0, 0, 1}
		};
	}
	
	
	
	public Quaternion rotate(double radians, Vector3d axis){
		Quaternion rotation = Quaternion.rotationQuat(radians, axis);
		return(rotation.multiply(this));
	}
	
	/**
	 * @param radians
	 * @param axis
	 * @return
	 */
	private static Quaternion rotationQuat(double radians, Vector3d axis) {
		double cos = Math.cos(radians/2);
		double sin = Math.sin(radians/2);
		return new Quaternion(cos,
				axis.getX()*sin,
				axis.getY()*sin,
				axis.getZ()*sin);

	}

	public Vector3d rotateVector(Vector3d v){
		Quaternion q = new Quaternion(0, v);
		Quaternion newQ = (this.multiply(q)).multiply(getInverse());
		return newQ.getVector();
	}

	/**
	 * @return
	 */
	private Vector3d getVector() {
		return v;
	}

	/**
	 * @return
	 */
	private Quaternion getInverse() {
        double a = w*w + getX()*getX() + getY()*getY() + getZ()*getZ();
        return new Quaternion(w/a, -1*getX()/a, -1*getY()/a, -1*getZ()/a);
	}

}
