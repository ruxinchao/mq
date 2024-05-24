package com.rxc.rocketmq.consumer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "消费者Demo", tags = { "消费者Demo" })
@ApiSort(2)
public class ConsumerController {

	private static Map<String, DefaultMQPushConsumer> produerMap = new HashMap<>();

	private static Map<String, List<MessageExt>> topicMsg = new HashMap<>();

	@GetMapping("/subscription/{group}/{topic}")
	@ApiOperation(value = "订阅topic", notes = "订阅topic")
	@ApiOperationSupport(order = 3, author = "rxc")
	public String getMessageCount(@PathVariable("group") String group, @PathVariable("topic") String topic,
			String nameServer) throws MQClientException {
		if (produerMap.get(group + "-" + topic) != null) {
			return "topic:[" + topic + "]已存在group:[" + group + "]的消费者，无需重复订阅。";
		}
		/**
		 * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		 * 注意：ConsumerGroupName需要由应用来保证唯一
		 */
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
		consumer.setNamesrvAddr(nameServer);
		consumer.setInstanceName(topic + "-" + group + "-Consumber");
		consumer.setMessageModel(MessageModel.CLUSTERING);
		consumer.setVipChannelEnabled(false);

		/**
		 * 订阅指定topic下tags分别等于TagA或TagC或TagD
		 */
		consumer.subscribe(topic, "*");

		consumer.registerMessageListener(new MessageListenerConcurrently() {

			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				for (MessageExt messageExt : msgs) {
					 String body = null;
					try {
						body = new String(messageExt.getBody(), "UTF-8");
						if("error".equals(body)) {
							return ConsumeConcurrentlyStatus.RECONSUME_LATER;
						}
						
						messageExt.setBody(body.getBytes());
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					messageExt.putUserProperty("msg-context-String", body);
				}
				
				List<MessageExt> msgList = topicMsg.get(group + "-" + topic);
				if (msgList == null) {
					msgList = new ArrayList<MessageExt>();
				}
				msgList.addAll(msgs);
				topicMsg.put(group + "-" + topic, msgList);
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

			}
		});
		
	    // 设置最大重试次数为3次
        consumer.setMaxReconsumeTimes(1);
       
		/**
		 * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
		 */
		consumer.start();
		produerMap.put(group + "-" + topic, consumer);
		return "success";
	}

	@GetMapping("/get/{group}/{topic}")
	@ApiOperation(value = "获取消息", notes = "获取消息")
	@ApiOperationSupport(order = 2, author = "rxc")
	public Object getMsg(@PathVariable("group") String group, @PathVariable("topic") String topic) {
		if(topicMsg.get(group + "-" + topic) == null || topicMsg.get(group + "-" + topic).size() ==0 ) {
			return "无消息";
		}
		return topicMsg.get(group + "-" + topic);
	}

	@GetMapping("/clean/{group}/{topic}")
	@ApiOperation(value = "clean消息", notes = "clean消息")
	@ApiOperationSupport(order = 1, author = "rxc")
	public Object cleanMsg(@PathVariable("group") String group, @PathVariable("topic") String topic) {
		topicMsg.remove(group + "-" + topic);
		if(topicMsg.get(group + "-" + topic) == null || topicMsg.get(group + "-" + topic).size() ==0 ) {
			return "无消息";
		}
		return topicMsg.get(group + "-" + topic);
	}
}
