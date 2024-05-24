# rocketmqDemo
文档地址

	http://127.0.0.1:8083/doc.html
## rocketmqDemo

1：生产消息

    http://127.0.0.1:8083/send/mygroup/mytopic?nameServer=10.64.15.144:9876&tag=mytag&key=mykey&msg=mymsg11222

2：注销生产者

    http://127.0.0.1:8083/stop/mygroup
3：订阅消息

    http://127.0.0.1:8083/subscription/mygroup/mytopic?nameServer=10.64.15.144:9876
    
4：获取消息

    http://127.0.0.1:8083/get/mygroup/mytopic
    
5：清空消息

    http://127.0.0.1:8083/clean/mygroup/mytopic
