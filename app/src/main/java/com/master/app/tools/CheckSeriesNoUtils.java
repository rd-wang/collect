package com.master.app.tools;

import java.io.UnsupportedEncodingException;

/**
 * Created by rd on 2017/4/1.
 */

public class CheckSeriesNoUtils {
//    获取注册码：
//    CheckSeriesNo(strUnitCode, tb_HDid.Text.Trim(), tb_SeriesNO.Text,ref newSeriesNo);
//    根据上面函数返回来的newSeriesNo值就是注册码

    //读取硬盘及注册信息
    public static boolean GetHardInfo(String strHdid) {
        try {
            if (strHdid.length() > 8) {
                //如果读取到的硬盘序列号长度大于8，则取后8位
                strHdid = strHdid.substring(strHdid.length() - 8, 8);
            } else if (strHdid == "") {
                //如果没有读取到序列号，则赋值位"CATSICAB"
                strHdid = "CATSICAB";
            }
            //硬件码加密
            strHdid = encryptHdid(strHdid);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 对硬件码进行加密
     *
     * @param strOriCode 原始的硬件码
     * @return 加密后的硬件码
     */
    public static String encryptHdid(String strOriCode) {
        int i;
        char[] strBaseCode = new char[91];
        char[] strBaseNum = new char[58];
        char[] chrOriCode = new char[8];

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

        for (i = 0; i < 8; i++) {
            intCode = (byte) chrOriCode[i];
            if (intCode > 64 && intCode < 91) {
                //A——Z
                strResult = strResult + strBaseCode[intCode];
            } else if (intCode > 47 && intCode < 58) {
                //0——9
                strResult = strResult + strBaseNum[intCode];
            } else {
                strResult = strResult + chrOriCode[i];
            }
        }
        return strResult;
    }

    /**
     * 将单位名称转换为十六进制
     *
     * @param strUnitCode 单位名称
     * @return 十六进制的单位名称
     */
    public static String covUnitCode(String strUnitCode) {
        byte[] myBytes;
        try {
//            myBytes = strUnitCode.getBytes("GBK");
            myBytes = strUnitCode.getBytes("ASCII");
            LoggerUtils.d("", "");
            for (byte b : myBytes) {
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
        int i = myBytes.length - 1;
        String LowCode = String.valueOf((Math.abs(myBytes[0] - 160)));
        if (LowCode.length() == 1) {
            LowCode = "0" + LowCode;
        }
        String HightCode = String.valueOf((Math.abs(myBytes[i] - 160)));
        if (HightCode.length() == 1) {
            HightCode = "0" + HightCode;
        }
        String Coding = LowCode + HightCode + "CATS";

        if (Coding.length() > 8) {
            Coding = Coding.substring(0, 8);
        }
        return Coding;
    }

//    public void WriteSeriesNo(string strUnit, string strHdid, string strSeriesNo)
//    {
//        try
//        {
//            string strFileName = Application.StartupPath + "\\Catsic.Data.ShareRg.dll";
//            System.IO.File.WriteAllText(strFileName, strUnit + (char)13  + strHdid + (char)13 + strSeriesNo, System.Text.Encoding.Unicode);
//        }
//        catch (Exception ex)
//        {
//            Console.WriteLine(ex.ToString());
//        }
//    }

//    public boolean CheckHdid() {
//        try {
//            //注册码文件
//            String newSeriesNo = "";
//            String strUint, strUser, strHdid, strSeriesNo;
//            //从注册文件中读取注册信息
//            strUint = objReader.ReadLine();
//            //  strUser = objReader.ReadLine();
//            strHdid = objReader.ReadLine();
//            strSeriesNo = objReader.ReadLine();
//            //--------------加上一个万能的注册码  --------------
//            //-----------------------崔应寿，怕一些无法读出硬盘序列号的机器-----
//            if (strSeriesNo.toLowerCase() == "catsic64925538") {
//                return true;
//            }
//            //将单位名称第一位和用户名称第一位转换为代码
//            // strUint = covUnitCode(strUint.Substring(0, 1) + strUser.Substring(0, 1));
//            strUint = covUnitCode(strUint.substring(0, 1));
//            //如果文件中保存的机器码和读取出来的不同，则要重新注册
//            if (tb_HDid.Text.Trim() != strHdid) {
//                return false;
//            }
//            //如果文件中保存的注册码和计算注册码不同，则返回false
//            else if (checkSeriesNo(strUint, strHdid, strSeriesNo) == false) {
//                return false;
//            } else {
//                return true;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
//    }

    public static boolean checkSeriesNo(String strUnitCode, String strHdid, String strSeriesNo) {
        //byte[] intBaseNumHD = new byte[8] { 5, 8, 2, 0, 9, 7, 4, 9 };
        //byte[] intBaseNumUnit = new byte[8] { 8, 2, 3, 0, 6, 6, 4, 7 };
//        strHdid = encryptHdid(strHdid);
        strUnitCode = covUnitCode(strUnitCode.substring(0, 1));
//        strUnitCode ="2913CATS";


        byte[] intBaseNumHD = new byte[]{8, 2, 3, 0, 6, 6, 4, 7};
        byte[] intBaseNumUnit = new byte[]{5, 8, 2, 0, 9, 7, 4, 9};

        int i, intResult;


        char[] chrHdid = new char[8];
        char[] chrUnitid = new char[8];

        char[] chrHd = new char[8];
        char[] chrUnit = new char[8];

        //对硬件码加密
        chrHdid = strHdid.toCharArray();
        for (i = 0; i < 8; i++) {
            intResult = (byte) chrHdid[i] + intBaseNumHD[i];
            if (intResult > 90) {
                intResult = intResult - 26;
            } else if (intResult > 57 && intResult < 65) {
                intResult = intResult - 10;
            }
            if (intResult == 73 || intResult == 105) {
                intResult = 49;
                // Console.WriteLine("chrHd字母：I改成数字：1");
            } else if (intResult == 79 || intResult == 111) {
                intResult = 48;
                // Console.WriteLine("chrHd字母：O改成数字：0");
            } else if (intResult == 90 || intResult == 122) {
                intResult = 50;
                //Console.WriteLine("chrHd字母：Z改成数字：z");
            }
            if (intResult < 48 || intResult > 122) {
                intResult = 68;
            }
            // Console.WriteLine((char)intResult);
            chrHd[i] = (char) intResult;
        }
        //对单位代码加密
        chrUnitid = strUnitCode.toCharArray();
        for (i = 0; i < 8; i++) {
            intResult = (byte) chrUnitid[i] + intBaseNumUnit[i];
            if (intResult > 90) {
                intResult = intResult - 26;   //65+(intResult-90)-1
            } else if (intResult > 57 && intResult < 65) {
                intResult = intResult - 10;   //48+(intResult-57)-1
            }
            if (intResult == 73 || intResult == 105) {
                intResult = 49;
                // Console.WriteLine("chrUnit字母：I改成数字：1");
            } else if (intResult == 79 || intResult == 111) {
                intResult = 48;
                // Console.WriteLine("chrUnit字母：O改成数字：0");
            } else if (intResult == 90 || intResult == 122) {
                intResult = 50;
                // Console.WriteLine("chrUnit字母：Z改成数字：2");
            }
            if (intResult < 48 || intResult > 122) {
                intResult = 68;
            }
            //  Console.WriteLine((char)intResult);
            chrUnit[i] = (char) intResult;
        }
        //将硬盘号和单位号混合得到注册码
        String strSeries = "";
        for (i = 0; i < 8; i++) {
            strSeries = strSeries + chrHd[i] + chrUnit[i];
            if (i == 1 || i == 3 || i == 5) strSeries = strSeries + "-";
        }
        if (StringUtils.isEmpty(strSeriesNo)) {
            return false;
        }
        if (strSeriesNo.equals(strSeries.substring(0, 4))) {
            return true;
        } else {
            return false;
        }
    }
}