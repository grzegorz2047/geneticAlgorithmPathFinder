package com.grzegorz2047.genetic.pathfinders.ga;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class GeneticAlgorithm {



    Random random = new Random();
    private final int maxGeneration;
    //the population of genomes
    List<Genome> genomes = new ArrayList<>();

    //size of population
    int populationSize;
    double crossoverRate;
    double mutationRate;
    //how many bits per chromosome
    int chromosomeLength;
    //how many bits per gene
    int geneLength;
    int fittestGenome;
    double bestFitnessScore;
    double totalFitnessScore;
    int currentGeneration;

    //create an instance of the map class
    ExperimentMap experimentMap;

    //we use another CBobsMap object to keep a record of
    //the best route each generation as an array of visited
    //cells. This is only used for display purposes.
    ExperimentMap currentStoredMap;

    //lets you know if the current run is in progress.
    boolean isInProgress;

    public GeneticAlgorithm(double crossoverRate,
                            double mutationRate,
                            int populationSize,
                            int chromosomeLength,
                            int geneLength,
                            int maxGeneration) {
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.populationSize = populationSize;
        this.chromosomeLength = chromosomeLength;
        this.totalFitnessScore = Double.MIN_VALUE;
        this.currentGeneration = 0;
        this.geneLength = geneLength;
        this.isInProgress = false;
        this.maxGeneration = maxGeneration;
        CreateStartPopulation();
        experimentMap = new ExperimentMap();
        currentStoredMap = new ExperimentMap();
    }

    //--------------------------RouletteWheelSelection-----------------
//
//	selects a member of the population by using roulette wheel
//	selection as described in the text.
//------------------------------------------------------------------
    Genome selectUsingRouletteWheel() {
        double slice = random.nextDouble() * totalFitnessScore;

        double fitnessTotal = Double.MIN_VALUE;

        int selectedgenome = 0;

        for (int i = 0; i < genomes.size(); i++) {

            Genome genome = genomes.get(i);
            fitnessTotal += genome.getFitness();

            if (fitnessTotal > slice) {
                selectedgenome = i;
                break;
            }
        }


        return genomes.get(selectedgenome);
    }

    //----------------------------Mutate---------------------------------
//	iterates through each genome flipping the bits acording to the
//	mutation rate
//--------------------------------------------------------------------


    //----------------------------Crossover--------------------------------
//	Takes 2 parent vectors, selects a midpoint and then swaps the ends
//	of each genome creating 2 new genomes which are stored in baby1 and
//	baby2.
//---------------------------------------------------------------------
    void crossover(final Genome mum,
                   final Genome dad,
                   Genome baby1,
                   Genome baby2) {
        //just return parents as offspring dependent on the rate
        //or if parents are the same
        List<Integer> dadGenes = dad.getGenes();
        List<Integer> mumGenes = mum.getGenes();
        if ((random.nextDouble() > crossoverRate) || (mum.equals(dad))) {
            baby1.setGenes(mumGenes);
            baby2.setGenes(dadGenes);
            return;
        }
        //determine a crossover point
        int cp = random.nextInt(0, chromosomeLength);

        //swap the bits
        for (int i = 0; i < cp; ++i) {
            baby1.addGene(mumGenes.get(i));
            baby2.addGene(dadGenes.get(i));
        }

        for (int i = cp; i < mumGenes.size(); ++i) {
            baby1.addGene(dadGenes.get(i));
            baby2.addGene(mumGenes.get(i));
        }
    }

    //-----------------------------------Run----------------------------------
//
//	This is the function that starts everything. It is mainly another
//	windows message pump using PeekMessage instead of GetMessage so we
//	can easily and dynamically make updates to the window while the GA is
//	running. Basically, if there is no msg to be processed another Epoch
//	is performed.
//------------------------------------------------------------------------
    public void run() {
        //The first thing we have to do is create a random population
        //of genomes
        CreateStartPopulation();
        isInProgress = true;

    }

    //----------------------CreateStartPopulation---------------------------
//
//-----------------------------------------------------------------------
    void CreateStartPopulation() {
        //clear existing population
        genomes.clear();

        for (int i = 0; i < populationSize; i++) {
            genomes.add(new Genome(chromosomeLength));
        }

        //reset all variables
        currentGeneration = 0;
        fittestGenome = 0;
        bestFitnessScore = Double.MIN_VALUE;
        totalFitnessScore = Double.MIN_VALUE;
    }

    //--------------------------------Epoch---------------------------------
//
//	This is the workhorse of the GA. It first updates the fitness
//	scores of the population then creates a new population of
//	genomes using the Selection, Croosover and Mutation operators
//	we have discussed
//----------------------------------------------------------------------
    public void epoch() {

        updateFitnessScores();
        if (!isInProgress) {
            return;
        }
        //create some storage for the baby genomes
        List<Genome> babyGenomes = new ArrayList<>();

        while (babyGenomes.size() < populationSize) {

            //select 2 parents
            Genome mum = selectUsingRouletteWheel();
            Genome dad = selectUsingRouletteWheel();

            //operator - crossover
            Genome baby1 = new Genome();
            Genome baby2 = new Genome();
            crossover(mum, dad, baby1, baby2);

            //operator - mutate
            mutate(baby1, baby2);

            //add to new population
            babyGenomes.add(baby1);
            babyGenomes.add(baby2);
        }

        //copy babies back into starter population
        genomes = babyGenomes;

        //increment the generation counter
        currentGeneration++;
        if (currentGeneration >= maxGeneration) {
            this.isInProgress = false;
        }
    }

    private void mutate(Genome baby1, Genome baby2) {
        baby1.mutate(mutationRate);
        baby2.mutate(mutationRate);
    }

    //---------------------------UpdateFitnessScores------------------------
//	updates the genomes fitness with the new fitness scores and calculates
//	the highest fitness and the fittest member of the population.
//	Also sets m_pFittestGenome to point to the fittest. If a solution
//	has been found (fitness == 1 in this example) then the run is halted
//	by setting isInProgress to false
//-----------------------------------------------------------------------
    void updateFitnessScores() {
        totalFitnessScore = 0;
        ExperimentMap tempMemory = new ExperimentMap();
        //update the fitness scores and keep a check on fittest so far
        for (int i = 0; i < genomes.size(); i++) {
            //decode each genomes chromosome into a vector of directions
            Genome genome = genomes.get(i);
            Vector<Integer> directions = genome.decodeChromosome(geneLength);
            //get it's fitness score
            genome.setFitness(experimentMap.calculateFitness(directions));
            //update total
            totalFitnessScore += genome.getFitness();
            //if this is the fittest genome found so far, store results
            if (genome.getFitness() > bestFitnessScore) {
                System.out.println(bestFitnessScore);
                bestFitnessScore = genome.getFitness();
                fittestGenome = i;
                currentStoredMap = new ExperimentMap(experimentMap.copyOfPath(directions, tempMemory).clone());

                //Has Bob found the exit?
                if (genome.getFitness() >= 1) {
                    //is so, stop the run
                    isInProgress = false;
                }
            }
        }//next genome
    }

    //---------------------------Decode-------------------------------------
//
//	decodes a vector of bits into a vector of directions (ints)
//
//	0=North, 1=South, 2=East, 3=West
//-----------------------------------------------------------------------


    //------------------------- Render -------------------------------
//
//	given a surface to render to this function renders the map
//	and the best path if relevant. cxClient/cyClient are the
//	dimensions of the client window.
//----------------------------------------------------------------
    public void render(double width, double height, GraphicsContext gc, Stage primaryStage) {
        renderWhite(width, height, gc);
        //render the map
        renderMap((int) width, (int) height, gc);
        //render the best route
        currentStoredMap.memoryRenderPath(gc, (int) width, (int) height);


        //Render additional information
        String generationNumber = "Generation: " + currentGeneration + ", best fitness: " + bestFitnessScore;
        Platform.runLater(() -> primaryStage.setTitle(generationNumber));

    }

    public void renderMap(int width, int height, GraphicsContext gc) {
        experimentMap.renderMap(gc, width, height);
    }

    public void renderWhite(double width, double height, GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);
    }


    public boolean isRunning() {
        return isInProgress;
    }


}
