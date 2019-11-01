package SparkRegressionTest

import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.{BinaryClassificationMetrics, MulticlassMetrics}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.util.MLUtils

/**
  * 使用spark Svm 进行 支持向量机运行测试
  */
object SparkLibSvmDemo {
  def main(args: Array[String]): Unit = {
    val sc: SparkContext = new SparkContext(new SparkConf().setMaster("local").setAppName("testRegressionSvmDemo"))
    //1 读取样本数据
    val data_path = "src\\resources\\regressionSVMTest"

    // 加载 LIBSVM 格式的数据
    val data = MLUtils.loadLibSVMFile(sc, data_path)

    // 切分数据，60%用于训练，40%用于测试
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = data.cache()
    val test = data

    // 运行训练算法 来构建模型
    val numIterations = 100
    val model = SVMWithSGD.train(training, numIterations) // 随机梯度下降法

    // 清空默认值
    model.clearThreshold()

    // 用训练集计算原始分数
    val scoreAndLabels = test.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }

    val result = scoreAndLabels.map { t =>
      val str = "point.label=" + t._2 + " score= " + t._1
      println("*********************************************************************************"+str)
      str
    }
    result.collect()

    // 获得评价指标
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC() //受试者操作特征
    println("Area under ROC = " + auROC+"**********************************************************************")

    sc.stop()
  }
}
