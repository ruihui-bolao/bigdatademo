/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.sdyc.jise;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface JISEProtocol {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"JISEProtocol\",\"namespace\":\"com.sdyc.jise\",\"types\":[{\"type\":\"record\",\"name\":\"AomenFoodReview\",\"doc\":\"AomenFoodReview\",\"fields\":[{\"name\":\"shopId\",\"type\":[\"null\",\"string\"],\"doc\":\"店铺id\"},{\"name\":\"shopName\",\"type\":[\"null\",\"string\"],\"doc\":\"店铺名称\"},{\"name\":\"shopImg\",\"type\":[\"null\",\"string\"],\"doc\":\"店铺图片\"},{\"name\":\"shopScore\",\"type\":[\"null\",\"double\"],\"doc\":\"店铺评分\"},{\"name\":\"cntReplied\",\"type\":[\"null\",\"int\"],\"doc\":\"店铺评论数\"},{\"name\":\"createdAt\",\"type\":[\"null\",\"string\"],\"doc\":\"评论时间\"},{\"name\":\"postId\",\"type\":[\"null\",\"string\"],\"doc\":\"评论id\"},{\"name\":\"postTitle\",\"type\":[\"null\",\"string\"],\"doc\":\"评论标题\"},{\"name\":\"postContentText\",\"type\":[\"null\",\"string\"],\"doc\":\"评论内容\"},{\"name\":\"userId\",\"type\":[\"null\",\"string\"],\"doc\":\"评论用户id\"},{\"name\":\"userName\",\"type\":[\"null\",\"string\"],\"doc\":\"评论用户昵称\"},{\"name\":\"userImg\",\"type\":[\"null\",\"string\"],\"doc\":\"评论用户头像\"},{\"name\":\"postUrl\",\"type\":[\"null\",\"string\"],\"doc\":\"帖子链接地址\"},{\"name\":\"reScore\",\"type\":[\"null\",\"double\"],\"doc\":\"评论分数\"},{\"name\":\"mediaName\",\"type\":[\"null\",\"string\"],\"doc\":\"媒体名称\"}]}],\"messages\":{}}");

  @SuppressWarnings("all")
  public interface Callback extends JISEProtocol {
    public static final org.apache.avro.Protocol PROTOCOL = com.sdyc.jise.JISEProtocol.PROTOCOL;
  }
}