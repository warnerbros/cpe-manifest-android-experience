package com.wb.nextgen.parser;

import android.content.res.AssetManager;
import android.util.Xml;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.parser.appdata.ManifestAppDataSetType;
import com.wb.nextgen.parser.manifest.schema.v1_4.MediaManifestType;
import com.wb.nextgen.util.utils.StringHelper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

        public NextGenManifestData(MediaManifestType mediaManifestType, ManifestAppDataSetType appDataFeedSetType){
            mainManifest = mediaManifestType;
            appDataManifest = appDataFeedSetType;
        }
    }

    public NextGenManifestData startParsing(){
        NextGenManifestData manifest = null;
        try{

            XmlPullParser parser2 = Xml.newPullParser();
            parser2.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            AssetManager am2 = NextGenApplication.getContext().getAssets();
            //parser2.setInput(am2.open("mos_appdata_locations_r60_v0.3.xml"), null);
            parser2.setInput(am2.open("mos_appdata_locations_r60-v0.5.xml"), null);
            parser2.nextTag();

            ManifestAppDataSetType appData = parseAppData(parser2);

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            AssetManager am = NextGenApplication.getContext().getAssets();
            //parser.setInput(am.open("mos_hls_manifest_v3.xml"), null);
            //parser.setInput(am.open("mos_hls_manifest_r60_v0.4.xml"), null);
            parser.setInput(am.open("mos_hls_manifest_r60-v0.5.xml"), null);
            parser.nextTag();


            MediaManifestType mainManifest = parseManifest(parser);
            manifest = new NextGenManifestData(mainManifest, appData);

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

                        Annotation annotations[] = field.getAnnotations();
                        for (Annotation annotation : annotations) {
                            if (annotation instanceof XmlElement) {
                                thisClassMap.classXmlNameToFieldMap.put(((XmlElement) annotation).name(), new FieldClassObject(field, currentClass));
                            } else if (annotation instanceof XmlValue) {
                                thisClassMap.isValue = true;
                            } else if (annotation instanceof XmlAttribute) {
                                String attributValue = parser.getAttributeValue("", ((XmlAttribute) annotation).name());
                                if (!StringHelper.isEmpty(attributValue)) {
                                    Object obj = toObject(field.getType(), attributValue);
                                    Method setter = currentClass.getDeclaredMethod("set" + StringHelper.capitalize(((XmlAttribute) annotation).name()), field.getType());
                                    thisClassMap.setterMap.put(((XmlAttribute) annotation).name(), setter);
                                    setter.invoke(retObj, obj);
                                }
                            }
                        }
                    }
                    currentClass = currentClass.getSuperclass();
                }
            }else {
                for (String key : thisClassMap.setterMap.keySet()){
                    Method setter = thisClassMap.setterMap.get(key);
                    String attributValue = parser.getAttributeValue("", key);
                    Object obj = toObject(setter.getParameterTypes()[0], attributValue);
                    setter.invoke(retObj, obj);

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
                            //thisField.set(thisField.getType(), fieldObj);
                            Method setter = thisFieldClass.fieldClass.getDeclaredMethod("set" + xmlElementName, thisFieldClass.field.getType());
                            /*Method setter;
                            if (canSkipAddToHashTable ){
                                setter = thisClassMap.setterMap.get(xmlElementName);
                            }else{
                                setter = thisFieldClass.fieldClass.getDeclaredMethod("set" + xmlElementName, thisFieldClass.field.getType());
                                thisClassMap.setterMap.put(xmlElementName, setter);
                            }*/

                            setter.invoke(retObj, fieldObj);
                        } else if (thisFieldClass.field.getType().equals(List.class)) {             // no setter for list, should use getter and add


                            if (thisFieldClass.field.getGenericType() instanceof ParameterizedType) {
                                Type elementType = ((ParameterizedType) thisFieldClass.field.getGenericType()).getActualTypeArguments()[0];        // get the List <Type>
                                Object listItmeObj = null;
                                Method getter = thisFieldClass.fieldClass.getDeclaredMethod("get" + xmlElementName);
                                /*Method getter;
                                if (canSkipAddToHashTable ){
                                    getter = thisClassMap.getterMap.get(xmlElementName);
                                }else{
                                    getter = thisFieldClass.fieldClass.getDeclaredMethod("get" + xmlElementName);
                                    thisClassMap.getterMap.put(xmlElementName, getter);
                                }*/
                                List listObj = (List) getter.invoke(retObj);

                                if (((Class) elementType).isAnnotationPresent(XmlType.class)) {
                                    listItmeObj = parseElement(parser, (Class) elementType, parser.getName());

                                } else {
                                    listItmeObj = toObject(thisFieldClass.field.getType(), readText(parser));
                                }


                                listObj.add(listItmeObj);

                            }


                            //setter.invoke(retObj, listObj);
                        } else {
                            //String stringValue = readText(parser, parser.getName());
                            Object obj = toObject(thisFieldClass.field.getType(), readText(parser));
                            Method setter = thisFieldClass.fieldClass.getDeclaredMethod("set" + xmlElementName, thisFieldClass.field.getType());
                            /*Method setter;
                            if (canSkipAddToHashTable ){
                                setter = thisClassMap.setterMap.get(xmlElementName);
                            }else{
                                setter = thisFieldClass.fieldClass.getDeclaredMethod("set" + xmlElementName, thisFieldClass.field.getType());
                                thisClassMap.setterMap.put(xmlElementName, setter);
                            }*/
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
            final com.wb.nextgen.model.Duration d = new com.wb.nextgen.model.Duration(stringValue);
            return new Duration() {
                @Override
                public int getSign() {
                    return 0;
                }

                @Override
                public Number getField(DatatypeConstants.Field field) {
                    if (field.equals(DatatypeConstants.HOURS))
                        return BigInteger.valueOf(d.hour);
                    else if (field.equals(DatatypeConstants.MINUTES))
                        return BigInteger.valueOf(d.minutes);
                    else if (field.equals(DatatypeConstants.SECONDS))
                        return BigInteger.valueOf(d.second);
                    return null;
                }

                String formattedString;
                @Override
                public String toString() {
                    if (formattedString != null)
                        return formattedString;

                    formattedString = "";
                    String hString="", mString="00", sString="00", resultString = "00";
                    if (d.hour > 0){
                        hString = Integer.toString(d.hour);
                    }
                    if (d.minutes > 0){
                        mString = d.minutes < 10 ? "0" + d.minutes : Integer.toString(d.minutes);
                    }else{

                    }
                    if (d.second > 0){
                        sString = d.second < 10 ? "0" + d.second : Integer.toString(d.second);
                    }



                    formattedString += hString + ":" + mString + ":" + sString;

                    while (formattedString.startsWith("0") || formattedString.startsWith(":")){
                        formattedString = formattedString.substring(1);
                    }


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


