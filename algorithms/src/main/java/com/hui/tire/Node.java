package com.hui.tire;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/7 15:10
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class Node {
    /**
     * 该字串的重复数目，  该属性统计重复次数的时候有用,取值为0、1、2、3、4、5……
     */
    public int dumpliNum;

    /**
     * 以该字串为前缀的字串数， 应该包括该字串本身！！！！！
     */
    public int prefixNum;

    /**
     * 此处用数组实现，当然也可以map或list实现以节省空间
     */
    public Node childs[];

    /**
     * 是否是汉字
     */
    public boolean isWord;

    /**
     * 是否是词组
     */
    public boolean isWordGroup;

    public Node() {
        dumpliNum = 0;
        prefixNum = 0;
        isWord = false;
        isWordGroup = false;
        childs = new Node[16];
    }

    public int getDumpliNum() {
        return dumpliNum;
    }

    public void setDumpliNum(int dumpliNum) {
        this.dumpliNum = dumpliNum;
    }

    public int getPrefixNum() {
        return prefixNum;
    }

    public void setPrefixNum(int prefixNum) {
        this.prefixNum = prefixNum;
    }

    public Node[] getChilds() {
        return childs;
    }

    public void setChilds(Node[] childs) {
        this.childs = childs;
    }

    public boolean isWord() {
        return isWord;
    }

    public void setWord(boolean word) {
        isWord = word;
    }

    public boolean isWordGroup() {
        return isWordGroup;
    }

    public void setWordGroup(boolean wordGroup) {
        isWordGroup = wordGroup;
    }
}
