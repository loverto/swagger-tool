package com.pwhxbdk.utils;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class CommentUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(CommentUtilsTest.class);

    @Test
    public void getDataType() {
    }

    @Test
    public void getCommentDesc() {
        String commentDesc = CommentUtils.getCommentDesc("/**\n" +
                " * @describe pwhxbdk\n" +
                " * @author pwhxbdk\n" +
                " * @date 2020/4/6\n" +
                " */");
        logger.info(commentDesc);
        int c = StringUtils.ordinalIndexOf("*@desc fdfdfdfdf", "c", 1);
        logger.info("",c);
    }

    @Test
    public void getCommentDescForMethod() {
        String commentDesc = CommentUtils.getCommentDesc("    /**\n" +
                "     * @Description: 取回结账申请单\n" +
                "     * @Param: [paramsMap]\n" +
                "     * @return: com.xfs.popay.common.utils.Result\n" +
                "     * @Author: CuiWei\n" +
                "     * @Date: 2019-5-20 14:40\n" +
                "     */");
        logger.info(commentDesc);
        Assert.assertEquals("取回结账申请单",commentDesc);
        int c = StringUtils.ordinalIndexOf("*@desc fdfdfdfdf", "c", 1);
        logger.info("",c);
    }
    @Test
    public void getCommentDescForClass() {
        String commentDesc = CommentUtils.getCommentDesc("/**\n" +
                " * @program: popay2.0\n" +
                " *\n" +
                " * @description: 结账申请单\n" +
                " *\n" +
                " * @author: CuiWei\n" +
                " *\n" +
                " * @create: 2019-04-18 13:36\n" +
                " **/");
        logger.info(commentDesc);
        Assert.assertEquals("结账申请单",commentDesc);
        int c = StringUtils.ordinalIndexOf("*@desc fdfdfdfdf", "c", 1);
        logger.info("",c);
    }

    @Test
    public void getCommentDescForChinese() {
        String commentDesc = CommentUtils.getCommentDesc(" /**\n" +
                "     * 方法实现说明：供应商审批流回写状态\n" +
                "     * @author      tangy\n" +
                "     * @methodName  supplierWriteBack\n" +
                "     * @param writeBack\n" +
                "     * @return      com.xfs.popay.common.utils.Result\n" +
                "     * @exception\n" +
                "     * @date        2020-4-22 13:25\n" +
                "     */");
        logger.info(commentDesc);
        Assert.assertEquals("供应商审批流回写状态",commentDesc);
        int c = StringUtils.ordinalIndexOf("*@desc fdfdfdfdf", "c", 1);
        logger.info("",c);
    }
}