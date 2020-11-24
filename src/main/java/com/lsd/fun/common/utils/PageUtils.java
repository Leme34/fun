package com.lsd.fun.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 *
 * @author lsd
 */
public class PageUtils implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 总记录数
     */
    private int totalCount;
    /**
     * 每页记录数
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 当前页数
     */
    private int currPage;
    /**
     * 列表数据
     */
    private List<?> list;

    /**
     * list 构造方法
     *
     * @param list           列表数据
     * @param currPage       当前页数
     * @param pageSize       每页记录数
     * @param needPagination 是否需要使用 java subList 对 list 逻辑分页
     */
    public PageUtils(
            List<?> list, int currPage, int pageSize, int totalCount, boolean needPagination) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        int m = totalCount % pageSize;
        if (m > 0) {
            totalPage = totalCount / pageSize + 1;
        } else {
            totalPage = totalCount / pageSize;
        }
        // 需要手动逻辑分页
        if (needPagination) {
            if (m == 0) { // 分页刚好整除
                int toIndex =
                        Math.min(
                                pageSize * (currPage),
                                totalCount); // 防止 pageSize * (currPage) > totalCount
                this.list = list.subList((currPage - 1) * pageSize, toIndex);
            } else { // 分页有余数
                if (currPage == totalPage) { // 最后一页
                    this.list = list.subList((currPage - 1) * pageSize, totalCount);
                } else {
                    int toIndex =
                            Math.min(
                                    pageSize * (currPage),
                                    totalCount); // 防止 pageSize * (currPage) > totalCount
                    this.list = list.subList((currPage - 1) * pageSize, toIndex);
                }
            }
        }
    }

    /**
     * java subList 手动分页
     *
     * @param list     列表数据
     * @param currPage 当前页数
     * @param pageSize 每页记录数
     */
    public PageUtils(List<?> list, int currPage, int pageSize) {
        this.totalCount = list.size();
        this.pageSize = pageSize;
        this.currPage = currPage;

        int m = totalCount % pageSize;
        if (m > 0) {
            totalPage = totalCount / pageSize + 1;
        } else {
            totalPage = totalCount / pageSize;
        }
        if (m == 0) { // 分页刚好整除
            int toIndex =
                    Math.min(
                            pageSize * (currPage),
                            totalCount); // 防止 pageSize * (currPage) > totalCount
            this.list = list.subList((currPage - 1) * pageSize, toIndex);
        } else { // 分页有余数
            if (currPage == totalPage) { // 最后一页
                this.list = list.subList((currPage - 1) * pageSize, totalCount);
            } else {
                int toIndex =
                        Math.min(
                                pageSize * (currPage),
                                totalCount); // 防止 pageSize * (currPage) > totalCount
                this.list = list.subList((currPage - 1) * pageSize, toIndex);
            }
        }
    }

    /**
     * mybatis-plus 物理分页
     */
    public PageUtils(IPage<?> page) {
        this.list = page.getRecords();
        this.totalCount = (int) page.getTotal();
        this.pageSize = (int) page.getSize();
        this.currPage = (int) page.getCurrent();
        this.totalPage = (int) page.getPages();
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public static void main(String[] args) {
        List list = new ArrayList<>();
        for (int i = 1; i <= 52; i++) {
            list.add(i);
        }
        PageUtils pageUtils = new PageUtils(list, 6, 10);
        System.out.println(
                "list=" + pageUtils.getList() + "，getTotalPage=" + pageUtils.getTotalPage());
    }
}
