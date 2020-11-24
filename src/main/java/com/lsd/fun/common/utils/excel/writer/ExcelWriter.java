package com.lsd.fun.common.utils.excel.writer;

import org.apache.poi.ss.usermodel.*;

import java.util.List;

/**
 * Created by lsd
 * 2019-10-12 10:08
 */
public interface ExcelWriter {

    /**
     * 生成Excel并写入数据信息
     *
     * @param msg 传递的信息
     * @param dataList 被写入的数据
     * @return 写入数据后的工作簿对象
     */
    Workbook exportExcel(Object msg, List dataList);

    /**
     * 生成sheet表，并写入第一行数据（列头）
     *
     * @param sheet sheet对象
     * @return 已经写入列头的Sheet
     */
    default Sheet buildSheetHeader(Sheet sheet, List<String> cellHeads) {
        // 设置列头宽度
        for (int i = 0; i < cellHeads.size(); i++) {
            sheet.setColumnWidth(i, 4000);
        }
        // 设置默认行高
        sheet.setDefaultRowHeight((short) 400);
        // 构建头单元格样式
        CellStyle cellStyle = buildHeadCellStyle(sheet.getWorkbook());
        // 写入第一行各列的数据
        Row head = sheet.createRow(0);
        for (int i = 0; i < cellHeads.size(); i++) {
            Cell cell = head.createCell(i);
            cell.setCellValue(cellHeads.get(i));
            cell.setCellStyle(cellStyle);
        }
        return sheet;
    }

    /**
     * 设置第一行列头的样式
     *
     * @param workbook 工作簿对象
     * @return 单元格样式对象
     */
    default CellStyle buildHeadCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //对齐方式设置
        style.setAlignment(HorizontalAlignment.CENTER);
        //边框颜色和宽度设置
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 下边框
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 左边框
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 右边框
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex()); // 上边框
        //设置背景颜色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //粗体字设置
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

}
