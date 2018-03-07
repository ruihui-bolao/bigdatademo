package com.tire;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/7 13:29
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   Tire 树的实例
 */
public class Tire {


    /**
     * 内部节点类
     */
    private class Node {
        //该字串的重复数目，  该属性统计重复次数的时候有用,取值为0、1、2、3、4、5……
        private int dumpli_num;
        //以该字串为前缀的字串数， 应该包括该字串本身！！！！！
        private int prefix_num;
        //此处用数组实现，当然也可以map或list实现以节省空间
        private Node childs[];
        //是否为单词节点
        private boolean isLeaf;

        public Node() {
            dumpli_num = 0;
            prefix_num = 0;
            isLeaf = false;
            childs = new Node[26];
        }
    }

    /**
     * 树根
     */
    private Node root;

    public Tire() {
        ///初始化trie 树
        root = new Node();
    }

    /**
     * 插入字符串
     *
     * @param words
     */
    public void insert(String words) {
        insert(this.root, words);
    }

    /**
     * 插入字符串
     *
     * @param root
     * @param words
     */
    private void insert(Node root, String words) {
        words = words.toLowerCase();
        char[] chars = words.toCharArray();
        int length = chars.length;
        for (int i = 0; i < chars.length; i++) {
            //用相对于a字母的值作为下标索引，也隐式地记录了该字母的值
            int index = chars[i] - 'a';
            if (root.childs[index] != null) {
                // 如果已经存在，该子节点的predix_num++
                root.childs[index].prefix_num++;
            } else {
                // 如果不存在的话
                root.childs[index] = new Node();
                root.childs[index].prefix_num++;
            }

            if (i == length - 1) {
                root.childs[index].isLeaf = true;
                root.childs[index].dumpli_num++;
            }
            ///root指向子节点，继续处理
            root = root.childs[index];
        }
    }

    /**
     * 遍历Trie 树，查找所有的 words 以及出现的次数
     *
     * @return
     */
    public HashMap<String, Integer> getAllWords() {
        return preTraversal(this.root, "");
    }

    /**
     * 前序遍历
     *
     * @param root    子树根节点
     * @param prefixs 查询到该节点前所遍历过的前缀
     * @return
     */
    private HashMap<String, Integer> preTraversal(Node root, String prefixs) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        if (root != null) {
            if (root.isLeaf == true) {
                // 当前即为一个单词
                map.put(prefixs, root.dumpli_num);
            }
            int length = root.childs.length;
            for (int i = 0; i < root.childs.length; i++) {
                if (root.childs[i] != null) {
                    char ch = (char) (i + 'a');
                    String tempStr = prefixs + ch;
                    map.putAll(preTraversal(root.childs[i],tempStr));
                }
            }
        }
        return map;
    }

    /**
     *  判断某个字符串是否存在字典树中
     * @param word
     * @return
     */
    public boolean isExist(String word){
        return search(this.root, word);
    }

    /**
     * 查询某字符串是否在字典树中
     * @param root
     * @param word
     * @return
     */
    private boolean search(Node root, String word){
        char[] chars = word.toLowerCase().toCharArray();
        int length = chars.length;
        for (int i = 0; i < chars.length; i++) {
            int index = chars[i] - 'a';
            if (root.childs[index] == null){
                // 如果不存在，则说明查找失败
                return false;
            }
            root = root.childs[index];
        }
        return true;
    }

    /**
     *  得到以某字符串为前缀的字符集，包括字符串本身！类似单词输入法的联想功能。
     * @param prefix  字符串前缀
     * @return 字符集以及出现次数，如果不存在返回null
     */
    public HashMap<String, Integer> getWordsForPrefix(String prefix){
        return getWordsForPrefix(this.root, prefix);
    }

    private HashMap<String, Integer> getWordsForPrefix(Node root,String prefix){
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        char[] chars = prefix.toLowerCase().toCharArray();
        int length = chars.length;
        for (int i = 0; i < chars.length; i++) {
            int index = chars[i] - 'a';
            if (root.childs[index] == null){
                return null;
            }
            root = root.childs[index];
        }
        return preTraversal(root, prefix);
    }

}
