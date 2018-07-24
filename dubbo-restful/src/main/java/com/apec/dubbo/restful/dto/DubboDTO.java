package com.apec.dubbo.restful.dto;

import lombok.Data;
import lombok.ToString;

/**   
 * @author: goofly
 * @date:   2018年7月24日 上午11:17:41   
 *      
 */
@Data
@ToString
public class DubboDTO {
	
	private String interfaceName;
	
	private String version;
	
	private String method;
	
	private String[] parameterTypes;
	
	private Object[] args;

}
