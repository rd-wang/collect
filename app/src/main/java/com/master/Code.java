package com.master;

/**
 * @param
 * @author Litao-pc on 2016/11/15.
 *         ~
 */

public class Code {


    private String EncryptHdid(String strOriCode)
    {
        int i;
        char[] strBaseCode=new char[91];
        char[] strBaseNum=new char[58];
        char[] chrOriCode=new char[8];

        //前面65个空格，第66位为8，表示Asci码为65的对应的字母转换成“8”
        String strConvertAZ = "                                                                 8XKQWU4N2BZ9FIAC5MV7RTSHG1";
        //前面48个空格
        String strConvert09 = "                                                P6EL0DOY3J";

        String strResult;
        byte intCode;

        strBaseCode = strConvertAZ.toCharArray();
        strBaseNum = strConvert09.toCharArray();

        chrOriCode = strOriCode.toCharArray();
        strResult = "";

        for(i=0;i<8;i++)
        {
            intCode = (byte)chrOriCode[i];
            if(intCode>64 && intCode<91)
            {
                //A——Z
                strResult = strResult + strBaseCode[intCode]+"";
            }
            else if (intCode > 47 && intCode < 58)
            {
                //0——9
                strResult = strResult + strBaseNum[intCode]+"";
            }
            else
            {
                strResult = strResult + chrOriCode[i]+"";
            }
        }
        return strResult;
    }


}
