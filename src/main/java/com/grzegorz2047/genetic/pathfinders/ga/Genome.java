package com.grzegorz2047.genetic.pathfinders.ga;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Genome {
    private final Random rand = new Random();
    private List<Integer> genes;
    private double fitness;

    public Genome() {
        this.genes = new ArrayList<>();
        this.fitness = Double.MIN_VALUE;
    }

    public Genome(int genesLength) {
        this();
        for (int i = 0; i < genesLength; ++i) {
            this.genes.add(rand.nextInt(2));
        }
    }

    public List<Integer> getGenes() {
        return genes;
    }


    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void mutate(double mutationRate) {
        int bound = genes.size();
        for (int curBit = 0; curBit < bound; curBit++) {
            if (rand.nextDouble() < mutationRate) {
                if (genes.get(curBit) == 0) {
                    genes.set(curBit, 1);
                } else {
                    genes.set(curBit, 0);
                }
            }
        }
    }

    public void setGenes(List<Integer> genes) {
        this.genes = genes;
    }

    public void addGene(Integer gene) {
        this.genes.add(gene);
    }
    Vector<Integer> decodeChromosome(int geneLength) {
        Vector<Integer> directions = new Vector<>();
        //step through the chromosome a gene at a time
        for (int gene = 0; gene < genes.size(); gene += geneLength) {
            //get the gene at this position
            List<Integer> thisGene = new ArrayList<>();

            for (int bit = 0; bit < geneLength; ++bit) {
                thisGene.add(genes.get(gene + bit));
            }

            //convert to decimal and add to list of directions
            directions.add(convertBinaryToInteger(thisGene));
        }

        return directions;
    }

    //-------------------------------BinToInt-------------------------------
//	converts a vector of bits into an integer
//----------------------------------------------------------------------
    /*

                    32 16  8  4  2  1 |    <- potęgi dwójki
                     0  0  0  0  0  1 |=1, tj. 1*1 + 2*0 +...
                     0  0  0  0  1  0 |=2  tj. 1*0 + 2*1 +...
                     0  0  0  0  1  1 |=3  tj 1*1 + 2*1 + ...
                     0  0  0  1  0  1 |=5  tj 1*1 + 2*0 + 4*1 + ...
                     0  0  0  0  1  1 |=3
     */
    int convertBinaryToInteger(final List<Integer> binaryValues) {
        int val = 0;
        int multiplier = 1;// Mnoży 1,2,4,8,16,32,64 od prawej.)

        int lastBitPostion = binaryValues.size();
        for (int bitPosition = lastBitPostion; bitPosition > 0; bitPosition--) {
            val += binaryValues.get(bitPosition - 1) * multiplier;
            multiplier *= 2;
        }
        return val;
    }

}