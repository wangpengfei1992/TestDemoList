package com.wpf.leecode.low;

/**
 * Author: feipeng.wang
 * Time:   2022/1/5
 * Description : leecode链接：https://leetcode-cn.com/problems/two-sum/
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。
 *
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。
 *
 * 你可以按任意顺序返回答案。
 */
public class AddSum {

    public static void main(String[] args) {
        int[] nums = {3,2,4};
        int target = 6;
        twoSum(nums,target);
    }
    public static int[] twoSum(int[] nums, int target) {
        int[] data = new int[2];

        int length = nums.length;
        for (int i = 0; i < length; i++) {
            for (int j = i+1; j < length ; j++) {
                if (nums[i]+nums[j] == target){
                    data[0] = i;
                    data[1] = j;
                    System.out.print("数组:"+i+",,,"+j);
                    return data;
                }
            }
        }
        return data;
    }
}
