package com.example.demo.Service.Posts;

//@Service
//public class PostCommentService {
//    private static final Logger logger = LoggerFactory.getLogger(PostCommentService.class);
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//
//    public List<NewestCommentVO> GetNewestComments() {
//        logger.info("Getting newest comments");
//        try {
//            List<Map<Short, Object>> newestCommentsList = commentRepository.findNewestComments();
//            if (!newestCommentsList.isEmpty()) {
//                logger.info("Newest comments found");
//                return TransferToNewestCommentVO(newestCommentsList);
//            } else {
//                logger.info("No newest comments found");
//            }
//        } catch (Exception e) {
//            logger.error("Failed to get newest comments", e);
//        }
//        return Collections.emptyList();
//    }
//
//    private static List<NewestCommentVO> TransferToNewestCommentVO(List<Map<Short, Object>> newestCommentsList) {
//        logger.info("Transferring to newest comment VO");
//        List<NewestCommentVO> newestCommentVOList = new ArrayList<>();
//        for (Map<Short, Object> map : newestCommentsList) {
//            try {
//                NewestCommentVO newestCommentVO = new NewestCommentVO();
//                newestCommentVO.setPostId((String) map.get("post_id"));
//                newestCommentVO.setContent((String) map.get("content"));
//                newestCommentVO.setGameName((String) map.get("game_name"));
//                Timestamp timestamp = (Timestamp) map.get("created_at");
//                newestCommentVO.setCreatedAt(timestamp.toInstant());
//                newestCommentVOList.add(newestCommentVO);
//            } catch (Exception e) {
//                logger.error("Failed to transfer to newest comment VO", e);
//            }
//        }
//        return newestCommentVOList;
//    }
//}

