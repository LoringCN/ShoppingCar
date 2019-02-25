package cn.huace.timer;

import cn.huace.common.utils.DateUtils;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.statis.mongo.entity.StatisticMongoCategory;
import cn.huace.statis.mongo.entity.StatisticMongoEntity;
import cn.huace.statis.mongo.service.StatisticMongoService;
import cn.huace.statis.utils.StatisticEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.awt.Font;
import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

//import org.jfree.chart.axis.CategoryAxis;
//import org.jfree.chart.axis.ValueAxis;

@Slf4j
@RestController
@RequestMapping("/mail")
@EnableScheduling
public class MailTimer {

    @Autowired
    private StatisticMongoService statisticMongoService;

    @Autowired
    private ShopService shopService;

    public static Map<Integer, String> map = new HashMap<>();
    //    每一个统计数据在excel的列数
    public static Integer TYPE_SPACING = 30;
    //    发送人邮箱
    @Value("${email.sender}")
    public String SENDER;
    //    发送人授权码
    @Value("${email.authorizationCode}")
    public String AUTHORIZATION_CODE;
    //    收件人邮箱 可以有多个 用逗号隔开 addressee
    @Value("${email.addressee}")
    public String ADDRESSEE;
    //    表到图的行数
    public static Integer TABLE_FIGURE_SPACING = 5;
    //    第一个数据开始的位置
    public static Integer START_POSITION = 5;

    public static String FILE_NAME = "static/天虹数据.xls";


    //    定时触发
    @Scheduled(cron = "0 0 4 ? * *")
    public void mailTimer() throws IOException, GeneralSecurityException, MessagingException {
        mail(null);
    }

    //    手动触发，这个会把邮件发送到自己的邮箱，一般不会使用这个
    @RequestMapping("/manual")
    public void mailManual(@RequestParam(required = false, defaultValue = "1") Integer specifyTime) throws IOException, GeneralSecurityException, MessagingException {
        mail(specifyTime);
    }

    //    除了定时任务之外，还设置手动触发，手动触发时，设置的是自己的邮箱接收
    public void mail(Integer specifyTime) throws IOException, GeneralSecurityException, MessagingException {
        Date startDate;
        if (specifyTime == null) {
            startDate = DateUtils.getNextStartTime(new Date(), -1);
        } else {
            startDate = DateUtils.getNextStartTime(new Date(), -specifyTime);
        }
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(FILE_NAME);
        HSSFWorkbook wb = new HSSFWorkbook(input);
        int sheets = wb.getNumberOfSheets();
        for (int i = 0; i < sheets; ++i) {
//            每次发送的邮件只保留两天的内容，其余删掉，因为新生成一定是在后面，所以每次只删除索引0即可
            Date startDate2 = DateUtils.getNextStartTime(startDate, -1);
            if (!wb.getSheetName(0).contains(DateUtils.getMonth(startDate2) + "-" + DateUtils.getDay(startDate2))) {
                wb.removeSheetAt(0);
            }
        }

        if (map.size() == 0) {
            for (StatisticEnum statisticEnum : StatisticEnum.values()) {
                map.put(Integer.parseInt(statisticEnum.getType()), statisticEnum.getDesc());
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String time = simpleDateFormat.format(startDate);

        Map<Integer, Integer> shopIdsMap = new HashMap<>();
        List<StatisticMongoEntity> statisticMongoEntityList = statisticMongoService.findList(null, null, time);
        for (StatisticMongoEntity statisticMongoEntity : statisticMongoEntityList) {
//            得到所有的商店
            shopIdsMap.put(statisticMongoEntity.getShopId(), 1);
        }
        for (Map.Entry idMap : shopIdsMap.entrySet()) {
            Integer shopId = (Integer) idMap.getKey();
            Shop shop = shopService.findOne(shopId);
            HSSFSheet sheet = wb.getSheet(DateUtils.getMonth(startDate) + "-" + DateUtils.getDay(startDate) + "-" + shop.getName());
            if (sheet == null) {
                sheet = wb.createSheet(DateUtils.getMonth(startDate) + "-" + DateUtils.getDay(startDate) +
                        "-" + shop.getName());
            } else {
                continue;
            }
//          在这配置固定的，如标题，style等
            setFinal(wb, sheet, startDate);
//            第一个数据开始的位置
            int location = START_POSITION;
            for (int i = 1; i <= 7; ++i) {
                if (i == 6) {
//                不展示购物车的位置数据
                    continue;
                }
//                List<StatisticMongoEntity> type = statisticMongoService.findList(15, String.valueOf(i), "20190107");
                for (StatisticMongoEntity statisticMongoEntity : statisticMongoEntityList) {
                    if (statisticMongoEntity.getShopId() == shopId) {
                        if (Integer.parseInt(statisticMongoEntity.getType()) == i) {
                            setCellRangeAddress(sheet, wb, map.get(i), location);
//                          对从mongo中取出的值转化成相应类型
                            List<StatisticMongoCategory> list = JSONArray.parseArray(JSON.toJSONString(statisticMongoEntity.getData()), StatisticMongoCategory.class);
                            if (list.size() > 0) {
//                              将list中的数据从大到小排序
                                list = sortList(list, i);
//                              将统计数据在excel中用表格展示
                                writeData(list, wb, sheet, location, i);
//                              画出上面数据的统计图
                                createByPoiAndJFreeChart(list, wb, sheet, i, location + TABLE_FIGURE_SPACING);
                                location += TYPE_SPACING;
                            }
                        }
                    }
                }
            }
        }
        outExcel(wb);
//        清空map
        map = new HashMap<>();
        if (wb.getNumberOfSheets() == 0) {
            log.warn("邮件发送出错，excel没有工作簿");
            return;
        }
        sendMail(specifyTime);
        log.info("邮件发送完成");
    }

    //    对从mongo中取出的内容排序，并且定位广告只取前10个
    public List<StatisticMongoCategory> sortList(List<StatisticMongoCategory> list, int type) {
        Collections.sort(list, new Comparator<StatisticMongoCategory>() {

            @Override
            public int compare(StatisticMongoCategory o1, StatisticMongoCategory o2) {
                return (int) (o2.getCount() - o1.getCount());
            }
        });
//        因为定位广告实在有点多，所以只取10个显示
        if (type == 4 || type == 5) {
            if (list.size() > 10) {
                list = list.subList(0, 10);
            }
        }
        return list;
    }

    //单纯使用POI操作Excel
    //把内容输出到excel
    public void outExcel(HSSFWorkbook wb) throws IOException {

        FileOutputStream fos = null;
        try {
            String filePath = this.getClass().getClassLoader().getResource(FILE_NAME).getFile();
            filePath = java.net.URLDecoder.decode(filePath, "utf-8");
            fos = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            wb.write(fos); // 写入到文本输出流中
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //    poi加jfreechart来生成图表
    public void createByPoiAndJFreeChart(List<StatisticMongoCategory> list, HSSFWorkbook wb, HSSFSheet sheet, int type, int location) throws IOException {

        Font xFont = new Font("宋体", Font.BOLD, 15);

        //设置样式
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
        //设置图例的字体
        standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
        //设置轴向的字体
        standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
        // 设置坐标轴的偏移量
        //standardChartTheme.setAxisOffset(new RectangleInsets(0,0,0,0));
        ChartFactory.setChartTheme(standardChartTheme);

        JFreeChart mBarChart = ChartFactory.createBarChart("", // 图表标题
                "", // 横轴
                "",// 纵轴
                GetDatasetBar(list, map.get(type)),  // 数据来源
                PlotOrientation.VERTICAL, // 图表方向
                true, // 是否显示图例
                true, // 是否生成提示工具
                false); // 是否生成url连接
        // 图表标题设置
//        TextTitle mTextTitle = mBarChart.getTitle();
//        mTextTitle.setFont(titleFont);

        // 图表图例设置
//        LegendTitle mLegend = mBarChart.getLegend();
//        mLegend.setItemFont(kFont);
//        if (mLegend != null)
//            mLegend.setItemFont(kFont);
        // 设置柱状图轴
        CategoryPlot mPlot = (CategoryPlot) mBarChart.getPlot();
        // 3:设置抗锯齿，防止字体显示不清楚
        mBarChart.setAntiAlias(false);// 抗锯齿
        // x轴
        CategoryAxis mDomainAxis = mPlot.getDomainAxis();
        // 设置x轴标题的字体
        mDomainAxis.setLabelFont(xFont);
        // 设置x轴坐标字体
        mDomainAxis.setTickLabelFont(xFont);
        //
        mDomainAxis.setMaximumCategoryLabelWidthRatio(2.0f);
        // 设置柱状体与右边框的距离
//        mDomainAxis.setLowerMargin(0.01);
//        mDomainAxis.setUpperMargin(0.01);
        // y轴
        ValueAxis mValueAxis = mPlot.getRangeAxis();
        // 设置柱状体与上边框的距离
//        mValueAxis.setLowerMargin(0.25);
        mValueAxis.setUpperMargin(0.15);
        // 设置y轴标题字体
        mValueAxis.setLabelFont(new Font("宋体", Font.PLAIN, 11));
        // 设置y轴坐标字体
        mValueAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 15));
        // 柱体显示数值
        BarRenderer3D mRenderer = new BarRenderer3D();
        mRenderer.setDefaultEntityRadius(5);
        mRenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        mRenderer.setItemLabelsVisible(true);
        mRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER_LEFT));
        // 柱体显示数值向上偏移一点点
        mRenderer.setItemLabelAnchorOffset(20);
        //设置柱形的宽度
//        if ("定位广告".equals(list.get(0).getCategory())) {
//            mRenderer.setMaximumBarWidth(0.02);
//        } else {
//            mRenderer.setMaximumBarWidth(0.04);
//        }
        mRenderer.setMaximumBarWidth(0.04);
        mRenderer.setMinimumBarLength(0.05);
//        mRenderer.setItemMargin(0.5);
        // 设置柱体显示数字格式
        mRenderer.setItemLabelFont(new Font("宋体", Font.BOLD, 15));
        mPlot.setRenderer(mRenderer);
        //设置y轴的刻度为
//        NumberAxis numberAxis = (NumberAxis) mPlot.getRangeAxis();
//        numberAxis.setTickUnit(new NumberTickUnit(10000));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // 将图表写入到ByteArrayOutputStream流中
            ChartUtilities.writeChartAsJPEG(bos, mBarChart, 800, 700);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray
        try {
            // 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
            // anchor主要用于设置图片的属性(1,1 表示图片左上角，15,31表示图片右下角，通过这个可以控制图片的大小)
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 255, 255,
                    (short) 1, location, (short) 6, location + 19);
            anchor.setAnchorType(ClientAnchor.AnchorType.byId(3));
            // 插入图片
            patriarch.createPicture(anchor,
                    wb.addPicture(bos.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //自定义style
    public HSSFCellStyle setStyle(HSSFWorkbook workbook, short fontSize) {
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();

        //设置字体
        font.setFontName("宋体");
        font.setFontHeightInPoints(fontSize);
        font.setBold(true);
        style.setFont(font);
        //设置居中
        style.setAlignment(HorizontalAlignment.forInt(2));
        return style;
    }

    //自定义style
    public HSSFCellStyle setTitleStyle(HSSFWorkbook workbook, short fontSize, boolean backgroudColor, boolean bold) {
        HSSFFont font = workbook.createFont();
        HSSFCellStyle style = workbook.createCellStyle();

        //背景色设置
        if (backgroudColor) {
            //这两个要一起用
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.RED.getIndex());
        }
        //设置边框
        style.setBorderBottom(BorderStyle.valueOf((short) 1));
        style.setBorderLeft(BorderStyle.valueOf((short) 1));
        style.setBorderRight(BorderStyle.valueOf((short) 1));
        style.setBorderTop(BorderStyle.valueOf((short) 1));
        style.setAlignment(HorizontalAlignment.forInt(1));//设置居右
        //设置字体
        font.setFontName("宋体");
        font.setFontHeightInPoints(fontSize);
        if (bold) {
            font.setBold(true);
        }
        style.setFont(font);
        return style;
    }

    //设置合并区域，并且写入数据
    public void setCellRangeAddress(HSSFSheet sheet, HSSFWorkbook workbook, String title, int location) {

        HSSFCellStyle style = setStyle(workbook, (short) 14);
        sheet.addMergedRegion(new CellRangeAddress(location - 1, location - 1, 1, 3));
        HSSFRow row11 = sheet.createRow(location - 1);
        HSSFCell cell11 = row11.createCell(1);
        cell11.setCellValue(title + ":");
        cell11.setCellStyle(style);
    }

    //    在这配置固定的，如标题，style等
    public void setFinal(HSSFWorkbook workbook, HSSFSheet sheet, Date startDate) {
        //在这里重新设置了合并单元的字体
        HSSFCellStyle style = setStyle(workbook, (short) 14);

        //得到今天的星期数
        String week = DateUtils.getWeek(startDate);
        //得到今天是多少号
        int day = DateUtils.getDay(startDate);
        //得到今天的月份
        int month = DateUtils.getMonth(startDate);

        HSSFRow row1 = sheet.createRow(1);
        HSSFCell cell1 = row1.createCell(5);
        cell1.setCellValue("天虹沙井店" + month + "-" + day + "(" + week + ")");
        cell1.setCellStyle(style);
    }

    //    设置图表内容
    public CategoryDataset GetDatasetBar(List<StatisticMongoCategory> list, String title) throws UnsupportedEncodingException {

        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
        for (StatisticMongoCategory statisticMongoCategory : list) {
            Long count = statisticMongoCategory.getCount();
            title = new String(title.getBytes(), "utf-8");
            String name = new String(statisticMongoCategory.getName().getBytes(), "utf-8");
            log.info("类型为：" + title + "  产品为：" + name + "  次数为：" + count);
            mDataset.addValue(count, title, name);
        }
        return mDataset;
    }

    //设置图表格式（大小,边框）
    public void writeData(List<StatisticMongoCategory> statisticList, HSSFWorkbook workbook, HSSFSheet sheet, int location, int type) {
        int column;
        HSSFCellStyle dataStyle = setTitleStyle(workbook, (short) 12, false, false);
        //设置为宋体11号
        HSSFCellStyle titleStyle = setTitleStyle(workbook, (short) 12, false, true);
        int time = 3;
        for (int i = 0; i < time; i++) {
            //第一行第一个数据从2开始
            column = 2;
            HSSFRow row = sheet.createRow(location);
            HSSFCell cell1 = row.createCell(column - 1);
            for (StatisticMongoCategory statisticMongoCategory : statisticList) {
                HSSFCell cell = row.createCell(column);
                if (i == 0) {
                    cell.setCellValue(statisticMongoCategory.getName());
                    cell.setCellStyle(titleStyle);
                    sheet.setColumnWidth(column, 14 * 256);
                }
                if (i == 1) {
                    cell1.setCellValue("统计次数");
                    cell.setCellValue(statisticMongoCategory.getCount());
                    cell.setCellStyle(dataStyle);
                }
                if (i == 2) {
                    cell1.setCellValue("所占比例");
                    cell.setCellValue(statisticMongoCategory.getRatio());
                    cell.setCellStyle(dataStyle);
                }
                cell1.setCellStyle(titleStyle);
                ++column;
            }
            ++location;
        }
    }

    //    发送邮件
    public void sendMail(Integer specifyTime) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException {
        Properties props = new Properties();
        // 开启debug调试
//        props.setProperty("mail.debug", "true");
        // 发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");
        // 设置邮件服务器主机名(第一个是qq邮箱，第二个是企业邮箱)
        props.setProperty("mail.host", "smtp.exmail.qq.com");
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");
        // 设置ssl加密
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getInstance(props);

        Message msg = new MimeMessage(session);
        // 主题
        msg.setSubject("天虹数据");
        // 内容
//        msg.setText("这就是内容");
        // 添加附件
        String filePath = this.getClass().getClassLoader().getResource(FILE_NAME).getFile();
        filePath = java.net.URLDecoder.decode(filePath, "utf-8");
        File file = new File(filePath);
        Multipart multipart = new MimeMultipart();
        // 添加邮件正文
        BodyPart contentPart = new MimeBodyPart();
        //这也是在添加内容(在这暂时不需要内容，只有附件)
        contentPart.setContent("", "text/html;charset=UTF-8");
        multipart.addBodyPart(contentPart);

        // 添加附件的内容
        BodyPart attachmentBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);
        attachmentBodyPart.setDataHandler(new DataHandler(source));

        //MimeUtility.encodeWord可以避免文件名乱码
        attachmentBodyPart.setFileName(MimeUtility.encodeWord(file.getName()));
        multipart.addBodyPart(attachmentBodyPart);
        // 将multipart对象放到message中
        msg.setContent(multipart);
        // 保存邮件
        msg.saveChanges();
        // 设置发送人
        msg.setFrom(new InternetAddress(SENDER));

        Transport transport = session.getTransport();
        // 企业邮箱
        transport.connect("smtp.exmail.qq.com", SENDER, AUTHORIZATION_CODE);

        // 发送信息并且设置接收人  huningdou@u-beacon.com
        String[] recipientEmails = ADDRESSEE.split(",");
        if (specifyTime == null) {
            log.info("定时任务完成！");
            for (String recipientEmail : recipientEmails) {
                transport.sendMessage(msg, new Address[]{new InternetAddress(recipientEmail)});
            }
        } else {
            log.info("手动触发完成！");
            transport.sendMessage(msg, new Address[]{new InternetAddress(SENDER)});
        }
        transport.close();
    }
}
