package com.lsd.fun.common.utils.excel.writer;

import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.excel.ExcelUtils;
import com.lsd.fun.modules.cms.entity.CategoryEntity;
import com.lsd.fun.modules.cms.entity.SellerEntity;
import com.lsd.fun.modules.cms.service.CategoryService;
import com.lsd.fun.modules.cms.service.SellerService;
import com.lsd.fun.modules.cms.vo.ShopVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出Excel
 * Created by lsd
 * 2019-08-13 14:20
 */
@Slf4j
@Component("shopExcelWriter")
public class ShopExcelWriter implements ExcelWriter {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SellerService sellerService;


    public static List<String> SHOP_CELL_HEADS; //固定资产设备Excel的列头
    public static List<String> SHOP_UPDATE_CELL_HEADS; //批量更新资产设备Excel的列头

    static {
        // 类装载时就载入指定好的列头信息，如有需要，可以考虑做成动态生成的列头
        SHOP_CELL_HEADS = new ArrayList<>();
        SHOP_CELL_HEADS.add("标题");
        SHOP_CELL_HEADS.add("商铺介绍");
        SHOP_CELL_HEADS.add("商铺评分");
        SHOP_CELL_HEADS.add("省份/直辖市");
        SHOP_CELL_HEADS.add("市级单位");
        SHOP_CELL_HEADS.add("区级单位");
        SHOP_CELL_HEADS.add("详细地址");
        SHOP_CELL_HEADS.add("商铺类别名称");
        SHOP_CELL_HEADS.add("商铺以空格分隔的标签");
        SHOP_CELL_HEADS.add("商家名称");
        SHOP_CELL_HEADS.add("人均消费");

        SHOP_UPDATE_CELL_HEADS = new ArrayList<>(SHOP_CELL_HEADS);
        SHOP_UPDATE_CELL_HEADS.add(0, "商铺ID");

    }

    /**
     * 生成Excel并写入数据信息
     *
     * @param msg 信息载体
     * @return 写入数据后的工作簿对象
     */
    public Workbook exportExcel(Object msg, List dataList) {
        final boolean isEmptyExcel = CollectionUtils.isEmpty(dataList);
        // 创建 地区-部门级联下拉列表（写死第5,6,7列） 且 xlsx类型 的Excel
        Workbook wb = ExcelUtils.getMultiPropDataWorkbook(isEmptyExcel);

        // 查出商铺类别、商家名称列表数据
        List<String> categories = categoryService.list().stream().map(CategoryEntity::getName).collect(Collectors.toList());
        List<String> sellers = sellerService.lambdaQuery().eq(SellerEntity::getDisabledFlag, 0).list().stream().map(SellerEntity::getName).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(categories)) {
            throw new RRException("请先添加 【商铺类别】 等系统基础数据");
        }
        if (CollectionUtils.isEmpty(sellers)) {
            throw new RRException("请先添加 【商家】 等系统基础数据");
        }

        // 生成Sheet表，写入第一行的列头
        List<String> cellHeads = isEmptyExcel ? SHOP_CELL_HEADS : SHOP_UPDATE_CELL_HEADS;
        Sheet mainSheet = buildSheetHeader(wb.getSheet(ExcelUtils.DICT_SHEET_MAIN), cellHeads);

        // 设置数据校验
        ExcelUtils.setDecimalConstraint(mainSheet, 1, 5000, isEmptyExcel ? 3 : 4, isEmptyExcel ? 3 : 4);
        ExcelUtils.setValidationListData(mainSheet, 1, 5000, isEmptyExcel ? 8 : 9, isEmptyExcel ? 8 : 9, categories.toArray(new String[0]));
        ExcelUtils.setFormulaListValidation(wb,sellers,"sellers",17,1, 5000, isEmptyExcel ? 10 : 11, isEmptyExcel ? 10 : 11);
        ExcelUtils.setIntegerConstraint(mainSheet, 1, 5000, isEmptyExcel ? 11 : 12, isEmptyExcel ? 11 : 12);

        // 设置dataSheet为隐藏
        wb.setSheetHidden(wb.getSheetIndex(ExcelUtils.DICT_SHEET_DATA), true);
        // 往数据页写入数据
        writeData(mainSheet, dataList);
        return wb;
    }


    /**
     * 往数据页写入数据
     *
     * @param dataList 源数据
     * @return 写入数据后的工作簿对象
     */
    private void writeData(Sheet sheet, List<ShopVO> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        int rowNum = 1;
        for (ShopVO vo : dataList) {
            //写入一行数据
            convertDataToRow(vo, sheet.createRow(rowNum++));
        }
    }

    /**
     * 把一个对象写入为数据行
     *
     * @param shop
     * @param row
     */
    private void convertDataToRow(ShopVO shop, Row row) {
        int cellNum = 0;
        Cell cell;
        // 1 商铺ID
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getId() ? "" : shop.getId().toString());
        // 2 标题
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getTitle() ? "" : shop.getTitle());
        // 3 商铺介绍
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getDescription() ? "" : shop.getDescription());
        // 4 商铺评分
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getRemarkScore() ? "" : shop.getRemarkScore().toString());
        // 5 省份/直辖市
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getProvince() ? "" : shop.getProvince());
        // 6 市级单位
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getCity() ? "" : shop.getCity());
        // 7 区级单位
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getRegion() ? "" : shop.getRegion());
        // 8 详细地址
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getAddress() ? "" : shop.getAddress());
        // 9 商铺类别
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getCategory() ? "" : shop.getCategory());
        // 10 商铺以空格分隔的标签
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getTags() ? "" : shop.getTags());
        // 11 商家名称
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getSeller() ? "" : shop.getSeller());
        // 12 人均消费
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == shop.getPricePerMan() ? "" : shop.getPricePerMan().toString());
    }

}
