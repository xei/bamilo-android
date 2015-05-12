///**
// *
// */
//package com.mobile.framework.objects;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//
///**
// * @author Manuel Silva
// *
// */
//@Deprecated
//public class TeaserBrand extends TeaserBrandElement implements ITargeting, Parcelable {
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see com.mobile.framework.objects.ITargeting#getTargetType()
//	 */
//	@Override
//	public TargetType getTargetType() {
//		return TargetType.BRAND;
//	}
//
//	public String getImage() {
//		return super.getImageUrl();
//	}
//
//	@Override
//	public String getBrandUrl() {
//		return super.getBrandUrl();
//	}
//
//	@Override
//	public String getTargetUrl() {
//		return super.getName();
//	}
//
//	@Override
//	public String getTargetTitle() {
//		return super.getName();
//	}
//
//	@Override
//	public String getImageTableUrl() {
//		return super.getImageTableUrl();
//	}
//
//	public TeaserBrand(){
//
//	}
//
//    /**
//     * Parcel constructor
//     * @param in
//     */
//    private TeaserBrand(Parcel in) {
//        super(in);
//    }
//
//    /**
//     * Create parcelable
//     */
//    public static final Parcelable.Creator<TeaserBrand> CREATOR = new Parcelable.Creator<TeaserBrand>() {
//        public TeaserBrand createFromParcel(Parcel in) {
//            return new TeaserBrand(in);
//        }
//
//        public TeaserBrand[] newArray(int size) {
//            return new TeaserBrand[size];
//        }
//    };
//
//}
