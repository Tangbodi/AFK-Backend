package com.example.demo.Mapper.Repository;

import com.example.demo.Model.Entity.CommentOnPostMention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentOnPostMentionRepository extends JpaRepository<CommentOnPostMention,Long> {
    @Modifying
    @Query(value = "UPDATE afk.comment_on_post_mentions SET mention_on = :status WHERE user_id = :userId", nativeQuery = true)
    void UpdateStatus(@Param("status") Integer status, @Param("userId") Long userId);

    @Query(value = "SELECT copm.user_id, mou.mention_on AS mention_of_username, copm.mention_on AS comment_on_post, locm.mention_on AS like_on_comment, lopm.mention_on AS like_on_post, posgm.mention_on AS post_on_saved_game, rocm.mention_on AS reply_on_comment, sopm.mention_on AS save_on_post\n" +
            "FROM afk.comment_on_post_mentions copm \n" +
            "JOIN afk.like_on_comment_mentions locm ON copm.user_id = locm.user_id\n" +
            "JOIN afk.like_on_post_mentions lopm ON copm.user_id = lopm.user_id\n" +
            "JOIN afk.post_on_saved_game_mentions posgm ON copm.user_id = posgm.user_id\n" +
            "JOIN afk.reply_on_comment_mentions rocm ON copm.user_id = rocm.user_id\n" +
            "JOIN afk.save_on_post_mentions sopm ON copm.user_id = sopm.user_id\n" +
            "JOIN afk.mention_of_username mou ON sopm.user_id = mou.user_id\n" +
            "WHERE copm.user_id = :userId", nativeQuery = true)
    List<Map<Short, Object>> findSettingByUserId(@Param("userId")Long userId);
}
