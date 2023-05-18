package com.wpf.cbasestudy.ecdhe;

import android.util.Log;

import com.wpf.cbasestudy.MyKeyEev;
import com.wpf.cbasestudy.ecdsa.HexUtil;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.xml.bind.DatatypeConverter;

/**
 * Author: feipeng.wang
 * Time:   2023/5/8
 * Description : ECDHE
 *
 * https://www.oomake.com/question/10261514
 *
 * https://github.com/nelenkov/ecdh-kx/blob/master/src/org/nick/ecdhkx/Crypto.javazl
 */
public class MyTestEcdH {

    private String TAG = "MyTestEcdH";

    /*算法验证：ECDH  */
    public static void testEcdh1() {

        try{
//            Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
//            setupBouncyCastle();
            Security.addProvider(new BouncyCastleProvider());
// Alice sets up the exchange
            KeyPairGenerator aliceKeyGen = KeyPairGenerator.getInstance("ECDH", "BC");
            aliceKeyGen.initialize(new ECGenParameterSpec("prime256v1"), new SecureRandom());
            KeyPair alicePair = aliceKeyGen.generateKeyPair();
            ECPublicKey alicePub = (ECPublicKey)alicePair.getPublic();
            ECPrivateKey alicePvt = (ECPrivateKey)alicePair.getPrivate();
            byte[] alicePubEncoded = alicePub.getEncoded();
            byte[] alicePvtEncoded = alicePvt.getEncoded();
             Log.e("wpf","Alice public: " + DatatypeConverter.printHexBinary(alicePubEncoded));
             Log.e("wpf","Alice private: " + DatatypeConverter.printHexBinary(alicePvtEncoded));
// POST hex(alicePubEncoded)
// Bob receives Alice's public key
            KeyFactory kf = KeyFactory.getInstance("EC");
            PublicKey remoteAlicePub = kf.generatePublic(new X509EncodedKeySpec(alicePubEncoded));
            KeyPairGenerator bobKeyGen = KeyPairGenerator.getInstance("ECDH", "BC");
            bobKeyGen.initialize(new ECGenParameterSpec("prime256v1"), new SecureRandom());
            KeyPair bobPair = bobKeyGen.generateKeyPair();
            ECPublicKey bobPub = (ECPublicKey)bobPair.getPublic();
            ECPrivateKey bobPvt = (ECPrivateKey)bobPair.getPrivate();
            byte[] bobPubEncoded = bobPub.getEncoded();
            byte[] bobPvtEncoded = bobPvt.getEncoded();
             Log.e("wpf","Bob public: " + DatatypeConverter.printHexBinary(bobPubEncoded));
             Log.e("wpf","Bob private: " + DatatypeConverter.printHexBinary(bobPvtEncoded));
            KeyAgreement bobKeyAgree = KeyAgreement.getInstance("ECDH");
            bobKeyAgree.init(bobPvt);
            bobKeyAgree.doPhase(remoteAlicePub, true);
             Log.e("wpf","Bob secret: " + DatatypeConverter.printHexBinary(bobKeyAgree.generateSecret()));
// RESPOND hex(bobPubEncoded)
// Alice derives secret
            KeyFactory aliceKf = KeyFactory.getInstance("EC");
            PublicKey remoteBobPub = aliceKf.generatePublic(new X509EncodedKeySpec(bobPubEncoded));
            KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("ECDH");
            aliceKeyAgree.init(alicePvt);
            aliceKeyAgree.doPhase(remoteBobPub, true);
             Log.e("wpf","Alice secret: " + DatatypeConverter.printHexBinary(aliceKeyAgree.generateSecret()));
//            Log.e("wpf","协商的对称密钥是否相等："+isTrue);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private static void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }


    /**
     * 生成ECDHE密钥对
     *
     * @return 密钥对
     */
    public MyKeyEev generateECKeyPair() throws Exception {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(256); // Set p=256
        KeyPair keyPair = generator.generateKeyPair();
        ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
        String pu = HexUtil.encodeHexString(ecPublicKey.getEncoded());
        String pr = HexUtil.encodeHexString(ecPrivateKey.getEncoded());
        Log.i(TAG, "====生成的EC秘钥对:" + pu+",私钥\n"+pr);
        MyKeyEev myKeyEev  = new MyKeyEev();
        myKeyEev.privateKey = pr;
        myKeyEev.publicKey = pu;
        return myKeyEev;
    }

    /**
     * 生成共享密钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public void deriveSharedSecret(String otherPublicKeyStr,String yourPrivateKeyStr) throws NoSuchAlgorithmException, InvalidKeyException {
/*        String otherPublicKeyStr = "otherPublicKey";
        String yourPrivateKeyStr = "yourPrivateKey";*/
        byte[] otherKeys = HexUtil.decode(otherPublicKeyStr);
        byte[] yourKeys = HexUtil.decode(yourPrivateKeyStr);
        final KeyFactory ec = KeyFactory.getInstance("EC");
        final X509EncodedKeySpec keySpecPb = new X509EncodedKeySpec(otherKeys);
        final PKCS8EncodedKeySpec keySpecPr = new PKCS8EncodedKeySpec(yourKeys);
        Log.i(TAG, "===================================================================下面开始反序列化生成钥匙");
        PublicKey publicKey = null;
        try {
            publicKey = ec.generatePublic(keySpecPb);
            Log.i(TAG, "===================================================================公钥生成成功");
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "===================================================================公钥生成失败", e);
        }
        PrivateKey privateKey = null;
        try {
            privateKey = ec.generatePrivate(keySpecPr);
            Log.i(TAG, "===================================================================私钥生成成功");
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "===================================================================私钥生成失败", e);
        }
        Log.i(TAG, "===================================================================成功获得了反序列化的钥匙");
        KeyAgreement ecdh = KeyAgreement.getInstance("ECDH");
        Log.i(TAG, "===================================================================下面开始ECDH生成器");
        ecdh.init(privateKey);
        ecdh.doPhase(publicKey, true);
        byte[] shareKey = ecdh.generateSecret();
        String finalShareKey = HexUtil.encodeHexString(shareKey);
        Log.i(TAG, "====生成共享密钥:" + finalShareKey);
    }

}
