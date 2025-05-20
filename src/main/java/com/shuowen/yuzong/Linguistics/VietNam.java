package com.shuowen.yuzong.Linguistics;

import java.util.Scanner;

public class VietNam
{
    public static void main(String[] args)
    {
        while (true)
        {
            Scanner sc = new Scanner(System.in);
            String str = sc.nextLine();
            String[] arr = str.split(" ");
            for (String s : arr)
            {
                System.out.print(Trasfer(s)+" ");
            }
        }
    }

    public static String Trasfer(String nam)
    {

        return nam

                .replace("dd","Đ".toLowerCase())

                .replace("a2", "á")
                .replace("a3", "à")
                .replace("a4", "ả")
                .replace("a5", "ã")
                .replace("a6", "ạ")

                .replace("ax2", "ắ")
                .replace("ax3", "ằ")
                .replace("ax4", "ẳ")
                .replace("ax5", "ẵ")
                .replace("ax6", "ặ")
                .replace("ax","ă")

                .replace("aa2", "â")
                .replace("aa3", "ấ")
                .replace("aa4", "ầ")
                .replace("aa5", "ẩ")
                .replace("aa6", "ậ")
                .replace("aa","â")

                .replace("ee2", "ế")
                .replace("ee3", "ề")
                .replace("ee4", "ể")
                .replace("ee5", "ễ")
                .replace("ee6", "ệ")
                .replace("ee","ê")

                .replace("e2", "é")
                .replace("e3", "è")
                .replace("e4", "ẻ")
                .replace("e5", "ẽ")
                .replace("e6", "ẹ")


//	í	ì	ỉ	ĩ	ị
                .replace("i2", "í")
                .replace("i3", "ì")
                .replace("i4", "ỉ")
                .replace("i5", "ĩ")
                .replace("i6", "ị")


                .replace("oo2", "ố")
                .replace("oo3", "ồ")
                .replace("oo4", "ổ")
                .replace("oo5", "ỗ")
                .replace("oo6", "ộ")
                .replace("oo","ô")

                .replace("oh2", "ớ")
                .replace("oh3", "ờ")
                .replace("oh4", "ở")
                .replace("oh5", "ỡ")
                .replace("oh6", "ợ")
                .replace("oh","ơ")

                .replace("o2", "ó")
                .replace("o3", "ò")
                .replace("o4", "ỏ")
                .replace("o5", "õ")
                .replace("o6", "ọ")


                .replace("u2", "ú")
                .replace("u3", "ù")
                .replace("u4", "ủ")
                .replace("u5", "ũ")
                .replace("u6", "ụ")


                .replace("uh2", "ứ")
                .replace("uh3", "ừ")
                .replace("uh4", "ử")
                .replace("uh5", "ữ")
                .replace("uh6", "ự")
                .replace("uh","ư")

                .replace("y2", "ý")
                .replace("y3", "ỳ")
                .replace("y4", "ỷ")
                .replace("y5", "ỹ")
                .replace("y6", "ỵ");
    }
}
