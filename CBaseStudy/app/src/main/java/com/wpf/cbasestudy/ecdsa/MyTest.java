package com.wpf.cbasestudy.ecdsa;

import android.util.Log;

import com.wpf.cbasestudy.MyKeyEev;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.asymmetric.ec.EC5Util;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.KeyAgreement;

/**
 * Author: feipeng.wang
 * Time:   2023/5/8
 * Description : https://blog.csdn.net/qq_34900897/article/details/129204179
 * secp256r1 secp256k1
 */
public class MyTest {
    private String TAG = "MyTest";
    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    /*算法验证：HexUtil  secp256r1 */
    public  void testECDH() {

        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
            keyGen.initialize(ecSpec);
            // A生成自己的私密信息
            KeyPair keyPairA = keyGen.generateKeyPair();
            KeyAgreement kaA = KeyAgreement.getInstance("ECDH");
            kaA.init(keyPairA.getPrivate());
            // B生成自己的私密信息
            KeyPair keyPairB = keyGen.generateKeyPair();
            KeyAgreement kaB = KeyAgreement.getInstance("ECDH");
            kaB.init(keyPairB.getPrivate());

            // B收到A发送过来的公用信息，计算出对称密钥
            kaB.doPhase(keyPairA.getPublic(), true);
            byte[] kBA = kaB.generateSecret();

            // A收到B发送过来的公开信息，计算对对称密钥
            kaA.doPhase(keyPairB.getPublic(), true);
            byte[] kAB = kaA.generateSecret();
            boolean isTrue = Arrays.equals(kBA, kAB);
            Log.e("wpf","协商的对称密钥是否相等："+isTrue);
        }catch (Exception e){

        }

    }


    /**
     * 生成ECDHE密钥对
     *
     * @return 密钥对
     */
    public MyKeyEev generateECKeyPair() throws Exception {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        generator.initialize(ecSpec);
//        generator.initialize(256); // Set p=256

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
    public MyKeyEev nnnn() {

        X9ECParameters ecp = SECNamedCurves.getByName("secp256k1");

        ECDomainParameters domainParams = new ECDomainParameters(
                ecp.getCurve(),
                ecp.getG(),
                ecp.getN(),
                ecp.getH(),
                ecp.getSeed());

// Generate a private key and a public key

        AsymmetricCipherKeyPair keyPair;

        ECKeyGenerationParameters keyGenParams = new ECKeyGenerationParameters(domainParams, new SecureRandom());

        ECKeyPairGenerator generator = new ECKeyPairGenerator();

        generator.init(keyGenParams);

        keyPair = generator.generateKeyPair();

        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) keyPair.getPrivate();

        ECPublicKeyParameters publicKey = (ECPublicKeyParameters) keyPair.getPublic();

        String pu = HexUtil.encodeHexString(publicKey.getQ().getEncoded());
        String pr = HexUtil.encodeHexString(privateKey.getD().toByteArray());
        Log.i(TAG, "====生成的EC秘钥对:" + pu+",私钥\n"+pr);
        MyKeyEev myKeyEev  = new MyKeyEev();
        myKeyEev.privateKey = pr;
        myKeyEev.publicKey = pu;

        byte[] privateKeyBytes = privateKey.getD().toByteArray();

// First print our generated private key and public key


        Log.e("mlt",".......Private key:..........."+HexUtil.toHex(privateKeyBytes));


        Log.e("mlt","........Public key:..........."+HexUtil.toHex(publicKey.getQ().getEncoded()));
// Then calculate the public key only using domainParams.getG() and private key

        ECPoint Q = domainParams.getG().multiply(new BigInteger(privateKeyBytes));

        Log.e("mlt",".......Calculated public key:...." +
                "......."+HexUtil.toHex(Q.getEncoded()));

// The calculated public key and generated public key should always match

        if (!HexUtil.toHex(publicKey.getQ().getEncoded()).equals(HexUtil.toHex(Q.getEncoded()))) {

            Log.e("mlt",".......ERROR: Public keys do not match!:...........");

        } else {

            Log.e("mlt",".......Congratulations, public keys match:...........");

        }
        return myKeyEev;
    }














    public String generateAgreedKey(PrivateKey privateKey, PublicKey publicKey) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "SC");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);

        byte[] sharedKeyBytes = keyAgreement.generateSecret();
//        return Base64.encodeToString(sharedKeyBytes, Base64.DEFAULT).replaceAll("\n", "");
        String shareS = HexUtil.toHex(sharedKeyBytes);
        Log.e(TAG, "====生成的协商秘钥:" + shareS);
        return shareS;
    }

    public static ECPublicKey keyToPublick(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // transform from hex to ECPublicKey
        byte[] ecRawExternalPublicKey = HexUtil.hexStringToByteArray(key);
        ECPublicKey ecExternalPublicKey = null;
        KeyFactory externalKeyFactor = null;

        ECNamedCurveParameterSpec ecExternalNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECCurve curve = ecExternalNamedCurveParameterSpec.getCurve();
        EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, ecExternalNamedCurveParameterSpec.getSeed());
        java.security.spec.ECPoint ecPoint = ECPointUtil.decodePoint(ellipticCurve, ecRawExternalPublicKey);
        java.security.spec.ECParameterSpec ecParameterSpec = EC5Util.convertSpec(ellipticCurve, ecExternalNamedCurveParameterSpec);
        java.security.spec.ECPublicKeySpec externalPublicKeySpec = new java.security.spec.ECPublicKeySpec(ecPoint, ecParameterSpec);

        externalKeyFactor = java.security.KeyFactory.getInstance("EC");
        // this is externalPubicKey
        ecExternalPublicKey = (ECPublicKey) externalKeyFactor.generatePublic(externalPublicKeySpec);
        return ecExternalPublicKey;
    }


    public static ECPrivateKey keyToPrivate(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // transform from hex to ECPublicKey
        byte[] ecRawExternalPublicKey = HexUtil.hexStringToByteArray(key);
        ECPrivateKey ecPrivateKey = null;
        KeyFactory externalKeyFactor = null;

        ECNamedCurveParameterSpec ecExternalNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECCurve curve = ecExternalNamedCurveParameterSpec.getCurve();
        EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, ecExternalNamedCurveParameterSpec.getSeed());
        java.security.spec.ECParameterSpec ecParameterSpec = EC5Util.convertSpec(ellipticCurve, ecExternalNamedCurveParameterSpec);
        java.security.spec.ECPrivateKeySpec externalPublicKeySpec = new java.security.spec.ECPrivateKeySpec(new BigInteger(ecRawExternalPublicKey), ecParameterSpec);

        externalKeyFactor = java.security.KeyFactory.getInstance("EC");
        // this is externalPubicKey
        ecPrivateKey = (ECPrivateKey) externalKeyFactor.generatePrivate(externalPublicKeySpec);
        return ecPrivateKey;
    }

}
