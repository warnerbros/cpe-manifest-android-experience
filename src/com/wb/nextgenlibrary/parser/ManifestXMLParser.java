package com.wb.nextgenlibrary.parser;

import android.util.Xml;

import com.wb.nextgenlibrary.parser.appdata.ManifestAppDataSetType;
import com.wb.nextgenlibrary.parser.cpestyle.CPEStyleSetType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.MediaManifestType;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by gzcheng on 3/16/16.
 */
public class ManifestXMLParser {
	private static final String ns = null;

    private static String MANIFEST_HEADER = "manifest:";
    private static String MANIFEST_APPDATA_HEADER = "manifestdata:";
    private static String MD_HEADER = "md:";

    public static class NextGenManifestData{
        public final MediaManifestType mainManifest;
        public final ManifestAppDataSetType appDataManifest;
        public final CPEStyleSetType cpeStyle;
        public final Locale locale;

        public NextGenManifestData(MediaManifestType mediaManifestType, ManifestAppDataSetType appDataFeedSetType, CPEStyleSetType style, Locale locale){
            mainManifest = mediaManifestType;
            appDataManifest = appDataFeedSetType;
            cpeStyle = style;
            this.locale = locale;
        }
    }

    public NextGenManifestData startParsing(final String manifestUrl, final String appDataUrl, final String styleDataUrl, Locale locale){
        final CPEStyleSetType styleData[] = new CPEStyleSetType[1];
        final ManifestAppDataSetType[] appData = new ManifestAppDataSetType[1];
        final MediaManifestType[] mainManifest = new MediaManifestType[1];

		final CountDownLatch latch = new CountDownLatch(StringHelper.isEmpty(appDataUrl) && StringHelper.isEmpty(styleDataUrl) ? 1 :
				(StringHelper.isEmpty(appDataUrl) || StringHelper.isEmpty(styleDataUrl) ? 2 : 3) );

		if (appDataUrl != null) {
            new Thread() {
                @Override
                public void run() {
                    // App Data Parsing

                    HttpURLConnection conn = null;
                    try {
                        URL appDataURL = new URL(appDataUrl);
                        conn = (HttpURLConnection) appDataURL.openConnection();
                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(20000);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        conn.setRequestProperty("Accept-Encoding", "gzip");
                        conn.connect();
                        String responseEncoding = conn.getHeaderField("Content-Encoding");
                        InputStream appDataIS;
                        if ("gzip".equalsIgnoreCase(responseEncoding)) {
                            appDataIS = new BufferedInputStream(new GZIPInputStream(conn.getInputStream()));
                        } else {
                            appDataIS = new BufferedInputStream(conn.getInputStream());
                        }

                        XmlPullParser appDataParser = Xml.newPullParser();
                        appDataParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                        //AssetManager am2 = NextGenExperience.getContext().getAssets();
                        appDataParser.setInput(appDataIS, null);
                        //appDataParser.setInput(am2.open(appDataUrl), null);
                        appDataParser.nextTag();

                        appData[0] = parseAppData(appDataParser);
                        conn.disconnect();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        if (conn != null)
                            conn.disconnect();
                    } finally {

                        latch.countDown();
                    }
                }
            }.start();
        }
            //************ End of AppData Parsing

            // CPE Style Parsing
            if (styleDataUrl != null) {
                new Thread() {
                    @Override
                    public void run() {
                        // App Data Parsing

                        HttpURLConnection conn = null;
                        try {
                            URL appDataURL = new URL(styleDataUrl);
                            conn = (HttpURLConnection) appDataURL.openConnection();
                            conn.setReadTimeout(10000);
                            conn.setConnectTimeout(20000);
                            conn.setRequestMethod("GET");
                            conn.setDoInput(true);
                            conn.setRequestProperty("Accept-Encoding", "gzip");
                            conn.connect();
                            String responseEncoding = conn.getHeaderField("Content-Encoding");
                            InputStream styleDataIS;
                            if ("gzip".equalsIgnoreCase(responseEncoding)) {
                                styleDataIS = new BufferedInputStream(new GZIPInputStream(conn.getInputStream()));
                            } else {
                                styleDataIS = new BufferedInputStream(conn.getInputStream());
                            }

                            XmlPullParser styleDataParser = Xml.newPullParser();
                            styleDataParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                            styleDataParser.setInput(styleDataIS, null);
                            styleDataParser.nextTag();

                            styleData[0] = parseStyleData(styleDataParser);

                            conn.disconnect();

                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            if (conn != null)
                                conn.disconnect();
                        } finally {

                            latch.countDown();
                        }
                    }
                }.start();
            }
            //************ End of CPE Style Parsing

        new Thread() {
            @Override
            public void run() {
                // App Data Parsing

                HttpURLConnection conn = null;
                try {
                    // Manifest parsing
                    URL manifestURL = new URL(manifestUrl);
                    conn = (HttpURLConnection) manifestURL.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(20000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setRequestProperty("Accept-Encoding", "gzip");
                    conn.connect();
                    String responseEncoding = conn.getHeaderField("Content-Encoding");
                    InputStream manifestIS;
                    if ("gzip".equalsIgnoreCase(responseEncoding)) {
                        manifestIS = new BufferedInputStream(new GZIPInputStream(conn.getInputStream()));
                    } else {
                        manifestIS = new BufferedInputStream(conn.getInputStream());
                    }

                    XmlPullParser manifestParser = Xml.newPullParser();
                    manifestParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    manifestParser.setInput(manifestIS, null);
                    manifestParser.nextTag();


                    mainManifest[0] = parseManifest(manifestParser);
                    conn.disconnect();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    if (conn != null) {
                        conn.disconnect();
                    }
                } finally {
                    latch.countDown();
                }
            }
        }.start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new NextGenManifestData(mainManifest[0], appData[0], styleData[0], locale);
    }

    private Annotation[] getAllInheritedAnnotations(Class classObj){
        List<Annotation> allAnnotations = new ArrayList<Annotation>();
        Class currentClass = classObj;
        while(!currentClass.equals(Object.class)){
            allAnnotations.addAll(Arrays.asList(currentClass.getAnnotations()) );
            currentClass = currentClass.getSuperclass();
        }

        if (allAnnotations.size() > 0){
            return allAnnotations.toArray(new Annotation[allAnnotations.size()]);
        }else
            return new Annotation[0];
    }

    private class FieldClassObject{
        public final Field field;
        public final Class fieldClass;
        public FieldClassObject(Field field, Class fieldClass){
            this.field = field;
            this.fieldClass = fieldClass;
        }
    }

    HashMap<String, ClassObjectFieldSetters> classObjFieldMap = new HashMap<String, ClassObjectFieldSetters>();


    private class ClassObjectFieldSetters{
        HashMap<String, FieldClassObject> classXmlNameToFieldMap = new HashMap<String, FieldClassObject>();
        HashMap<String, Method> setterMap = new HashMap<String, Method>();
        HashMap<String, Method> getterMap = new HashMap<String, Method>();
        boolean isValue = false;
    }

    private <T extends Object> T parseElement(XmlPullParser parser, Class<T> classObj, String tagName){
        T retObj = null;
        ClassObjectFieldSetters thisClassMap = null;
        boolean canSkipAddToHashTable = false;
        if (classObjFieldMap.containsKey(classObj.toString())) {
            thisClassMap = classObjFieldMap.get(classObj.toString());
            canSkipAddToHashTable = true;
        }else{
            thisClassMap = new ClassObjectFieldSetters();
            classObjFieldMap.put(classObj.toString(), thisClassMap);
        }


        String currentInClass, currentFieldName = "";

        try {
            retObj = classObj.getConstructor().newInstance();
            //boolean bIsValue = false;

            if (!canSkipAddToHashTable) {
                Class currentClass = classObj;
                while (!currentClass.equals(Object.class)) {                  //handle XmlElement

                    Field declaredFields[] = currentClass.getDeclaredFields();
                    for (Field field : declaredFields) {

                        boolean bFoundAnnotations = false;
                        Annotation annotations[] = field.getAnnotations();
                        for (Annotation annotation : annotations) {
                            if (annotation instanceof XmlElement) {
                                bFoundAnnotations = true;
                                thisClassMap.classXmlNameToFieldMap.put(((XmlElement) annotation).name(), new FieldClassObject(field, currentClass));
                            } else if (annotation instanceof XmlValue) {
                                bFoundAnnotations = true;
                                thisClassMap.isValue = true;
                            } else if (annotation instanceof XmlAttribute) {
                                bFoundAnnotations = true;

                                Method setter = currentClass.getDeclaredMethod("set" + StringHelper.capitalize(((XmlAttribute) annotation).name()), field.getType());
                                thisClassMap.setterMap.put(((XmlAttribute) annotation).name(), setter);

                                String attributValue = parser.getAttributeValue("", ((XmlAttribute) annotation).name());
                                if (!StringHelper.isEmpty(attributValue)) {
                                    Object obj = toObject(field.getType(), attributValue);
                                    setter.invoke(retObj, obj);
                                }
                            }
                        }
                        if (!bFoundAnnotations){
                            try {
                                thisClassMap.classXmlNameToFieldMap.put(field.getName(), new FieldClassObject(field, currentClass));
                                /*Method setter = currentClass.getDeclaredMethod("set" + StringHelper.capitalize(field.getName()), field.getType());
                                thisClassMap.setterMap.put(field.getName(), setter);
                                String attributValue = parser.getAttributeValue("", field.getName());
                                if (!StringHelper.isEmpty(attributValue)) {
                                    Object obj = toObject(field.getType(), attributValue);
                                    setter.invoke(retObj, obj);
                                }*/
                            }catch (Exception ex){}
                        }
                    }
                    currentClass = currentClass.getSuperclass();
                }
            }else {
                for (String key : thisClassMap.setterMap.keySet()){
                    Method setter = thisClassMap.setterMap.get(key);
                    String attributValue = parser.getAttributeValue("", key);
                    if (attributValue != null) {
                        Object obj = toObject(setter.getParameterTypes()[0], attributValue);
                        setter.invoke(retObj, obj);
                    }

                }
            }

            if (thisClassMap.isValue){
                try {
                    Method setter = classObj.getDeclaredMethod("setValue", String.class);
                    setter.invoke(retObj, readText(parser));
                    return retObj;
                } catch (Exception ex) {

                }

            }


            // Handle XmlAttribute
            // Handle XmlVlaue

            //Parse the element
            parser.require(XmlPullParser.START_TAG, ns, tagName);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String xmlElementName = parser.getName();
                int nameSpaceendPos = xmlElementName.indexOf(":");
                if (nameSpaceendPos != -1){
                    xmlElementName = xmlElementName.substring(nameSpaceendPos+1);

                }
                /*if (xmlElementName.startsWith(MANIFEST_HEADER)){
                    xmlElementName = xmlElementName.replaceFirst(MANIFEST_HEADER, "");
                } else if (xmlElementName.startsWith(MANIFEST_APPDATA_HEADER)){
                    xmlElementName = xmlElementName.replaceFirst(MANIFEST_APPDATA_HEADER, "");
                }*/
                currentFieldName = xmlElementName;
                if (thisClassMap.classXmlNameToFieldMap.containsKey(xmlElementName)){
                    FieldClassObject thisFieldClass = thisClassMap.classXmlNameToFieldMap.get(xmlElementName);

                    try {
                        if (thisFieldClass.field.getType().isAnnotationPresent(XmlType.class)) {
                            Object fieldObj = parseElement(parser, thisFieldClass.field.getType(), parser.getName());
                            Method setter = thisFieldClass.fieldClass.getDeclaredMethod("set" + xmlElementName, thisFieldClass.field.getType());

                            setter.invoke(retObj, fieldObj);
                        } else if (thisFieldClass.field.getType().equals(List.class)) {             // no setter for list, should use getter and add


                            if (thisFieldClass.field.getGenericType() instanceof ParameterizedType) {
                                Type elementType = ((ParameterizedType) thisFieldClass.field.getGenericType()).getActualTypeArguments()[0];        // get the List <Type>
                                Object listItmeObj = null;
                                Method getter = thisFieldClass.fieldClass.getDeclaredMethod("get" + xmlElementName);

                                List listObj = (List) getter.invoke(retObj);

                                if (((Class) elementType).isAnnotationPresent(XmlType.class)) {
                                    listItmeObj = parseElement(parser, (Class) elementType, parser.getName());

                                } else {
                                    listItmeObj = toObject(thisFieldClass.field.getType(), readText(parser));
                                }


                                listObj.add(listItmeObj);

                            }


                        } else {
                            Object obj = toObject(thisFieldClass.field.getType(), readText(parser));
                            try {
                                String setterName = getSetterName(xmlElementName);
                                Method setter = thisFieldClass.fieldClass.getDeclaredMethod(setterName, thisFieldClass.field.getType());

                                setter.invoke(retObj, obj);
                            }catch (Exception ex){
                                obj = null;
                            }
                        }
                    }catch (IllegalArgumentException iex){
                        System.out.println("PARSER VALUE ERROR " + iex.getLocalizedMessage());
                    }
                }else
                    skip(parser);


            }



    }catch (Exception ex){
            System.out.println(ex.getLocalizedMessage() + "Current injecting class: " + classObj.getName() + " Field: " + currentFieldName );
            ex.printStackTrace();
        }

        return retObj;
    }

    private static String getSetterName(String xmlElementName){  // to avoid calling setClass
        if (xmlElementName.equalsIgnoreCase("class")) {
            xmlElementName = xmlElementName.replace("s", "z");
        }
        return "set" + xmlElementName.substring(0,1).toUpperCase() + xmlElementName.substring(1, xmlElementName.length());
    }

    private Object toObject(Class targetClass, String stringValue) throws IOException, XmlPullParserException{

        if (targetClass.equals(String.class)) {
            return stringValue;
        }else if (targetClass.equals(int.class)){
            return Integer.parseInt(stringValue);
        }else if (targetClass.equals(boolean.class)){
            return Boolean.parseBoolean(stringValue);
        }else if (targetClass.equals(BigInteger.class)) {
            return new BigInteger(stringValue);
        }else if (targetClass.equals(Integer.class)){
            return new Integer(stringValue);
        }else if (targetClass.equals(Boolean.class)){
            return new Boolean(stringValue);
        }else if (targetClass.equals(Float.class)){
            return new Float(stringValue);
        }else if (targetClass.equals(Double.class)){
            return new Double(stringValue);
        }else if (targetClass.equals(XMLGregorianCalendar.class)){
            try {

                return DatatypeFactory.newInstance().newXMLGregorianCalendar(stringValue);
            }catch (Exception ex){
                return null;
            }
        }else if (targetClass.equals(Duration.class)){
            final com.wb.nextgenlibrary.model.Duration d = new com.wb.nextgenlibrary.model.Duration(stringValue);
            return new Duration() {
                @Override
                public int getSign() {
                    return 0;
                }

                @Override
                public Number getField(DatatypeConstants.Field field) {
                    if (field.equals(DatatypeConstants.HOURS))
                        return BigInteger.valueOf(d.hours);
                    else if (field.equals(DatatypeConstants.MINUTES))
                        return BigInteger.valueOf(d.minutes);
                    else if (field.equals(DatatypeConstants.SECONDS))
                        return BigInteger.valueOf(d.seconds);
                    return null;
                }

                String formattedString;
                @Override
                public String toString() {
                    if (formattedString != null)
                        return formattedString;

                    formattedString = "";
                    SimpleDateFormat formatter = null;
                    if (d.hours > 0) {
                        formatter = new SimpleDateFormat("h:mm:ss");
                    } else {
                        formatter = new SimpleDateFormat("m:ss");
                    }
                    formattedString = formatter.format(d.getDate());

                    return formattedString;
                }

                @Override
                public boolean isSet(DatatypeConstants.Field field) {
                    return false;
                }

                @Override
                public Duration add(Duration rhs) {
                    return null;
                }

                @Override
                public void addTo(Calendar calendar) {

                }

                @Override
                public Duration multiply(BigDecimal factor) {
                    return null;
                }

                @Override
                public Duration negate() {
                    return null;
                }

                @Override
                public Duration normalizeWith(Calendar startTimeInstant) {
                    return null;
                }

                @Override
                public int compare(Duration duration) {
                    return 0;
                }

                @Override
                public int hashCode() {
                    return 0;
                }
            };
        }else {
            return  stringValue;
        }
    }


    private MediaManifestType parseManifest(XmlPullParser parser) throws XmlPullParserException, IOException {
        return parseElement(parser, MediaManifestType.class, "manifest:MediaManifest");

    }

    private ManifestAppDataSetType parseAppData(XmlPullParser parser) throws XmlPullParserException, IOException {
        return parseElement(parser, ManifestAppDataSetType.class, "manifestdata:ManifestAppDataSet");

    }

    private CPEStyleSetType parseStyleData(XmlPullParser parser) throws XmlPullParserException, IOException {
        return parseElement(parser, CPEStyleSetType.class, "cpestyle:CPEStyleSet");

    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}


