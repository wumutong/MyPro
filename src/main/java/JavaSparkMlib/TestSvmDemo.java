package JavaSparkMlib;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import  org.apache.spark.api.java.JavaSparkContext;
import  org.apache.spark.mllib.classification.SVMModel;
import  org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.linalg.Vectors;
import  org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

/**
 * 鹏哥 简书
 */
public class TestSvmDemo {
    public static void main(String[] args) {
        System.setProperty("hadoop.home.dir","D:\\hadoop-2.7.2\\bin");
        //创建sc对象
        SparkConf conf = new SparkConf().setAppName("SVM").setMaster("local");
        JavaSparkContext sc = new  JavaSparkContext(conf);
        //获取项目代码中的测试数据
        JavaRDD<String> source = sc.textFile("src\\resources\\iris.data");

        //把数据添加label标签
        //vector向量类型向量数据都是Double数据类型
        JavaRDD<LabeledPoint> data =  source.map(
                new Function<String, LabeledPoint>() {
                    @Override
                    public LabeledPoint call(String a) throws Exception {
                        String[] parts = a.split(",");
                        double label =0.0;
                        if(parts[4].equals("Iris-setosa")) {
                            label = 0.0;
                        }else  if(parts[4].equals("Iris-versicolor")) {
                            label = 1.0;
                        }else {
                            label = 2.0;
                        }
                        return new LabeledPoint(label, Vectors.dense(Double.parseDouble(parts[0]),
                                Double.parseDouble(parts[1]),
                                Double.parseDouble(parts[2]),
                                Double.parseDouble(parts[3])));
                }
        });

        //因为Svm只是支持 2元回归，（就是两类数据）。所以我们需要
        //去除上面生成的label = 2带标签的数据

        JavaRDD<LabeledPoint>[] filters =  data.filter(line->{
            return line.label()!=2;
        }).randomSplit(new  double[]{0.6,0.4},11L);//其中训练集占60%
        //测试集占40%
        //训练集
        JavaRDD<LabeledPoint> training =  filters[0].cache();
        //测试集
        JavaRDD<LabeledPoint> test = filters[1];




    //构建训练集 SVMWithSGD
    // SGD即著名的随机梯度下降算法（Stochastic  Gradient Descent）
    // 设置迭代次数为1000，
    // 除此之外还有stepSize（迭代步伐大小），
    // regParam（regularization正则化控制参数），
    // miniBatchFraction（每次迭代参与计算的样本比例），
    //initialWeights（weight向量初始值）等参数可以进行设置*/
        SVMModel model =  SVMWithSGD.train(training.rdd(), 1000);


    //模型评估


    //清除默认阈值，这样会输出原始的预测评分，即带有确信度的结果
        model.clearThreshold();
        JavaRDD<Tuple2<Object,Object>>  scoreAndLabels = test.map(point->
                new  Tuple2<>(model.predict(point.features()),point.label()));
        scoreAndLabels.foreach(x->{
            System.out.println(x);
        });



    }
}
