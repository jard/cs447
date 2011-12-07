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
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

import jig.engine.ImageResource;
import jig.engine.ResourceFactory;

import planetDefense.geometry.Matrix;
import planetDefense.geometry.Vector3d;
import planetDefense.objects.EnemyShip;
import planetDefense.objects.Asteroid;
import planetDefense.objects.GameObject;
import planetDefense.objects.RayWeapon;
import planetDefense.objects.UserShip;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;



public class PlanetDefense extends JFrame implements GLEventListener, KeyListener, MouseListener{

	private static final long serialVersionUID = 1L;
	private static final double CAM_BEHIND_SCALAR = 3;
	private static final double CAM_ABOVE_SCALAR = 1;
	public static String score;
	
	private long gameClock;
	private long lastFrame;
	// FPSAnimator performs animation by repeatedly calling
	// the display method
	private FPSAnimator animator;

    // light properties
    private final float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
    private final float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private final float[] lightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
    private final float[] lightPosition = {1.0f, 1.0f, 0.0f, 0.0f};
	private final double MAX_POSITION = 10000.0;
	
    private GLU glu;
	private UserShip user;
	private Texture earth;
    private Texture asteroidTexture;
    private Texture randomTexture;
    private Texture starTexture;
    private StarScape Starscape;
    private StarScapeCube StarscapeCube;
    
	private ArrayList<EnemyShip> enemies;
	private ArrayList<Asteroid> asteroids;
	private GLProfile glp = GLProfile.getDefault();
	private Clip clip2;
	private Clip clip1;
	private boolean button2clicked;
	private ArrayList<RayWeapon> userWeapons;
	private boolean gameOver;
	private Random random;
	private long spawnTimer;
	private Clip shootClip;
	private Clip damageClip;
	private AudioInputStream damageAudio;
	private AudioInputStream shootAudio;

    
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


	/**
		 * 
		 */
		private void positionCamera() {
			Vector3d cameraPosition = user.getPosition();
			Vector3d userDirection = user.getRollAxis();
			Vector3d userUp = user.getYawAxis();
			
			cameraPosition = Vector3d.add(cameraPosition, Vector3d.scale(CAM_BEHIND_SCALAR, userDirection));
			cameraPosition = Vector3d.add(cameraPosition, Vector3d.scale(CAM_ABOVE_SCALAR, userUp));
	
			Vector3d userPosition = user.getPosition();
			glu.gluLookAt(cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ(), // camera position
					userPosition.getX(), userPosition.getY(), userPosition.getZ(), 	// look at position
					userUp.getX(), userUp.getY(), userUp.getZ());	// up direction
			
//			glu.gluLookAt(0, 1, 103, // camera position
//					0, 0, 0, 	// look at position
//					0, 1, 0);	// up direction
			
		}
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void display(final GLAutoDrawable glDrawable) {
		final GL2 gl = glDrawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

			long currentTime = System.currentTimeMillis();
			long delta = currentTime - lastFrame;
			gameClock += delta;
			lastFrame = currentTime;
			update(delta);


			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
			positionCamera();
			gl.glPushMatrix();
	
			// draw planet
	        GLUquadric SOLID = glu.gluNewQuadric();
	        glu.gluQuadricDrawStyle( SOLID, GLU.GLU_FILL);
	        glu.gluQuadricNormals( SOLID, GLU.GLU_SMOOTH );
	        gl.glEnable(GL2.GL_LIGHTING);
	        glu.gluQuadricTexture(SOLID, true);
	        
	        earth.enable(gl);
	        earth.bind(gl);
	       
	        
	        gl.glDisable(GL2.GL_LIGHTING);
	        gl.glColor3f(1, 1, 1);
	        glu.gluSphere(SOLID, 50, 50, 50);
	        earth.disable(gl);
	        
	        StarscapeCube.display(gl, SOLID, glu, starTexture);
	        if(!gameOver){
		        gl.glEnable(GL2.GL_LIGHTING);
		        // draw user spaceship
		        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
				user.display(gl);
				// draw user rays	
				for(RayWeapon rw : userWeapons){
					rw.display(gl);
				}
				
				for(EnemyShip ship : enemies){
					ship.display(gl);
				}
				for(Asteroid asteroid : asteroids){
					asteroid.display(gl);
				}
	        }
				
			gl.glPopMatrix();
			glDrawable.swapBuffers();

	}


	/**
	 * @param delta 
	 * 
	 */
	private void update(long delta) {
		user.score += 1;
		user.update(delta);
		for(EnemyShip enemy : enemies){
			enemy.update(delta);
		}
		for(Asteroid asteroid : asteroids){
			asteroid.update(delta);
		}
		testUserCollisions();
		testUserWeaponCollisions();
		testAsteroidCollisions();
		testEnemyCollisions();

		StarscapeCube.updatePosition(user.getPosition());
		for(RayWeapon rw : userWeapons){
			rw.update(delta);
		}
		for(int i = 0; i < userWeapons.size(); ++i){
			if(userWeapons.get(i).getDistance() > 1000){
				userWeapons.remove(i);
				--i;
			}

		}
		for(int i = 0; i < enemies.size(); ++i){
			if(enemies.get(i).exploded){
				enemies.remove(i);
				--i;
			}

		}
		if(user.exploded){
			gameOver = true;
		}
		spawnTimer += delta;
		if(spawnTimer>1000){
			spawnTimer = 0;
			addEnemies();	
		}
		Vector3d userPosition = user.getPosition();
		double dist = Vector3d.magnitude(userPosition);
		if(dist<55){
			Vector3d tangent = new Vector3d(userPosition.getX(), userPosition.getY(), userPosition.getZ());
			double mag = Vector3d.magnitude(user.getVelocity());
			Vector3d newV = Vector3d.mirror(Vector3d.normalize(tangent), user.getVelocity());
//			newV = Vector3d.scale(mag, Vector3d.normalize(newV));
			user.setPosition(Vector3d.add(userPosition, Vector3d.scale(-(55-dist), Vector3d.normalize(user.getVelocity()))));
			user.setVelocity(Vector3d.scale(-1, newV));
		}
		
		for(int i = 0; i < enemies.size(); ++i){
			double enemyDist = Vector3d.magnitude(enemies.get(i).getPosition());
			if(enemyDist < 50){
				user.inflictDamage(enemies.get(i).getDamage());
				enemies.get(i).explode();
			}
		}
		
	}

	/**
	 * 
	 */
	private void addEnemies() {
		int r = random.nextInt((int) (gameClock+100000));
		if(r < gameClock){
			int x = random.nextInt(100);
			int z = random.nextInt(100);
			int y = 0;
			Vector3d position = Vector3d.scale(100, Vector3d.normalize(new Vector3d(x, y, z)));
//			Vector3d position = new Vector3d(1, 0, 100);
			enemies.add(new EnemyShip(10, position));
			x = random.nextInt(100);
			y = random.nextInt(100);
			z = random.nextInt(100);
			position = Vector3d.scale(100, Vector3d.normalize(new Vector3d(x, y, z)));
			asteroids.add(new Asteroid(20, position, asteroidTexture));
		}
		
	}
	/**
	 * 
	 */
	private void gameOver() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */
	private void testUserWeaponCollisions() {
		for(int i = 0; i < userWeapons.size(); ++i){
			for(EnemyShip enemy : enemies){
				boolean collision = testWeaponCollision(userWeapons.get(i), enemy);
				if(collision){
					enemy.inflictDamage(userWeapons.get(i).getDamage());
				}
			}
//			for(Asteroid asteroid : asteroids){
//				
//			}
		}
		
	}
	/**
	 * @param weapon
	 * @param enemy
	 */
	private boolean testWeaponCollision(RayWeapon weapon, GameObject object) {

		Vector3d[] aNormals = object.getCurrentFaceNormals();
//		Vector3d[] bVerts = objectB.getCurrentVertices();
		
		double[] mins = object.getMinProjectedVals();
		double[] maxs = object.getMaxProjectedVals();
		
		for(int i = 0; i < aNormals.length; ++i){
			double startProj = Vector3d.dot(weapon.start, aNormals[i]);
			double wMin = startProj;
			double wMax = startProj;
			double endProj = Vector3d.dot(weapon.end, aNormals[i]);
			if(endProj < wMin){
				wMin = endProj;
			}
			if(endProj > wMax){
				wMax = endProj;
			}
			double overlap = getOverlap(wMin, wMax, mins[i], maxs[i]);
			if(overlap == 0){
				return false;
			}
		}
		return true;
		
//
//		double wMin = objectB.getMin(projectedVals);
//		double wMax = objectB.getMax(projectedVals);
//		double overlap = getOverlap(aMins[0], aMaxs[0], bMin, bMax);
//		if(overlap == 0){
//			return;
//		} else {
//			minOverlap = overlap;
//			resolveVector = aNormals[0];
//			moveB = true;
//		}
//		for(int i = 1; i < aNormals.length; ++i){
//			projectedVals = objectB.getProjectedValues(aNormals[i]);
//			bMin = objectB.getMin(projectedVals);
//			bMax = objectB.getMax(projectedVals);
//			overlap = getOverlap(aMins[i], aMaxs[i], bMin, bMax);
//			if(overlap == 0){
//				return;
//			} else if(overlap < minOverlap){
//				minOverlap = overlap;
//				resolveVector = aNormals[i];
//			}
//		}
//		
//		double[] bMins = objectB.getMinProjectedVals();
//		double[] bMaxs = objectB.getMaxProjectedVals();
//		for(int i = 0; i < bNormals.length; ++i){
//			projectedVals = objectA.getProjectedValues(bNormals[i]);
//			double aMin = objectA.getMin(projectedVals);
//			double aMax = objectA.getMax(projectedVals);
//			overlap = getOverlap(bMins[i], bMaxs[i], aMin, aMax);
//			if(overlap == 0){
//				return;
//			} else if(overlap < minOverlap){
//				minOverlap = overlap;
//				resolveVector = bNormals[i];
//				moveB = false;
//			}
//		}
		
	}
	/**
	 * 
	 */
	private void testEnemyCollisions() {
		//for each enemy ship
			// test bounding sphere collision between other asteroids
			// if overlap
				// check SAT
			// test bounding sphere collision between other enemy ships
			// if overlap
				// check SAT
			// test bounding sphere collision with home planet
		

		
	}
	/**
	 * 
	 */
	private void testAsteroidCollisions() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */
	private void testUserCollisions() {
		user.projectVertices();
		// for each enemy ship
			// test bounding spheres
			// if spheres collide
				// test SAT


		// test these projected vertices against all other objects
		for(EnemyShip enemy : enemies){
			enemy.projectVertices();
			testCollision(user, enemy);
		}
		for(Asteroid asteroid : asteroids){
			// test against asteroids
		}
		
	}

	/**
	 * @param user2
	 * @param enemy
	 * @return
	 */
	private void testCollision(GameObject objectA, GameObject objectB) {
		double minOverlap = 0;
		boolean moveB = false;
		Vector3d resolveVector = null;
		Vector3d[] aNormals = objectA.getCurrentFaceNormals();
		Vector3d[] bNormals = objectB.getCurrentFaceNormals();
		Vector3d[] aVerts = objectA.getCurrentVertices();
//		Vector3d[] bVerts = objectB.getCurrentVertices();
		
		double[] aMins = objectA.getMinProjectedVals();
		double[] aMaxs = objectA.getMaxProjectedVals();
		
		double[] projectedVals = objectB.getProjectedValues(aNormals[0]);
		double bMin = objectB.getMin(projectedVals);
		double bMax = objectB.getMax(projectedVals);
		double overlap = getOverlap(aMins[0], aMaxs[0], bMin, bMax);
		if(overlap == 0){
			return;
		} else {
			minOverlap = overlap;
			resolveVector = aNormals[0];
			moveB = true;
		}
		for(int i = 1; i < aNormals.length; ++i){
			projectedVals = objectB.getProjectedValues(aNormals[i]);
			bMin = objectB.getMin(projectedVals);
			bMax = objectB.getMax(projectedVals);
			overlap = getOverlap(aMins[i], aMaxs[i], bMin, bMax);
			if(overlap == 0){
				return;
			} else if(overlap < minOverlap){
				minOverlap = overlap;
				resolveVector = aNormals[i];
			}
		}
		
		double[] bMins = objectB.getMinProjectedVals();
		double[] bMaxs = objectB.getMaxProjectedVals();
		for(int i = 0; i < bNormals.length; ++i){
			projectedVals = objectA.getProjectedValues(bNormals[i]);
			double aMin = objectA.getMin(projectedVals);
			double aMax = objectA.getMax(projectedVals);
			overlap = getOverlap(bMins[i], bMaxs[i], aMin, aMax);
			if(overlap == 0){
				return;
			} else if(overlap < minOverlap){
				minOverlap = overlap;
				resolveVector = bNormals[i];
				moveB = false;
			}
		}
		// if execution reaches here, there is a collision
		objectA.inflictDamage(objectB.getDamage());
		objectB.inflictDamage(objectA.getDamage());
        

		// step 1: resolve penetration
		if(moveB){
			// move B by resolveVector
			objectB.setPosition(Vector3d.add(objectB.getPosition(), Vector3d.scale(minOverlap, resolveVector)));
		} else {
			// move A by resolveVector
			objectA.setPosition(Vector3d.add(objectA.getPosition(), Vector3d.scale(minOverlap, resolveVector)));
		}
		// step 2: calculate new velocities
		// since ships have the same mass, they exchange velocities
		Vector3d vA = objectA.getVelocity();
		Vector3d vB = objectB.getVelocity();
		objectA.setVelocity(vB);
		objectB.setVelocity(vA);
	}
	
	
	
	/**
	 * @param userMin
	 * @param userMax
	 * @param enemyMin
	 * @param enemyMax
	 * @return
	 */
	private double getOverlap(double minA, double maxA, double minB, double maxB) {
		if(minA >= maxB || minB >= maxA){
			return 0; // no overlap
		} else {
			if(minA <= minB){
				return maxA-minB;
			} else {
				return maxB-minA;
			}
		}
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
		random = new Random();
		glu = new GLU();
        spawnTimer = 0;  
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

		Matrix I = new Matrix(3, 3);
		I.set(0,0, .15*.15+1.2*1.2);
		I.set(0,1, 0);
		I.set(0,2, 0);
		
		I.set(1,0, 0);
		I.set(1,1, .8*.8+1.2*1.2);
		I.set(1,2, 0);
		
		I.set(2,0, 0);
		I.set(2,1, 0);
		I.set(2,2, .8*.8+.15*.15);
		user = new UserShip(10);
		
		
		Starscape = new StarScape(gl);
		StarscapeCube = new StarScapeCube(gl);
		
		enemies = new ArrayList<EnemyShip>();
		enemies.add(new EnemyShip(10, new Vector3d(1,0,100)));
		asteroids = new ArrayList<Asteroid>();
		
		userWeapons = new ArrayList<RayWeapon>();
		
		
		this.getContentPane().getComponent(0).addKeyListener(this);
		this.getContentPane().getComponent(0).addMouseListener(this);
		this.getContentPane().getComponent(0).requestFocus();
		
        try {
//            earth = TextureIO.newTexture(new File("resources/quom.png"), false);

	        InputStream stream = getClass().getResourceAsStream("planet2.jpg");
	        TextureData data = TextureIO.newTextureData(glp, stream, false, "jpg");
	        earth = TextureIO.newTexture(data);
	        stream = getClass().getResourceAsStream("asteroid1.jpg");
	        data = TextureIO.newTextureData(glp, stream, false, "jpg");
	        asteroidTexture = TextureIO.newTexture(data);
	        stream = getClass().getResourceAsStream("klendathu.png");
	        data = TextureIO.newTextureData(glp, stream, false, "png");
	        randomTexture = TextureIO.newTexture(data);
	        stream = getClass().getResourceAsStream("starscape2.jpg");
//	        stream = getClass().getResourceAsStream("starfield.jpg");
	        data = TextureIO.newTextureData(glp, stream, false, "jpg");
	        starTexture = TextureIO.newTexture(data);
        } catch (IOException e) {    
            javax.swing.JOptionPane.showMessageDialog(null, e);
        }
        
        try{
//            InputStream shootStream = getClass().getResourceAsStream("shoot.wav");
//            shootAudio = AudioSystem.getAudioInputStream(shootStream);
//            
//            
//            InputStream damageStream = getClass().getResourceAsStream("enemyDamage.wav");
//            damageAudio = AudioSystem.getAudioInputStream(damageStream);
//            DataLine.Info damageInfo = new DataLine.Info(Clip.class, damageAudio.getFormat());
//            damageClip = (Clip) AudioSystem.getLine(damageInfo);
//            damageClip.open(damageAudio);
//            damageClip.addLineListener(new LineListener() {
//                public void update(LineEvent event) {
//                  if (event.getType() == LineEvent.Type.STOP) {
//                    event.getLine().close();
//                    System.exit(0);
//                  }
//                }
//              });
        	
        	
        	
            InputStream stream1 = getClass().getResourceAsStream("ambient1.wav");
            AudioInputStream audio1 = AudioSystem.getAudioInputStream(stream1);
//            DataLine.Info info = new DataLine.Info(Clip.class, damageAudio.getFormat());
//            clip1 = (Clip) AudioSystem.getLine(info);
            clip1 = AudioSystem.getClip();
            clip1.open(audio1);
            clip1.loop(1000);
//            clip1.start();
//            stream1 = getClass().getResourceAsStream("laserCannon.wav");
//            AudioInputStream audio2 = AudioSystem.getAudioInputStream(stream1);
//            clip2 = AudioSystem.getClip();
//            clip2.open(audio2);
            //
            //Do clip2.start(); when ever you fire a missile
            //
            //
            
        } catch(IOException e){
           System.out.println(e); 
        } catch(UnsupportedAudioFileException uae){
            System.out.println(uae);
        } catch(LineUnavailableException lae){
            System.out.println(lae);
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
		gameClock = 0;
		lastFrame = System.currentTimeMillis();
		animator.start();
	}


	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_N){
			user.toggleNorms();
		}
		user.updateKeyPressed(e.getKeyCode(), true);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		user.updateKeyPressed(e.getKeyCode(), false);		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		int button = e.getButton();
		if(button == MouseEvent.BUTTON1){
			user.thrust(true);
		} else if(!button2clicked){
			button2clicked = true;
			fireUserWeapon();
		}
	}


	/**
	 * 
	 */
	private void fireUserWeapon() {
		RayWeapon weapon = new RayWeapon(user.getPosition(), Vector3d.scale(-1, user.getRollAxis()));
		userWeapons.add(weapon);
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			user.thrust(false);
		} else {
			button2clicked = false;
		}
	}

}