package MyMath;

/**
 * @author:WuMuTong
 * @Describe: 全排列
 */
public class permutations {
    public static char[] text = {'a','b','c','d'};
    public static void main(String[] args) {
        permutation(text,0,text.length);
    }
    /*
        全排列输出
        要输出的字符数组 a[]
        输出字符数组的起始位置 m
        输出字符数组的长度  n
     */
    public static void permutation(char a[],int m,int n){
        int i;
        char t;
        if(m<n-1){
            //全排列就是从第一个数字起每个数分别与它后面的数字交换
          /*
            如： a  b  c
             a 和  b  c 进行交换   bac 和  cba
             然后 b 和 后面的 c 进行交换  acb
             bac 中 a 和 c进行交换 bca
             cba 中 b 和 a进行交换 cab
             得到： abc bac cba acb bca  cab
           */
            permutation(a,m+1,n);
            for(i=m+1;i<n;i++){
                t=a[m];
                a[m]=a[i];
                a[i]=t;
                permutation(a,m+1,n);
                t=a[m];
                a[m]=a[i];
                a[i]=t;
            }
        }else{
            printResult(a);
        }}
    /*
        输出指定字符数组
        text 将要输出的字符数组
     */
    public static void printResult(char[] text){
        for(int i = 0;i<text.length;i++){
            System.out.print(text[i]);
        }
        System.out.println();
    }}