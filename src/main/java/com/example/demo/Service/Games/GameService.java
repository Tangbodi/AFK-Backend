package com.example.demo.Service.Games;

//@Service
//public class GameService {
//    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
//    @Autowired
//    private GameRepository gameRepository;
//    public GameVO GetGameById(Short gameId) {
//        logger.info("Getting game by id: gameId = {}", gameId);
//        try {
//            Game game = gameRepository.findById(gameId).orElse(null);
//            if(game == null) {
//                logger.info("Game not found with id: {}", gameId);
//                return null;
//            }else{
//                GameVO gameVO = new GameVO();
//                gameVO.setGameId(game.getId());
//                return gameVO;
//            }
//
//        } catch (Exception e) {
//            logger.error("Failed to get game by id: {}", e.getMessage(), e);
//            return null;
//        }
//    }
//
//}
