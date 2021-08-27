import java.util.*;
import java.io.*;

public class Neuron
{
	double[] weights;
	Random r = new Random();
	int
		BMU = 0, 	// Testing
		hits = 0; 	// Training BMUs
	ArrayList<String> titles, cats;
	Neuron above, below, right, left;
	int x, y;
	double error = 0.0, error2 = 0.0;
	
	public Neuron(int inputs)
	{
		weights = new double[inputs];
		for(int i = 0; i < inputs; i++)
		{
			weights[i] = 0.5 + r.nextGaussian() / 2.0; // Randomized weights
		}
		titles = new ArrayList<String>();
		cats = new ArrayList<String>();
	}
	
	public Neuron(int inputs, Neuron n1, Neuron n2, int rule)
	{
		weights = new double[inputs];
		
		switch(rule)
		{
			case 1:
				for(int i = 0; i < weights.length; i++)
				{
					weights[i] = n1.weights[i] - (n2.weights[i] - n1.weights[i]);
				}
				break;
			case 2:
				for(int i = 0; i < weights.length; i++)
				{
					weights[i] = (n2.weights[i] + n1.weights[i])/2;
				}
				break;
			case 3:
				double sum1 = 0.0, sum2 = 0.0, subtract = 0.0;
				for(int i = 0; i < weights.length; i++)
				{
					sum1 += n1.weights[i];
					sum2 += n2.weights[i];
				}
				if(sum2 > sum1)
				{
					for(int i = 0; i < weights.length; i++)
					{
						weights[i] = n1.weights[i] - (n2.weights[i] - n1.weights[i]);
					}
				}
				else
				{
					for(int i = 0; i < weights.length; i++)
					{
						weights[i] = n1.weights[i] + (n1.weights[i] - n2.weights[i]);
					}
				}
				break;
			case 4:
				double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
				for(int i = 0; i < weights.length; i++)
				{
					if(n1.weights[i] > max)
						max = n1.weights[i];
					if(n1.weights[i] < min)
						min = n1.weights[i];
				}
				for(int i = 0; i < weights.length; i++)
				{
					weights[i] = (min + max) /2;
				}
				break;
			default: break;
		}
		
		titles = new ArrayList<String>();
		cats = new ArrayList<String>();
		error = 0.2 * n1.error;
	}
	
	public void changeWeights(double lRate, int[] inputVector, double theta)
	{
		double value;
		for(int i = 0; i < weights.length; i++)
		{
			value = (double)inputVector[i]/(double)inputVector[inputVector.length-1];
			weights[i] = weights[i] + theta * lRate *  (value - weights[i]);
		}
	}
	
	public void distributeWeights(Neuron n)
	{
		for(int i = 0; i < weights.length; i++)
		{
			weights[i] = (weights[i] + n.weights[i]) / 2.0;
		}
		
		error += 0.2 * n.error;
	}
	
	public double getDistance(int[] inputVector)
	{
		double distance = 0.0;
		double value;
		for(int i = 0; i < weights.length; i++)
		{
			value = (double)inputVector[i]/(double)inputVector[inputVector.length-1];
			distance += Math.pow(value - weights[i], 2);
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	
	public double getCosSimilarity(int[] inputVector)
	{
		double similarity = 0.0, magnitudeW = 0.0, magnitudeI = 0.0;
		
		for(int i = 0; i < weights.length; i++)
		{
			similarity += weights[i] * ((double)inputVector[i]/(double)inputVector[inputVector.length-1]);
			magnitudeW += weights[i] * weights[i];
			magnitudeI += Math.pow((double)inputVector[i]/(double)inputVector[inputVector.length-1], 2);
		}
		
		return 2* (1.0 - similarity / (Math.sqrt(magnitudeI) * Math.sqrt(magnitudeW)));
	}
	
	public int getHighestIndex()
	{
		int index = 0;
		double max = Double.MIN_VALUE;
		for(int i = 0; i < weights.length; i++)
		{
			if(weights[i] > max)
			{
				index = i;
				max = weights[i];
			}
		}
		
		return index;
	}
	
	public int[] getHighestFive()
	{
        double[] copy = Arrays.copyOf(weights,weights.length);
        Arrays.sort(copy);
        double[] honey = Arrays.copyOfRange(copy,copy.length - 5, copy.length);
        int[] result = new int[5];
        int resultPos = 0;
        for(int i = 0; i < weights.length; i++)
		{
            double onTrial = weights[i];
            int index = Arrays.binarySearch(honey,onTrial);
            if(index < 0) continue;
            result[resultPos++] = i;
        }
        return result;
    }
	
	public void printResults(int k, int j)
	{
		try
		{
			PrintWriter out = new PrintWriter("Results-" + k + "-" + j);
			for(int i = 0; i < cats.size(); i++)
			{
				out.println(titles.get(i) + "\t\t" + cats.get(i));
			}
			out.close();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
}