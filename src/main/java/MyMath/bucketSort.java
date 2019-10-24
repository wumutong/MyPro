package MyMath;

import java.util.Scanner;
import static java.lang.Integer.parseInt;

/**
 * 桶排序（简单案例）
 *  基数是10   求10分以下的排序
 *  例子  5  2  3  ->  2  3  5
 */
public class bucketSort {
    public static void main(String[] args) {
        String scanner = new Scanner(System.in).next();
        String[] split = scanner.split(",");
        bucketSortMath(split);
    }

    public static void bucketSortMath(String[] a){
        //转换成Int类型
        int[] bucket  =  new int[10];
        for(int j = 0;j<10;j++){
            bucket[j] = -1;
        }
        for(int i = 0;i<a.length;i++){
            if(bucket[parseInt(a[i])]==-1){
                bucket[parseInt(a[i])]=1;
            }else{
                bucket[parseInt(a[i])] = bucket[parseInt(a[i])]+1;
            }
        }

        //输出
        for(int result = 1;result<10;result++){
            if(bucket[result]!=-1){
                for(int flag = 1;flag<=bucket[result];flag++)
                    System.out.print(result+" ");
            }
        }

    }
}
