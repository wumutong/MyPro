package MyMath;

import java.util.Arrays;

/**
 * 归并排序
 */
public class mergeIntoSort {
    static public void mergeSort(int[] array,int start,int end){
        if(start < end ){
            //折半成两个小集合，分别进行递归
            int mid = (start+end)/2;
            mergeSort(array,start,mid);
            mergeSort(array,mid+1,end);
            //把两个有序的小集合，归并成一个大集合
            merge(array,start,mid,end);
        }
    }

    private static void merge(int[] array, int start, int mid, int end) {
        //开辟额外大集合，设置指针
        int[] temp = new int[end-start+1];
        int p1=start,p2=mid+1,p=0;
        //比较两个小集合的元素，依次放入大集合
        while(p1<=mid && p2<=end){
            if(array[p1]<=array[p2]){
                temp[p++] = array[p1++];
            }else{
                temp[p++] = array[p2++];
            }
        }
        //左侧小集合还有剩余，依次放入大集合的尾部
        while(p1<=mid){
            temp[p++]=array[p1++];
        }
        //右侧小集合还有剩余，依次放入大集合的尾部
        while(p2<=end){
            temp[p++] = array[p2++];
        }
        //把大集合的元素复制回原数组
        for(int i=0;i<temp.length;i++){
            array[i+start] = temp[i];
        }
    }

    public static void main(String[] args) {
        int[] array = {9,3,2,1,4,4,5,67,8,23};
        mergeSort(array,0,array.length-1);
        System.out.println(Arrays.toString(array));
    }
}
