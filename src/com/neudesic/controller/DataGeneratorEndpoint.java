package com.neudesic.controller;

import com.neudesic.model.EventCheckoutData;
import com.neudesic.model.EventData;
import com.neudesic.service.EventCheckoutDataGeneratorService;
import com.neudesic.service.EventDataGeneratorService;

public class DataGeneratorEndpoint {
	private EventDataGeneratorService eventService;
	private EventCheckoutDataGeneratorService eventCheckoutservice;
	
	public DataGeneratorEndpoint(){
		eventService = new EventDataGeneratorService();
		eventCheckoutservice = new EventCheckoutDataGeneratorService();
	}
	public void generateEventData() {		
		eventService.writeToCSV(eventService.generateEventData());
	}

	public void generateEventCheckoutData() {
		//eventCheckoutservice
	}
	
	public static void main(String[] args) {
		DataGeneratorEndpoint endpoint = new DataGeneratorEndpoint();
		endpoint.generateEventData();
	}
}
