/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package planetDefense;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.texture.*;
import javax.media.opengl.GL2;


public class StarScape{
    
    
    
    public StarScape(GL2 gl){
        
    }
    
    public void display(GL2 gl,GLUquadric SOLID,GLU glu,Texture starTexture){
        
        gl.glPushMatrix();
        starTexture.enable(gl);
        starTexture.bind(gl);
        glu.gluSphere(SOLID, 10000f, 100, 100);
        starTexture.disable(gl);
        gl.glPopMatrix();
        
    }
    
    public void Update(long delta){
        
    }
    
}
