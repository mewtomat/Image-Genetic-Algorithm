import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.*;

class Rectangle {
	public int x, y;										// the coordinates of the upper left of the rectangle
	public int width, height;								// the dimensions of the rectangle
	public int red, green, blue;							// the RGB values of the color of the rectangle
	public int alpha; 										// the alpha value of the color
	public int time;										// the time when this rectangle was added to the member to which it belongs

	public boolean isPointInside(int i, int j) {
		return (i >= x && i < x + width && j <= y && j > y - height) ;
	}

	public Rectangle() {

	}

	public Rectangle(Rectangle other) {
		this.x = other.x ;
		this.y = other.y ;
		this.red = other.red ;
		this.green = other.green ;
		this.blue = other.blue ;
		this.alpha = other.alpha ;
		this.time = other.time ;
		this.width = other.width;
		this.height = other.height;
	}
}

class Genotype {
	public Rectangle rectangles[] = new Rectangle[GeneticAlgorithmRec.rectangleCount];								// each member of population contains a list of rectangles
	public int count;										// stores the number of rectangles in the present member of population
	public int bg_red, bg_green, bg_blue;					// the RGB values of the background
	public int bg_alpha;									// the alpha value of the background
	public double fitness;
	public double cfitness;
	public double rfitness;

	public Genotype() {
		for(int i = 0 ; i < rectangles.length ; i++)
			this.rectangles[i] = new Rectangle();
	}

	public Genotype(Genotype other) {
		for(int i = 0 ; i < rectangles.length ; i++)
			this.rectangles[i] = new Rectangle(other.rectangles[i]);
		this.count = other.count ;
		this.bg_red = other.bg_red ;
		this.bg_green = other.bg_green ;
		this.bg_blue = other.bg_blue ;
		this.bg_alpha = other.bg_alpha ;
		this.fitness = other.fitness ;
		this.cfitness = other.cfitness ;
		this.rfitness = other.rfitness ;
	}

	public Color getColorOfPoint(int i, int j) {
		TreeMap <Integer, Rectangle> treemap = new TreeMap <Integer, Rectangle> ();
		for(int k = 0 ; k < rectangles.length ; ++k) {
			if(rectangles[k].isPointInside(i, j))
				treemap.put(rectangles[k].time, rectangles[k]);
		}
		// Color color = new Color(bg_red, bg_green, bg_blue);

		Iterator it = treemap.entrySet().iterator();
		// int entries = 0;
		// int sum_r = 0, sum_g = 0, sum_b=0;
		double color_red = bg_red, color_green = bg_green, color_blue = bg_blue, color_alpha = bg_alpha; 
		while(it.hasNext()) {
			// entries ++;
			Map.Entry entry = (Map.Entry)it.next();
			Rectangle c = (Rectangle)entry.getValue();
			double t = (double)(c.alpha/255.0) ;
			// double new_bg_alpha = (double)(color.getAlpha()/255.0);
			double new_alpha=(double)(t  + (1.0 - t) * color_alpha);
			// System.out.println("red = "+Float.toString((double)((t * c.red + (1.0 - t) * color.getRed()*new_bg_alpha )/new_alpha)));
			// int bg_red = color.getRed();
			// int bg_green = color.getGreen();
			// int bg_blue = color.getBlue();
			// sum_r += c.red;
			// sum_g += c.green;
			// sum_b += c.blue;
			color_red = (t*c.red + (1.0-t)*color_red*color_alpha)/new_alpha;
			color_green = (t*c.green + (1.0-t)*color_green*color_alpha)/new_alpha;
			color_blue = (t*c.blue + (1.0-t)*color_blue*color_alpha)/new_alpha;
			color_alpha = new_alpha;
			// color = new Color((int)((t * c.red + (1.0 - t) *bg_red *new_bg_alpha )/new_alpha), (int)((t * c.green + (1.0 - t) * bg_green*new_bg_alpha)/new_alpha), (int)((t * c.blue + (1.0 - t) * bg_blue*new_bg_alpha)/new_alpha),(int)(new_alpha*255.0) );	
		};
		Color color = new Color((int)color_red, (int)color_green, (int)color_blue, (int)(255*color_alpha) );
		// if(entries !=0)
		// {
		// 		sum_r = (int)(sum_r/(entries*1.0));
		// 		sum_g = (int)(sum_g/(entries*1.0));
		// 		sum_b = (int)(sum_b/(entries*1.0));
		// 		color = new Color(sum_r, sum_g, sum_b);
		// }

		return color;
	}
	
	public int abs(int diff)
	{
			if(diff < 0) return -1*diff;
			return diff;
	}
	public int idnt(int diff)
	{
			if(diff == 0)
				return 1;
			return 0;
	}

	public double getFitness(int[][] result) {
		double ans = 0;
		for(int i = 0 ; i < result.length ; i++) {
			for(int j = 0 ; j < result[0].length ; j++) {
				Color m = getColorOfPoint(i, j);
				Color n = new Color(result[i][j]);
				double moda = Math.sqrt(m.getRed()*m.getRed() + m.getGreen()*m.getGreen()+m.getBlue()*m.getBlue());
				double modb = Math.sqrt(n.getRed()*n.getRed() + n.getGreen()*n.getGreen()+n.getBlue()*n.getBlue());
				// System.out.println(((m.getRed()*n.getRed())+(m.getGreen()*n.getGreen())+(m.getBlue()+n.getBlue()))/(moda*modb));
				// ans += ((m.getRed()*n.getRed())+(m.getGreen()*n.getGreen())+(m.getBlue()+n.getBlue()))/((1+moda)*(modb+1));
				ans += ((Math.pow(2,abs(m.getRed() - n.getRed())/255.0) + Math.pow(2,abs(m.getGreen() - n.getGreen())/255.0) + Math.pow(2,abs(m.getBlue() - n.getBlue())/255.0)) )/6.0;
				//ans += abs((m.getRed() - n.getRed())) + abs((m.getGreen() - n.getGreen())) + abs((m.getBlue() - n.getBlue()));
				//ans += idnt((m.getRed() - n.getRed())) + idnt((m.getGreen() - n.getGreen())) + idnt((m.getBlue() - n.getBlue()));
			}
		}
		// System.out.println(ans);
		this.fitness = 100000.0/ans;
		return this.fitness;
	}
	public void print(){
		//for(int i=0;i<GeneticAlgorithm.rectangleCount;++i)
		for(int i=0;i<-1;++i)
			{
				Rectangle temp = new Rectangle(rectangles[i]);
					//System.out.println("x = "+Integer.toString(temp.x)+" |y = "+Integer.toString(temp.y)+" |radius = "+Integer.toString(temp.radius) + " |red= "+ Integer.toString(temp.red) + " |green = "+ Integer.toString(temp.green)+" |blue= "+Integer.toString(temp.blue)+" |alpha= "+Float.toString(temp.alpha)+" |time= "+Integer.toString(temp.time));
			}
	}
}

public class GeneticAlgorithmRec {

	private static int[][] convertTo2D(BufferedImage image) {

		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		//Raster raster = image.getRaster();
		final int width = image.getWidth();
		final int height = image.getHeight();

		int[][] result = new int[height][width];
		if (image.getAlphaRaster() != null) {
			System.out.println("You are screwed");
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				//argb += -16777216;									  // 255 alpha
				//argb += (((int) pixels[pixel] & 0xff) << 24);		   // alpha
				argb += ((int) pixels[pixel + 1] & 0xff);			   // blue
				argb += (((int) pixels[pixel + 2] & 0xff) << 8);		// green
				argb += (((int) pixels[pixel + 3] & 0xff) << 16);	   // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		} 
	  else {
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				//argb += -16777216;									  // 255 alpha
				argb += ((int) pixels[pixel] & 0xff);				   // blue
				argb += (((int) pixels[pixel + 1] & 0xff) << 8);		// green
				argb += (((int) pixels[pixel + 2] & 0xff) << 16);	   // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		}
		return result;
	}

 	public static void convertColorArrayToImage(int[][] color, int generation, int member, String imageName, String directory) {
 		String curr = imageName ;
 		// if(generation < 10)
 		// 	curr = curr + "000" ;
 		// else if(generation < 100)
 		// 	curr = curr + "00" ;
 		// else if(generation < 1000)
 		// 	curr = curr + "0" ;
		String path = directory+curr +"-"+ Integer.toString(generation) + ".jpg";
		//String path = "result" + Integer.toString(generation) + ".jpg";
		BufferedImage image = new BufferedImage(color[0].length, color.length, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < color.length; i++)
		  	for(int j = 0; j < color[0].length; j++)
				image.setRGB(j, i, color[i][j]);

		File ImageFile = new File(path);
		try {
	  		ImageIO.write(image, "jpg", ImageFile);
		}
		catch(IOException e) {
	  		e.printStackTrace();
		}
 	}

 	public static int POPSIZE = 10;
 	public static int MAXGENS = 150000;
 	public static double PXOVER = 0.8;
 	public static double PMUTATION = 0.1;
 	public static Genotype population[];
 	public static Genotype newpopulation[];
 	public static int row, col;
 	public static int rectangleCount = 30;
 	public static int[][] result;
 	public static int WIDTHLIMIT=175;
 	public static int HEIGHTLIMIT=175;
 	public static double PMUTATIONCOLOR = .2;

 	public static void crossover() {
 		int mem;
		int one = 0;
		int first = 0;
		double x;

		for(mem = 0; mem < POPSIZE; ++mem) {
			Random randomGenerator = new Random();
			x = (randomGenerator.nextInt(1000)) / 1000.0;
			if(x < PXOVER) {
				++first;
				if(first % 2 == 0)
					Xover(one, mem);
				else
					one = mem;
			}
		}
 	}

 	public static void elitist() {
 		int i;
		double best;
		int best_mem = 0;
		double worst;
		int worst_mem = 0;

		best = population[0].fitness;
		worst = population[0].fitness;

		for(i = 0; i < POPSIZE ; ++i) {
			if(population[i].fitness > best) {
				best = population[i].fitness;
				best_mem = i ;
			}
			if(population[i].fitness < worst) {
				worst = population[i].fitness ;
				worst_mem = i ;
			}
		}
	//
	//	If the best individual from the new population is better than
	//	the best individual from the previous population, then
	//	copy the best from the new population; else replace the
	//	worst individual from the current population with the
	//	best one from the previous generation										
	//
		if(best >= population[POPSIZE].fitness)
			population[POPSIZE] = new Genotype(population[best_mem]);
		else
			{
			population[worst_mem] = new Genotype(population[POPSIZE]);
			}
 	}

 	public static void evaluate() {
		//System.out.println("inside evaluate");
		for(int member = 0; member < POPSIZE; member++)
			population[member].getFitness(result);
 	}

 	public static void initialize() {
 		population = new Genotype[POPSIZE + 1];
 		newpopulation = new Genotype[POPSIZE + 1];
 			
 		Random randomGenerator = new Random();

 		for(int i = 0 ; i <= POPSIZE ; i++) {
 			population[i] = new Genotype();
 			newpopulation[i] = new Genotype();
 			population[i].count = rectangleCount;
 			for(int j = 0 ; j < rectangleCount ; j++) {
 				population[i].rectangles[j].x = new Integer(randomGenerator.nextInt(row));
				population[i].rectangles[j].y = new Integer(randomGenerator.nextInt(col));
				population[i].rectangles[j].blue = new Integer(randomGenerator.nextInt(255));
				population[i].rectangles[j].red = new Integer(randomGenerator.nextInt(255));
				population[i].rectangles[j].green = new Integer(randomGenerator.nextInt(255));
				//System.out.println(i + "\t" + j + "\t" + population[i].rectangles[j].red + "\t" + population[i].rectangles[j].green + "\t" + population[i].rectangles[j].red);
				population[i].rectangles[j].alpha = new Integer(randomGenerator.nextInt(255));
				population[i].rectangles[j].width = new Integer(randomGenerator.nextInt(WIDTHLIMIT));
				population[i].rectangles[j].height = new Integer(randomGenerator.nextInt(HEIGHTLIMIT));
				population[i].rectangles[j].time = new Integer(randomGenerator.nextInt(10000));
				//System.out.println("Radius = " + population[i].rectangles[j].radius);
 			}
 			population[i].bg_red =255;
			population[i].bg_green = 255;
			population[i].bg_blue = 255;
			population[i].bg_alpha = 0;
 		}
 		//System.out.println("inside initialize");
 		//population[POPSIZE].print();
 	}

 	public static void keep_the_best() {
 		int cur_best;
		int mem;
		int i;

		cur_best = 0;

		for(mem = 1; mem < POPSIZE; mem++) {
			if(population[mem].fitness > population[cur_best].fitness)
				cur_best = mem;
		}
	//
	//	Once the best member in the population is found, copy the genes.
	//
		population[POPSIZE] = new Genotype(population[cur_best]);
 	}

 	public static void mutate() {
		for(int i = 0; i < POPSIZE; i++) {
			Random randomGenerator = new Random();
			for(int j = 0 ; j<rectangleCount;++j)
			{
				double x = randomGenerator.nextInt(10000) / 10000.0;
					//System.out.println("x = " + x);
				if(x<PMUTATION)
				{
					 x= randomGenerator.nextInt(10000) / 10000.0;
					if(x < PMUTATIONCOLOR) {						
						//int j = new Integer(randomGenerator.nextInt(rectangleCount));
						//System.out.println("Mutating");
						//System.out.println("Value = " + population[i].rectangles[j].x);
						population[i].rectangles[j].x = new Integer(randomGenerator.nextInt(row));
					}
					 x= randomGenerator.nextInt(10000) / 10000.0;
					if(x < PMUTATIONCOLOR) {	
						//System.out.println("Value = " + population[i].rectangles[j].x);
						population[i].rectangles[j].y = new Integer(randomGenerator.nextInt(col));
					}
					x= randomGenerator.nextInt(10000) / 10000.0;
					if(x < PMUTATIONCOLOR) {	
						population[i].rectangles[j].red = new Integer(randomGenerator.nextInt(255));
					}
					x= randomGenerator.nextInt(10000) / 10000.0;
					if(x < PMUTATIONCOLOR) {	
						population[i].rectangles[j].green = new Integer(randomGenerator.nextInt(255));
					}
					x= randomGenerator.nextInt(10000) / 10000.0;
					if(x < PMUTATIONCOLOR) {	
						population[i].rectangles[j].blue = new Integer(randomGenerator.nextInt(255));
					}
					x= randomGenerator.nextInt(10000) / 10000.0;
					if(x < PMUTATIONCOLOR) {	
						population[i].rectangles[j].alpha = new Integer(randomGenerator.nextInt(255));
					}
					x= randomGenerator.nextInt(10000) / 10000.0;
					if(x < PMUTATION) {	
						population[i].rectangles[j].width = new Integer(randomGenerator.nextInt(WIDTHLIMIT));
					}
					x= randomGenerator.nextInt(10000) / 10000.0;
					if(x < PMUTATION) {	
						population[i].rectangles[j].height = new Integer(randomGenerator.nextInt(HEIGHTLIMIT));
					}
					x= randomGenerator.nextInt(10000) / 10000.0;
					if(x < PMUTATION) {	
						population[i].rectangles[j].time = new Integer(randomGenerator.nextInt(10000));
					}
				}
			}
		}
			//population[i].bg_red = randomGenerator.nextInt(255);
			//population[i].bg_green = randomGenerator.nextInt(255);
			//population[i].bg_blue = randomGenerator.nextInt(255);
			//population[i].bg_alpha = randomGenerator.nextInt(255);
 	}

 	public static double randval(double low, double high) {
 		Random randomGenerator = new Random();
		return ((double)(randomGenerator.nextInt(1000)) / 1000.0) *(high - low) + low;

 	}

 	public static void selector() {
 		int i, j, mem;
		double p, sum = 0.0;
	//
	//	Find total fitness of the population
	//
		for(mem = 0; mem < POPSIZE; mem++)
			sum = sum + population[mem].fitness;
	//
	//	Calculate the relative fitness.
	//
		for(mem = 0; mem < POPSIZE; mem++)
			population[mem].rfitness = population[mem].fitness / sum;	 
		
		population[0].cfitness = population[0].rfitness;
	//
	//	Calculate the cumulative fitness.
	//
		for(mem = 1; mem < POPSIZE; mem++)
			population[mem].cfitness = population[mem-1].cfitness +	population[mem].rfitness;
	//
	//	Select survivors using cumulative fitness.
	//
		// System.out.println("cfitness values new");
		// for(int z = 0 ; z < POPSIZE ; z++) {
		// 	System.out.println(z + " : " + population[z].cfitness);
		// }

		for(i = 0; i < POPSIZE; i++)
		{
			Random randomGenerator = new Random();
			p = (randomGenerator.nextInt(1000)) / 1000.0;
			//System.out.println("p = " + p);
			if(p < population[0].cfitness) {
	//			System.out.println(i + " Picking : " + 0);
				newpopulation[i] = new Genotype(population[0]);
			}
			else
			{
				for(j = 0; j < POPSIZE; j++) {
					if(p >= population[j].cfitness && p < population[j+1].cfitness) {
	//					System.out.println(i + " Picking : " + (j + 1));
						newpopulation[i] = new Genotype(population[j+1]);
						break ;
					}
				}
			}
		}
	//
	//	Once a new population is created, copy it back
	//
		// System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		// System.out.println("New pop");
		// for(i = 0; i < POPSIZE; i++) {
		// 	System.out.println(newpopulation[i].fitness);
		// 	population[i] = new Genotype(newpopulation[i]);
		// }
		// System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
 	}

 	public static void Xover(int one, int two) {
 		Genotype a = new Genotype(population[one]);
 		Genotype b = new Genotype(population[two]);

 		Random randomGenerator = new Random();
 		int k = randomGenerator.nextInt(a.rectangles.length);
 		for(int i = 0 ; i < k ; i++) {
 			Rectangle tmp = new Rectangle(a.rectangles[i]);
 			a.rectangles[i] = new Rectangle(b.rectangles[i]) ;
 			b.rectangles[i] = new Rectangle(tmp) ;
 		}
 		
 		// int j=k;																			//two point crossover
 		// while(j==k) {j = randomGenerator.nextInt(a.rectangles.length);}
 		// for(int i = 0 ; i < j ; i++) {
 		// 	Rectangle tmp = new Rectangle(a.rectangles[i]);
 		// 	a.rectangles[i] = new Rectangle(b.rectangles[i]) ;
 		// 	b.rectangles[i] = new Rectangle(tmp) ;
 		// }
 		
 		population[one] = new Genotype(a) ;
 		population[two] = new Genotype(b) ;
 	}
		
	

 	public static void main(String[] args) throws IOException {

 		PrintWriter writer = new PrintWriter(args[1]+"info.txt", "UTF-8");
		writer.println("The first line");
			writer.println("POPSIZE=" + Integer.toString(POPSIZE));
			writer.println("MAXGENS=" + Integer.toString(MAXGENS));
			writer.println("rectangleCount=" + Integer.toString(rectangleCount));
			writer.println("WIDTHLIMIT=" + Integer.toString(WIDTHLIMIT));
			writer.println("HEIGHTLIMIT=" + Integer.toString(HEIGHTLIMIT));
			writer.println("PXOVER=" + Double.toString(PXOVER));
			writer.println("PMUTATION=" + Double.toString(PMUTATION));
			writer.println("PMUTATIONCOLOR=" + Double.toString(PMUTATIONCOLOR));
 	// public static int MAXGENS = 300000;
 	// public static double PXOVER = 0.8;
 	// public static double PMUTATION = 0.2;
 	// public static Genotype population[];
 	// public static Genotype newpopulation[];
 	// public static int row, col;
 	// public static int rectangleCount = 30;
 	// public static int[][] result;
 	// public static int RADIUSLIMIT=70;
 	// public static double PMUTATIONCOLOR = .4;
		// writer.println("The second line");
		writer.close();

		BufferedImage image = ImageIO.read(GeneticAlgorithmRec.class.getResource(args[0] + ".jpg"));	//read the image into the image object
		result = convertTo2D(image);
		row = result.length;
		col = result[0].length;
		Genotype best = null;
		initialize();
		//System.out.println("After Initialize");
			best = new Genotype(population[POPSIZE]);
			best.print();
		evaluate();
		//System.out.println("After Evaluate");
			best = new Genotype(population[POPSIZE]);
			best.print();
		keep_the_best();
		//System.out.println("After Keep_the_best");
			best = new Genotype(population[POPSIZE]);
			best.print();
		
		double old_fitness = -1;
		for(int generation = 0 ; generation < MAXGENS ; generation++) {
			selector();
			//System.out.println("After Selector");
			//best = new Genotype(population[POPSIZE]);
			//best.print();
			// for(int i = 0 ; i <= POPSIZE ; i++)
			// 	population[i].getFitness(result);
			crossover();
			//System.out.println("After Crossover");
			// best = new Genotype(population[POPSIZE]);
			//best.print();
			// for(int i = 0 ; i <= POPSIZE ; i++)
			// 	population[i].getFitness(result);
			mutate();
			//System.out.println("After Mutate");
			 //best = new Genotype(population[POPSIZE]);
			//best.print();
			// for(int i = 0 ; i <= POPSIZE ; i++)
			// 	population[i].getFitness(result);
			evaluate();
			//System.out.println("After Evaluate");
			// best = new Genotype(population[POPSIZE]);
			//best.print();
			// for(int i = 0 ; i <= POPSIZE ; i++)
			// 	population[i].getFitness(result);
			elitist();
			//System.out.println("After Elitist");
			 best = new Genotype(population[POPSIZE]);
			//best.print();
			// for(int i = 0 ; i <= POPSIZE ; i++)
			// 	population[i].getFitness(result);
			//System.out.println("Generation " + generation);
			// for(int i = 0 ; i < POPSIZE ; i++) {
			// 	System.out.println(population[i].fitness);
			// }
			double new_fitness = best.fitness;
			
			if(old_fitness!=new_fitness)
			{
					int answer[][] = new int[row][col];
					for(int i = 0 ; i < row ; i++) {
						for(int j = 0 ; j < col ; j++) {
							Color c = population[POPSIZE].getColorOfPoint(i, j);
							answer[i][j] = (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();
											//answer[i][j] = (c.getRed() << 24) | (c.getGreen() << 16) | (c.getBlue() << 8) | c.getAlpha();
						}
					}
					convertColorArrayToImage(answer, generation,POPSIZE,args[0], args[1]);
			}
			old_fitness = new_fitness;
			System.out.println(generation + "\t" + population[POPSIZE].fitness);
	
		
		}
	}
}
