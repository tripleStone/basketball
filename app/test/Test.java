package test;

import java.net.URLDecoder;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.*;
 
import java.io.*;

public class Test {
	 public static void main(String[] args) {
//	        String algorithm = "DES"; // 定义加密算法,可用 DES,DESede,Blowfish
//	        String message = "321113"; // 生成个DES密钥 
//	        Key key;
//	 
//	        CipherMessage cm = new CipherMessage(algorithm, message);
//	        key = cm.initKey();
//	        byte[] msg = cm.CipherMsg();
//	        System.out.println("加密后的密文为：" + new String(msg));
//	        // System.out.println("密钥为："+new String(cm.getBinaryKey(key)));
//	 
//	        /*byte[] bk = cm.getBinaryKey(key);
//	        System.out.println(bk);
//	         
//	        for (int i = 0; i < bk.length; i++)
//	            System.out.print(bk[i]);*/
//	        System.out.println("解密密文为：" + cm.EncipherMsg(msg, key));
//	 
//		 String msg = "13716321552561000";
//		 System.out.println(msg.substring(1,msg.length()-4));
		 Date date = new Date();
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		 System.out.println(format.format(date));
	    }
}
