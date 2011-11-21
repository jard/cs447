/*
 * This program uses openGL through the Java OpenGL (JOGL)
 * libraries.  It creates a robot with independent points
 * of rotation at all arm and leg joints, as well as at
 * the hinge of the jaw, and at the bottom of the torso.
 * Each joint is randomly assigned an update value which
 * controls the change in the joint angle during each
 * update.  This ensures that all joints rotate independently.
 * The program can be stopped by pressing the escape key or
 * closing the window.
 */
package planetDefense;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;
import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

import jig.engine.ImageResource;
import jig.engine.ResourceFactory;

import planetDefense.objects.UserShip;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;



public class PlanetDefense extends JFrame implements GLEventListener {

	private static final long serialVersionUID = 1L;
	private static final double CAM_BEHIND_SCALAR = 3;
	private static final double CAM_ABOVE_SCALAR = 1;
	
	// FPSAnimator performs animation by repeatedly calling
	// the display method
	private FPSAnimator animator;

    // light properties
    private final float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
    private final float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private final float[] lightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
    private final float[] lightPosition = {1000.0f, 1000.0f, 0.0f, 0.0f};
	private final double MAX_POSITION = 10000.0;
	
    private GLU glu;
	private UserShip user;
	private Texture earth;

    
	public static void main (final String[] args){
		final PlanetDefense app = new PlanetDefense();
		app.run();
	}
	public PlanetDefense(){
		super("Space");
//		ResourceFactory.getFactory().loadResources("resources/", "resources.xml");
	}

	public void centerWindow(final Component frame){
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension frameSize = frame.getSize();

		if(frameSize.width > screenSize.width){
			frameSize.width = screenSize.width;
		}
		if(frameSize.height > screenSize.height){
			frameSize.height = screenSize.height;
		}

		frame.setLocation(
			(screenSize.width - frameSize.width) >> 1,
			(screenSize.height - frameSize.height) >> 1
		);
	}


	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void display(final GLAutoDrawable glDrawable) {
		update();
		final GL2 gl = glDrawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		positionCamera();
		gl.glPushMatrix();

		// draw planet
        GLUquadric SOLID = glu.gluNewQuadric();
        GLUquadric stars = glu.gluNewQuadric();
        glu.gluQuadricDrawStyle(stars, GLU.GLU_POINT);
        glu.gluQuadricDrawStyle( SOLID, GLU.GLU_FILL);
        //glu.gluQuadricDrawStyle(qobj0, GLU.GLU_FLAT); 
        glu.gluQuadricNormals( SOLID, GLU.GLU_SMOOTH );
//                        gl.glColor4f(1, 0, 0, 1);
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(400, 0, 0);
        gl.glEnd();
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 400, 0);
        gl.glEnd();
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 0, 400);
        gl.glEnd();
        glu.gluQuadricTexture(SOLID, true);
        
        earth.enable(gl);
        earth.bind(gl);
//        try {
//          earth = TextureIO.newTexture(new File("resources/quom.png"), true);
//          earth.enable(gl);
//          earth.bind(gl);
//        }
//        catch (IOException e) {    
//          javax.swing.JOptionPane.showMessageDialog(null, e);
//        }
        
        
        gl.glDisable(GL2.GL_LIGHTING);
        glu.gluSphere(SOLID, 100f, 50, 50);
        earth.disable(gl);
        glu.gluDeleteQuadric(SOLID);
        gl.glEnable(GL2.GL_LIGHTING);
        // draw user spaceship
		user.display(gl);
			
//                        gl.glTranslatef(FORWARD, LEFT, RIGHT); 
//                        //glu.gluSphere(qobj0, 5, 100, 100);
//                        gl.glPushMatrix();
//                        gl.glRotated(90, 1, 0, 0);
//                        gl.glRotated(-90, 0, 1, 0);
//                        gl.glPushMatrix();
//                        gl.glTranslatef(0,0,8);
//                        glu.gluCylinder(SOLID, 5, 5, 15, 10, 10);
//                        gl.glTranslatef(0, 0, 15);
//                        glu.gluDisk(SOLID, 0, 5, 10, 10);
//                        gl.glPushMatrix();
//                        //gl.glTranslatef(10,0,0);
//                        gl.glBegin(GL.GL_TRIANGLES);        // Drawing Using Triangles
//                        gl.glVertex3f(0.0f, -15.0f, 0.0f);
//                        gl.glVertex3f(0.0f, 0.0f, -22.0f);
//                        gl.glVertex3f(0.0f, 15.0f, 10.0f);
//                        /*
//                        gl.glVertex3f(-1.0f, -15.0f, 0.0f);
//                        gl.glVertex3f(0.0f, 0.0f, -22.0f);
//                        gl.glVertex3f(-1.0f, 15.0f, 0.0f);
//                        
//                        gl.glVertex3f(-1.0f, -15.0f, 0.0f);
//                        gl.glVertex3f(0.0f, 0.0f, -22.0f);
//                        gl.glVertex3f(1.0f, -15.0f, 0.0f);
//                        
//                        gl.glVertex3f(-1.0f, 15.0f, 0.0f);
//                        gl.glVertex3f(0.0f, 0.0f, -22.0f);
//                        gl.glVertex3f(1.0f, 15.0f, 0.0f);
//                        */
//                        
//                        gl.glEnd();                         // Finished Drawing The Triangle
//                        gl.glPopMatrix();
//                        gl.glPopMatrix();
//                        
//                        glu.gluCylinder(SOLID, 0, 5, 8, 10, 10);
//                        gl.glPopMatrix();

		gl.glPopMatrix();
		glDrawable.swapBuffers();
	}


	/**
	 * 
	 */
	private void positionCamera() {
		double[] cameraPosition = user.getPosition().clone();
		double[] userDirection = user.getRollAxis();
		double[] userUp = user.getYawAxis();
		for(int i = 0; i < 3; ++i){
			cameraPosition[i] += CAM_BEHIND_SCALAR * -userDirection[i];
			cameraPosition[i] += CAM_ABOVE_SCALAR * userUp[i];
		}

		double[] userPosition = user.getPosition();
		glu.gluLookAt(cameraPosition[0], cameraPosition[1], cameraPosition[2], // camera position
				userPosition[0], userPosition[1], userPosition[2], 	// look at position
				userUp[0], userUp[1], userUp[2]);	// up direction
//		glu.gluLookAt(300, 100, 200, // camera position
//				300, 0, 0, 	// look at position
//				0, 1, 0);	// up direction
		
	}

	/**
	 * 
	 */
	private void update() {
		user.update(0);
		
	}

	public void displayChanged(final GLAutoDrawable glDrawable, final boolean modeChanged, final boolean deviceChanged){
		glDrawable.getGL().getGL2().glViewport(0, 0, getWidth(), getHeight());
		display(glDrawable);
	}

	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void dispose(final GLAutoDrawable glDrawable) {
	}





	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void init(final GLAutoDrawable glDrawable) {
		glu = new GLU();
                
		final GL2 gl = glDrawable.getGL().getGL2();
		gl.glShadeModel(GL2.GL_FLAT);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	    gl.glEnable(GL2.GL_DEPTH_TEST);
	    gl.glDepthFunc(GL2.GL_LEQUAL);
		// set light properties
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightSpecular, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
                
		gl.glPointSize(1.0f);
		gl.glLineWidth(1.0f);
		user = new UserShip(gl);
		this.getContentPane().getComponent(0).addKeyListener(user);
		this.getContentPane().getComponent(0).addMouseListener(user);
		this.getContentPane().getComponent(0).requestFocus();
		
        try {
//        	ImageResource imgRes= ResourceFactory.getFactory().getFrames("resources/quom.png").get(0);
//        	URL url = getClass().getResource("resources/quom.png");
//        	System.out.println(url.getPath());
            earth = TextureIO.newTexture(new File("resources/quom.png"), false);
          }
          catch (IOException e) {    
            javax.swing.JOptionPane.showMessageDialog(null, e);
          }
	}

//
//	private void loadShaders(final GL2 gl) {
//		vertexShader = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
//		fragmentShader = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
//		String vSource = "";
//		String fSource = "";
//		try {
//			final BufferedReader vBuff = new BufferedReader(new FileReader("vertex.glsl"));
//			String vLine;
//			while((vLine = vBuff.readLine()) != null){
//				vSource += vLine + "\n";
//			}
//
//			final BufferedReader fBuff = new BufferedReader(new FileReader("fragment.glsl"));
//			String fLine;
//			while((fLine = fBuff.readLine()) != null){
//				fSource += fLine + "\n";
//			}
//		} catch (final Exception e) {
//			e.printStackTrace();
//		}
//		// compile vertex shader
//		gl.glShaderSource(vertexShader, 1, new String[]{vSource}, (int[])null, 0);
//		gl.glCompileShader(vertexShader);
//		// compile fragment shader
//		gl.glShaderSource(fragmentShader, 1, new String[]{fSource}, (int[])null, 0);
//		gl.glCompileShader(fragmentShader);
//
//		// create shader program
//		final int shaderprogram = gl.glCreateProgram();
//		// attach shaders
//		gl.glAttachShader(shaderprogram, vertexShader);
//		gl.glAttachShader(shaderprogram, fragmentShader);
//
//		gl.glLinkProgram(shaderprogram);
//		gl.glValidateProgram(shaderprogram);
//		gl.glUseProgram(shaderprogram);
//	}

	private double[] normalize(final double[] vector) {
		final double magnitude = Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2]);
		final double[] normal = new double[3];
		normal[0] = vector[0]/magnitude;
		normal[1] = vector[1]/magnitude;
		normal[2] = vector[2]/magnitude;
		return normal;
	}



	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	@Override
	public void reshape(final GLAutoDrawable glDrawable, final int x, final int y, final int width, final int height) {
		final GL2 gl = glDrawable.getGL().getGL2();
		// set viewport
		gl.glViewport(0, 0, width, height);
		// set perspective
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		final float widthHeightRatio = (float) width / (float) height;
		glu.gluPerspective(45.0f, widthHeightRatio, 1.0f, 1.5*MAX_POSITION);
		display(glDrawable);
	}


	private void run() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final GLProfile prof = GLProfile.get(GLProfile.GL2);
		final GLCapabilities glcaps = new GLCapabilities(prof);
		glcaps.setDoubleBuffered(true);
		glcaps.setHardwareAccelerated(true);
		glcaps.setDepthBits(16);
		final GLCanvas glcanvas = new GLCanvas(glcaps);
		glcanvas.addGLEventListener(this);
//		glcanvas.setSize(width, height);

		this.getContentPane().add(glcanvas, BorderLayout.CENTER);
//		setSize(800, 800);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		centerWindow(this);
		animator = new FPSAnimator(30);
		animator.add(glcanvas);
		setVisible(true);
		animator.start();
	}



}