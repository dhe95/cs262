/**
 * Created by devinhe on 3/31/16.
 */
package src;

import src.libsvm.*;
import java.io.IOException;
import java.util.Vector;

public class SVM {

    public static String training_loc = "/Users/devinhe/research/262/server/data/digit-dataset/data-training.csv";
    public static String test_loc = "/Users/devinhe/research/262/server/data/digit-dataset/data-test.csv";
    public static String model_loc = "/Users/devinhe/research/262/server/javalibsvm-digits.bin";

    public static void main(String[] args) throws IOException {
        svm_model model;
        try {

            model = svm.svm_load_model(model_loc);
        } catch (Exception e) {
            System.err.println("Unable to load model from file, generating from scratch");
            model = generateModelFromFile(training_loc);
            svm.svm_save_model(model_loc, model);
        }
        LabelData testData = LabelData.loadLabelAndData(test_loc);
        svm.svm_predict(model, testData.x.elementAt(0));
    }

    private static svm_model generateModelFromFile(String file) throws IOException {
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        // training only parameters?
        param.cache_size = 200.0;
        param.eps = 0.001; // default value from documentation
        param.C = 0.000001; // good value found from cs189
        param.nr_weight = 0; // not using weights
        param.nu = 0.0; // not used
        param.p = 0.0; // not used
        param.shrinking = 1; // default value
        param.probability = 0; // default value

        svm_problem prob = getProblemFromFile(file);
        svm_model model = svm.svm_train(prob, param);

        return model;
    }


    private static svm_problem getProblemFromFile(String file) throws IOException {
        LabelData data = LabelData.loadLabelAndData(file);
        Vector<Double> vy = data.y;
        Vector<svm_node[]> vx = data.x;
        svm_problem prob = new svm_problem();
        prob.l = vy.size();
        prob.x = new svm_node[prob.l][];
        for(int i=0;i<prob.l;i++)
            prob.x[i] = vx.elementAt(i);
        prob.y = new double[prob.l];
        for(int i=0;i<prob.l;i++)
            prob.y[i] = vy.elementAt(i);
        return prob;
    }

}
