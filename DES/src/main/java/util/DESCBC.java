package util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/1 14:29
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   JAVA实现DES加密实现详解
 */

/**
 * DES 算法       1972美国IBM研制，对称加密算法
 *
 * @author stone
 * @date 2014-03-10 04:47:05
 */
public class DESCBC {
    // 算法名称
    public static final String KEY_ALGORITHM = "DES";
    // 算法名称/加密模式/填充方式
//    public static final String CIPHER_ALGORITHM_ECB = "DES/ECB/PKCS5Padding";
    public static final String CIPHER_ALGORITHM_CBC = "DES/CBC/NoPadding";
    public static String key = "12345678";      //密匙
    public static String iv = "87654321";     //初始化向量

    /**
     * 生成初始化向量
     *
     * @return
     */
    public static byte[] getIV() {
        byte[] bytes = iv.getBytes();
        return bytes;
    }

    /**
     * 生成密钥
     *
     * @return
     * @throws Exception
     */
    public static byte[] generateKey() throws Exception {
        byte[] bytes = key.getBytes();
        return bytes;
    }


    /**
     * DES 加密
     *
     * @param data 原文
     * @return 密文
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data) throws Exception {
        DESKeySpec dks = new DESKeySpec(generateKey());
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey secretKey = factory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(getIV()));
        byte[] enc = cipher.doFinal(data); //加密
        return enc;
    }

    /**
     * DES 解密
     *
     * @param data 密文
     * @return 明文、原文
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data) throws Exception {
        DESKeySpec dks = new DESKeySpec(generateKey());

        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey secretKey = factory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(getIV());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] dec = cipher.doFinal(data); // 解密
        return dec;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}