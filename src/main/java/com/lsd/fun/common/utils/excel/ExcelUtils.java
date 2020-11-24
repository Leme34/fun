package com.lsd.fun.common.utils.excel;

import cn.hutool.http.HttpStatus;
import com.lsd.fun.common.exception.RRException;
import com.lsd.fun.modules.cms.entity.AreaEntity;
import com.lsd.fun.modules.cms.service.AreaService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel工具类（只支持XSSF、HSSFSheet），数据有效性校验，输入格式限制，单元格提示 ...
 * <p>
 * Created by lsd
 * 2019-08-12 17:12
 */
@Component
public class ExcelUtils {

    /**
     * 设置单元格上的提示，行号列号从 1 开始计数
     * 注意：此功能是通过有效性公式实现的，因此不能与其他公式有效性共存（例如：日期校验等）
     *
     * @param sheet         要设置的sheet.
     * @param promptTitle   标题
     * @param promptContent 内容
     * @param firstRow      开始行
     * @param lastRow       结束行
     * @param firstCol      开始列
     * @param lastCol       结束列
     */
    public static void setPrompt(Sheet sheet, String promptTitle, String promptContent,
                                 int firstRow, int lastRow, int firstCol, int lastCol) {
        firstRow -= 1;
        lastRow -= 1;
        firstCol -= 1;
        lastCol -= 1;
        checkArguments(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = null;
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        if (sheet instanceof XSSFSheet || sheet instanceof SXSSFSheet) {
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            DataValidationConstraint constraint = dvHelper.createCustomConstraint("BB1");
            validation = dvHelper.createValidation(constraint, addressList);
        } else if (sheet instanceof HSSFSheet) {
            DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("BB1");
            validation = new HSSFDataValidation(addressList, constraint);
        }
        if (validation != null) {
            validation.setShowPromptBox(true);
            validation.setSuppressDropDownArrow(false);  //不显示下拉列表的箭头
            validation.createPromptBox(promptTitle, promptContent);
            sheet.addValidationData(validation);
        }
    }

    /**
     * 添加数据有效性检查（下拉框选择），行号列号从 1 开始计数
     * ps：此方法只适用于下拉列表数据总字符数 < 255的情况！！！否则下拉列表会显示不出来，请使用 this.setFormulaListValidation()
     *
     * @param sheet              要添加此检查的Sheet
     * @param firstRow           开始行
     * @param lastRow            结束行
     * @param firstCol           开始列
     * @param lastCol            结束列
     * @param explicitListValues 有效性检查的下拉列表
     * @throws IllegalArgumentException 如果传入的行或者列小于0(< 0)或者结束行/列比开始行/列小
     */
    public static void setValidationListData(Sheet sheet, int firstRow, int lastRow,
                                             int firstCol, int lastCol, String[] explicitListValues) throws IllegalArgumentException {
        firstRow -= 1;
        lastRow -= 1;
        firstCol -= 1;
        lastCol -= 1;
        checkArguments(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = null;
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        if (sheet instanceof XSSFSheet || sheet instanceof SXSSFSheet) {
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            validation = dvHelper.createValidation(dvHelper.createExplicitListConstraint(explicitListValues),
                    addressList);
        } else if (sheet instanceof HSSFSheet) {
            DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(explicitListValues);
            validation = new HSSFDataValidation(addressList, dvConstraint);
        }
        if (validation != null) {
            validation.createErrorBox("错误提示", "请填写列表中的值，或下载最新模版重试");
            validation.setShowErrorBox(true);
            validation.setShowPromptBox(false);
            validation.setEmptyCellAllowed(false); //必填
            sheet.addValidationData(validation);
        }
    }


    /**
     * 添加数据有效性检查（只允许输入 >= 0 的整数），行号列号从 1 开始计数
     *
     * @param sheet    要添加此检查的Sheet
     * @param firstRow 开始行
     * @param lastRow  结束行
     * @param firstCol 开始列
     * @param lastCol  结束列
     * @throws IllegalArgumentException 如果传入的行或者列小于0(< 0)或者结束行/列比开始行/列小
     */
    public static void setIntegerConstraint(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        firstRow -= 1;
        lastRow -= 1;
        firstCol -= 1;
        lastCol -= 1;
        checkArguments(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = null;
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        if (sheet instanceof XSSFSheet || sheet instanceof SXSSFSheet) {
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            DataValidationConstraint constraintNum = new XSSFDataValidationConstraint(
                    DataValidationConstraint.ValidationType.INTEGER,
                    DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, "0");
            validation = dvHelper.createValidation(constraintNum, addressList);
        } else if (sheet instanceof HSSFSheet) {
            DVConstraint dvConstraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.INTEGER,
                    DVConstraint.OperatorType.GREATER_OR_EQUAL, "0", null);
            validation = new HSSFDataValidation(addressList, dvConstraint);
        }
        if (validation != null) {
            validation.createErrorBox("输入值类型出错", "数值型,请输入大于或等于0的整数值");
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
        }
    }

    /**
     * 添加数据有效性检查（只允许输入 >= 0 的整数 或 小数）
     *
     * @param sheet    要添加此检查的Sheet
     * @param firstRow 开始行
     * @param lastRow  结束行
     * @param firstCol 开始列
     * @param lastCol  结束列
     * @throws IllegalArgumentException 如果传入的行或者列小于0(< 0)或者结束行/列比开始行/列小
     */
    public static void setDecimalConstraint(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        firstRow -= 1;
        lastRow -= 1;
        firstCol -= 1;
        lastCol -= 1;
        checkArguments(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = null;
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        if (sheet instanceof XSSFSheet || sheet instanceof SXSSFSheet) {
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            DataValidationConstraint constraintNum = new XSSFDataValidationConstraint(
                    DataValidationConstraint.ValidationType.DECIMAL,
                    DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, "0");
            validation = dvHelper.createValidation(constraintNum, addressList);
        } else if (sheet instanceof HSSFSheet) {
            DVConstraint dvConstraint = DVConstraint.createNumericConstraint(DVConstraint.ValidationType.DECIMAL,
                    DVConstraint.OperatorType.GREATER_OR_EQUAL, "0", null);
            validation = new HSSFDataValidation(addressList, dvConstraint);
        }
        if (validation != null) {
            validation.createErrorBox("输入值类型出错", "数值型,请输入大于或等于0的整数值");
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
        }
    }

    /**
     * 添加数据有效性检查（日期格式限制）
     *
     * @param sheet    要添加此检查的Sheet
     * @param firstRow 开始行
     * @param lastRow  结束行
     * @param firstCol 开始列
     * @param lastCol  结束列
     * @throws IllegalArgumentException 如果传入的行或者列小于0(< 0)或者结束行/列比开始行/列小
     */
    public static void setDateConstraint(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        firstRow -= 1;
        lastRow -= 1;
        firstCol -= 1;
        lastCol -= 1;
        checkArguments(firstRow, lastRow, firstCol, lastCol);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = null;
        if (sheet instanceof XSSFSheet || sheet instanceof SXSSFSheet) {
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            DataValidationConstraint dateConstraint = dvHelper.createDateConstraint(DVConstraint.OperatorType.BETWEEN, "date(1900,1,1)",
                    "date(5000,1,1)", "yyyy/MM/dd");
            validation = dvHelper.createValidation(dateConstraint, addressList);
        } else if (sheet instanceof HSSFSheet) {
            DVConstraint dvConstraint = DVConstraint.createDateConstraint(DVConstraint.OperatorType.BETWEEN, "1900/01/01",
                    "5000/01/01", "yyyy/MM/dd");
            validation = new HSSFDataValidation(addressList, dvConstraint);
        }
        if (validation != null) {
            validation.createPromptBox("注意", "请务必按照'年/月/日'的格式输入！");
            validation.setShowPromptBox(true);
            validation.setSuppressDropDownArrow(false);
            validation.createErrorBox("错误提示", "你输入的日期格式不符合'年/月/日'格式规范，请重新输入！");
            validation.setShowErrorBox(true);
            validation.setEmptyCellAllowed(false);  //是否允许空
            sheet.addValidationData(validation);
        }
    }


    /**
     * 设置隐藏数据页中的下拉列表公式校验
     * 隐藏数据页第naturalDataRowIndex行存放设备类别，并由nameCode管理，在mainSheet中为指定范围的列使用公式引用nameCode管理的下拉列表
     *
     * @param data                下拉列表的数据
     * @param nameCode            为此下拉列表创建的名称管理器的名称
     * @param naturalDataRowIndex 隐藏数据页中此下拉列表数据所在的行数
     * @param firstRow            开始行
     * @param lastRow             结束行
     * @param firstCol            开始列
     * @param lastCol             结束列
     * @throws IllegalArgumentException 如果传入的行或者列小于0(< 0)或者结束行/列比开始行/列小
     */
    public static void setFormulaListValidation(Workbook wb, List<String> data, String nameCode, int naturalDataRowIndex, int firstRow, int lastRow, int firstCol, int lastCol) {
        firstRow -= 1;
        lastRow -= 1;
        firstCol -= 1;
        lastCol -= 1;
        checkArguments(firstRow, lastRow, firstCol, lastCol);
        Sheet mainSheet = wb.getSheet(ExcelUtils.DICT_SHEET_MAIN);
        Sheet hiddenSheet = wb.getSheet(ExcelUtils.DICT_SHEET_DATA);
        // 索引到主sheet的第colName列
        createRowData(hiddenSheet.createRow(naturalDataRowIndex - 1), data);
        createExcelName(wb, nameCode, naturalDataRowIndex, data.size(), false);

        // 主sheet加公式引用下拉列表的校验
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = null;
        if (mainSheet instanceof XSSFSheet || mainSheet instanceof SXSSFSheet) {
            DataValidationHelper dvHelper = mainSheet.getDataValidationHelper();
            DataValidationConstraint dateConstraint = dvHelper.createFormulaListConstraint(nameCode);
            validation = dvHelper.createValidation(dateConstraint, addressList);
        } else if (mainSheet instanceof HSSFSheet) {
            DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(nameCode);
            validation = new HSSFDataValidation(addressList, dvConstraint);
        }
        if (validation != null) {
            validation.createErrorBox("错误提示", "请填写列表中的值，或下载最新模版重试");
            validation.setShowErrorBox(true);
            validation.setShowPromptBox(false);
            validation.setEmptyCellAllowed(false); //必填
            mainSheet.addValidationData(validation);
        }
    }


    // 自然行列号-1 后，校验从 0 开始的行列号是否合法
    private static void checkArguments(int firstRow, int lastRow, int firstCol, int lastCol) {
        if (firstRow < 0 || lastRow < 0 || firstCol < 0 || lastCol < 0 || lastRow < firstRow || lastCol < firstCol) {
            throw new IllegalArgumentException("Wrong Row or Column index : " + firstRow + ":" + lastRow + ":" + firstCol + ":" + lastCol);
        }
    }

    /**
     * ======================================= 多级联动下拉列表 =============================================================
     */

    public final static String DICT_SHEET_MAIN = "主工作表sheet";
    public final static String DICT_SHEET_DATA = "隐藏数据sheet";

    /**
     * 把 dataList 放入隐藏 sheet 的第 curRow 行数据行
     */
    private static void createRowData(Row curRow, List<String> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int m = 0;
            for (String dataValue : dataList) {
                curRow.createCell(m++).setCellValue(dataValue);
            }
        }
    }

    /**
     * 创建名称管理器。把隐藏数据 sheet 的第 rowIndex 行的数据，级联到此 nameCode 的下拉列表中
     *
     * @param size        隐藏数据 sheet 的第 rowIndex 行的数据量
     * @param cascadeFlag 是否级联
     */
    private static void createExcelName(Workbook workbook, String nameCode, int rowIndex, int size, boolean cascadeFlag) {
        Name name;
        name = workbook.createName();
        name.setNameName(nameCode);
        String cellString = DICT_SHEET_DATA + "!" + createExcelNameList(rowIndex, size, cascadeFlag);
        name.setRefersToFormula(cellString);
    }

    /**
     * 名称数据行列计算表达式
     */
    private static String createExcelNameList(int rowIndex, int size, boolean cascadeFlag) {
        char start = 'A';
        if (cascadeFlag) {
            start = 'B';
            if (size <= 25) {
                char end = (char) (start + size - 1);
                return "$" + start + "$" + rowIndex + ":$" + end + "$" + rowIndex;
            } else {
                char endPrefix = 'A';
                char endSuffix = 'A';
                if ((size - 25) / 26 == 0 || size == 51) { //26-51之间，包括边界
                    if ((size - 25) % 26 == 0) {  //边界值
                        endSuffix = (char) ('A' + 25);
                    } else {
                        endSuffix = (char) ('A' + (size - 25) % 26 - 1);
                    }
                } else {  //51之上
                    if ((size - 25) % 26 == 0) {
                        endSuffix = (char) ('A' + 25);
                        endPrefix = (char) (endPrefix + (size - 25) / 26 - 1);
                    } else {
                        endSuffix = (char) ('A' + (size - 25) % 26 - 1);
                        endPrefix = (char) (endPrefix + (size - 25) / 26);
                    }
                }
                return "$" + start + "$" + rowIndex + ":$" + endPrefix + endSuffix + "$" + rowIndex;
            }
        } else {
            if (size <= 26) {
                char end = (char) Math.max((start + size - 1), 65);  //防止size=0时列号小于'A'出错
                return "$" + start + "$" + rowIndex + ":$" + end + "$" + rowIndex;
            } else {
                char endPrefix = 'A';
                char endSuffix = 'A';
                if (size % 26 == 0) {
                    endSuffix = (char) ('A' + 25);
                    if (size > 52 && size / 26 > 0) {
                        endPrefix = (char) (endPrefix + size / 26 - 2);
                    }
                } else {
                    endSuffix = (char) ('A' + size % 26 - 1);
                    if (size > 52 && size / 26 > 0) {
                        endPrefix = (char) (endPrefix + size / 26 - 1);
                    }
                }
                return "$" + start + "$" + rowIndex + ":$" + endPrefix + endSuffix + "$" + rowIndex;
            }
        }
    }

    /**
     * 设置级联，即级联下拉列表的生成
     * 第 6 列级联第 E 列的数据
     * 使第 naturalColIndex列下拉列表 级联到 columnName 坐标列
     *
     * @param columnName      Excel表格固定的列坐标名称，例如：A、B、C...
     * @param naturalColIndex 列数从 1 开始计数
     * @param size            为 wb 的多少行设置数据的有效性
     */
    private static void setFormulaStringDataValidation(Workbook wb, String columnName, int naturalColIndex, int size) {
        Sheet sheet = wb.getSheet(DICT_SHEET_MAIN);
        for (int x = 1; x <= size + 1; x++) {
            setFormulaStringDataValidationByRow(sheet, String.format("IF($%s$" + x + "=\"  \",\"  \",INDIRECT($%s$" + x + "))", columnName, columnName), x, naturalColIndex);
        }
    }

    /**
     * 根据公式设置级联数据下拉列表
     *
     * @param naturalColIndex 从 1 开始计数
     * @param naturalRowIndex 从 1 开始计数
     */
    private static void setFormulaStringDataValidationByRow(Sheet sheet, String formulaString, int naturalRowIndex, int naturalColIndex) {
        //设置数据有效性加载在哪个单元格上  四个参数：起始行、终止行、起始列、终止列
        int firstRow = naturalRowIndex - 1;
        int lastRow = naturalRowIndex - 1;
        int firstCol = naturalColIndex - 1;
        int lastCol = naturalColIndex - 1;
        checkArguments(firstRow, lastRow, firstCol, lastCol);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = null;
        if (sheet instanceof XSSFSheet || sheet instanceof SXSSFSheet) {
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            validation = dvHelper.createValidation(dvHelper.createFormulaListConstraint(formulaString), addressList);
        } else if (sheet instanceof HSSFSheet) {
            DVConstraint constraint = DVConstraint.createFormulaListConstraint(formulaString);
            validation = new HSSFDataValidation(addressList, constraint);
        }
        if (validation != null) {
            validation.createErrorBox("错误提示", "请填写列表中的值，或下载最新模版重试");
            validation.setShowErrorBox(true);
            //设置输入信息提示信息
            validation.createPromptBox("提示", "请先选择好地区");
            validation.setShowPromptBox(true);
            validation.setEmptyCellAllowed(false); //必填
            sheet.addValidationData(validation);
        }
    }

    @Autowired
    private AreaService tAreaService;
    private static AreaService areaService;

    @PostConstruct
    public void init() {
        areaService = tAreaService;
    }


    /**
     * 创建多级联动下拉列表的Excel表
     * 将所有级联列表的字段从数据库查询出，并生成对应的名称管理器，存放至隐藏的sheet中
     *
     * @param isEmptyExcel 导出的表是否为空表
     */
    public static Workbook getMultiPropDataWorkbook(boolean isEmptyExcel) {
        // 创建大数据xlsx的Excel，compressTmpFiles必须设置为true，否则临时文件会占满磁盘空间
        final SXSSFWorkbook wb = new SXSSFWorkbook(new XSSFWorkbook(), 1000, true);

        // 如需生成xls的Excel，请使用下面的工作簿对象，注意后续输出时文件后缀名也需更改为xls
//        Workbook wb = new HSSFWorkbook();

        Sheet mainSheet = wb.createSheet(DICT_SHEET_MAIN);  // 主sheet
        Sheet dataSheet = wb.createSheet(DICT_SHEET_DATA);  // 隐藏的数据sheet
        List<AreaEntity> areaList = areaService.list();
        if (CollectionUtils.isEmpty(areaList)) {
            throw new RRException("请先添加【地区】等系统基础数据", HttpStatus.HTTP_BAD_REQUEST);
        }
        Map<Integer, List<AreaEntity>> areaGroupByLevel = areaList.stream().collect(Collectors.groupingBy(AreaEntity::getLevel));
        // 从第0行开始写数据，一个行存放一个下拉列表的数据
        int index = 0;
        // 3个级别（0:省份/直辖市,1:市级单位,2:区级单位）
        for (int i = 0; i < 2; i++) {
            // 此级别下的所有地区
            List<AreaEntity> areas = areaGroupByLevel.get(i);
            // 放入隐藏数据行
            createRowData(dataSheet.createRow(index++), areas.stream().map(AreaEntity::getName).collect(Collectors.toList()));
            for (AreaEntity area : areas) {
                // 下一层级中的子地区
                List<AreaEntity> subAreas = areaGroupByLevel.get(i + 1)
                        .stream()
                        .filter(subArea -> subArea.getPid().equals(area.getId()))
                        .collect(Collectors.toList());
                // 放入下一个隐藏数据行
                createRowData(dataSheet.createRow(index++), subAreas.stream().map(AreaEntity::getName).collect(Collectors.toList()));
                // 把隐藏数据sheet的第index行的子地区数据，级联到此地区名的下拉列表中
                createExcelName(wb, area.getName(), index, subAreas.size(), false);
            }
        }
        // 第6列数据级联到坐标第E列，第7列数据级联到坐标第F列
        setFormulaStringDataValidation(wb, isEmptyExcel ? "D" : "E", isEmptyExcel ? 5 : 6, 5000);
        setFormulaStringDataValidation(wb, isEmptyExcel ? "E" : "F", isEmptyExcel ? 6 : 7, 5000);
        // 第5列放入省份/直辖市
        setValidationListData(mainSheet, 1, 5000, isEmptyExcel ? 4 : 5, isEmptyExcel ? 4 : 5, areaGroupByLevel.get(0).stream().map(AreaEntity::getName).toArray(String[]::new));

        return wb;
    }


    public static void main(String[] args) throws Exception {
        // xlsx的excel文件
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet");
        FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\success.xlsx");

        // xls的excel文件
//        HSSFWorkbook wb = new HSSFWorkbook();
//        HSSFSheet sheet = wb.createSheet("sheet");
//        FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\success.xls");


        String[] textlist = {"列表1", "列表2", "列表3", "列表4", "列表5"};

        // 第一列的前501行都设置为选择列表形式.
        setValidationListData(sheet, 1, 500, 1, 1, textlist);

        // 第二列的前501行都设置提示.
        setPrompt(sheet, "Prompt Title", "Prompt Content", 1, 500, 2, 2);

        // 第三列的前501行都限定整数
        setIntegerConstraint(sheet, 1, 500, 3, 3);

        // 第四列的前501行都限定小数
        setDecimalConstraint(sheet, 1, 500, 4, 4);

        // 第五列的前501行都限定日期
        setDateConstraint(sheet, 1, 500, 5, 5);

        wb.write(out);
        out.close();
    }

}
