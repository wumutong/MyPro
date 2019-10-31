package JavaSparkMlib;

import org.apache.spark.SparkConf;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.*;


/**
 * 测试Spark中的Statistics( 统计 )下面的colStatis方法
 * colStats方法可以计算每列最大值、最小值、平均值、方差值、L1范数、L2范数
 */
public class TestStatistics {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("testColStatis");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<Vector> map = sc.textFile("colStatsTest", 3).map(new Function<String, Vector>() {
            @Override
            public Vector call(String a) throws Exception {
                String[] b = a.split(" ");
                double[] c = new double[b.length];
                for (int i = 0; i < b.length; i++) {
                    c[i] = Double.parseDouble(b[i]);
                }
                Vector dense = Vectors.dense(c);
                return dense;
            }
        });
       // Statistics.colStats(map);

    }
}
