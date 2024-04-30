package com.example.weatherapp.XmlParser;

/*
Name: Sumaiya Juma
ID: s2110905
*/

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapXmlPullParser {
    List<MapData> dataList = new ArrayList<MapData>();


    public List<MapData> getData(){
        return dataList;
    }
    public MapData parse(InputStream is,String locations) {
       MapData data = null;
       String text = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, null);
            int eventType = parser.getEventType();

            boolean isFirstItemParsed = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName != null && tagName.equalsIgnoreCase("item")) {
                            // Only parse the first <item> element
                            if (!isFirstItemParsed) {
                                data = new MapData();
                                isFirstItemParsed = true;
                            } else {
                                // If already parsed the first item, return it
                                return data;
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName != null && data != null) {
                            if (tagName.equalsIgnoreCase("title")) {
                                // Extract condition from the title
                                data.setCondition(text.split(",")[0].split(":")[1]);
                            } else if (tagName.equalsIgnoreCase("point")) {
                                String[] coordinates = text.split(" ");
                                System.out.println(text);
                                    double latitude = Double.parseDouble(coordinates[0]);
                                    double longitude = Double.parseDouble(coordinates[1]);
                                    data.setLatT(latitude);
                                    data.setLongT(longitude);
                                    data.setLocationName(locations);

                            }
                        }
                        break;
                }

                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

}