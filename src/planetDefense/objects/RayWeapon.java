/**
 * 
 */
package planetDefense.objects;

import javax.media.opengl.GL2;

import planetDefense.geometry.Vector3d;

/**
 * @author Justin
 *
 */
public class RayWeapon {
	private static final double SPEED = 500.0;
	public static final int DAMAGE = 10;
	private double distance;
	public Vector3d start;
	public Vector3d end;
	public Vector3d direction;
	
	public RayWeapon(Vector3d s, Vector3d v){
		start = s;
		direction = v;
		end = Vector3d.add(start, Vector3d.scale(500, direction));
		distance = 0.0;
	}
	
	public void update(long delta){		
		double displace = SPEED*delta/1000;
		distance += displace;
		start = Vector3d.add(start, Vector3d.scale(displace, direction));
		end = Vector3d.add(start, Vector3d.scale(500, direction));
	}
	
	public double getDistance(){
		return distance;
	}

	/**
	 * @param gl
	 */
	public void display(GL2 gl) {
		gl.glLineWidth(4.0f);
		gl.glColor4d(1.0, 0, 0, 1.0);
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3d(start.getX(), start.getY(), start.getZ());
		gl.glColor4d(1.0, 0, 0, 0.0);
		gl.glVertex3d(end.getX(), end.getY(), end.getZ());
		gl.glEnd();
		gl.glLineWidth(1.0f);
		
	}

	/**
	 * @return
	 */
	public int getDamage() {
		return this.DAMAGE;
	}
	
}
