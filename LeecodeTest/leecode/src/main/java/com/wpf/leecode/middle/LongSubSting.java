package com.wpf.leecode.middle;

/**
 * Author: feipeng.wang
 * Time:   2022/1/5
 * Description :leecode链接：https://leetcode-cn.com/problems/longest-palindromic-substring/
 * 给你一个字符串 s，找到 s 中最长的回文子串。
 */
public class LongSubSting {

    public static void main(String[] args) {
        longestPalindrome2("civilwartestingwhetherthatnaptionoranynartionsoconceivedandsodedicatedcanlongendureWeareqmetonagreatbattlefiemldoftzhatwarWehavecometodedicpateaportionofthatfieldasafinalrestingplaceforthosewhoheregavetheirlivesthatthatnationmightliveItisaltogetherfangandproperthatweshoulddothisButinalargersensewecannotdedicatewecannotconsecratewecannothallowthisgroundThebravelmenlivinganddeadwhostruggledherehaveconsecrateditfaraboveourpoorponwertoaddordetractTgheworldadswfilllittlenotlenorlongrememberwhatwesayherebutitcanneverforgetwhattheydidhereItisforusthelivingrathertobededicatedheretotheulnfinishedworkwhichtheywhofoughtherehavethusfarsonoblyadvancedItisratherforustobeherededicatedtothegreattdafskremainingbeforeusthatfromthesehonoreddeadwetakeincreaseddevotiontothatcauseforwhichtheygavethelastpfullmeasureofdevotionthatweherehighlyresolvethatthesedeadshallnothavediedinvainthatthisnationunsderGodshallhaveanewbirthoffreedomandthatgovernmentofthepeoplebythepeopleforthepeopleshallnotperishfromtheearth");
    }
    /*中心法*/
    public static String longestPalindrome2(String s) {
        String t = "";
        if (s.length()<=1||s.length()>1000){
            return s;
        }
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            int l1 = extent(s,i,i);
            int l2 = extent(s,i,i+1);
            int max = Math.max(l1,l2);
            //场景一：oabba i=2 max = 4
            //场景二：oabcba i=3 max = 5
            if (max>end-start){
                start = i - (max-1)/2;
                end = i+ max/2;
                t= s.substring(start, end + 1);
            }
        }
        System.out.print("最长的回文子串:"+t);
        return t;
    }
    private static int extent(String s,int left,int right){
        int L = left;
        int R = right;
        while (L>=0&&R<s.length()&&s.charAt(L) == s.charAt(R)){
            L--;
            R++;
        }
        //下面R-1和L+1是因为上面多执行了一次L--和R++
        return (R-1) - (L+1) + 1;//这里不懂

    }




    /*不通过*/
    public static String longestPalindrome1(String s) {
        String t = "";
        if (s.length()<=1||s.length()>1000){
            return s;
        }
        for (int i = 0; i < s.length(); i++) {
            for (int j = i+1; j <=s.length(); j++) {
                String now = s.substring(i,j);
                System.out.print("子串:"+now+"\n");
                if (isHuiWen(now)&&now.length()>t.length()){
                    t = now;
                }
            }
        }
        System.out.print("最长的回文子串:"+t);
        return t;
    }
    private static boolean isHuiWen(String s){
        String t1 = new StringBuilder(s).reverse().toString();
        return t1.equals(s);
    }
    public static String reverse1(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}
