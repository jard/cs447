/**
 * 
 */
package planetDefense.geometry;

/**
 * @author Justin
 *
 */
public class Matrix {

	
	private double[][] m;
	int rows;
	int cols;
	
	
	public Matrix(int rows, int columns){
		m = new double[rows][columns];
		this.rows = rows;
		this.cols = columns;
	}
	
	public void set(int row, int column, double value){
		m[row][column] = value;
	}
	
	public double get(int row, int column){
		return m[row][column];
	}
	
	public static Matrix multiply(Matrix a, Matrix b){
		if(a.cols != b.rows){
			return null;
		}
		Matrix newM = new Matrix(a.rows, b.cols);
		for(int i = 0; i < b.cols; ++i){
			for(int j = 0; j < a.rows; ++j){
				for(int k = 0; k < b.rows; ++k){
					newM.m[j][i] += a.m[j][k]*b.m[i][k];
				}
			}
		}
		return newM;
	}
	
	public static Vector3d multiply4(Matrix m, Vector3d v){
		double[] vec = new double[]{v.getX(), v.getY(), v.getZ(), 1};
		double [] newVec= new double[4];
		for(int i = 0; i < 4; ++i){
			for(int j = 0; j < 4; ++j){
				newVec[i]+=vec[j]*m.get(i, j);
			}
		}
		return new Vector3d(newVec[0], newVec[1], newVec[2]);
	}
	
	public static Vector3d multiply3(Matrix m, Vector3d v){
		double[] vec = {v.getX(), v.getY(), v.getZ()};
		double [] newVec= new double[3];
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 3; ++j){
				newVec[i]+=vec[j]*m.get(i, j);
			}
		}
		return new Vector3d(newVec[0], newVec[1], newVec[2]);
	}
	
	public static Matrix scale(double scale, Matrix m){
		Matrix newM = new Matrix(m.rows, m.cols);
		for(int i = 0; i < m.rows; ++i){
			for(int j = 0; j < m.cols; ++j){
				newM.set(i, j, m.get(i, j)*scale);
			}
		}
		return newM;
	}
	
	public static Matrix inverse33(Matrix m){
		assert(m.cols == 3 && m.rows == 3);
		double det = Matrix.determinant33(m);
		if(det == 0){
			// not invertable matrix
			return null;
		}
		double scale = 1.0/det;
		Matrix invM = new Matrix(3, 3);
		invM.set(0, 0, m.get(1,1)*m.get(2,2) - m.get(1,2)*m.get(2,1));
		invM.set(0, 1, m.get(0,2)*m.get(2,1) - m.get(0,1)*m.get(2,2));
		invM.set(0, 2, m.get(0,1)*m.get(1,2) - m.get(0,2)*m.get(1,1));
		
		invM.set(1, 0, m.get(1,2)*m.get(2,0) - m.get(1,0)*m.get(2,2));
		invM.set(1, 1, m.get(0,0)*m.get(2,2) - m.get(0,2)*m.get(2,0));
		invM.set(1, 2, m.get(0,2)*m.get(1,0) - m.get(0,0)*m.get(1,2));
		
		invM.set(2, 0, m.get(1,0)*m.get(2,1) - m.get(1,1)*m.get(2,0));
		invM.set(2, 1, m.get(0,1)*m.get(2,0) - m.get(0,0)*m.get(2,1));
		invM.set(2, 2, m.get(0,0)*m.get(1,1) - m.get(0,1)*m.get(1,0));
		
		invM = Matrix.scale(scale, invM);
		return invM;
	}
	
	/**
	 * @param m2
	 * @return
	 */
	private static double determinant33(Matrix m) {
		double det = m.get(0,0)*(m.get(1,1)*m.get(2,2) - m.get(1,2)*m.get(2,1))
				- m.get(0,1)*(m.get(1,0)*m.get(2,2) - m.get(1,2)*m.get(2,0))
				+ m.get(0,2)*(m.get(1,0)*m.get(2,1)-m.get(1,1)*m.get(2,0));		
		return det;
	}

	public static Matrix transpose(Matrix m){
		Matrix transpose = new Matrix(m.cols,m.rows);
		for(int i = 0; i < m.rows; ++i){
			for(int j = 0; j < m.cols; ++j){
				transpose.set(j, i, m.get(i,j));
			}
		}		
		return transpose;
	}
	
	public static Matrix star(Vector3d v){
		Matrix m = new Matrix(3,3);
		m.set(0,0,0);
		m.set(0,1, -v.getZ());
		m.set(0, 2, v.getY());
		
		m.set(1,0, v.getZ());
		m.set(1,1, 0);
		m.set(1,2, -v.getX());
		
		m.set(2,0, -v.getY());
		m.set(2,1, v.getX());
		m.set(2,2, 0);
		
		return m;
	}

	/**
	 * @param multiply
	 * @param rotation
	 * @return
	 */
	public static Matrix add(Matrix a, Matrix b) {
		Matrix sum = new Matrix(a.rows, a.cols);
		for(int i = 0; i < a.rows; ++i){
			for(int j = 0; j < a.cols; ++j){
				sum.set(i,j, a.get(i,j)+b.get(i,j));
			}
		}
		return sum;
	}
}
