package com.wpf.cbasestudy.ecdsa;


import android.util.Log;

import com.wpf.cbasestudy.MyKeyEev;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

/**
 * ECCDSA加签验签工具类
 * @author Administrator
 *
 * https://zhuanlan.zhihu.com/p/97953640
 *
 */
public class ECDSAUtil {
    private String TAG = "ECDSAUtil";

    private final String SIGNALGORITHMS = "SHA256withECDSA";
    private final String ALGORITHM = "EC";
    private final String SECP256K1 = "secp256k1";
    private final String SECP256R1 = "secp256r1";
    private final String P_256 = "P-256";
    public  void testEcdha() {

        try{
            //        生成公钥私钥
            KeyPair keyPair1 = getKeyPair();
            PublicKey publicKey1 = keyPair1.getPublic();
            PrivateKey privateKey1 = keyPair1.getPrivate();
            //密钥转16进制字符串
            String publicKey = HexUtil.encodeHexString(publicKey1.getEncoded());
            String privateKey = HexUtil.encodeHexString(privateKey1.getEncoded());
            Log.e(TAG,"生成公钥："+publicKey);
            Log.e(TAG,"生成私钥："+privateKey);

            //16进制字符串转密钥对象
            PrivateKey privateKey2 = getPrivateKey(privateKey);
            PublicKey publicKey2 = getPublicKey(publicKey);
            //加签验签
            String data="需要签名的数据";
            String signECDSA = signECDSA(privateKey2, data);
            boolean verifyECDSA = verifyECDSA(publicKey2, signECDSA, data);
            Log.e(TAG,"验签结果："+verifyECDSA);
        }catch (Exception e){
              Log.e(TAG,"Exception testEcdha：" + e);

        }


    }

    /**
     * 加签
     * @param privateKey 私钥
     * @param data 数据
     * @return
     */
    public  String signECDSA(PrivateKey privateKey, String data) {
        String result = "";
        try {
            //执行签名
            Signature signature = Signature.getInstance(SIGNALGORITHMS);
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            byte[] sign = signature.sign();
            return HexUtil.encodeHexString(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 验签
     * @param publicKey 公钥
     * @param signed 签名
     * @param data 数据
     * @return
     */
    public  boolean verifyECDSA(PublicKey publicKey, String signed, String data) {
        try {
            //验证签名
            Signature signature = Signature.getInstance(SIGNALGORITHMS);
            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            byte[] hex = HexUtil.decode(signed);
            boolean bool = signature.verify(hex);
            //  Log.e(TAG,"验证：" + bool);
            return bool;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从string转private key
     * @param key 私钥的字符串
     * @return
     * @throws Exception
     */
    public  PrivateKey getPrivateKey(String key) throws Exception {

        byte[] bytes = DatatypeConverter.parseHexBinary(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 从string转publicKey
     * @param key 公钥的字符串
     * @return
     * @throws Exception
     */
    public  PublicKey getPublicKey(String key) throws Exception {

        byte[] bytes = DatatypeConverter.parseHexBinary(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }




    /**
     * 生成密钥对
     * @return
     * @throws Exception
     */
    public  KeyPair getKeyPair1() throws Exception{
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SECP256K1);
        KeyPairGenerator kf = KeyPairGenerator.getInstance(ALGORITHM);
//        kf.initialize(ecSpec, new SecureRandom());
        kf.initialize(ecSpec);
        KeyPair keyPair = kf.generateKeyPair();
        return keyPair;
    }

    public KeyPair getKeyPair() throws Exception {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(P_256);
        generator.initialize(ecSpec);
//        generator.initialize(256); // Set p=256

        KeyPair keyPair = generator.generateKeyPair();
        return keyPair;
    }
}
