package com.rxc.rocketmq.produce;

import java.util.HashMap;
import java.util.Map;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "生产者Demo", tags = { "生产者Demo" })
@ApiSort(9999)
public class ProducerController {

	private static Map<String, DefaultMQProducer> produerMap = new HashMap<>();

	@GetMapping("/send/{group}/{topic}")
    @ApiOperation(value = "发送消息", notes = "发送消息")
    @ApiOperationSupport(order = 1, author = "rxc")
    @ApiImplicitParams({ @ApiImplicitParam(name = "nameServer", value = "nameServer地址", required = true),
    	@ApiImplicitParam(name = "tag", value = "tag", required = true),
    	@ApiImplicitParam(name = "key", value = "key", required = true),
    @ApiImplicitParam(name = "msg", value = "消息内容", required = true)
    })
	public SendResult getMessageCount(@PathVariable("group") String group, @PathVariable("topic") String topic,
			String nameServer, String tag, String key, String msg)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {

		/**
		 * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
		 * 注意：ProducerGroupName需要由应用来保证唯一<br>
		 * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键，
		 * 因为服务器会回查这个Group下的任意一个Producer
		 */

		DefaultMQProducer producer = produerMap.get(group);
		if (producer == null) {
			producer = new DefaultMQProducer(group);
			producer.setNamesrvAddr(nameServer);
			producer.setInstanceName(topic +"-" +group + "-Producer");
			/**
			 * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br>
			 * 注意：切记不可以在每次发送消息时，都调用start方法
			 */
			producer.start();
			
			produerMap.put(group, producer);
		}

		/**
		 * 下面这段代码表明一个Producer对象可以发送多个topic，多个tag的消息。
		 * 注意：send方法是同步调用，只要不抛异常就标识成功。但是发送成功也可会有多种状态，<br>
		 * 例如消息写入Master成功，但是Slave不成功，这种情况消息属于成功，但是对于个别应用如果对消息可靠性要求极高，<br>
		 * 需要对这种情况做处理。另外，消息可能会存在发送失败的情况，失败重试由应用来处理。
		 */
		Message message = new Message(topic, // topic
				tag, // tag
				key, // key
				msg.getBytes());// body
		SendResult sendResult = producer.send(message);

		return sendResult;
	}

	@GetMapping("/stop/{group}")
    @ApiOperation(value = "注销生产者", notes = "注销生产者")
    @ApiOperationSupport(order = 2, author = "rxc")
	public int getMessageCount(@PathVariable("group") String group) {
		DefaultMQProducer producer = produerMap.get(group);
		produerMap.remove(group);
		if (producer != null) {
			producer.shutdown();
		}

		return 1;
	}

}
