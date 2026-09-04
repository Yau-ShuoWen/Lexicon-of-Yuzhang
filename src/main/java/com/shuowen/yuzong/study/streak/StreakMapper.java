package com.shuowen.yuzong.study.streak;

import com.shuowen.yuzong.study.streak.data.StreakRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StreakMapper
{
    List<StreakRecordEntity> findByUserAndRange(@Param("userId") Integer userId,
                                                @Param("from") LocalDate from,
                                                @Param("to") LocalDate to);

    List<StreakRecordEntity> findAllByUser(@Param("userId") Integer userId);

    StreakRecordEntity findByUserAndDate(@Param("userId") Integer userId,
                                         @Param("studyDate") LocalDate studyDate);

    void insert(StreakRecordEntity record);

    void updateStatus(@Param("userId") Integer userId,
                      @Param("studyDate") LocalDate studyDate,
                      @Param("status") String status);

    Integer findProtectionBalance(@Param("userId") Integer userId);

    void insertProtectionBalance(@Param("userId") Integer userId,
                                 @Param("balance") Integer balance);

    int consumeProtection(@Param("userId") Integer userId);

    void deleteByUser(@Param("userId") Integer userId);

    void resetProtectionBalance(@Param("userId") Integer userId);

    int addProtection(@Param("userId") Integer userId,
                      @Param("amount") Integer amount);
}
