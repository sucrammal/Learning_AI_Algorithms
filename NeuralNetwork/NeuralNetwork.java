import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

/**
models a simple ANN to solve the Titanic problem
@author Marcus Lam
@version 05/14/2023
*/
public class NeuralNetwork
{
   /** number of features in the input file */
   public static final int NUM_FEATS_READ = 13;

   /** number of features in the model */
   public static final int NUM_FEATS = 9;
   
   /** multiple of inputs to use for number of hidden nodes */
   public static final int HIDDEN_PCT = 1;
   
   /** number of output nodes */
   public static final int NUM_OUTPUTS = 1;
   
   /** number of training epochs */
   public static final int NUM_EPOCHS = 10000;
   
   /** rate at which weights are adjusted */
   public static final double LEARN_RATE = 1.0;
   
   /** array of passenger data */
   private ArrayList<Passenger> passengers;
   
   
   /** Convinient FeatureSet object to store maximum values for age, sibsp, and parch */
   FeatureSet myMaxes = new FeatureSet();
   
   /** Input layer values*/
   private double[] inputs;
   
   /** Weights between input layer and hidden layer */
   private double[][] weights_input;
   
   /** Hidden Layer values*/
   private double[] hidden;
   
   /** Weights between hidden and output layer */
   private double[][] weights_hidden;
   
   /** Stores the output value */
   private double[] outputs;

         
   /** 
   default constructor
   */
   public NeuralNetwork()
   {
      // remove this code once you start the lab
      read_passengers("./titanic/train.csv");
      for(Passenger p : passengers)
      {
         System.out.println(p);
      }
   }
         
   /**
   reads data from input file
   @param filename the file to read
   @return a list of Passenger objects
   */
   public ArrayList<Passenger> read_passengers(String filename)
   {
      passengers = new ArrayList<Passenger>();
      
      try
      {
         BufferedReader reader = new BufferedReader(new FileReader(filename));
         
         String line;
         line = reader.readLine();
         line = reader.readLine();
         while(line != null)
         {
            String[] read = line.split(",");
            
            String[] parts = new String[NUM_FEATS_READ];
            for(int idx = 0; idx < read.length; idx++)
            {  
               if(read[idx].equals(""))
               {
                  parts[idx] = "-1";
               }
               else
               {
                  parts[idx] = read[idx];
               }
            }
            
            if(read.length == NUM_FEATS_READ - 1)
            {
               parts[NUM_FEATS_READ - 1] = "-1";
            }
            else
            {
               parts[NUM_FEATS_READ - 1] = read[NUM_FEATS_READ - 1];
            }
            
            Passenger p = new Passenger();
            p.id = Integer.parseInt(parts[0]);
            p.survived = Integer.parseInt(parts[1]);
            p.pclass = Integer.parseInt(parts[2]);
            p.name = parts[3] + "," + parts[4];
            
            if(parts[5].equals("male"))
            {
               p.sex = Passenger.MALE;
            }
            else
            {
               p.sex = Passenger.FEMALE;
            }
            
            p.age = Double.parseDouble(parts[6]);
            p.sibsp = Integer.parseInt(parts[7]);
            p.parch = Integer.parseInt(parts[8]);
            p.ticket = parts[9];
            p.fare = Double.parseDouble(parts[10]);
            p.cabin = parts[11];
            
            if(parts[12].equals("S"))
            {
               p.embarked = Passenger.SOUTHAMPTON;
            }
            else if(parts[12].equals("C"))
            {
               p.embarked = Passenger.CHERBOURG;
            }
            else if(parts[12].equals("Q"))
            {
               p.embarked = Passenger.QUEENSLAND;
            }
            
            passengers.add(p);
            
            line = reader.readLine();
         }
      }
      catch(IOException ioe)
      {
         System.err.println(ioe);
         System.exit(1);
      }
      
      return passengers;
   }
   
   /**
   runs sigmoid function for forward propagation
   @param x the input value
   @return sigmoid(x)
   */
   public double sigmoid(double x)
   {
      return 1 / (1 + Math.exp(x * -1));
   }
   
   /**
   runs sigmoid derivative function for backward propagation
   @param x the input value
   @return sigmoid derivative of x
   */
   public double sigmoid_derivative(double x)
   {
      return x * (1 - x);
   }
   
   /**
   runs step function for a given value
   @param x the input value
   @return step function value for x
   */
   public int step_function(double x)
   {
      if(x < 0)
      {
         return 0;
      }
      else
      {
         return 1;
      }
   }
   
   /**
   runs hard max on a set of doubles
   @param vals the set of values
   @return int vector with hard max applied to vals
   */
   public int[] hardMax(double[] vals)
   {
      int maxIdx = 0;
      double maxVal = vals[0];
      
      for(int idx = 1; idx < vals.length; idx++)
      {
         if(vals[idx] > maxVal)
         {
            maxIdx = idx;
         }
      }
      
      int outs[] = new int[vals.length];
      outs[maxIdx] = 1;
      
      return outs;
   }
   
   /**
   rounds a double to two decimal places
   @param val the input value
   @return the rounded value
   */
   public double round2(double val)
   {
      return (Math.round(val * 100.0) / 100.0);
   }
   
   
   // YOUR CODE HERE!
   /**
   extracts features from the input data
   @return array of input elements
   */
   public FeatureSet[] get_feature_data()
   {
      FeatureSet[] list = new FeatureSet[passengers.size()];
      int index = 0;
            
      for (Passenger somePassenger: passengers)
      {
         FeatureSet tempFeatureSet = new FeatureSet();
         tempFeatureSet.featureSet[tempFeatureSet.AGE] = somePassenger.age;
         tempFeatureSet.featureSet[tempFeatureSet.NUM_SIBLINGSPOUSE] = somePassenger.sibsp;
         tempFeatureSet.featureSet[tempFeatureSet.NUM_PARENTSCHILDREN] = somePassenger.parch;
         if (somePassenger.sex == somePassenger.MALE)
         {
            tempFeatureSet.featureSet[tempFeatureSet.SEX_MALE] = 1;
            tempFeatureSet.featureSet[tempFeatureSet.SEX_FEMALE] = 0;
         }
            
         else 
         {
            tempFeatureSet.featureSet[tempFeatureSet.SEX_MALE] = 0;
            tempFeatureSet.featureSet[tempFeatureSet.SEX_FEMALE] = 1;
         }
            
         tempFeatureSet.featureSet[tempFeatureSet.CLASS] = somePassenger.pclass;
            
         if (somePassenger.embarked == somePassenger.SOUTHAMPTON)
         {
            tempFeatureSet.featureSet[tempFeatureSet.EMB_S] = 1;
         }
            
         else if (somePassenger.embarked == somePassenger.CHERBOURG)
         {
            tempFeatureSet.featureSet[tempFeatureSet.EMB_C] = 1;
         }
            
         else 
         {
            tempFeatureSet.featureSet[tempFeatureSet.EMB_Q] = 1;
         }
          
         list[index] = tempFeatureSet; 
         index++;
            
      }
      
      return list;   
   }
   
   /**
   extracts labels from input data
   @return array of output vectors
   */
   public int[][] get_labels()
   {
      int[][] labelList = new int[passengers.size()][1];
      for (int i = 0; i < passengers.size(); i++)
      {
         labelList[i][0] = passengers.get(i).survived;
      }
      
      return labelList; 
   }
   
   /**
   finds max vals for age, parents, siblings
   */
   public void find_maxes()
   {
      double maxAge = passengers.get(0).age;
      double maxSibsp = passengers.get(0).sibsp;
      double maxParch = passengers.get(0).parch;
      
      for (int i = 0; i < passengers.size() - 1; i++)
      {
         if (passengers.get(i + 1).age > maxAge)
         {
            maxAge = passengers.get(i + 1).age; 
         }
         
      }
      
      for (int j = 0; j < passengers.size() - 1; j++)
      {
         if (passengers.get(j + 1).sibsp > maxSibsp)
         {
            maxSibsp = passengers.get(j + 1).sibsp; 
         }
         
      }
      
      for (int k = 0; k < passengers.size() - 1; k++)
      {
         if (passengers.get(k + 1).parch > maxParch)
         {
            maxParch = passengers.get(k + 1).parch; 
         }
         
      }
      
      myMaxes.featureSet[myMaxes.AGE] = maxAge;
      myMaxes.featureSet[myMaxes.NUM_SIBLINGSPOUSE] = maxSibsp;
      myMaxes.featureSet[myMaxes.NUM_PARENTSCHILDREN] = maxParch;  
   }
   
   /**
   scales the data so each feature is in [0.0, 1.0]
   @param the data to scale
   @return array with scaled data
   */
   public FeatureSet[] scale_dataset (FeatureSet[] data)
   {
      find_maxes(); 
    
      double totalAge = 0;
      
      for (FeatureSet someFeatureSet: data)
      {
         totalAge += someFeatureSet.featureSet[someFeatureSet.AGE]; 
      }
      
      double meanAge = totalAge / data.length; 
      
      
      for (FeatureSet someFeatureSet: data)
      {
         if (someFeatureSet.featureSet[someFeatureSet.AGE] != -1) // if has a valid age
         {
            someFeatureSet.featureSet[someFeatureSet.AGE] = someFeatureSet.featureSet[someFeatureSet.AGE] / myMaxes.featureSet[myMaxes.AGE]; 
         }
         
         else
         {
            someFeatureSet.featureSet[someFeatureSet.AGE] = meanAge; 
         } 
         
         someFeatureSet.featureSet[someFeatureSet.NUM_SIBLINGSPOUSE] = someFeatureSet.featureSet[someFeatureSet.NUM_SIBLINGSPOUSE] / myMaxes.featureSet[myMaxes.NUM_SIBLINGSPOUSE]; 
         someFeatureSet.featureSet[someFeatureSet.NUM_PARENTSCHILDREN] = someFeatureSet.featureSet[someFeatureSet.NUM_PARENTSCHILDREN] / myMaxes.featureSet[myMaxes.NUM_PARENTSCHILDREN]; 
         someFeatureSet.featureSet[someFeatureSet.CLASS] = 1 / someFeatureSet.featureSet[someFeatureSet.CLASS];
      }
      
      return data; 
      
   }
   
   /**
   sets up network structure
   */
   public void setup_network()
   {
      inputs = new double[NUM_FEATS];
      hidden = new double[NUM_FEATS * HIDDEN_PCT];
      outputs = new double[NUM_OUTPUTS]; 
      weights_input = new double[inputs.length][hidden.length];
      weights_hidden = new double[hidden.length][outputs.length];
      
      for (int i = 0; i < weights_input.length; i++)
      {
         for (int j = 0; j < weights_input[0].length; j++)
         {
            weights_input[i][j] = Math.random(); 
         }
         
      }
      
      for (int k = 0; k < weights_hidden.length; k++)
      {
         for (int p = 0; p < weights_hidden[0].length; p++)
         {
            weights_hidden[k][p] = Math.random(); 
         }
         
      }
      
   }
   
   /**
   runs forward propagation algorithm for a given element
   @param data, the element to process
   @return the vector of output values
   */
   public double[] forward_propagation(FeatureSet data)
   {
      for (int inIdx = 0; inIdx < inputs.length; inIdx++)
      {
         inputs[inIdx] = data.featureSet[inIdx];
      }
      
      //System.out.println(Arrays.toString(inputs));
      
      for (int hIdx = 0; hIdx < hidden.length; hIdx++)
      {
         hidden[hIdx] = 0.0; // reset hidden layer inputs
         
         for (int inIdx = 0; inIdx < inputs.length; inIdx++)
         {
            hidden[hIdx] += inputs[inIdx] * weights_input[inIdx][hIdx]; // computes linear combination of inputs and weights. 
         }
         
         hidden[hIdx] = sigmoid(hidden[hIdx]); // apply sigmoid squish
      }
      
      outputs[0] = 0.0; 
      for (int hIdx = 0; hIdx < hidden.length; hIdx++)
      {
         outputs[0] += hidden[hIdx] * weights_hidden[hIdx][0];
      }
      
      double finalOutput = sigmoid(outputs[0]);
      outputs[0] = sigmoid(outputs[0]);
      
      // System.out.println(finalOutput);
      
      return outputs; 
      
   }
   
   /**
   runs back propagation algorithm to update weights
   @param label_set - the labels to use for cost calculation
   */
   public void back_propagation(int[] label_set)
   {
      for (int outIdx = 0; outIdx < outputs.length; outIdx++)
      {
         // determines the magnitude of our error of outputs, and the direction of our gradient descent. 
         double output_error = sigmoid_derivative(outputs[outIdx]) * (label_set[outIdx] - outputs[outIdx]);  
         
         // assigns "blame" to each hidden node based on the weights of the hiddne nodes, AKA how much they contribute to error. 
         // Derivative also determines the relationship between the hidden node value and the cost. Is it positive or negative? 
         double[] hiddenErrors = new double[hidden.length];   
         for (int hiddenIdx = 0; hiddenIdx < hidden.length; hiddenIdx++)
         {
            hiddenErrors[hiddenIdx] = weights_hidden[hiddenIdx][outIdx] * output_error * sigmoid_derivative(hidden[hiddenIdx]);
         }
         
         // update weight values at hidden to output. Again, adjusts based on how much the node has contributed to the error. 
         for (int hiddenIdx = 0; hiddenIdx < hidden.length; hiddenIdx++) 
         {
            weights_hidden[hiddenIdx][outIdx] = weights_hidden[hiddenIdx][outIdx] + (LEARN_RATE * hidden[hiddenIdx] * output_error);
         }
 
         
         // update weight values at input to hidden.
         for (int hiddenIdx = 0; hiddenIdx < hidden.length; hiddenIdx++)  
         {
            for (int inIdx = 0; inIdx < inputs.length; inIdx++)
            {
               weights_input[inIdx][hiddenIdx] = weights_input[inIdx][hiddenIdx] + (LEARN_RATE * inputs[inIdx] * hiddenErrors[hiddenIdx]);
            
            }
            
         }
         
      }
      
   }
   
   /**
   runs the training algorithm on a network
   @param epochs - the number of epochs to train
   @param filename - the input file of training data
   */
   public void train_neural_network (int epochs, String filename)
   {
      setup_network();
      passengers = read_passengers(filename);
      FeatureSet[] finalFeatureSets = get_feature_data();
      FeatureSet[] scaled_feature_data = scale_dataset(finalFeatureSets);
      int[][] labelList = get_labels();
      for (int i = 0; i < epochs; i++)
      {
         double numCorrect = 0.0;
         for (int idx = 0; idx < scaled_feature_data.length; idx++)
         {
            double[] outputs = forward_propagation(scaled_feature_data[idx]);
            if (labelList[idx][0] == Math.round(outputs[0]))
            {
               numCorrect++; 
            }
            
            back_propagation(labelList[idx]);
         }
         
         System.out.println(numCorrect / scaled_feature_data.length); 
              
      }
         
   }
   
   /**
   runs the test algorithm on a network
   @param filename - the input file of training data
   */
   public void test_neural_network(String filename)
   {
      passengers = read_passengers(filename);
      FeatureSet[] finalFeatureSets = get_feature_data();
      FeatureSet[] scaled_feature_data = scale_dataset(finalFeatureSets);
      int[][] labelList = get_labels();
      double numCorrect = 0.0;
      
      for (int idx = 0; idx < scaled_feature_data.length; idx++)
      {
         String isCorrect = "NO MATCH"; 
         double[] outputs = forward_propagation(scaled_feature_data[idx]);
         if (labelList[idx][0] == Math.round(outputs[0]))
         {
            numCorrect++; 
            isCorrect = "MATCH";
         }
         
         System.out.println(scaled_feature_data[idx] + ", " + isCorrect);
         
      }
      
      System.out.println("TEST% = " + (numCorrect / scaled_feature_data.length));
   }


   public static void main(String[] args)
   {
      NeuralNetwork test = new NeuralNetwork();      
      test.train_neural_network(NUM_EPOCHS, "./titanic/train.csv");
      test.test_neural_network("./titanic/train.csv");
   }
   
   /**
   stores info for each row in data file
   */
   private class Passenger
   {
      /** passenger died */
      public static final int DEAD = 0;
      
      /** passenger survived */
      public static final int ALIVE = 1;
      
      /** passenger is male */
      public static final int MALE = 0;
      
      /** passenger is female */
      public static final int FEMALE = 1;
      
      /** passenger embarked in Southampton */
      public static final int SOUTHAMPTON = 0;
      
      /** passenger embarked in Cherbourg */
      public static final int CHERBOURG = 1;
      
      /** passenger embarked in Queensland */
      public static final int QUEENSLAND = 2;
   
      /** array of toString values for survived */
      public static final String[] LIV_STRS = { "D", "A" };
      
      /** array of toString values for sex */
      public static final String[] SEX_STRS = { "M", "F" };
      
      /** array of toString values for embarked */
      public static final String[] EMB_STRS = { "S", "C", "Q" };
   
      /** unique id value for passenger */
      public int id;
      
      /** indicates whether passenger survived */
      public int survived;
      
      /** class of passenger (1, 2, 3) */
      public int pclass;
      
      /** name of passenger */
      public String name;
      
      /** sex of passenger (male or female) */
      public int sex;
      
      /** age of passenger */
      public double age;
      
      /** number of siblings or spouses for passenger */
      public int sibsp;
      
      /** number of parents or children for passenger */ 
      public int parch;
      
      /** ticket number of passenger */
      public String ticket;
      
      /** cost of fare for passenger */
      public double fare;
      
      /** cabin ID for passenger */
      public String cabin;
      
      /** location where passenger embarked */
      public int embarked;
   
      /**
      converts passenger data to a string
      @return string containing all properties
      */
      public String toString()
      {
         String out = LIV_STRS[survived] + " ";
         out += "#" + id;
         out += ": " + name + " ";
         out += "(" + SEX_STRS[sex] + "/" + age + ") ";
         out += "(" + pclass + "C, T=" + ticket + " F=" + fare + " C=" + cabin + " E=" + EMB_STRS[embarked] + ") "; 
         out += "(S=" + sibsp + ", P=" + parch + ") ";
      
         return out;
      }
   }
   
   /**
   Stores sorted list of features for each passenger. 
   */
   private class FeatureSet
   {
      public double[] featureSet = new double[9];
     
      // column headings for the featureSet – our index values:
      public final int AGE = 0;
      
      public final int NUM_SIBLINGSPOUSE = 1;
      
      public final int NUM_PARENTSCHILDREN = 2;
      
      public final int SEX_MALE = 3;
      
      public final int SEX_FEMALE = 4;
      
      public final int CLASS = 5;
      
      public final int EMB_S = 6;
      
      public final int EMB_C = 7;
      
      public final int EMB_Q = 8; 
      
      
      public FeatureSet()
      {
         
      }
      
      /**
      converts each passenger's featureset to a string
      @return string containing all featureset properties
      */
      public String toString()
      {
         String out = "Age = " + featureSet[AGE] + ", ";
         out += "Sib or Spouse = " + featureSet[NUM_SIBLINGSPOUSE] + ", ";
         out += "Par or Chil = " + featureSet[NUM_SIBLINGSPOUSE]+ ", ";
         out += "M? = " + featureSet[SEX_MALE]+ ", ";
         out += "F? = " + featureSet[SEX_FEMALE]+ ", ";
         out += "Class = " + featureSet[CLASS]+ ", ";
         out += "EMB_S? = " + featureSet[EMB_S]+ ", ";
         out += "EMB_C? = " + featureSet[EMB_C]+ ", ";
         out += "EMB_Q? = " + featureSet[EMB_Q];
         
         return out; 
      }
       
   }
   
}