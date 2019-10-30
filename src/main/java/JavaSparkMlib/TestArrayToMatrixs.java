package JavaSparkMlib;

/**
 * 测试使用sparkMlib 数组 和 向量之间的转换
 */
import org.apache.spark.mllib.linalg.*;

public class TestArrayToMatrixs {
    public static void main(String[] args) {
        //注意 数组准换成向量必须是 double类型
        double [] aArray = {1,2,3,4,5,6};
        Matrix matrix = Matrices.dense(2,3,aArray);
        System.out.println(matrix);
    }
}
