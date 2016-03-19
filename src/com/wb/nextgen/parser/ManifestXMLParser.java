package com.wb.nextgen.parser;

import android.content.res.AssetManager;
import android.util.Xml;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.parser.manifest.schema.v1_4.MediaManifestType;
import com.wb.nextgen.util.utils.StringHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by gzcheng on 3/16/16.
 */
public class ManifestXMLParser {
    private static final String ns = null;
    /*public static void kParser() {

        try {
            // Parser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            AssetManager am = NextGenApplication.getContext().getAssets();

            xpp.setInput(new InputStreamReader(am.open("mos_hls_manifest_v3.xml")));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)

            {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    System.out.println("Start document");
                } else if (eventType == XmlPullParser.END_DOCUMENT) {
                    System.out.println("End document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    System.out.println("Start tag " + xpp.getName());
                } else if (eventType == XmlPullParser.END_TAG) {
                    System.out.println("End tag " + xpp.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    System.out.println("Text " + xpp.getText());
                }
                eventType = xpp.next();
            }


            am.close();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }*/

    private List result;

    private static String MANIFEST_HEADER = "manifest:";

    public MediaManifestType startParsing(){
        MediaManifestType manifest = null;
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            AssetManager am = NextGenApplication.getContext().getAssets();
            parser.setInput(am.open("mos_hls_manifest_v3.xml"), null);
            parser.nextTag();
            //result = readFeed(parser);

            manifest = parseManifest(parser);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return manifest;
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

    private <T extends Object> T parseElement(XmlPullParser parser, Class<T> classObj, String tagName){
        T retObj = null;
        HashMap<String, FieldClassObject> classXmlNameToFieldMap = new HashMap<String, FieldClassObject>();

        String currentInClass, currentFieldName = "";

        try {
            retObj = classObj.getConstructor().newInstance();

            Class currentClass = classObj;
            while(!currentClass.equals(Object.class)){                  //handle XmlElement
                Annotation classAnnotations[] = currentClass.getAnnotations();
                if (classAnnotations != null && classAnnotations.length > 0){
                    for(Annotation thisAnnotation : classAnnotations){
                        if (thisAnnotation instanceof XmlType && ((XmlType)thisAnnotation).propOrder() != null && ((XmlType)thisAnnotation).propOrder().length > 0){
                            for (String fieldName : ((XmlType)thisAnnotation).propOrder()){
                                if (!StringHelper.isEmpty(fieldName)) {
                                    Field field = currentClass.getDeclaredField(fieldName);
                                    for (Annotation annotation : field.getAnnotations()) {
                                        if (annotation instanceof XmlElement) {
                                            classXmlNameToFieldMap.put(((XmlElement) annotation).name(), new FieldClassObject(field, currentClass));
                                        } else if (annotation instanceof XmlValue) {

                                        } else if (annotation instanceof XmlAttribute){

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                currentClass = currentClass.getSuperclass();
            }


            // Handle XmlAttribute
            // Handle XmlVlaue

            //Parse the element
            parser.require(XmlPullParser.START_TAG, ns, tagName);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() == XmlPullParser.TEXT){
                    try{
                        Method setter = classObj.getDeclaredMethod("setValue", String.class);
                        setter.invoke(retObj, readText(parser));
                        return retObj;
                    }catch (Exception ex){

                    }
                    continue;
                }else if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String xmlElementName = parser.getName();
                if (xmlElementName.startsWith(MANIFEST_HEADER)){
                    xmlElementName = xmlElementName.replaceFirst(MANIFEST_HEADER, "");
                }
                currentFieldName = xmlElementName;
                if (classXmlNameToFieldMap.containsKey(xmlElementName)){
                    FieldClassObject thisFieldClass = classXmlNameToFieldMap.get(xmlElementName);

                    try {

                        if (thisFieldClass.field.getType().isAnnotationPresent(XmlType.class)) {
                            Object fieldObj = parseElement(parser, thisFieldClass.field.getType(), parser.getName());
                            //thisField.set(thisField.getType(), fieldObj);
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
                                    listItmeObj = toObject(thisFieldClass.field.getType(), parser);
                                }


                                listObj.add(listItmeObj);

                            }


                            //setter.invoke(retObj, listObj);
                        }/*else if (thisFieldClass.field.getType().isPrimitive()) {

                        Method setter = thisFieldClass.fieldClass.getDeclaredMethod("set" + xmlElementName, thisFieldClass.field.getType());
                        if (parser.next() == XmlPullParser.TEXT) {
                            setter.invoke(retObj, parser.get);
                            parser.nextTag();
                        }
                        setter.invoke(retObj, obj);
                    }*/ else {
                            //String stringValue = readText(parser, parser.getName());\
                            Object obj = toObject(thisFieldClass.field.getType(), parser);
                            Method setter = thisFieldClass.fieldClass.getDeclaredMethod("set" + xmlElementName, thisFieldClass.field.getType());
                            setter.invoke(retObj, obj);
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

    private Object toObject(Class targetClass, XmlPullParser parser) throws IOException, XmlPullParserException{

        String stringValue = readText(parser);
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
        }else if (targetClass.equals(XMLGregorianCalendar.class)){
            try {

                return DatatypeFactory.newInstance().newXMLGregorianCalendar(stringValue);
            }catch (Exception ex){
                return null;
            }
        }else if (targetClass.equals(Duration.class)){
            return new Duration() {
                @Override
                public int getSign() {
                    return 0;
                }

                @Override
                public Number getField(DatatypeConstants.Field field) {
                    return null;
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

/*
    private void getObjectValue(XmlPullParser parser, Field field, Class fieldClass, Object targetObj) throws IOException, XmlPullParserException, NoSuchMethodException,IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (field.getType().isAnnotationPresent(XmlType.class)){
            String stringValue = readText(parser, parser.getName());
            Method setter = fieldClass.getDeclaredMethod("set" + StringHelper.capitalize(field.getName()), field.getType());
            setter.invoke(targetObj, stringValue);
        }else {
            String stringValue = readText(parser, parser.getName());
        }
    }*/


    private MediaManifestType parseManifest(XmlPullParser parser) throws XmlPullParserException, IOException {
        return parseElement(parser, MediaManifestType.class, "manifest:MediaManifest");

    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "manifest:MediaManifest");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            Object obj = null;
            Class c = null;
            try {
                String className = name.replace("manifest:", this.getClass().getPackage().getName() + ".");
                c = Class.forName(name);
                //Object o = c.getConstructor(int.class).newInstance(1);
            }catch (Exception ex){

            }
            if (obj != null ) {
                entries.add(createObject(parser, c));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class Entry {
        public final String title;
        public final String link;
        public final String summary;

        private Entry(String title, String summary, String link) {
            this.title = title;
            this.summary = summary;
            this.link = link;
        }
    }

    private Object createObject(XmlPullParser parser, Class c) throws XmlPullParserException, IOException {
        Object obj = null;
        try {
            obj = c.getConstructor().newInstance();
        }catch (Exception ex){

        }
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Field field = null;
            try {
                field = c.getField(name);
            }catch (NoSuchFieldException nex){

            }
            /*if (field != null) {
                c.get
            } else if (name.equals("summary")) {
                summary = readSummary(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else {
                skip(parser);
            }*/
        }
        return obj;
    }


    // Processes summary tags in the feed.
    /*private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }*/

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


