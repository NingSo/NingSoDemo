package com.ningso.ningsodemo;

import java.util.Random;

class GetIndex {
    static int[] Getnum() {
        int[] temp = new int[12];
        int[] temp_ = new int[6];
        int[] index = new int[6];
        int temp1;
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            temp1 = r.nextInt(36);//0-35
            temp[i] = GetNumber(temp, i, 1, 36, temp1, r);
        }

        System.arraycopy(temp, 0, temp_, 0, 6);

        for (int i = 0; i < 6; i++) {
            index[i] = r.nextInt(6);
            for (int j = i; j < 6; j++) {
                if (index[j] == index[i]) {
                    index[j] = r.nextInt(6);
                }
            }
        }

        int[] num = new int[6];
        for (int i = 0; i < num.length; i++) {
            num[i] = i + 1;
        }
        Random w = new Random();
        int[] result = new int[6];
        int max = 6;
        for (int j = 0; j < result.length; j++) {

            int nindex = w.nextInt(max);

            result[j] = num[nindex] - 1;

            num[nindex] = num[max - 1];
            max--;
        }

        for (int i = 6; i < 12; i++) {

            temp[i] = temp_[result[i - 6]];
        }
        return temp;
    }

    private static int GetNumber(int[] a, int index, int minValue, int maxValue, int temp, Random r) {
        for (int i = 0; i < index; i++) {
            if (a[i] == temp) {
                int newTemp = r.nextInt(maxValue) + minValue;
                a[index] = newTemp;
                return GetNumber(a, index, minValue, maxValue, newTemp, r);
            }
        }
        return temp;
    }


}
