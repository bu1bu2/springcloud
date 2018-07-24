package com.apec.dubbo.restful.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.apec.dubbo.restful.dto.DubboDTO;

@RestController
public class DemoConsumerController {
	
	private static Logger logger = LoggerFactory.getLogger(DemoConsumerController.class);

	@RequestMapping(value="/call",method=RequestMethod.POST)
	public Object get(@RequestBody DubboDTO dto) {

		// 创建服务实例
		ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
		reference.setGeneric(true);
		reference.setInterface(dto.getInterfaceName());
		reference.setVersion(dto.getVersion());
		
		// 获取缓存中的实例
		ReferenceConfigCache cache = ReferenceConfigCache.getCache(); 
		GenericService genericService = cache.get(reference);
		
		// 调用实例
		Object result = genericService.$invoke(dto.getMethod(), dto.getParameterTypes(),dto.getArgs());
		logger.info(">>>>>调用dubbo服务接口,入参:{},出参:{}",dto.toString(),result);
		return result;
	}
}
