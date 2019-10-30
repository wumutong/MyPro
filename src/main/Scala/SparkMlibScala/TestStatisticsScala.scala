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

    println(summary.count)
    println(summary.max)
    println(summary.min)
    println(summary.mean)
    println(summary.normL1)
    println(summary.normL2)
    println(summary.variance)


  }

}
