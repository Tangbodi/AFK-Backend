package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.PostReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReplyRepository extends JpaRepository<PostReply, String> {
    @Query(value = "SELECT \n" +
            "r.comment_id, r.content, r.reply_id, fm.username as from_username, tm.username as to_username, r.created_at\n" +
            "FROM afk.post_replies r\n" +
            "JOIN afk.post_comments c ON r.comment_id = c.comment_id\n" +
            "left join vw_userid_username_mapping fm\n" +
            "on r.from_uid = fm.user_id\n" +
            "left join vw_userid_username_mapping tm\n" +
            "on r.to_uid = tm.user_id\n" +
            "WHERE c.comment_id IN  (:ids)", nativeQuery = true)
    List<Map<Short, Object>> findByCommentId(@Param("ids") List<String> commentIds);
}
