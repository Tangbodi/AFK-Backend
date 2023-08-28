package com.example.demo.Repository;

import com.example.demo.Model.Entity.PostComment;
import com.example.demo.Model.Entity.PostReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReplyRepository extends JpaRepository<PostReply, Long> {
    @Query(value = "SELECT pr.reply_id, pr.comment_id, pr.from_uid, ui.avatar_url as fmui_avatar_url, " +
            "fm.username as from_username, pr.to_uid, tm.username as to_username, pr.content, ulr.like_status, pr.created_at\n" +
            "FROM afk.post_replies pr\n" +
            "JOIN afk.post_comments pc ON pr.comment_id = pc.comment_id \n" +
            "LEFT JOIN vw_userid_username_mapping fm\n" +
            "ON pr.from_uid = fm.user_id\n" +
            "LEFT JOIN vw_userid_username_mapping tm\n" +
            "ON pr.to_uid = tm.user_id\n" +
            "LEFT JOIN users_info ui ON fm.user_id = ui.user_id\n" +
            "LEFT JOIN users_like_replies ulr ON pr.reply_id = ulr.reply_id\n" +
            "AND ulr.user_id = :userId\n" +
            "WHERE pc.comment_id IN  (:ids) ORDER BY pr.created_at ASC", nativeQuery = true)
    List<Map<Short, Object>> findByCommentId(@Param("ids") List<Long> commentIds, @Param("userId") Long userId);

    @Query(value = "SELECT pi.post_id, pi.comment_reply FROM afk.post_replies pr JOIN afk.post_comments pc ON pr.comment_id = pc.comment_id JOIN afk.posts_info pi ON pc.post_id = pi.post_id WHERE pr.reply_id = :replyId", nativeQuery = true)
    List<Map<Short, Object>> findPostIdByReplyId(@Param("replyId") Long replyId);
}
