package SparkCore

package checksql

import java.text.SimpleDateFormat
import java.util.{Date, Properties}
import java.text.ParseException

import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.types.{DataTypes, Decimal, DecimalType}

object CheckSql {
  var master: String = "yarn"
  var appName: String = "CheckSql"


  def main(args: Array[String]): Unit = {

    //    if (args.length >= 2) {
    //      master = if (StringUtils.isNotEmpty(args(0))) {
    //        args(0)
    //      } else {
    //        "yarn"
    //      }
    //      appName = if (StringUtils.isNotEmpty(args(1))) {
    //        args(1)
    //      } else {
    //        "CheckSql"
    //      }
    //
    //    }

    val spark = SparkSession.builder.master("local[*]").appName("CheckSql").getOrCreate()

    val properties = new Properties()
    properties.put("user","cdh")
    properties.put("password","vCspiwSgXHgRBpP9")

    val DbUserUrl= "jdbc:mysql://rm-2zepj33l0l4tesw04.mysql.rds.aliyuncs.com:3306/db_user"
    val DbCourseUrl = "jdbc:mysql://rm-2zepj33l0l4tesw04.mysql.rds.aliyuncs.com:3306/db_course"

    //读取mysql数据
    val bookRecordMysql :DataFrame = spark.read.jdbc(DbUserUrl,"book_record",properties)

    val cancelBookRecordMysqsl :DataFrame = spark.read.jdbc(DbUserUrl,"cancel_book_record",properties)

    var parent2student = spark.read.jdbc(DbUserUrl,"parent2student_0",properties)
    for (a <- 1 until 10){
      var parent2student_1 = spark.read.jdbc(DbUserUrl,"parent2student_" + a,properties)
      parent2student = parent2student.union(parent2student_1)
    }


    var  curencyRecordUnion :DataFrame = spark.read.jdbc(DbUserUrl,"currency_record_0",properties)
    for(a <-1 until 10){
      var  curencyRecord0 :DataFrame = spark.read.jdbc(DbUserUrl,"currency_record_" + a,properties)
      curencyRecordUnion = curencyRecordUnion.union(curencyRecord0)
    }

    val mainCalss :DataFrame =spark.read.jdbc(DbCourseUrl,"main_class",properties)

    println("==============================DataFrame create success====================================================")

    mainCalss.repartition(50)
    curencyRecordUnion.repartition(50)

    val castTimeToDay = spark.udf.register("castTimeToDay", (timemellions :Long) => new SimpleDateFormat("yyyy-MM-dd").format(timemellions))


    val castTime = spark.udf.register("castTime", (timemellions: Long) => new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      .format(timemellions))

    //TODO::获取当月一号日期
    val dateFormat = new SimpleDateFormat("yyyy-MM")
    val dt = new Date
    val MonthOne = dateFormat.format(dt)+"-01"

    //TODO::获取今天的日期
    val dayFormat = new SimpleDateFormat("yyyy-MM-dd")
    val today = dayFormat.format(dt)

    println("==========================================TempView create success=========================================")

    // 获取cancel_book_record当月截止到今天的start_time
    //    val cancelBookRecordTmp1 = cancelBookRecordTmp.selectExpr("cast(create_date as string) as create_date ","student_id","cancel_course_mode","concat(from_unixtime(unix_timestamp(),'yyyy-MM'),'-01') as monthday","from_unixtime(unix_timestamp(),'yyyy-MM-dd') as today","cast(start_time as bigint) as start_time1","from_unixtime(cast(start_time/1000 as bigint),'yyyy-MM-dd') as cancelBookRecordStartTime" )
    //      .where("cancelBookRecordStartTime > monthday")
    //      .where("cancelBookRecordStartTime < today")

    val cancelBookRecordTmp1= cancelBookRecordMysqsl.withColumn("start_timeDate",castTimeToDay(cancelBookRecordMysqsl("start_time")))


    // TODO::  cancelBookRecordTmp1.show(1)
    // TODO::  println("cancelBookRecordTmp1==============================")

    val dtypes: Array[(String, String)] = cancelBookRecordTmp1.dtypes

    // TODO:: dtypes.foreach(println)
    // TODO::  println("dtypes=======================")


    // TODO:: cancelBookRecordTmp1.show(1)
    // TODO:: println("cancelBookRecordTmp1=============================")
    val cancelBookRecordOpt=cancelBookRecordTmp1.where((cancelBookRecordTmp1("start_timeDate").geq(MonthOne)) and(cancelBookRecordTmp1("start_timeDate").leq(today)))



    // TODO:: cancelBookRecordOpt.show(3)
    // TODO::  println("cancelBookRecordOpt==================================")
    // cancelBookRecordTmp1.show(10)

    //cancel_book_record获取parent_id字段
    //    val cancelBookRecord = cancelBookRecordTmp1.join(parent2student,cancelBookRecordTmp1("student_id").equalTo(parent2student("student_id")),"left")
    //      .selectExpr("cast(parent_id as string) as parent_idS","cast(cancel_course_mode as string) as cancel_course_modeS","start_time1","cast(create_date as string) as create_dateS")

    val cancelBookRecord = cancelBookRecordOpt.join(parent2student,cancelBookRecordOpt("student_id").equalTo(parent2student("student_id")),"left")



    // TODO::  cancelBookRecord.show(3)
    // TODO::  println("cancelBookRecord============================")

    // 获取book_record当月截止到今天的start_time
    //    val bookRecordTmp = bookRecord.selectExpr("cast(parent_id as string) as parent_idS","cast(state as string) as stateS","concat(from_unixtime(unix_timestamp(),'yyyy-MM'),'-01') as monthday","from_unixtime(unix_timestamp(),'yyyy-MM-dd') as today","cast(start_time as string) as start_timeS","from_unixtime(cast(start_time/1000 as bigint),'yyyy-MM-dd') as bookRecordStartTime" )
    //      .where("bookRecordStartTime > monthday")
    //      .where("bookRecordStartTime < today")

    val bookRecordOpt =  bookRecordMysql.withColumn("start_timeDate",castTimeToDay(bookRecordMysql("start_time")))
    val bookRecord= bookRecordOpt.where((bookRecordOpt("start_timeDate").gt(MonthOne)) and(bookRecordOpt("start_timeDate").lt(today)))


    // TODO:: bookRecord.show(1)
    // TODO:: println("bookRecord============================")


    // bookRecordTmp.show(10)

    //main_calss获取parent_id字段
    val mainCalssWhithParent2Student = mainCalss.join(parent2student,mainCalss("student_id")=== parent2student("student_id"),"left")
      .where(mainCalss("student_id").notEqual (-1))



    //mainCalssWhithParent2Student.show(3)

    //.select(mainCalss("course_id"),parent2student("parent_id"),mainCalss("start_time"))


    // 获取main_calss当月截止到今天的start_time
    //    val mainCalssWhithParent2StudentTmp = mainCalssWhithParent2Student.selectExpr("cast(course_id as string) as course_idS","concat(from_unixtime(unix_timestamp(),'yyyy-MM'),'-01') as monthday","from_unixtime(unix_timestamp(),'yyyy-MM-dd') as today","cast(start_time as string) as start_timeS","course_id ","cast(parent_id as string) as parent_idS","from_unixtime(cast(start_time/1000 as bigint),'yyyy-MM-dd') as StartTimeTmp")
    //      .where("StartTimeTmp > monthday")
    //      .where("StartTimeTmp < today")


    val mainCalssWhithParent2StudentOpt = mainCalssWhithParent2Student.withColumn("start_timeDate",castTimeToDay(mainCalssWhithParent2Student("start_time")))


    val mainClassWithPs = mainCalssWhithParent2StudentOpt.where((mainCalssWhithParent2StudentOpt("start_timeDate").gt(MonthOne)) and(mainCalssWhithParent2StudentOpt("start_timeDate").lt(today)))



    // TODO:: mainClassWithPs.show(3)
    // TODO:: println("mainClassWithPs=============================")
    //mainCalssWhithParent2StudentTmp.show(10)

    // TODO::自定义函数 toDate 获取data里面的时间戳， decide 判断是否为取消课
    //   spark.udf.register("toDate", new StringToTime(),DataTypes.StringType)
    //val decide =   spark.udf.register("decide", new AlterRemark(), DataTypes.IntegerType)

    val decide = spark.udf.register("decide", (remark: String) => if(remark.contains("取消")){1}else{0})

    val toDate =spark.udf.register("toDate",(data1:String) => (data1.split("="))(1).toLong)


    val toTime = spark.udf.register("toTime", (timemellions: Long) => new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timemellions.toString).getTime())

    //    val curencyRecord0 = curencyRecord.selectExpr("create_time","decide(remark) as remark_0","course_id","toDate(data) as coursce_time","cast(parent_id as string) as parent_idS","quantity","data")
    //      .where("data like '%courseTime=%' ")
    //      .where(curencyRecord("quantity")<'0')

    val curencyRecordUnion1 = curencyRecordUnion.withColumn("coursce_time", toDate(curencyRecordUnion("data"))).withColumn("remark_0",decide(curencyRecordUnion("remark"))).where("data like '%courseTime=%' ").where(curencyRecordUnion("quantity")<'0')

    val curencyRecordUnion2 = curencyRecordUnion1.withColumn("coursce_time_1",castTime(curencyRecordUnion1("coursce_time")))
    val curencyRecordUnion3 = curencyRecordUnion2.withColumn("dt",toTime(curencyRecordUnion2("coursce_time_1") - toTime(curencyRecordUnion2("coursce_time_1"))))
    val curencyRecordUnion4 =  curencyRecordUnion3.withColumn("coursce_timeDate",castTimeToDay(curencyRecordUnion3("coursce_time")))
    val curencyRecordUnion5 = curencyRecordUnion4.where((curencyRecordUnion4("coursce_timeDate").gt(MonthOne))and(curencyRecordUnion4("coursce_timeDate").lt(today)))
      .where((curencyRecordUnion4("remark_0").equalTo(0)) or ((curencyRecordUnion4("remark_0").equalTo(1)) and (curencyRecordUnion4("dt")<(600))))


    // TODO::   curencyRecordUnion5.show(3)
    // TODO:: println("curencyRecordUnion5==================")

    // val curencyRecord1 =curencyRecord0.selectExpr("create_time","remark_0","course_id","coursce_time","parent_idS","quantity","data","from_unixtime(cast(coursce_time/1000 as bigint),'yyyy-MM-dd HH:mm:ss') as coursce_time_1")

    //curencyRecord1.show(10)

    //    val curencyRecord2= curencyRecord1.selectExpr("(unix_timestamp(coursce_time_1) - unix_timestamp(create_time)) as dt ","remark_0","create_time","course_id","concat(from_unixtime(unix_timestamp(),'yyyy-MM'),'-01') as monthday","from_unixtime(unix_timestamp(),'yyyy-MM-dd') as today","cast(coursce_time as string) as coursce_timeS","parent_idS","from_unixtime(cast(coursce_time/1000 as bigint),'yyyy-MM-dd') as coursce_time_dt")
    //      .where("coursce_time_dt > monthday")
    //      .where("coursce_time_dt < today")

    //tm3 为currency_record表筛选后的Dataframe
    //    val curencyRecord3 =curencyRecord2.where( (curencyRecord1("remark_0").equalTo(0) )or((curencyRecord1("remark_0").equalTo(1)) and (curencyRecord2("dt")<(600))))
    //
    //    curencyRecord3.show(10)


    println("===================================begin mainClass  Except  CurencyRecord ==============================================")




    //mainClass  Except  CurencyRecord
    val mainClassExceptCurencyRecordResultTmp =mainClassWithPs.select(mainClassWithPs("parent_id"),mainClassWithPs("start_time"))
      .except(curencyRecordUnion5.select(curencyRecordUnion5("parent_id"),curencyRecordUnion5("coursce_time")))

    // TODO::  mainClassExceptCurencyRecordResultTmp.show(3)
    // TODO::  println("mainClassExceptCurencyRecordResultTmp====================")



    val mainClassDrop = mainClassWithPs.drop("student_id")
    val a = mainClassDrop.dtypes
    a.foreach(println)
    println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

    val c =mainClassExceptCurencyRecordResultTmp.dtypes
    println("mainClassExceptCurencyRecordResultTmp=====================")
    c.foreach(print)

    //差集后mainClass关联自己的course_id
    val mainClassExceptCurencyRecordResult1 = mainClassExceptCurencyRecordResultTmp.join(mainClassWithPs,(mainClassExceptCurencyRecordResultTmp("parent_id")
      .equalTo(mainClassWithPs("parent_id")) and (mainClassExceptCurencyRecordResultTmp("start_time").equalTo(mainClassWithPs("start_time")) )),"left")
      .select(mainClassExceptCurencyRecordResultTmp("parent_id"),mainClassExceptCurencyRecordResultTmp("start_time") ,mainClassWithPs("course_id"))
    // TODO::  mainClassExceptCurencyRecordResult1.show(3)
    // TODO:: println("mainClassExceptCurencyRecordResult1===================")



    //关联book_record表的state字段
    val result = mainClassExceptCurencyRecordResult1.join(bookRecord,(mainClassExceptCurencyRecordResult1("parent_id")
      .equalTo(bookRecord("parent_id")) and(mainClassExceptCurencyRecordResult1("start_time")
      .equalTo(bookRecord("start_time")))), "left")
      .select(mainClassExceptCurencyRecordResult1("parent_id"),mainClassExceptCurencyRecordResult1("start_time"),mainClassExceptCurencyRecordResult1("course_id"),bookRecord("state"))

    // TODO::     result.show(3)
    // TODO::   println("result===============================")

    //关联cancel_book_record表的cancel_course_mode字段和取消约课时间
    val  mainClassExceptCurencyRecordResult = result.join(cancelBookRecord,(result("parent_id")
      .equalTo(cancelBookRecord("parent_id")) and(result("start_time")
      .equalTo(cancelBookRecord("start_time")))),"left")
      .select(result("parent_id"),result("start_time"),(result("course_id")),cancelBookRecord("cancel_course_mode"),result("state"),cancelBookRecord("create_date"))
      .selectExpr("parent_id","concat('\t',start_time)","concat('\t',course_id)","cancel_course_mode","state","concat('\t',create_date)")

    // TODO:: mainClassExceptCurencyRecordResult.show(3)
    // TODO:: println("mainClassExceptCurencyRecordResult==========================")


    val df = new SimpleDateFormat("yyyy-MM-dd")
    val date = df.format(new Date())




    println("===================================begin mainClassExceptCurencyRecordResult save ==============================================")



    // mainClassExceptCurencyRecordResult.repartition(1).write.option("ignoreLeadingWhiteSpace","false").mode(SaveMode.Append).csv("/Users/hanyugui/outpath/"+date+"/mainClassExceptCurencyRecordResult")

    // mainClassExceptCurencyRecordResult.repartition(1).write.option("ignoreLeadingWhiteSpace","false").mode(SaveMode.Append).csv("/mmears/demo2/CheckSql/"+date+"/mainClassExceptCurencyRecordResult")


    println("==================================mainClassExceptCurencyRecordResult save success========================================")


    println("=================================begin CurencyRecord   Except   mainClass ==============================================")

    //CurencyRecord   Except   mainClass
    println("=====================================================================")

    println("curencyRecordUnion5+++++++++++++++++")
    val dtypes1: Array[(String, String)] = curencyRecordUnion5.dtypes
    dtypes1.foreach(println)

    println("mainClassWithPs+++++++++++++++++++++++")
    val dtypes2: Array[(String, String)] = mainClassWithPs.dtypes
    dtypes2.foreach(println)
    val CurencyRecordExceptmainClassResultTmp=curencyRecordUnion5.select(curencyRecordUnion5("parent_id"),curencyRecordUnion5("coursce_time"))
      .except(mainClassWithPs.select(mainClassWithPs("parent_id"),mainClassWithPs("start_time")))

    println("============+++++++++++++++++++++++++++++++++===================================")

    CurencyRecordExceptmainClassResultTmp.show(3)
    println("CurencyRecordExceptmainClassResultTmp=========================")

    //curency_record表关联自己的course_id字段
    val CurencyRecordExceptmainClassResult = CurencyRecordExceptmainClassResultTmp
      .join(curencyRecordUnion5,CurencyRecordExceptmainClassResultTmp("parent_id").equalTo(curencyRecordUnion5("parent_id")),"left")
      .select(CurencyRecordExceptmainClassResultTmp("parent_id"),CurencyRecordExceptmainClassResultTmp("coursce_time"),curencyRecordUnion5("course_id"))
      .selectExpr("parent_id","concat('\t',coursce_time)","concat('\t',course_id)")


    println("===================================begin CurencyRecordExceptmainClassResult save ===========================================")

    CurencyRecordExceptmainClassResult.repartition(1).write.option("ignoreLeadingWhiteSpace","false").mode(SaveMode.Append).csv("/Users/hanyugui/outpath/"+date+"/CurencyRecordExceptmainClassResult")


    // CurencyRecordExceptmainClassResult.repartition(1).write.option("ignoreLeadingWhiteSpace","false").mode(SaveMode.Append).csv("/mmears/demo2/CheckSql/"+date+"/CurencyRecordExceptmainClassResult")

    println("==================================CurencyRecordExceptmainClassResult save success=========================================")


    spark.stop()



  }
}
