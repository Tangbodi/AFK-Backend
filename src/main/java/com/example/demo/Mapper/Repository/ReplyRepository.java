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
    @Query(value = "SELECT r.reply_id, r.comment_id, r.from_uid, ui.avatar_url as fmui_avatar_url,  fm.username as from_username, r.to_uid, tm.username as to_username, r.content, r.created_at\n" +
            "FROM afk.post_replies r\n" +
            "JOIN afk.post_comments c ON r.comment_id = c.comment_id \n" +
            "LEFT JOIN vw_userid_username_mapping fm\n" +
            "ON r.from_uid = fm.user_id\n" +
            "LEFT JOIN vw_userid_username_mapping tm\n" +
            "ON r.to_uid = tm.user_id\n" +
            "LEFT JOIN users_info ui ON fm.user_id = ui.user_id\n" +
            "WHERE c.comment_id IN  (:ids) ORDER BY r.created_at ASC", nativeQuery = true)
    List<Map<Short, Object>> findByCommentId(@Param("ids") List<String> commentIds);
}
