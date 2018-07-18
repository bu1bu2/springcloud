package com.king.eureka;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaServerStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import com.netflix.appinfo.InstanceInfo;

@Component
public class EurekaStateChangeListener {

	@Value("${iptable.platform}")
	private String platform;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private static Logger logger = LoggerFactory.getLogger(EurekaStateChangeListener.class);
	private static final String COLON = ":";

	@EventListener//(condition = "#event.replication==false")
	public void listen(EurekaInstanceCanceledEvent eurekaInstanceCanceledEvent) {
		// 服务断线事件
		String appName = eurekaInstanceCanceledEvent.getAppName();
		String serverId = eurekaInstanceCanceledEvent.getServerId();
		Objects.requireNonNull(appName, "服务名不能为空!");

		SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
		opsForSet.remove((platform + appName).toLowerCase(), serverId);
		logger.info(">>>>>>> 失效服务:{},已被剔除!", serverId);
	}

	@EventListener//(condition = "#event.replication==false")
	public void listen(EurekaInstanceRegisteredEvent event) {
		// 服务注册
		InstanceInfo instanceInfo = event.getInstanceInfo();
		String appName = instanceInfo.getAppName();
		Objects.requireNonNull(appName, "服务名不能为空!");

		SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
		opsForSet.add((platform + appName).toLowerCase(), instanceInfo.getIPAddr() + COLON + instanceInfo.getPort());
		logger.info(">>>>>>> 服务名:{},端口号:{},已缓存至redis", appName, instanceInfo.getPort());
	}

	@EventListener//(condition = "#event.replication==false")
	public void listen(EurekaInstanceRenewedEvent event) {
		// 服务续约
		logger.info(">>>>>>>>>>>>>>>Server续约:" + event.getServerId());
	}

	@EventListener
	public void listen(EurekaRegistryAvailableEvent event) {
		// 注册中心启动
		logger.info(">>>>>>>>>>>>>>>Server注册中心:" + event);
	}

	@EventListener
	public void listen(EurekaServerStartedEvent event) {
		// Server启动
		logger.info(">>>>>>>>>>>>>>>Server启动:" + event);
	}
}