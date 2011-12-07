/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package planetDefense;
import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.texture.*;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import planetDefense.geometry.Vector3d;


public class StarScapeCube{
    
    final float vert1[] = {0,0,0};
    final float vert2[] = {0,1,0};
    final float vert3[] = {1,1,0};
    final float vert4[] = {1,0,0};
    final float vert5[] = {0,0,1};
    final float vert6[] = {0,1,1};
    final float vert7[] = {1,1,1};
    final float vert8[] = {1,0,1};
	private GLProfile glp = GLProfile.getDefault();
	private Texture starsFront;
	private Texture starsBack;
	private Texture starsDown;
	private Texture starsUp;
	private Texture starsLeft;
	private Texture starsRight;
	private Vector3d position;
    
    public StarScapeCube(GL2 gl){
        try {
//          earth = TextureIO.newTexture(new File("resources/quom.png"), false);
      	
	        InputStream stream = getClass().getResourceAsStream("sp2front.bmp");
	        TextureData data = TextureIO.newTextureData(glp , stream, false, "bmp");
	        starsFront = TextureIO.newTexture(data);
	        
	        stream = getClass().getResourceAsStream("sp2back.bmp");
	        data = TextureIO.newTextureData(glp,  stream, false, "bmp");
	        starsBack = TextureIO.newTexture(data);
	        
	        stream = getClass().getResourceAsStream("sp2down.bmp");
	        data = TextureIO.newTextureData(glp,  stream, false, "bmp");
	        starsDown = TextureIO.newTexture(data);
	        
	        stream = getClass().getResourceAsStream("sp2up.bmp");
	        data = TextureIO.newTextureData(glp,  stream, false, "bmp");
	        starsUp = TextureIO.newTexture(data);
	        
	        stream = getClass().getResourceAsStream("sp2left.bmp");
	        data = TextureIO.newTextureData(glp,  stream, false, "bmp");
	        starsLeft = TextureIO.newTexture(data);
	        
	        stream = getClass().getResourceAsStream("sp2right.bmp");
	        data = TextureIO.newTextureData(glp,  stream, false, "bmp");
	        starsRight = TextureIO.newTexture(data);

      } catch (IOException e) {    
          javax.swing.JOptionPane.showMessageDialog(null, e);
      }
        position = new Vector3d(0,0,0);
    }
    
    public void display(GL2 gl,GLUquadric SOLID,GLU glu,Texture starTexture){
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();
        
        gl.glPushMatrix();
        // draw front face
        starsFront.enable(gl);
        starsFront.bind(gl);
        gl.glBegin(gl.GL_QUADS);        
        gl.glTexCoord2f(0,0);
        gl.glVertex3d(x-5000, y+5000, z-5000);
        gl.glTexCoord2f(1,0);
        gl.glVertex3d(x+5000, y+5000, z-5000);
        gl.glTexCoord2f(1,1);
        gl.glVertex3d(x+5000, y-5000, z-5000);
        gl.glTexCoord2f(0,1);
        gl.glVertex3d(x-5000, y-5000, z-5000);
        gl.glEnd();
        starsFront.disable(gl);        
       
        // draw bottom face
        starsDown.enable(gl);
        starsDown.bind(gl);
        gl.glBegin(gl.GL_QUADS);        
        gl.glTexCoord2f(0,0);
        gl.glVertex3d(x-5000, y-5000, z-5000);
        gl.glTexCoord2f(1,0);
        gl.glVertex3d(x+5000, y-5000, z-5000);
        gl.glTexCoord2f(1,1);
        gl.glVertex3d(x+5000, y-5000, z+5000);
        gl.glTexCoord2f(0,1);
        gl.glVertex3d(x-5000, y-5000, z+5000);
        gl.glEnd();
        starsDown.disable(gl);
        
        // draw back face
        starsBack.enable(gl);
        starsBack.bind(gl);
        gl.glBegin(gl.GL_QUADS);        
        gl.glTexCoord2f(0,0);
        gl.glVertex3d(x+5000, y+5000, z+5000);
        gl.glTexCoord2f(1,0);
        gl.glVertex3d(x-5000, y+5000, z+5000);
        gl.glTexCoord2f(1,1);
        gl.glVertex3d(x-5000, y-5000, z+5000);
        gl.glTexCoord2f(0,1);
        gl.glVertex3d(x+5000, y-5000, z+5000);
        gl.glEnd();
        starsBack.disable(gl);
        
        // draw top face
        starsUp.enable(gl);
        starsUp.bind(gl);
        gl.glBegin(gl.GL_QUADS);        
        gl.glTexCoord2f(0,0);
        gl.glVertex3d(x-5000, y+5000, z+5000);
        gl.glTexCoord2f(1,0);
        gl.glVertex3d(x+5000, y+5000, z+5000);
        gl.glTexCoord2f(1,1);
        gl.glVertex3d(x+5000, y+5000, z-5000);
        gl.glTexCoord2f(0,1);
        gl.glVertex3d(x-5000, y+5000, z-5000);
        gl.glEnd();
        starsUp.disable(gl);
        
        // draw left face
        starsLeft.enable(gl);
        starsLeft.bind(gl);
        gl.glBegin(gl.GL_QUADS);        
        gl.glTexCoord2f(0,0);
        gl.glVertex3d(x-5000, y+5000, z+5000);
        gl.glTexCoord2f(1,0);
        gl.glVertex3d(x-5000, y+5000, z-5000);
        gl.glTexCoord2f(1,1);
        gl.glVertex3d(x-5000, y-5000, z-5000);
        gl.glTexCoord2f(0,1);
        gl.glVertex3d(x-5000, y-5000, z+5000);
        gl.glEnd();
        starsLeft.disable(gl);
        
        // draw right face
        starsRight.enable(gl);
        starsRight.bind(gl);
        gl.glBegin(gl.GL_QUADS);        
        gl.glTexCoord2f(0,0);
        gl.glVertex3d(x+5000, y+5000, z-5000);
        gl.glTexCoord2f(1,0);
        gl.glVertex3d(x+5000, y+5000, z+5000);
        gl.glTexCoord2f(1,1);
        gl.glVertex3d(x+5000, y-5000, z+5000);
        gl.glTexCoord2f(0,1);
        gl.glVertex3d(x+5000, y-5000, z-5000);
        gl.glEnd();
        starsRight.disable(gl);
        
        
        
//        gl.glNormal3f(0,0,1);
//        gl.glTexCoord2f(0,0);
//        gl.glVertex3fv(vert1,0);
//        gl.glTexCoord2f(1,0);
//        gl.glVertex3fv(vert2,0);
//        gl.glTexCoord2f(1,1);
//        gl.glVertex3fv(vert3,0);
//        gl.glTexCoord2f(0,1);
//        gl.glVertex3fv(vert4,0);
//        gl.glEnd();
        
//        gl.glBegin(gl.GL_QUADS);
//        gl.glTexCoord2f(0,0);
//        gl.glVertex3fv(vert1,0);
//        gl.glTexCoord2f(0,1);
//        gl.glVertex3fv(vert5,0);
//        gl.glTexCoord2f(1,1);
//        gl.glVertex3fv(vert6,0);
//        gl.glTexCoord2f(1,0);
//        gl.glVertex3fv(vert2,0);
//        gl.glEnd();
//        
//        gl.glBegin(gl.GL_QUADS);
//        gl.glTexCoord2f(0,0);
//        gl.glVertex3fv(vert4,0);
//        gl.glTexCoord2f(0,1);
//        gl.glVertex3fv(vert3,0);
//        gl.glTexCoord2f(1,1);
//        gl.glVertex3fv(vert7,0);
//        gl.glTexCoord2f(1,0);
//        gl.glVertex3fv(vert8,0);
//        gl.glEnd();
//        
//        gl.glBegin(gl.GL_QUADS);
//        gl.glTexCoord2f(0,0);
//        gl.glVertex3fv(vert8,0);
//        gl.glTexCoord2f(0,1);
//        gl.glVertex3fv(vert7,0);
//        gl.glTexCoord2f(1,1);
//        gl.glVertex3fv(vert6,0);
//        gl.glTexCoord2f(1,0);
//        gl.glVertex3fv(vert5,0);
//        gl.glEnd();
//        
//        gl.glBegin(gl.GL_QUADS);
//        gl.glTexCoord2f(0,0);
//        gl.glVertex3fv(vert1,0);
//        gl.glTexCoord2f(0,1);
//        gl.glVertex3fv(vert5,0);
//        gl.glTexCoord2f(1,1);
//        gl.glVertex3fv(vert8,0);
//        gl.glTexCoord2f(1,0);
//        gl.glVertex3fv(vert4,0);
//        gl.glEnd();
//        
//        gl.glBegin(gl.GL_QUADS);
//        gl.glTexCoord2f(0,0);
//        gl.glVertex3fv(vert2,0);
//        gl.glTexCoord2f(0,1);
//        gl.glVertex3fv(vert6,0);
//        gl.glTexCoord2f(1,1);
//        gl.glVertex3fv(vert7,0);
//        gl.glTexCoord2f(1,0);
//        gl.glVertex3fv(vert5,0);
//        gl.glEnd();
        
        gl.glPopMatrix();
        
    }
    
    public void updatePosition(Vector3d v){
//        position = v;
    }
    
}
