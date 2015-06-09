//package com.mobile.utils;
//
//import android.app.Activity;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
///**
//* This Class is responsible to inflate the new layout.
//* It is used by all activities. <p/><br>
// *
// * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
// *
// * Unauthorized copying of this file, via any medium is strictly prohibited <br>
// * Proprietary and confidential.
//*
//* @author Sergio Pereira
//*
//* @version 1.01
//*
//* 2012/06/19
//*
//*/
//public class InflateLayout {
//
//    /**
//     * Inflates a layout into a given view
//     * @param activity The activity where this laysout should be inflated to
//     * @param layoutId The layout id that is going to post as the parent of the new layout
//     * @param newLayout The id of the new layout to inflate
//     */
//	public static void inflate(Activity activity, int layoutId, int newLayout){
//		// Get the Inflate Service
//		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		// Get The Content Layout
//		ViewGroup contentLayout = (ViewGroup) activity.findViewById(layoutId);
//		// Inflate the Categories Layout
//		inflater.inflate(newLayout, contentLayout);
//	}
//
//}
