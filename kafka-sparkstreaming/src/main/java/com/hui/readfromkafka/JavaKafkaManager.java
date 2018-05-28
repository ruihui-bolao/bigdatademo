package com.hui.readfromkafka;

import kafka.common.TopicAndPartition;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkException;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaCluster;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.kafka.OffsetRange;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.collection.immutable.Map$;
import scala.collection.mutable.ArrayBuffer;
import scala.util.Either;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/23 15:51
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   java 实现管理 offset
 */
public class JavaKafkaManager implements Serializable {

    private scala.collection.immutable.Map kafkaParams;

    /**
     * KafkaCluster类用于建立和Kafka集群的链接相关的操作工具类
     */
    private KafkaCluster kafkaCluster;

    public JavaKafkaManager(Map kafkaParams) {
        this.kafkaParams = toScalaImmutableMap(kafkaParams);
        this.kafkaCluster = new KafkaCluster(this.kafkaParams);
    }

    /**
     * readfromkafka 创建 DirctStraam
     *
     * @param jssc
     * @param kafkaParams
     * @param topics
     * @return
     */
    public JavaInputDStream creatDirctStream(JavaStreamingContext jssc, Map kafkaParams, Set topics) {
        // 获取kafka 消费的groupId
        String groupId = String.valueOf(kafkaParams.get("group.id"));
        JavaInputDStream<String> message = null;
        // 根据实际情况更新或者消费offsets
        try {
            setOrUpdateOffsets(topics, groupId);
            scala.collection.immutable.Set immutableTopics = JavaConversions.asScalaSet(topics).toSet();
            Either<ArrayBuffer<Throwable>, scala.collection.immutable.Set<TopicAndPartition>> partitionEaily = kafkaCluster.getPartitions(immutableTopics);
            if (partitionEaily.isLeft()) {
                throw new SparkException("get readfromkafka partition failed: ${partitionEaily.left.get}");
            }
            scala.collection.immutable.Set<TopicAndPartition> partitionNow = partitionEaily.right().get();
            Either<ArrayBuffer<Throwable>, scala.collection.immutable.Map<TopicAndPartition, Object>>
                    consumerOffsetsEaily = kafkaCluster.getConsumerOffsets(groupId, partitionNow);
            if (consumerOffsetsEaily.isLeft()) {
                throw new SparkException("get readfromkafka consumer offsets failed: ${consumerOffsetsE.left.get}");
            }
            scala.collection.immutable.Map<TopicAndPartition, Object> consumerOffsetsTemp = consumerOffsetsEaily.right().get();
            Map<TopicAndPartition, Object> consumerOffsets = JavaConversions.asJavaMap(consumerOffsetsTemp);
            HashMap<TopicAndPartition, Long> consumerOffsetsLong = new HashMap<TopicAndPartition, Long>();
            for (TopicAndPartition key : consumerOffsets.keySet()) {
                consumerOffsetsLong.put(key, (Long) consumerOffsets.get(key));
            }

            /**
             * jssc：JavaSaprkStreamingContext
             * String.class: key值类型
             * String.class: value类型
             * StringDecoder.class: 解码器
             * kafkaParams: kafka的配置参数
             * consumerOffsetsLong: readfromkafka consumer offset
             * new Function: 返回kafka数据源的信息
             */
            message = KafkaUtils.createDirectStream(jssc,
                    String.class,
                    String.class,
                    StringDecoder.class,
                    StringDecoder.class,
                    String.class,
                    kafkaParams,
                    consumerOffsetsLong,
                    new Function<MessageAndMetadata<String, String>, String>() {
                        @Override
                        public String call(MessageAndMetadata<String, String> v) throws Exception {
                            return v.message();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * 创建Kafka数据流之前，根据实际的情况更新消费offsets
     *
     * @param topics
     * @param groupId
     */
    public void setOrUpdateOffsets(Set<String> topics, String groupId) throws Exception {
        for (String topic : topics) {
            boolean hasConsumered = true;
            HashSet<String> topicSet = new HashSet<String>();
            topicSet.add(topic);
            // JavaConversions 实现将java集合修改为scala集合
            scala.collection.immutable.Set<String> immutableTopic = JavaConversions.asScalaSet(topicSet).toSet();
            Either<ArrayBuffer<Throwable>, scala.collection.immutable.Set<TopicAndPartition>> partitionsEarly = kafkaCluster.getPartitions(immutableTopic);
            // isleft 表示在该分区之前还有没有读到的分区
            if (partitionsEarly.isLeft()) {
                throw new SparkException("get readfromkafka partition failed: ${partitionsE.left.get}");
            }

            // 获取kafka consumer 的 offset（是从zk中获取的）
            scala.collection.immutable.Set<TopicAndPartition> partitionNow = partitionsEarly.right().get();
            Either<ArrayBuffer<Throwable>, scala.collection.immutable.Map<TopicAndPartition, Object>> consumerOffsetEaily = kafkaCluster.getConsumerOffsets(groupId, partitionNow);
            // isleft 表示 offset 之前还有没有消费的数据
            if (consumerOffsetEaily.isLeft()) {
                hasConsumered = false;
            }
            /**
             * 如果streaming程序执行的时候出现kafka.common.OffsetOutOfRangeException，
             * 说明zk上保存的offsets已经过时了，即kafka的定时清理策略已经将包含该offsets的文件删除。
             * 针对这种情况，只要判断一下zk上的consumerOffsets和earliestLeaderOffsets的大小，
             * 如果consumerOffsets比earliestLeaderOffsets还小的话，说明consumerOffsets已过时,
             * 这时把consumerOffsets更新为earliestLeaderOffsets
             */
            // 如果消费过数据
            if (hasConsumered) {
                // 获取kafka现在保存最早的offset( 是从kafka中获取的)
                Either<ArrayBuffer<Throwable>, scala.collection.immutable.Map<TopicAndPartition, KafkaCluster.LeaderOffset>>
                        earliestLeaderOffsetsEaily = kafkaCluster.getEarliestLeaderOffsets(partitionNow);
                if (earliestLeaderOffsetsEaily.isLeft()) {
                    throw new SparkException("get earliest leader offsets failed: ${earliestLeaderOffsetsE.left.get}");
                }
                scala.collection.immutable.Map<TopicAndPartition, KafkaCluster.LeaderOffset> earliestLeaderOffsets = earliestLeaderOffsetsEaily.right().get();
                scala.collection.immutable.Map<TopicAndPartition, Object> consumerOffsets = consumerOffsetEaily.right().get();
                // 可能存在部分分区consumerOffsets过时，所以要更新过时分区的consumerOffsets为earliestLeaderOffsets
                HashMap offsets = new HashMap();
                // 将scala的map 转换为 java map
                Map<TopicAndPartition, Object> topicAndPartitionObjectMap = JavaConversions.asJavaMap(consumerOffsets);
                // 遍历consumerList 获取对应的 offset
                for (TopicAndPartition key : topicAndPartitionObjectMap.keySet()) {
                    Long n = (Long) topicAndPartitionObjectMap.get(key);
                    long earliestOffset = earliestLeaderOffsets.get(key).get().offset();
                    if (n < earliestOffset) {
                        System.out.println("consumer group:"
                                + groupId + ",topic:"
                                + key.topic() + ",partition:" + key.partition()
                                + " offsets已经过时，更新为" + earliestOffset);
                        offsets.put(key, earliestLeaderOffsets);
                    }
                }

                // 更新offset
                if (!offsets.isEmpty()) {
                    scala.collection.immutable.Map topicAndPartitionLongMap = toScalaImmutableMap(offsets);
                    kafkaCluster.setConsumerOffsets(groupId, topicAndPartitionLongMap);
                }

            } else {
                String offsetReset = String.valueOf(kafkaParams.get("auto.offset.reset").get()).toLowerCase();
                scala.collection.immutable.Map<TopicAndPartition, KafkaCluster.LeaderOffset> leaderOffsets = null;
                if ("smallest".equals(offsetReset)) {
                    Either<ArrayBuffer<Throwable>, scala.collection.immutable.Map<TopicAndPartition, KafkaCluster.LeaderOffset>> leaderOffsetsEaily = kafkaCluster.getEarliestLeaderOffsets(partitionNow);
                    if (leaderOffsetsEaily.isLeft()) {
                        throw new SparkException("get earliest leader offsets failed: ${leaderOffsetsE.left.get}");
                    }
                    leaderOffsets = leaderOffsetsEaily.right().get();
                } else {
                    Either<ArrayBuffer<Throwable>, scala.collection.immutable.Map<TopicAndPartition, KafkaCluster.LeaderOffset>> latestLeaderOffsets = kafkaCluster.getLatestLeaderOffsets(partitionNow);
                    if (latestLeaderOffsets.isLeft()) {
                        throw new SparkException("get latest leader offsets failed: ${leaderOffsetsE.left.get}");
                    }
                    leaderOffsets = latestLeaderOffsets.right().get();
                }
                Map<TopicAndPartition, KafkaCluster.LeaderOffset> topicAndPartitionLeaderOffsetMap = JavaConversions.mapAsJavaMap(leaderOffsets);
                Map offsets = new HashMap<TopicAndPartition, Long>();
                for (TopicAndPartition key : topicAndPartitionLeaderOffsetMap.keySet()) {
                    KafkaCluster.LeaderOffset offset = topicAndPartitionLeaderOffsetMap.get(key);
                    long offset1 = offset.offset();
                    offsets.put(key, offset1);
                }
                scala.collection.immutable.Map immutableOffsets = toScalaImmutableMap(offsets);
                kafkaCluster.setConsumerOffsets(groupId, immutableOffsets);
            }
        }
    }

    /**
     * 更新zookeeper上的消费offsets
     *
     * @param offsetRanges
     */
    public void updateZKOffsets(OffsetRange[] offsetRanges) {
        // 获取kafka 消费的groupId
        String groupId = String.valueOf(kafkaParams.get("group.id").get());
        for (OffsetRange offset : offsetRanges) {
            TopicAndPartition topicAndPartition = new TopicAndPartition(offset.topic(), offset.partition());
            Map offsets = new HashMap();
            offsets.put(topicAndPartition, offset.untilOffset());
            Either o = kafkaCluster.setConsumerOffsets(groupId, toScalaImmutableMap(offsets));
            if (o.isLeft()) {
                System.out.println("Error updating the offset to Kafka cluster: ${o.left.get}");
            }
        }

    }

    /**
     * 对javaMap进行转换为 scala collection.immutable.Map
     *
     * @param javaMap
     * @return
     */
    private static scala.collection.immutable.Map toScalaImmutableMap(Map javaMap) {
        ArrayList<Tuple2<Object, Object>> list = new ArrayList<Tuple2<Object, Object>>(javaMap.size());
        for (Object entry : javaMap.entrySet()) {
            Map.Entry line = (Map.Entry) entry;
            list.add(new Tuple2<Object, Object>(line.getKey(), line.getValue()));
        }
        final Seq<Tuple2<Object, Object>> seq = JavaConverters.asScalaBufferConverter(list).asScala().toSeq();
        scala.collection.immutable.Map res = Map$.MODULE$.apply(seq);
        return res;
    }
}
