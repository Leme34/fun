package com.lsd.fun.common.utils.excel;

import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.common.utils.excel.writer.ShopExcelWriter;
import com.lsd.fun.modules.cms.dto.ShopExcelDTO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入Excel
 * Created by lsd
 * 2019-08-13 14:12
 */
public class ExcelReader {

    private static Logger logger = LoggerFactory.getLogger(ExcelReader.class.getName()); // 日志打印类

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";

    /**
     * 根据文件后缀名类型获取对应的工作簿对象
     *
     * @param inputStream 读取文件的输入流
     * @param fileType    文件后缀名类型（xls或xlsx）
     * @return 包含文件数据的工作簿对象
     * @throws IOException
     */
    public static Workbook getWorkbook(InputStream inputStream, String fileType) throws IOException {
        Workbook workbook = null;
        if (fileType.equalsIgnoreCase(XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileType.equalsIgnoreCase(XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }

    /**
     * 读取Excel文件内容
     *
     * @param file 上传的Excel文件
     * @return 读取结果列表，读取失败时返回null
     */
    public static List<ShopExcelDTO> readExcel(MultipartFile file, Integer isUpdate) {
        Workbook workbook = null;
        // 获取Excel后缀名
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty() || fileName.lastIndexOf(".") < 0) {
            logger.warn("解析Excel失败，因为获取到的Excel文件名非法！");
            throw new RRException("解析Excel失败，因为获取到的Excel文件名非法！");
        }

        String fileType = StringUtils.substringAfterLast(fileName, ".");

        // 获取Excel工作簿
        try {
            workbook = getWorkbook(file.getInputStream(), fileType);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RRException("解析Excel失败");
        } finally {
            IOUtils.closeQuietly(workbook);
        }
        // 读取excel中的数据
        return parseExcel(workbook, isUpdate);
    }

    /**
     * 解析Excel数据
     *
     * @param workbook Excel工作簿对象
     * @return 解析结果
     */
    private static List<ShopExcelDTO> parseExcel(Workbook workbook, Integer isUpdate) {
        List<ShopExcelDTO> resultDataList = new ArrayList<>();
        // 解析sheet
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            // 不读取空的 或 被隐藏的sheet
            if (sheet == null || sheet.getSheetName().equals(ExcelUtils.DICT_SHEET_DATA)) {
                continue;
            }
            // 获取第一行数据
            int firstRowNum = sheet.getFirstRowNum();
            Row firstRow = sheet.getRow(firstRowNum);
            if (null == firstRow) {
                logger.error("解析Excel失败，在第一行没有读取到任何数据！");
                throw new RRException("解析Excel失败，在第一行没有读取到任何数据！");
            }
            // 若列头不匹配
            final int numberOfCells = firstRow.getPhysicalNumberOfCells();
            if (isUpdate == 1 && numberOfCells != ShopExcelWriter.SHOP_UPDATE_CELL_HEADS.size()) {
                throw new RRException("请上传最新的【商铺信息表格】");
            }

            // 解析每一行的数据，构造数据对象
            int rowStart = firstRowNum + 1;
            int rowEnd = sheet.getPhysicalNumberOfRows();
            for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (null == row) {
                    continue;
                }
                ShopExcelDTO resultData = convertRowToData(row, isUpdate);
                // 必填项为空则认为是空行
                final boolean isNullRow = StringUtils.isBlank(resultData.getTitle());
                final int naturalRowNum = row.getRowNum() + 1;
                if (isNullRow) {
                    logger.warn("第 " + naturalRowNum + "行数据为空行，已忽略！");
                    continue;
                }
                // 保存Excel行号到读入的Vo对象中，使入库校验出错时能提示用户
                resultData.setRow(naturalRowNum); //转为自然行号
                resultDataList.add(resultData);
            }
        }
        return resultDataList;
    }

    /**
     * 读取内容，将单元格内容转换为字符串
     *
     * @param cell
     * @return
     */
    private static String convertCellValueToString(Cell cell) {
        if (cell == null) {
            return null;
        }
        String returnValue = null;
        switch (cell.getCellType()) {
            case NUMERIC:   //数字
                // Excel存储日期、时间均以数值类型进行存储，读取时POI先判断是是否是数值类型，再进行判断 TODO XSSF 无效
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    returnValue = cell.getDateCellValue().toString();
                    break;
                }
                Double doubleValue = cell.getNumericCellValue();
                returnValue = String.valueOf(doubleValue.longValue());  // 转为对应类型的字符串，消除自带的 ".0"
//                BigDecimal bigDecimal = BigDecimal.valueOf(doubleValue);
//                returnValue = bigDecimal.toString();
                break;
            case STRING:    //字符串
                returnValue = cell.getStringCellValue();
                break;
            case BOOLEAN:   //布尔
                Boolean booleanValue = cell.getBooleanCellValue();
                returnValue = booleanValue.toString();
                break;
            case BLANK:     // 空值
                break;
            case FORMULA:   // 公式
                returnValue = cell.getCellFormula();
                break;
            case ERROR:     // 故障
                break;
            default:
                break;
        }
        return returnValue;
    }

    /**
     * 提取每一行中需要的数据，构造成为一个结果数据对象
     * <p>
     * 当该行中有单元格的数据为空或不合法时，忽略该行的数据
     *
     * @param row 行数据
     * @return 解析后的行数据对象，行数据错误时返回null
     */
    private static ShopExcelDTO convertRowToData(Row row, Integer isUpdate) {
        ShopExcelDTO resultData = new ShopExcelDTO();
        Cell cell;
        int cellNum = 0;
        if (isUpdate == 1) {
            // ID
            cell = row.getCell(cellNum++);
            String id = convertCellValueToString(cell);
            resultData.setId(StringUtils.isBlank(id) ? null : new Integer(id));
        }
        // 标题
        cell = row.getCell(cellNum++);
        String title = convertCellValueToString(cell);
        resultData.setTitle(title);
        // 获取商铺介绍
        cell = row.getCell(cellNum++);
        String description = convertCellValueToString(cell);
        resultData.setDescription(description);
        // 获取商铺评分
        cell = row.getCell(cellNum++);
        String remarkScore = convertCellValueToString(cell);
        resultData.setRemarkScore(StringUtils.isBlank(remarkScore) ? "0" : remarkScore);
        // 获取省份/直辖市
        cell = row.getCell(cellNum++);
        String province = convertCellValueToString(cell);
        resultData.setProvince(province);
        // 获取市级单位
        cell = row.getCell(cellNum++);
        String city = convertCellValueToString(cell);
        resultData.setCity(city);
        // 获取区级单位
        cell = row.getCell(cellNum++);
        String region = convertCellValueToString(cell);
        resultData.setRegion(region);
        // 获取详细地址
        cell = row.getCell(cellNum++);
        String address = convertCellValueToString(cell);
        resultData.setAddress(address);
        // 获取商铺类别
        cell = row.getCell(cellNum++);
        String category = convertCellValueToString(cell);
        resultData.setCategory(category);
        // 获取商铺以空格分隔的标签
        cell = row.getCell(cellNum++);
        String tags = convertCellValueToString(cell);
        resultData.setTags(tags);
        // 获取商家名称
        cell = row.getCell(cellNum++);
        String seller = convertCellValueToString(cell);
        resultData.setSeller(seller);
        // 获取人均消费
        cell = row.getCell(cellNum++);
        String pricePerMan = convertCellValueToString(cell);
        if (StringUtils.isBlank(pricePerMan)){
            resultData.setPricePerMan(0);
        }else {
            resultData.setPricePerMan(new Integer(pricePerMan));
        }

        return resultData;
    }
}
