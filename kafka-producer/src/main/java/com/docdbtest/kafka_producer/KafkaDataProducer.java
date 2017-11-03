package com.docdbtest.kafka_producer;

import java.util.*;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaDataProducer {
	
	private static final String topic = "pagevisit-docdb-data";
	private static final String[] visitedSites = {"www.site1.com",
			"www.site2.com","www.site3.com","www.site4.com","www.site5.com"	};
	
	public static void main(String[] args) {
		
		long events = 10;
		if (args.length > 0)
		{
			events = Long.parseLong(args[0]);
		}
		Properties properties = new Properties();
		// replace with kafka brokers
		properties.put("metadata.broker.list", "localhost:9092");
		properties.put("serializer.class", "kafka.serializer.StringEncoder");
		properties.put("client.id","docdbdataproducer");
		properties.put("request.required.acks", "1");
		ProducerConfig producerConfig = new ProducerConfig(properties);
		kafka.javaapi.producer.Producer<String, String> producer = new kafka.javaapi.producer.Producer<String, String>(
				producerConfig);
		
		Random rnd = new Random();
		for (long nEvent = 0; nEvent < events; nEvent++) {
			long runtime = new Date().getTime();
			String ip = "192.168.2." + rnd.nextInt(255);
			String userName = "user"+rnd.nextInt(5);
			String visitedSite = visitedSites[(int) (nEvent%5)];
			// send message of type "1,time,www.site1.com,192.168.2.5,user4"
	        String msg = nEvent + "," + runtime + "," + visitedSite + "," + ip + "," + userName; 
	        KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, ip, msg);
	        System.out.println("sending msg" + msg + " ...");
	        try {
				Thread.sleep(1000);
				producer.send(data);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	        System.out.println("sent msg" + msg + "to topic" + topic);
		}
		producer.close();
	}
}