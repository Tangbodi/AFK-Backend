package com.example.demo.Controller;

//@RestController
//public class TestController {
//    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
//    @Autowired
//    private RedisLikeSaveService redisLikeSaveService;
//    @Autowired
//    private UserLikeSaveService userLikeSaveService;
//    @Autowired
//    private KafkaTemplate<Object, Object> template;
//
//    @PostMapping("/getData")
//    public ResponseEntity getData() {
//        ApiResponse apiResponse;
//        Map<String,Map<String,String>> res = new HashMap();
//        Set<String> postIds =  redisLikeSaveService.GetAllSetMembers("POST_LIKE");
//        List<UsersFavoritePostId> postUserIdMap = new ArrayList<>();
//        for(String postId: postIds){
//            //userId,date
//            Map<String, String> fieldValue = redisLikeSaveService.GetHashValue("POST_LIKE:::"+postId);
//            fieldValue.entrySet().stream().forEach(entry -> {
//                UsersFavoritePostId usersFavoritePostId = new UsersFavoritePostId();
//                usersFavoritePostId.setPostId(Long.valueOf(postId));
//                usersFavoritePostId.setUserId(Long.valueOf(entry.getKey()));
//                postUserIdMap.add(usersFavoritePostId);
//            });
//        }
//        for(UsersFavoritePostId usersFavoritePostId: postUserIdMap){
//            userFavoritePostService.SetUserLikePost(usersFavoritePostId);
//        }
//        apiResponse = ApiResponse.success(postUserIdMap);
//        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
//    }
//
//}
