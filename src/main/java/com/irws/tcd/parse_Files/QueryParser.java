package com.irws.tcd.parse_Files;

import com.irws.tcd.Beans.QueryBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryParser {
    public static void main(String[] args) {
        List<QueryBean> queryList= new ArrayList<>();
        File queryFile= new File("src/main/resources/query/topics");
        try {
            Scanner scanner = new Scanner(queryFile);
            QueryBean queryBean = null;
            while (scanner.hasNext())
            {

                String next_line=scanner.nextLine();
                if (!next_line.equals(""))
                    if(next_line.contains("<top>"))
                        next_line=scanner.nextLine();
                        else if (next_line.contains("<num>")) {
                            queryBean= new QueryBean();
                            queryBean.setNum(next_line.replace("<num> Number: ",""));
                        }
                        else if (next_line.contains("<title>"))
                            queryBean.setTitle(next_line.replace("<title> ",""));
                        else if (next_line.contains("<desc>"))
                        {
                            next_line=scanner.nextLine();
                            String desc="";
                            while(!next_line.contains("<narr>"))
                            {
                                desc=desc.concat(next_line);
                                next_line=scanner.nextLine();
                            }
                            queryBean.setDescription(desc);
                            String narr="";
                            while(!next_line.contains("</top>"))
                            {
                                narr=narr.concat(next_line);
                                next_line=scanner.nextLine();

                            }
                            queryBean.setNarrative(narr.replace("<narr> Narrative: ",""));
                            queryList.add(queryBean);
                            System.out.println(queryBean.getDescription());
                        }
                        else
                            continue;

            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        List<Map<String,String>> query = new ArrayList<>();
//        Document document= null;
//        try {
//            document = Jsoup.parse(queryFile,"UTF-8");
//            Elements link = document.select("top");
//            for (Element e: link){
//                System.out.println(e.text());
////                System.out.println(e.getElementsByTag("num").text());
////                System.out.println(e.getElementsByTag("title").text());
////                System.out.println(e.getElementsByTag("desc").text());
////                System.out.println(e.getElementsByTag("narr").text());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



    }
}
