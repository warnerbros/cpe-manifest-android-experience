package com.wb.nextgen.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by gzcheng on 4/7/16.
 */
public class TheTakeData {
    public static class TheTakeCategory{
        public int categoryId;
        public String categoryName;
        public List<TheTakeCategory> childCategories;
    }

    public static class TheTakeProductFrame{
        public FrameImages frameImages;
        public int frameTime;
        public double frameLetterboxRatio;
    }

    public static class FrameImages{
        /*1000pxFrameLink	String	https://img.thetake.com/frame_images/c9994d204a97f30ba94b38887ebdae4068848e64b64660505dd134be44c2bf9a.jpeg
                500pxFrameLink	String	https://img.thetake.com/frame_images/c90d9a64b3ab8d0e1f54a3ca9a801dd0e284f5c40ea603429e8afca11a6ee739.jpeg
                50pxFrameLink	String	https://img.thetake.com/frame_images/bd17430b105f2286bd0c7b04754cd6aea927853d92482f7bd6fb7cf6aa09ffb7.jpeg
        fullSizeFrameLink	String	https://img.thetake.com/frame_images/1f2f2a96e774b50a8563b7d9582c9a76940eaccec42f4d747de7c3547d3870a9.jpeg
                250pxFrameLink	String	https://img.thetake.com/frame_images/d197416f8b5e04ab8b5b2fc53afe843ae09300abc5c41413d176f368f9a330ce.jpeg
                125pxFrameLink	String	https://img.thetake.com/frame_images/fe47367a66b108532eacbb1245d9b13be01b1a6f7c08fda0fd9697e52a40fd9e.jpeg*/
        @SerializedName("1000pxFrameLink")
        public String image1000px;
        @SerializedName("500pxFrameLink")
        public String image500px;
        @SerializedName("fullSizeFrameLink")
        public String image1FullSize;
        @SerializedName("250pxFrameLink")
        public String image250px;
        @SerializedName("125pxFrameLink")
        public String image125px;
        @SerializedName("50pxFrameLink")
        public String image50px;
    }

}
/*
class TheTakeCategory: NSObject {

        struct Keys {
static let CategoryID = "categoryId"
static let CategoryName = "categoryName"
static let ChildCategories = "childCategories"
        }

        var id: Int!
        var name: String!
        var children: [TheTakeCategory]?

        convenience init(info: NSDictionary) {
        self.init()

        id = (info[Keys.CategoryID] as! NSNumber).integerValue
        name = info[Keys.CategoryName] as! String

        if let childCategories = info[Keys.ChildCategories] as? [NSDictionary] {
        children = [TheTakeCategory]()
        for childCategory in childCategories {
        children!.append(TheTakeCategory(info: childCategory))
        }
        }
        }

        }*/