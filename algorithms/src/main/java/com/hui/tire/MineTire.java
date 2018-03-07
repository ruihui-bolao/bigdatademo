package com.hui.tire;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/7 15:09
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class MineTire {

    /**
     * 根节点
     */
    private static Node root;

    /**
     *  字符串 list 添加
     */
    public static List charList;

    static {
        charList = Arrays.asList("1","2","3","4","5","6","7","8","9","a","b","c","d","e","f");
        // 初始化 tire root node.
        root = new Node();
    }

    /**
     * 插入数据
     */
    public void insert(String words){
        insert(this.root,words);
    }

    /**
     * 插入数据
     * @param root
     * @param words
     */
    private void insert(Node root, String words){
        String encodStr = CodeUtils.gbEncoding(words);
        char[] chars = encodStr.toCharArray();
        int length = chars.length;
        for (int i = 0; i < chars.length; i++) {
            // 获取字符串对应的下标索引。
            int index = charList.indexOf(String.valueOf(chars[i]));
            if (root.childs[index] != null){
                root.childs[index].prefixNum++;
            }else {
                // 如果不存在的话
                root.childs[index] = new Node();
                root.childs[index].prefixNum++;
            }
            if ( (i+1)% 4 == 0){
                root.childs[index].isWord = true;
            }
            if ( i == length - 1){
                root.childs[index].isWordGroup = true;
                root.childs[index].dumpliNum++;
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
            if (root.isWordGroup == true) {
                // 当前即为一个单词
                String words = CodeUtils.decodeUnicode(prefixs);
                map.put(words, root.dumpliNum);
            }
            for (int i = 0; i < root.childs.length; i++) {
                if (root.childs[i] != null) {
                    String s = (String) charList.get(i);
                    String tempStr = prefixs + s;
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
        char[] chars = CodeUtils.gbEncoding(word).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int index = charList.indexOf(String.valueOf(chars[i]));
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
     * @param word  字符串前缀
     * @return 字符集以及出现次数，如果不存在返回null
     */
    public HashMap<String, Integer> getWordsForPrefix(String word){
        String prefix = CodeUtils.gbEncoding(word);
        return getWordsForPrefix(this.root, prefix);
    }

    private HashMap<String, Integer> getWordsForPrefix(Node root, String prefix){
        char[] chars = prefix.toLowerCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int index = charList.indexOf(String.valueOf(chars[i]));
            if (root.childs[index] == null){
                return null;
            }
            root = root.childs[index];
        }
        return preTraversal(root, prefix);
    }

}
