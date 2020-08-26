package com.cc.simulationlogin;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author chwl
 * @Date 2020/8/26 15:46
 * @Description VerificationCodeTest
 * @Version 1.0
 */
public class Login {
    private Map<String, String> cookies = null;

    int i = 0;
    String templateName = "cc";
    String ter = "315,316,317,318,319,320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,364,402";
    String msg = "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccc";
    String s1;
    String templateName1;
    String ter1;
    {
        try {
            s1 = toGBK(URLDecoder.decode(URLEncoder.encode(msg, "gbk"),"gbk"));
            ter1 = toGBK(URLDecoder.decode(URLEncoder.encode(ter, "gbk"),"gbk"));
            templateName1 = toGBK(URLDecoder.decode(URLEncoder.encode(templateName, "gbk"),"gbk"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public Login(String templateName, String ter, String msg) throws UnsupportedEncodingException {
        this.templateName = templateName;
        this.ter = ter;
        this.msg = msg;
    }

    /**
     * 模拟登录获取cookie和sessionid 先请求一个sessionid根据session去获取验证码，登录
     */
    public boolean login() throws Exception {
        String url = "http://:8065";
        Connection connect1 = Jsoup.connect(url);
        connect1.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;" +
                "q=0.8,application/signed-exchange;v=b3;q=0.9").header("Accept-Encoding",
                "gzip, deflate");
        connect1.header("Accept-Language", "zh-CN,zh;q=0.9").header("Connection", "keep-alive");
        connect1.header("Content-Length", "135").header("Content-Type",
                "application/x-www-form-urlencoded");
        connect1.header("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36")
                .header("X-Requested-With", "XMLHttpRequest");
        Connection.Response res1 = connect1.ignoreContentType(true).method(Connection.Method.GET).execute();// 执行请求
        cookies = res1.cookies();
        String urlLogin = "http://10.137.64.133:8065/employeeLogin.do";
        Connection connect = Jsoup.connect(urlLogin);
        // 伪造请求头
        connect.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9").header("Accept-Encoding",
                "gzip, deflate");
        connect.header("Cookie", cookieToString());
        connect.header("Accept-Language", "zh-CN,zh;q=0.9").header("Connection", "keep-alive");
        connect.header("Content-Length", "135").header("Content-Type",
                "application/x-www-form-urlencoded");
        connect.header("Host", "10.137.64.133:8065").header("Referer", "http://10.137.64.133:8065/employeeLogin.do");
        connect.header("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36")
                .header("X-Requested-With", "XMLHttpRequest");
        for (int i = 0; i < 10; i++) {
            System.out.println("第" + i + "次获取code");
            String code = getCode().trim();
            System.out.println(code.trim() + "code");
//            start=0&length=12&uid=&ClientDigest=&userpin=12345678&buffer=&usbinfo=&loginType=0&employeeID=Wlsqxj&password=2981096822_Wlqx&rand=5434
            connect.data("start", "0").data("length", "12").data("uid", "").data("ClientDigest", "").data("userpin", "12345678").data("employeeID",
                    "Wlsqxj").data("buffer", "").data("usbinfo", "").data(
                    "password", "2981096822_Wlqx").data("rand", code).data("loginType", "0");
            //connect.data("name", "2").data("passwd", "1");
            //请求url获取响应信息
            Connection.Response res = connect.ignoreContentType(true).cookies(cookies).method(Connection.Method.POST).execute();// 执行请求
            // 获取返回的cookie
            BufferedInputStream bufferedInput = res.bodyStream();
//            bufferedInputStream
            byte[] buffer = new byte[1024];
            try {
                int bytesRead = 0;
                //从文件中按字节读取内容，到文件尾部时read方法将返回-1
                while ((bytesRead = bufferedInput.read(buffer)) != -1) {
                    //将读取的字节转为字符串对象
                    String chunk = new String(buffer, 0, bytesRead).trim();
//                    script html
/*                    if("<html>".equals(chunk)){
                        System.out.println("登录成功");
                        break;
                    }else if ("<script language=\"javascript\">".equals(chunk)){
                        System.out.println("错误");
                        continue;
                    }
                    System.out.print(chunk);*/
                    if(chunk.startsWith("<html")){
                        System.out.println("succ");
                        return true;
                    }
                }

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                //关闭 BufferedInputStream
                try {
                    if (bufferedInput != null) {
                        bufferedInput.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;

    }


    public String getCode() throws Exception {
        BufferedInputStream bufferedInputStream = getImg();

        //FileInputStream in = new FileInputStream("C:\\img\\images.jpg");
//        InputStream inputStream = new InputStream();
//        InputStream in = ImgCode.class.getResourceAsStream("/image/images2.jpg");
//        InputStream in = ImgCode.class.getResourceAsStream("D:\\captcha\\images2.jpg");
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        BufferedImage bin = ImageIO.read(bufferedInputStream);
        bin = process(bin);
        try {
            String result = instance.doOCR(bin);
            return result;
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public BufferedImage process(BufferedImage bin) {

        int endX = bin.getWidth();
        int endY = bin.getHeight();
        // 这里对图片黑白处理,增强识别率.这里先通过截图,截取图片中需要识别的部分
        BufferedImage textImage = ImageHelper.convertImageToGrayscale(ImageHelper.getSubImage(bin, 0, 0, endX, endY));
        // 图片锐化,自己使用中影响识别率的主要因素是针式打印机字迹不连贯,所以锐化反而降低识别率
        // textImage = ImageHelper.convertImageToBinary(textImage);
        // 图片放大5倍,增强识别率(很多图片本身无法识别,放大5倍时就可以轻易识,但是考滤到客户电脑配置低,针式打印机打印不连贯的问题,这里就放大5倍)
        textImage = ImageHelper.getScaledInstance(textImage, endX * 5, endY * 5);
        AsFile(textImage);
        return textImage;
    }

    public void AsFile(BufferedImage textImage) {

        File gifFile = new File("C:\\img\\" + i++ + ".jpg");
        OutputStream out = null;
        try {
            out = new FileOutputStream(gifFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(textImage, "jpg", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IOUtils.closeQuietly(out);
    }


    public BufferedInputStream getImg() throws Exception {
//        String urlLogin = "https://www.quanjing.com/createImg.aspx";
        String urlLogin = "http://10.137.64.133:8065/images.jsp";
        Connection connect = Jsoup.connect(urlLogin);
        connect.header("Accept", "image/webp,*/*").header("Accept-Encoding",
                "gzip, deflate");
        connect.header("Cookie", cookieToString());
        connect.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2").header("Connection", "keep-alive");
        connect.header("Content-Length", "135");
        connect.header("Host", "10.137.64.133:8065").header("Referer", "http://10.137.64.133:8065/employeeLogin.do");
        connect.header("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147" +
                        ".135 Safari/537.36");
        // 伪造请求头
//        connect.header("Accept", "image/webp,image/apng,image/*,*/*;q=0.8").header("Accept-Encoding",
//                "gzip, deflate");
//        connect.header("Accept-Language", "zh-CN,zh;q=0.9").header("Connection", "keep-alive");
//        connect.header("Content-Length", "135").header("Content-Type",
//                "application/x-www-form-urlencoded; charset=UTF-8");
//        connect.header("Host", "10.137.64.133:8065").header("Referer", "http://10.137.64.133:8065/");
//        connect.header("User-Agent",
//                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36")
//                .header("X-Requested-With", "XMLHttpRequest");
        Connection.Response res =
                connect.ignoreContentType(true).cookies(cookies).method(Connection.Method.GET).execute();
        // 执行请求
//        Document document = Jsoup.connect(urlLogin).cookies(cookies).post();
//        cookies = res.cookies();
        return res.bodyStream();
        /*byte[] data = readInputStream(bufferedInputStream);
        //new一个文件对象用来保存图片，默认保存当前工程根目录
        File imageFile = new File("D:\\img\\BeautyGirl.jpg");
        //创建输出流
        FileOutputStream outStream = new FileOutputStream(imageFile);
        //写入数据
        outStream.write(data);
        //关闭输出流
        outStream.close();*/


    }

    public String cookieToString() {
        String cook = "";
        Iterator iter = cookies.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            cook += (String) entry.getKey();
            cook +="=";
            cook += (String) entry.getValue();
        }
        System.out.println(cook);
        return cook.trim();
    }

    public void sendMessage() throws IOException, InterruptedException {
        String url = "http://10.137.64.133:8065/msgSend.do";
        Connection connect = Jsoup.connect(url);
        // 伪造请求头
        connect.header("Accept", "application/json, text/javascript, */*").header("Accept-Encoding",
                "gzip, deflate");
        connect.header("Cookie", cookieToString());
        connect.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2").header("Connection", "keep-alive");
        connect.header("Content-Length", "1772").header("Content-Type",
                "application/x-www-form-urlencoded");
        connect.header("Host", "10.137.64.133:8065").header("Referer", "http://10.137.64.133:8065/msgSend.do?method=toPage&forward=msg_send_standard");
        connect.header("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36")
                .header("X-Requested-With", "XMLHttpRequest");
        connect.header("Origin","http://10.137.64.133:8065");

        connect.data("method","checkIsContentBeyondLimit").data("terminalIdStr","85").data("content",s1);
        Connection.Response res =
                connect.ignoreContentType(true).cookies(cookies).method(Connection.Method.POST).execute();//
        sendMessage1(s1);
        System.out.println("发送sc");
    }

    public String toGBK(String str) throws UnsupportedEncodingException {
        byte bs[] = null;
        bs = str.getBytes("GBK");
        return str = new String(bs, "GBK");
    }


    public void sendMessage1(String msg1){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE)+2;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String url = "http://10.137.64.133:8065/msgSend.do?method=addMsgStandard";
        Connection connect = Jsoup.connect(url);
        connect.postDataCharset("gbk");
        // 伪造请求头
        connect.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").header("Accept-Encoding",
                "gzip, deflate");
        connect.header("Cookie", cookieToString());
        connect.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2").header("Connection", "keep-alive");
        connect.header("Content-Length", "15870").header("Content-Type",
                "application/x-www-form-urlencoded");
        connect.header("Host", "10.137.64.133:8065").header("Referer", "http://10.137.64.133:8065/msgSend.do?method=toPage&forward=msg_send_standard");
        connect.header("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36")
                .header("X-Requested-With", "XMLHttpRequest");
        connect.header("Origin","http://10.137.64.133:8065");
        connect.data("SendType","0").data("msgtypeID","-99").data("msgorivr","0");
        connect.data("tasktype","").data("isAdt","0").data("auditUserStr","").data("auditIdstrStr","");
        connect.data("auditType","0").data("isSendHurryVal","0").data("templateID","60");
        connect.data("templateName",templateName1);
        connect.data("templateTypeID","1").data("sendMethod","0").data("sendMethodVal","0");
        connect.data("pingtemp","").data("isTB","0");
        connect.data("msgcontentStr","").data("msgcontentFormat",msg1);
        connect.data("msgcontentFormat10",msg1);
        connect.data("msgcontentFormat9","");
        connect.data("msgcontentFormat8","");
        connect.data("msgcontentFormat7","");
        connect.data("msgcontentFormat6","");
        connect.data("msgcontentFormat5",msg1);
        connect.data("msgcontentFormat4",msg1);
        connect.data("msgcontentFormat3",msg1);
        connect.data("msgcontentFormat2","");
        connect.data("msgcontentFormat1",msg1);
        connect.data("msgcontent",msg1);
        connect.data("msghistory",msg1);
        connect.data("msghistory10",msg1);
        connect.data("msghistory9","");
        connect.data("msghistory8","");
        connect.data("msghistory7","");
        connect.data("msghistory6","");
        connect.data("msghistory5",msg1);
        connect.data("msghistory4",msg1);
        connect.data("msghistory3",msg1);
        connect.data("msghistory2","");
        connect.data("msghistory1",msg1);
        connect.data("msgcontent10",msg1);
        connect.data("msgcontent9","");
        connect.data("msgcontent8","");
        connect.data("msgcontent7","");
        connect.data("msgcontent6","");
        connect.data("msgcontent5",msg1);
        connect.data("msgcontent4",msg1);
        connect.data("msgcontent3",msg1);
        connect.data("msgcontent2","");
        connect.data("msgcontent1",msg1);

        connect.data("strmsgcontent1","");
        connect.data("lemmaContentLis","");
        connect.data("fontsize","0");
        connect.data("fontStyle","-1");
        connect.data("isSendHurry","0");
        //预约时间
        connect.data("bookingTime","2020-08-25+11%3A00%3A07");
        connect.data("effectTimeSend","48");
        connect.data("hasEffectTimeSendVal","1");
        connect.data("hasEffectTimeSend","1");
        connect.data("endyear",String.valueOf(year));
        connect.data("endmonth",String.valueOf(month));
        connect.data("endday",String.valueOf(date));
        connect.data("endhour",String.valueOf(hour));
        connect.data("effectTimeTemplate","48");
        connect.data("terminalListVal","218");
        connect.data("terminalAtTempl",ter1);
        connect.data("terminalTypeVal","");
        connect.data("terminalType","");
        connect.data("sendLinkManFlag","0");
        connect.data("terminalGroupListVal","");
        connect.data("terminalGroupListStr","");
        connect.data("terminalListValHorn","");
        connect.data("terminalAtTemplHorn","");
        connect.data("sendLinkManFlag","");
        connect.data("terminalListValTparty","");
        connect.data("terminalAtTemplTparty","");
        connect.data("sendLinkManFlag","");
        connect.data("civilterminalListVal","");
        connect.data("civilterminalGroupListVal","");
        connect.data("telnolistandgroup","0");
        connect.data("telnolist","");
        connect.data("telnogrouphead","");
        connect.data("telnogrouplast","");
        connect.data("sound","1");
        connect.data("power1","1");
        connect.data("power2","1");
        connect.data("led","1");
        connect.data("wirelessword","1");
        connect.data("soundtimes","1");
        connect.data("wirelesssound","1");
        connect.data("putonghua","1");
        connect.data("puTongboying","0");
        connect.data("dialect","1");
        connect.data("fangyanboying","0");
        connect.data("fangyanNum","0");
        connect.data("languagech","1");
        connect.data("language","1");
        connect.data("fayinren","0");
        connect.data("yusu","5");
        connect.data("yudiao","5");
        connect.data("yinliang","5");
        connect.data("alarmTerminalLiense","-1");
        connect.data("alarmTerminalLienseNum","1");
        connect.data("ledarea","1");
        connect.data("msgNo","-1");
        connect.data("noworlater","0");
        connect.data("sendtelno","13888888888");
        connect.data("sendfile","D%3A%5C");
        connect.data("hdinfoboxNo","");
        connect.data("maxCount","3");
        connect.data("internalTime","300");
        connect.data("mannerValue","2");
        connect.data("manner","2");
        connect.data("retryOptionValue","011");
        connect.data("retryOption","1");
        connect.data("retryOption","1");
        try {
            Connection.Response res = connect.ignoreContentType(true).cookies(cookies).method(Connection.Method.POST).execute();// 执行请求
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String unicodeToUtf8 (String s) throws UnsupportedEncodingException {
        return new String( s.getBytes("utf-8") , "utf-8");
    }

}

