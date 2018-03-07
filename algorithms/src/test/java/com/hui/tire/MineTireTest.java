package com.hui.tire;

import java.util.HashMap;

public class MineTireTest {

    public static void main(String[] args) {

        MineTire mineTire = new MineTire();
        mineTire.insert("中国");
//        HashMap<String, Integer> map = mineTire.getAllWords();
//        for(String key:map.keySet()){
//            System.out.println(key+" 出现: "+ map.get(key)+"次");
//        }

/*        HashMap<String, Integer> map = mineTire.getWordsForPrefix("中");
        System.out.println("\n\n包含chin（包括本身）前缀的单词及出现次数：");
        for(String key:map.keySet()){
            System.out.println(key+" 出现: "+ map.get(key)+"次");
        }*/

        if(mineTire.isExist("国")==false){
            System.out.println("\n\n字典树中不存在：国 ");
        }
    }
}