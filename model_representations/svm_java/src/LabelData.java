import libsvm.svm_node;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by devinhe on 4/3/16.
 */
public class LabelData {
    public Vector<Double> y;
    public Vector<svm_node[]> x;

    private LabelData(Vector<Double> y, Vector<svm_node[]> x) {
        this.y = y;
        this.x = x;
    }

    private static int atoi(String s)
    {
        return Integer.parseInt(s);
    }

    private static double atof(String s) {
        double d = Double.valueOf(s).doubleValue();
        if (Double.isNaN(d) || Double.isInfinite(d))
        {
            System.err.print("NaN or Infinity in input\n");
            System.exit(1);
        }
        return(d);
    }

    public static LabelData loadLabelAndData(String file) throws IOException, FileNotFoundException {
        BufferedReader fp = new BufferedReader(new FileReader(file));
        Vector<Double> vy = new Vector<Double>();
        Vector<svm_node[]> vx = new Vector<svm_node[]>();
        int max_index = 0;
        while(true) {
            String line = fp.readLine();
            if(line == null) break;

            StringTokenizer st = new StringTokenizer(line,",");

            vy.addElement(atof(st.nextToken()));
            int m = st.countTokens();
            int nonzero_m = 0;
            double[] features = new double[m];
            for (int j = 0; j < m; j++) {
                features[j] = atof(st.nextToken());
                if(features[j] != 0.0) {
                    nonzero_m++;
                }
            }
            svm_node[] x = new svm_node[nonzero_m];
            int j = 0;
            int k = 0;
            while (j < nonzero_m)
            {
                x[j] = new svm_node();
                while (features[k++] == 0.0);
                x[j].index = k;
                x[j].value = features[k];
                j++;
            }
            if(nonzero_m>0) max_index = Math.max(max_index, x[nonzero_m-1].index);
            vx.addElement(x);
        }
        return new LabelData(vy, vx);
    }

}
