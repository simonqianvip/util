package com.simon.api; 
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.io.*;
import java.util.*;

/**
 *
 * @author hrb
 */
public class ApiUtil {
	//http://hadoop.apache.org/docs/r2.7.1/api/index.html
	//https://storm.apache.org/javadoc/apidocs/index.html

    private String ENTERURL = "http://hadoop.apache.org/docs/r2.7.1/api/index.html";
    private String ENTERFILEPATH = "D:\\package\\API\\Hadoop_2_7_API";
    private HttpURLConnection conn;
    private String BASEURL = "hadoop.apache.org";
    private URL httpurl;
    private Map downedurl = new HashMap();
    private Map folder = new HashMap();

    private Map waitingUrl = new HashMap();
    private Map reverseWaitingUrl = new HashMap();
    private long inPointer = 0;
    private long outPointer = 0;
    private long downCounter = 0 ;

    public static void main(String[] args) {
        ApiUtil main = new ApiUtil();
        if (args != null && args.length == 2) {
            if (!args[0].equals("")) {
                main.ENTERURL = args[0];
                main.BASEURL = main.getBaseUrl(args[0]);
            }
            if (!args[1].equals("")) {
                main.ENTERFILEPATH = args[1];
            }
        }
   
        main.init();

        //main.execute(main.ENTERURL, main.ENTERFILEPATH);
        main.execute();
        System.out.println("===================== OVER ===================");

    }

    public void init(){
        this.BASEURL = getBaseUrl(this.ENTERURL);
        PageAttribute atr = new PageAttribute();
        atr.url = this.ENTERURL ;
        atr.path = formatPath(this.ENTERFILEPATH)+"\\"+this.ENTERURL.substring(this.ENTERURL.lastIndexOf("/")+1);
        this.addWaitUrl(atr);
    }

    /**
     * 下载网页
     * 给一个Url，返回字符串,同时将源码保存到硬盘中
     */
    public String downloadpage(String url) {
        if (!domainfilte(url)) {
            return "";
        }
        try {
            System.out.println("50:" + url);
            this.httpurl = new URL(url);
            this.conn = (HttpURLConnection) this.httpurl.openConnection();

            StringBuffer retrunstr = new StringBuffer();
            String temp;
            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            while ((temp = br.readLine()) != null) {
                retrunstr.append(temp);
            }
            return retrunstr.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean domainfilte(String url) {
        String urltemp;
        
      //非本域的不下
        urltemp = getBaseUrl(url);
        if (!urltemp.equals(this.BASEURL)) {
            return false;
        }

      //已经下载过的不下载
        if (downedurl.get(url) != null) {
            return false;
        } else {
            downedurl.put(url, url);
        }
        
      //无明确地址的不能下
        String reg = "[a-zA-Z0-9]{1,5}";
        urltemp = url.substring(url.lastIndexOf(".")+1);
        urltemp = urltemp.substring(urltemp.indexOf(".")+1);
        if(!urltemp.matches(reg)){
            return false ;
        }

        return true;
    }

    public String getBaseUrl(String url) {
        int start;
        start = url.indexOf("//");
        url = url.substring(start + 2);
        start = url.indexOf("/");

        return url.substring(0, start);
    }
    /*从源码中提取下载地址，任何的下载连接*/
    public String[] collectUrl(String sourcecode) {
        Set urls = new HashSet();
        String[] keys = new String[]{"src", "href"};
        String key;
        String[] temp = new String[0];
        String reg;
        String urltemp;

        for (int i = 0; i < keys.length; i++) {
            temp = null;
            key = keys[i];
            reg = " (?i)" + key + "[\\s]*=[\\s]*\"";
            temp = sourcecode.split(reg);
            for (int j = 1; j < temp.length; j++) {
                urltemp = temp[j].substring(0, temp[j].indexOf("\""));
                urls.add(urltemp);
            }
        }


        temp = new String[urls.size()];
        return (String[]) urls.toArray(temp);
    }

    /**分析提取到的下载地址和当前的网页地址
     * 得到真正的url和文件保存地址
     * 返回downurl 和 path 的映射
     * [{url:'',path:''}]
     * path的格式应为
     */
    public PageAttribute[] parseUrl(String[] urlArr, String baseUrl, String path) {
        path = formatPath(path);
        baseUrl = formatUrl(baseUrl);

        PageAttribute[] url2path = new PageAttribute[1];
        Set set = new HashSet();

        String urltemp = "";
        String baseUrltemp = "";
        String pathtemp = "";
        PageAttribute atttemp;

        for (int i = 0; i < urlArr.length; i++) {
            baseUrltemp = baseUrl;
            urltemp = urlArr[i];
            pathtemp = path;
            atttemp = new PageAttribute();
            
          //如果包含整个的完整的http链接，那么不要加在进去，因为不知道把他放在哪
            if (urltemp.indexOf("//") >= 0) {
                continue;
            }

          //锚在本页面内，不管他
            if (urltemp.indexOf("#") >= 0) {
                urltemp = urltemp.substring(0, urltemp.indexOf("#"));
            }

            if (urltemp.equals("")) {
                continue;
            }
            
          //此时path的格式应为d:\jaxen\org ,最后的后缀不带 \
            while (urltemp.startsWith("../") || urltemp.startsWith("./") || urltemp.startsWith("/")) {
            	//各往上走一层
                urltemp = urltemp.substring(urltemp.indexOf("/") + 1);
                baseUrltemp = baseUrltemp.substring(0, baseUrltemp.lastIndexOf("/"));
                //if (pathtemp.startsWith(this.ENTERFILEPATH) && pathtemp.length() > this.BASEURL.length()) {
                pathtemp = pathtemp.substring(0, pathtemp.lastIndexOf("\\"));
            //}
            }

            urltemp = urltemp.substring(urltemp.indexOf("?") + 1);

            pathtemp = pathtemp + "\\" + urltemp;
            pathtemp = pathtemp.replaceAll("/", "\\\\");
            //pathtemp = pathtemp.substring(0, pathtemp.lastIndexOf("\\"));

            atttemp.path = pathtemp;
            atttemp.url = baseUrltemp + "/" + urltemp;
            System.out.println("176:" + atttemp.url);

            if (/*atttemp.path.split("\\.").length > 2 || atttemp.path.split("org").length > 2 ||*/ atttemp.url.indexOf("org/org")>=0 || atttemp.path.indexOf(".")<0) {
                System.out.println("err:" + atttemp.path);
                continue;
            }
            //System.out.println("=="+atttemp.path+"----"+atttemp.url);
            set.add(atttemp);// = atttemp;
        }
        return (PageAttribute[]) set.toArray(url2path);
    }

    /**
     * 保存网页
     * @param url
     * @param path
     * @param sourcecode
     */
    public void savePage(String url, String path, String sourcecode) {
        try {

        	///D:\\package\\API\\Hadoop_2_7_API
            System.out.println("192:" + path + "    " + url);

            String reg = "[a-zA-Z0-9]*";
            if (path.indexOf(".") < 0 || !path.split("\\.")[1].matches(reg)) {
                path = makeDefaultPath(url, path);
            }

            createFile(path);

            wirteToFile(path, sourcecode);

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    /**
     * 创建文件
     * @param path
     */
    public void createFile(String path) {
        try {
            if (!checkHasFolder(path)) {
                createFolder(path);
            }

            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            System.out.println("createFile" + e.toString());
        }
    }

    /**
     * 写入到文件
     * @param path
     * @param content
     */
    public void wirteToFile(String path, String content) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println("wirteToFile:" + e.toString());
        }
    }

    public String makeDefaultPath(String url, String path) {
        path = path + "\\" + url.substring(url.lastIndexOf("/") + 1);
        return path.replaceAll("\\\\\\\\", "\\\\");
    }

    public boolean checkHasFolder(String url) {
        Object o = this.folder.get(url.substring(0, url.lastIndexOf("\\")));
        return o == null ? false : true;
    }
    
    /**
     * format path
     * @param path
     * @return
     */
    public String formatPath(String path) {
        String reg = "[a-zA-Z0-9]*";
        if (path.indexOf(".") >= 0 && path.substring(path.indexOf(".") + 1).matches(reg)) {
            path = path.substring(0, path.lastIndexOf("\\"));
        }

        if (path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * format url
     * //保证不带文件，最后不带 /
     * @param url
     * @return
     */
    public String formatUrl(String url) {
        String reg = "[a-zA-Z0-9]*";
        String temp = url;
        temp = temp.substring(temp.lastIndexOf("/") + 1);
        temp = temp.substring(temp.lastIndexOf(".") + 1);
        if (temp.matches(reg)) {
            url = url.substring(0, url.lastIndexOf("/"));
        }

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
    
    /**
     * 创建文件
     * @param path
     */
    public void createFolder(String path) {
        try {
            path = formatPath(path);

            File file = new File(path);
            file.mkdirs();
            this.folder.put(path, "has");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void execute(String url, String path) {
        try {
            String code = downloadpage(url);
            savePage(url, path, code);
            if (!code.equals("")) {
                String[] urlArr = collectUrl(code);
                if (urlArr.length > 0) {
                    PageAttribute[] urlpath = parseUrl(urlArr, url, path);
                    for (int i = 0; i < urlpath.length; i++) {
                        if (urlpath[i] != null) {
                            this.addWaitUrl(urlpath[i]);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(e.toString());
        }

    }

    public void execute_back(String url, String path) {
        try {
            String code = downloadpage(url);
            savePage(url, path, code);
            String[] urlArr = collectUrl(code);
            PageAttribute[] urlpath = parseUrl(urlArr, url, path);
            for (int i = 0; i < urlpath.length; i++) {
                if (urlpath[i] != null) {
                    execute(urlpath[i].url, urlpath[i].path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(e.toString());
        }

    }

    public void execute(){
        PageAttribute atr ;
        try{
            while(this.waitingUrl.size()>0){
                atr = (PageAttribute)this.waitingUrl.remove(""+this.outPointer++);
                execute(atr.url,atr.path);
                
                System.out.println(this.waitingUrl.size()+"   已下载"+this.downCounter++);
            }
        }catch(Exception e){

        }
    }

    public void addWaitUrl(PageAttribute atr){
        Object o = this.reverseWaitingUrl.get(atr.url);
        if(o==null){
            this.reverseWaitingUrl.put(atr.url, atr);
            this.waitingUrl.put(""+this.inPointer++, atr);
        }
    }

    class PageAttribute {

        public String url;
        public String path;
    }
}
