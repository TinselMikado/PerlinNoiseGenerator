package main;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
import java.util.Random;


@SuppressWarnings("serial")
public class NoiseGen extends Canvas implements Runnable{
	
	Random r;
	//private int[][] pixels;
	private double[] noiseArray1;
	
	private static final int WIDTH = 512, HEIGHT = WIDTH;
	private boolean running = false;
	private Thread thread;
	private static final int levels = 8;
	private int xar = (int) Math.pow(2, levels);
	private boolean draw = false;
	
	public NoiseGen() {
		
		r = new Random();
		new Frame(WIDTH, HEIGHT, "Noise", this);
		
		noiseArray1 = new double[xar]; //double[time]
		generate1D(noiseArray1, levels);
		//createPureNoise(noiseArray1);
		flattenNoise(noiseArray1, 6, 7);
		//setArrayAverage(noiseArray1, 50);
		draw = true;
		//System.out.println("draw boxes");
		
		
	}

	public void generate1D(double[] arr, int iterations) {
		
		int len = arr.length;
		double[] arrayHolder = new double[len]; //create main array
		
		for(int i = 1; i <= iterations; i++) { //i = number of current iteration
			
			int thisIterationArrayLength = (int) Math.pow(2, i); // first iteration, arraylength = 2, second iter. array length = 4, 3rd it , AL = 8....
			double[] nA = new double[thisIterationArrayLength]; // new array with array length of ^^^^^
			int chunk = len / thisIterationArrayLength; //chunk splits main array (((arrayHolder))) into (iteration array length) parts eg.   first iteration, chunk = xar / 2,   second  chunk = xar/4
			
			createPureNoise(nA); //creates array of random double 0-100
			
			for (int j = 0; j<len; j += chunk) {//j = current position in main array
				
				for(int y = 0; y<chunk; y++) { //y current position in chunk
					arrayHolder[j+y] += nA[j / chunk]; //sets all values in the current chunk to random double 0-100
				}
				
			}
			
			
			
		}
		for(int u = 0; u < xar; u++) {
			noiseArray1[u] += arrayHolder[u] / iterations;
		}
				
	}
	
	public void createPureNoise(double[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = 100* r.nextDouble();
		}
	}
	
	public void setArrayAverage(double[] arr, double av) {
		
		double arrAv = getAverageValue(arr);
		for (int i = 0; i < arr.length; i++) {
			arr[i] *= av / arrAv;
		}
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				tick();
				delta--;
			}
			if(running)
				draw();
				frames++;
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);;
				frames = 0;
			}
		}
		stop();
	}
	
	public double getMaxValue(double[] na) {
		double r = 0;
		for (int i = na.length-1; i >= 0; i--) {
			if (na[i] > r)
				r = na[i];
		}
		return r;
	}
	
	public double getAverageValue(double[] na) {
		double sum = 0;
		for (double i : na) {
			sum += i;
		}
		return sum / na.length;
	}
	
	public void flattenNoise(double[] na, int iterations, int range) {
		
		double[] storedV = na;
		
		for (int i = 0; i < iterations; i++) {
			for(int t = 0; t < na.length; t++) {
				if(t-range>=0 && t+range < na.length) {
					double sum = 0;
					int counter = 0;
					for(int t_ = t-range; t_ <= t+range; t_++) {
						counter++;
						sum += storedV[t_];
					}
					double av = (2*(sum / (counter - 1)) + storedV[t]) / 3;
					//System.out.println(counter + ", " + 2*range);
					na[t] = av; //if t has full range, loop through values from t-range to t+range, find average, set na[t] to average
					//System.out.println(t+ ", " + av);
				}
				else if (t-range < 0) {
					double sum = 0;
					int counter = 0;
					for(int t_ = 0; t_ <= t+range; t_++) {
						sum += storedV[t_];
						counter++;
					}
					double av = (2*(sum / (counter - 1)) + storedV[t]) / 3;
					na[t] = av;
				}
				else if (t+range >= na.length) {
					double sum = 0;
					int counter = 0;
					for(int t_ = t-range; t_ <= na.length - 1; t_++) {
						sum += storedV[t_];
						counter++;
					}
					double av = (2*(sum / (counter - 1)) + storedV[t]) / 3;
					na[t] = av;
				}
				else {
					System.out.println("Range too large for noise array");
					return;
				}
			}
		}
		
	}
	
	public void averageArray(double[] na, int range) {
		for (int i = -range; i <=range; i++) {
			
		}
	}
	
	public void draw() {
		
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.white);
		g.fillRect(0,0,WIDTH,HEIGHT);
		
		g.setColor(Color.black);
		if(draw) {
			for(int i = 0; i<xar; i++) {
				g.fillRect(WIDTH * i / xar, HEIGHT, 1 + WIDTH / (int)Math.pow(2,levels), -(int)(noiseArray1[i] * HEIGHT / 100));
				//System.out.println(i + ", " + noiseArray1[i]);
				//System.out.println(WIDTH + ", " + xar + ", " + (int)Math.pow(2,levels+1));
			}
		}
		
		
		g.dispose();
		bs.show();
		
	}
	
	public void tick() {
		
	}
	
	public void start() {
		if(running)
			return;
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public void stop() {
		if(!running)
			return;
		try {
			thread.join();
			running = false;
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public static void main(String args[]) {
		new NoiseGen();
	}
	
}
