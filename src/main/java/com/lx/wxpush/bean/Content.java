package com.lx.wxpush.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 常保安
 * @date 2022/9/3 20:05
 * @description
 */
@Data
@TableName(value = "content")
public class Content {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "content")
    private String content;

    @TableField(value = "had_sent")
    private Integer hadSent;
}
