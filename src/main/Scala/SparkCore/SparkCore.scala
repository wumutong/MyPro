package SparkCore

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 使用本地Spark方式，实现WordCount
  */
object SparkCore {
  def main(args: Array[String]): Unit = {
    //加载文件中的单词
    val spark = new SparkContext(new SparkConf()
      .setMaster("local[*]").setAppName("wordCount"))
      .textFile("src\\main\\File\\Word.txt")

    val tuples: Array[(String, Int)] = spark.flatMap(_.split(" ")).map((_, 1))
      .reduceByKey(_ + _).sortBy(_._2, false).collect()





  }


}
