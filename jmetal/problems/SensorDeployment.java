package jmetal.problems;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.util.JMException;

public class SensorDeployment extends IntRealProblem {

    private static final double RADIUS = 10.0;
    private double[][] targets = {
            { 50, 50 }, { 41, 50 }, { 90, 90 }, { 86, 89 }, { 10, 10 }, { 15, 95 }
    };

    public SensorDeployment(String solutionType, int numberOfSensors) {
        super(solutionType, 0, numberOfSensors * 2);
        problemName_ = "SensorDeployment";
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
        Variable[] variables = solution.getDecisionVariables();
        double[] fx = new double[numberOfObjectives_];
        boolean[] coveredTargets = new boolean[targets.length];
        int[] targetCoverage = new int[targets.length];
        // Calculer la couverture
        for (int i = 0; i < targets.length; i++) {
            coveredTargets[i] = false;
            targetCoverage[i] = 0;
        }
        for (int i = 0; i < variables.length; i += 2) {
            double sensorX = variables[i].getValue();
            double sensorY = variables[i + 1].getValue();
            for (int j = 0; j < targets.length; j++) {
                double distance = Math.pow(sensorX - targets[j][0], 2) + Math.pow(sensorY - targets[j][1], 2);
                if (distance <= Math.pow(RADIUS, 2)) {
                    coveredTargets[j] = true;
                    targetCoverage[j]++;
                }
            }
        }
        // Objectif 1:
        int coveredCount = 0;
        for (boolean covered : coveredTargets) {
            if (covered) {
                coveredCount++;
            }
        }
        fx[0] = -coveredCount; // Minimisation
        // Objectif 2:
        int minCoverage = Integer.MAX_VALUE;
        for (int coverage : targetCoverage) {
            if (coverage < minCoverage) {
                minCoverage = coverage;
            }
        }
        fx[1] = -minCoverage; // Minimisation

        solution.setObjective(0, fx[0]);
        solution.setObjective(1, fx[1]);
    }
}
