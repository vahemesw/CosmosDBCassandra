package com.cosmosdb.kafka_producer;

import java.util.*;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class kafkaProducerAllDataTypes {
	
	private static final String topic = "all-data-types";
	private static final String[] brandNames = {"ABC",
			"XYZ","MNO","RST"};
	private static final String[] itemNames = {"toys",
			"chairs","cots","cycles"};
	
	public static void main(String[] args) {
		
		int events = 10;
		if (args.length > 0)
		{
			events = Integer.parseInt(args[0]);
		}
		Properties properties = new Properties();
		// replace with kafka brokers of the kafka cluster
		properties.put("metadata.broker.list", "localhost:9092");
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("client.id","docdbdataproducer");
		properties.put("request.required.acks", "1");
		ProducerConfig producerConfig = new ProducerConfig(properties);
		kafka.javaapi.producer.Producer<String, String> producer = new kafka.javaapi.producer.Producer<String, String>(
				producerConfig);
		
		Random rnd = new Random();
		for (int nEvent = 0; nEvent < events; nEvent++) {
			String item_brand_name = brandNames[rnd.nextInt(brandNames.length)];
			String item_name = itemNames[(nEvent%itemNames.length)];
			float item_price = 20*rnd.nextFloat();
			double available_units = 500*rnd.nextDouble();
			boolean expired = (nEvent%2 ==0) ? true : false;
			int rack_number = rnd.nextInt(10);

			// send message eg., 'RST,toys,13.215782,226.27475541089382,true,1'
	        String msg = item_brand_name + "," + item_name + "," + item_price + "," + available_units + "," + expired + "," + rack_number ; 
	        KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, item_brand_name, msg);
	        System.out.println("sending msg" + msg + " ...");
	        try {
				Thread.sleep(1000);
				producer.send(data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	        System.out.println("sent msg" + msg + "to topic " + topic);
	    }
		producer.close();
	}
}