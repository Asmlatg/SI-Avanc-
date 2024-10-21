package jmetal.problems;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.util.JMException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SensorDeployment extends IntRealProblem {

    private static final double RADIUS = 10.0;
    private double[][] targets;

    public SensorDeployment(String solutionType, int numberOfSensors) {
        super(solutionType, 0, numberOfSensors * 2); // No integer variables, 2 real variables per sensor
        problemName_ = "SensorDeployment";
    }

    public void readTargetPositions(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            List<double[]> targetList = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("[ ,]"); // Split by space or comma
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                targetList.add(new double[] { x, y });
            }
            targets = new double[targetList.size()][2];
            for (int i = 0; i < targetList.size(); i++) {
                targets[i] = targetList.get(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
        Variable[] variables = solution.getDecisionVariables();
        double[] fx = new double[numberOfObjectives_];

        boolean[] coveredTargets = new boolean[targets.length];
        int[] targetCoverage = new int[targets.length];

        // Compute coverage
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

        // Objective 1: Maximize the number of covered targets
        int coveredCount = 0;
        for (boolean covered : coveredTargets) {
            if (covered) {
                coveredCount++;
            }
        }
        fx[0] = -coveredCount; // Minimize the negative number of covered targets

        // Objective 2: Maximize the minimum coverage of targets
        int minCoverage = Integer.MAX_VALUE;
        for (int coverage : targetCoverage) {
            if (coverage < minCoverage) {
                minCoverage = coverage;
            }
        }
        fx[1] = -minCoverage; // Minimize the negative minimum coverage

        solution.setObjective(0, fx[0]);
        solution.setObjective(1, fx[1]);
    }
}
