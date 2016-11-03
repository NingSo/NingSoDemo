package com.ningso.ningsodemo;

import java.util.Random;

class CardIndexManager {

    private static final int MAX_IMAGE_NUM = 16;

    public static int[] getIndexNum(int MAX_CARD_NUM) {
        int HALF_CARD_NUM = MAX_CARD_NUM / 2;
        int[] temp = new int[MAX_CARD_NUM];//存入不同的随机种子
        int[] temp_ = new int[HALF_CARD_NUM];//前6个数
        int temp1;
        Random r = new Random();
        for (int i = 0; i < HALF_CARD_NUM; i++) {
            temp1 = r.nextInt(MAX_IMAGE_NUM);
            temp[i] = getNumber(temp, i, 1, MAX_IMAGE_NUM, temp1, r);
        }
        System.arraycopy(temp, 0, temp_, 0, HALF_CARD_NUM);//保存temp中的前6位

        int[] num = new int[HALF_CARD_NUM];
        for (int i = 0; i < num.length; i++) {
            num[i] = i + 1;
        }
        Random w = new Random();
        int[] result = new int[HALF_CARD_NUM];
        int max = HALF_CARD_NUM;
        for (int i = 0; i < result.length; i++) {
            int nindex = w.nextInt(max);
            result[i] = num[nindex] - 1;
            num[nindex] = num[max - 1];
            max--;
        }
        for (int i = HALF_CARD_NUM; i < MAX_CARD_NUM; i++) {
            temp[i] = temp_[result[i - HALF_CARD_NUM]];
        }
        //Collections.shuffle()
        return temp;
    }

    private static int getNumber(int[] a, int index, int minValue, int maxValue, int temp, Random r) {
        for (int i = 0; i < index; i++) {
            if (a[i] == temp) {
                int newTemp = r.nextInt(maxValue) + minValue;
                a[index] = newTemp;
                return getNumber(a, index, minValue, maxValue, newTemp, r);
            }
        }
        return temp;
    }

    public static void main(String[] args) {
        for (int indexvaule : getIndexNum(12)) {
            System.out.println("index: " + indexvaule);
        }
    }

}
