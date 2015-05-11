///**
// *
// */
//package com.mobile.framework.objects;
//
//import org.json.JSONObject;
//
///**
// * @author nutzer2
// *
// */
//@Deprecated
//public class UnknownTeaserGroup extends TeaserSpecification<ITargeting> {
//
//	public static final UnknownTeaserGroup INSTANCE = new UnknownTeaserGroup();
//
//	/**
//	 * @param type
//	 */
//	private UnknownTeaserGroup() {
//		super(TeaserGroupType.UNKNOWN);
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mobile.framework.objects.TeaserSpecification#parseData(org.json.JSONObject)
//	 */
//	@Override
//	protected ITargeting parseData(JSONObject object) {
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mobile.framework.objects.TeaserSpecification#initialize(org.json.JSONObject)
//	 */
//	@Override
//	public boolean initialize(JSONObject jsonObject) {
//		return false;
//	}
//
//}
