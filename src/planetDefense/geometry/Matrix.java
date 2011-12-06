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
	
	public Matrix multiply(Matrix other){
		if(this.cols != other.rows){
			return null;
		}
		Matrix newM = new Matrix(rows, other.cols);
		for(int i = 0; i < other.cols; ++i){
			for(int j = 0; j < rows; ++j){
				for(int k = 0; k < other.rows; ++i){
					newM.m[j][i] += m[j][k]*other.m[i][k];
				}
			}
		}
		return newM;
	}
	
	public Vector3d multiply(Vector3d v){
		double[] vec = new double[]{v.getX(), v.getY(), v.getZ(), 1};
		double [] newVec= new double[4];
		for(int i = 0; i < 4; ++i){
			for(int j = 0; j < 4; ++j){
				newVec[i]+=vec[j]*get(i, j);
			}
		}
		return new Vector3d(newVec[0], newVec[1], newVec[2]);
	}
}
