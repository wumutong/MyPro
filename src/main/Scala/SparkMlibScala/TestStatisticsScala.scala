package SparkMlibScala

import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
/**
  *  使用Scala 进行对向量的分析
  *  Statistics ( 统计 )中的colStats分析
  *  colStats方法可以计算每列最大值、最小值、平均值、方差值、L1范数、L2范数
  */
object TestStatisticsScala {
  def main(args: Array[String]): Unit = {
    val context: SparkContext = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("TestColStats"))
    val sc = context
    val mapResult: RDD[Array[Double]] = sc.textFile("src\\resources\\colStatsTest")
      .map(_.split(" ")).map(line => line.map(f => f.toDouble))

    //转换为Vector类型
    val mapVector: RDD[linalg.Vector] = mapResult.map(lines => Vectors.dense(lines))

    //使用Statistics中的colStats
    val summary: MultivariateStatisticalSummary = Statistics.colStats(mapVector)

    println(summary.count)  //一共几行
    println(summary.max)    //[2.0,23.0,42.0,52.0,5.0] 取出每一列中最大的
    println(summary.min)    //[1.0,2.0,3.0,4.0,4.0]   取出每一列中最小的
    //[1.3333333333333333,9.333333333333334,16.333333333333332,32.666666666666664,4.333333333333333]
    //求出来每一列平均数
    println(summary.mean)
    //求每一列的和 ( 计算曼哈段距离 )
    println(summary.normL1) //[4.0,28.0,49.0,98.0,13.0]
    //计算欧几里得距离：平方根
    println(summary.normL2) //[2.449489742783178,23.280893453645632,42.2965719651132,66.96267617113283,7.54983443527075]
    //计算标准差
    println(summary.variance) //[0.3333333333333333,140.33333333333334,494.3333333333333,641.3333333333334,0.3333333333333333]


  }

}
