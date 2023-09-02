package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.UsersLikeReply;
import com.example.demo.Model.Entity.UsersLikeReplyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserLikeReplyRepository extends JpaRepository<UsersLikeReply, UsersLikeReplyId> {

    @Query(value = "SELECT * FROM afk.users_like_replies WHERE like_status = 1", nativeQuery = true)
    List<UsersLikeReply> findAllByLikeStatus();
    @Query(value = "SELECT COUNT(*) AS total_like\n" +
            "FROM afk.users_like_replies\n" +
            "WHERE like_status = 1\n" +
            "AND reply_id = :replyId", nativeQuery = true)
    Map<String,Object> findReplyTotalLikeByLikeStatus(Long replyId);
}
