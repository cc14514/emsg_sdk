package com.emsg.test.blacklist;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.emsg.client.service.BlackList;

/**
 * 使用随机生成账号进行测试（账号A不可能在账号B的黑名单）
 * 1、A不在B的黑名单
 * 2、将A加入到B的黑名单
 * 3、A出现在B的黑名单列表中
 * 4、将A从B的黑名单中删除
 * 5、A不在B的黑名单中
 * 
 * @author tiger
 *
 */
public class BlackFunctionTester {

	private BlackList bl = new BlackList();
	private String userA; 
	private String userB; 
	private String pwd = "123123";
	
    @Before
    public void setUp() throws Exception {
    	// 产生随机账号
    	userA = UUID.randomUUID() + "@test.com";
    	userB = UUID.randomUUID() + "@test.com";
    }

    @After
    public void clearup() throws Exception {
    }
    
    @Test
    public void testBlackList() throws Exception {
    	// 取B的黑名单
    	List<Map<String, String>> list = bl.fetch(userB, "123123");
    	// A不在B的黑名单
    	Assert.assertFalse(conains(list, userA));

    	// 将A加入到B的黑名单
    	bl.append(userA, userB, pwd);
    	
    	Thread.sleep(1000);
    	// 取B的黑名单
    	list = bl.fetch(userB, pwd);
    	// A出现在B的黑名单列表中
    	Assert.assertTrue(conains(list, userA));
    	
    	Thread.sleep(1000);
    	// 将A从B的黑名单中删除
    	bl.remove(userA, userB, pwd);
    	
    	Thread.sleep(1000);
    	// 取B的黑名单
    	list = bl.fetch(userB, pwd);
    	// A不在B的黑名单
    	Assert.assertFalse(conains(list, userA));
    }
    
    
    /**
     * 判断列表中是否包含jid
     * 
     * @param list
     * @param jid
     * @return
     */
    private boolean conains(List<Map<String, String>> list, String jid) {
    	if (list == null)
    		return false;
    	
    	for (Map<String, String> map: list) {
    		if (jid.equals(map.get("jid"))) {
    			return true;
    		}
    	}
    	return false;
    }
    
}