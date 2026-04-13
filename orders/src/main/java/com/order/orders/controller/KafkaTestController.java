//package com.order.orders.controller;
//
//import com.order.orders.dto.OrderDto;
//import com.order.orders.entity.Order;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/test")
//public class KafkaTestController {
//
//    private final KafkaTemplate<String, OrderDto> kafkaTemplate;
//
//    public KafkaTestController(KafkaTemplate<String, OrderDto> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @PostMapping
//    public String sendMessage(@RequestBody OrderDto orderDto) {
////        kafkaTemplate.send("orders-topic", orderDto);
////        return "Message seng";
//        System.out.println("🔥 HIT CONTROLLER");
//
//        kafkaTemplate.send("orders-topic", orderDto)
//                .whenComplete((result, ex) -> {
//                    if (ex == null) {
//                        System.out.println("✅ Sent to Kafka");
//                        System.out.println(result.getRecordMetadata());
//                    } else {
//                        System.out.println("❌ FAILED TO SEND");
//                        ex.printStackTrace();
//                    }
//                });
//
//        return "Message sent";
//    }
//
//}
