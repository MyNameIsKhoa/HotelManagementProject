package com.hsf302.hotelmanagementproject.utils;

public class MoneyUtils {

    private static final String[] units = {
            "", "một", "hai", "ba", "bốn", "năm",
            "sáu", "bảy", "tám", "chín"
    };

    public static String numberToVietnamese(long number) {
        if (number == 0) return "không đồng";

        String[] thousands = {"", " nghìn", " triệu", " tỷ"};
        String result = "";
        int i = 0;

        while (number > 0) {
            int part = (int) (number % 1000);
            if (part != 0) {
                result = readThreeDigits(part) + thousands[i] + " " + result;
            }
            number /= 1000;
            i++;
        }

        return result.trim() + " đồng";
    }

    private static String readThreeDigits(int number) {
        int hundred = number / 100;
        int ten = (number % 100) / 10;
        int unit = number % 10;

        String result = "";

        if (hundred > 0) {
            result += units[hundred] + " trăm ";
        }

        if (ten > 1) {
            result += units[ten] + " mươi ";
            if (unit == 1) result += "mốt ";
            else if (unit == 5) result += "lăm ";
            else if (unit > 0) result += units[unit] + " ";
        } else if (ten == 1) {
            result += "mười ";
            if (unit == 5) result += "lăm ";
            else if (unit > 0) result += units[unit] + " ";
        } else if (unit > 0) {
            result += units[unit] + " ";
        }

        return result;
    }
}