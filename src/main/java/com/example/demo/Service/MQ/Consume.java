package com.example.demo.Service.MQ;


//@Component
//public class Consume {
//    @Autowired
//    private LikeSaveStrategy likeSaveStrategy;
//
//    private final Logger logger = LoggerFactory.getLogger(Consume.class);
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//
//    @KafkaListener(topics = {"like-save-redis"}, groupId = "afk-group")
//    public void consumeMessage(ConsumerRecord<String, String> userLikeSaveDTORecord) {
//        try {
//            UserLikeSaveDTO userLikeSaveDTO = objectMapper.readValue(userLikeSaveDTORecord.value(), UserLikeSaveDTO.class);
//            logger.info("消费者消费topic:{} partition:{}的消息 -> {}", userLikeSaveDTORecord.topic(), userLikeSaveDTORecord.partition(), userLikeSaveDTO.toString());
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
//}
