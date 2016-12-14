package com.xuxl.tcctransaction.capital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xuxl.tcctransaction.capital.api.CapitalTradeOrderService;
import com.xuxl.tcctransaction.capital.api.dto.CapitalTradeOrderDto;

@RestController
public class CapitalTradeOrderController {
	
	@Autowired
	private CapitalTradeOrderService service;
	
	@RequestMapping("/record")
	public String record(@RequestBody CapitalTradeOrderDto tradeOrderDto) {
		return service.record(null, tradeOrderDto);
	}

}
