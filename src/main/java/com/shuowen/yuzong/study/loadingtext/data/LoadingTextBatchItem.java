package com.shuowen.yuzong.study.loadingtext.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoadingTextBatchItem extends LoadingTextUpdate
{
    private boolean deleted;
}
